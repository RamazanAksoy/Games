package com.ramazanaksoy.games.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.ramazanaksoy.games.R
import com.ramazanaksoy.games.database.GamesDatabase
import com.ramazanaksoy.games.database.GamesEntity
import com.ramazanaksoy.games.models.GameDetails
import kotlinx.android.synthetic.main.activity_details.*
import okhttp3.*
import java.io.IOException

class DetailsActivity : AppCompatActivity() {
    val context:Context=this
    lateinit var newList:GameDetails
    var favori:Boolean = false
    lateinit var gamesList: ArrayList<GamesEntity>
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val gamesDatabase = GamesDatabase.getGamesDatabase(this)

        //gelen idnin değişkene aktarılması
        val id = intent.getIntExtra("id", 0)
        progressBar.visibility=View.VISIBLE


        favoriCheck(gamesDatabase,id)
        init()
        gameDetailsDatawithId(id)

        //firebase detay sayfasının açıldığına dair event gönderme
        firebaseAnalytics.logEvent("detay_activity_open",null)


        favoriteAddButon.setOnClickListener{

            if (!favori)
            {
                //firebase favorilere eklendiğine dair event gönderme(oyun ismi ile birlikte)
                val bundle = Bundle()
                bundle.putString("name", gamesList[0].name)
                firebaseAnalytics.logEvent("added_to_favourites" ,bundle)


                //butona tıklanıldığında database de favorilerde yok ise favori alanın 1 olarak güncellenmesi
                gamesDatabase?.gamesDao()?.update(1,id)
                favoriteAddButon.setImageResource(R.drawable.ic_baseline_favorite_24)
                favori=true
            }
            else
            {
                //firebase favorilerden kaldırıldığına dair event gönderme(oyun ismi ile birlikte)
                val bundle = Bundle()
                bundle.putString("name", gamesList[0].name)
                firebaseAnalytics.logEvent("removed_from_favourites" ,bundle)

                //butona tıklanıldığında favorilerde var ise favori alanın 0 olarak güncellenmesi
                gamesDatabase?.gamesDao()?.update(0,id)
                favoriteAddButon.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                favori=false
            }

        }
    }
    fun init(){
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val media =gamesList[0].background_image
        Glide.with(context)
            .load(media)
            .into(imageGameDetails)
    }
    fun favoriCheck(gamesDatabase: GamesDatabase?, id: Int) {
        //gelen id veritabanındaki favori tablosunda var ise favori butonu dolu halde getirilmesi
        gamesList = gamesDatabase?.gamesDao()?.getIdGames(id) as ArrayList<GamesEntity>
        if(gamesList[0].favorite==1)
        {
            favoriteAddButon.setImageResource(R.drawable.ic_baseline_favorite_24)
            favori=true
        }
    }
    fun gameDetailsDatawithId(id: Int) {

        //api url ve keyler girildi
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://rawg-video-games-database.p.rapidapi.com/games/${id.toString()}?key=6ec2f4b5dfed46238709bd2dfe1e92fd")
            .get()
            .addHeader("x-rapidapi-host", "rawg-video-games-database.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "a5d8c22548msh0051beb427f9aecp1a2ddejsn8fb5213e81c9")
            .build()


        val response = client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Failed", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                var gson = Gson()
                newList = gson.fromJson(body, GameDetails::class.java)


                runOnUiThread {
                    progressBar.visibility=View.GONE
                    //gelen verileri ekranda gösterme
                    tvDescription.text = newList.description
                    tvMetacriticDate.text = newList.metacritic.toString()
                    tvNameGameDetail.text = newList.name
                    tvReleaseDate.text = newList.released
                }
            }
        })
    }

}
