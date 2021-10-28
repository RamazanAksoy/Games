package com.ramazanaksoy.games.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.ramazanaksoy.games.R
import com.ramazanaksoy.games.adapter.PageViewAdapter
import com.ramazanaksoy.games.database.GamesDatabase
import com.ramazanaksoy.games.database.GamesEntity
import com.ramazanaksoy.games.models.Games
import com.ramazanaksoy.games.models.GamesAdapter
import com.ramazanaksoy.games.models.Resultt
import com.ramazanaksoy.games.ui.activity.DetailsActivity
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.*
import java.io.IOException

class HomeFragment : Fragment() {
    lateinit var filteredList: ArrayList<GamesEntity>
    lateinit var newList: ArrayList<Resultt>
    lateinit var gamesDatabase: GamesDatabase
    var databaseDataEmpty: Boolean = false
    lateinit var gamesList: ArrayList<GamesEntity>
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        databaseDataCheck()

        //home sayfasının açıldığına dair event gönderme
        firebaseAnalytics.logEvent("home_fragment_open", null)

        //databasede veri yok ise apiden veriler çekilir
        if (databaseDataEmpty) {
            getGameDataApi()
        }
        //database apiden çekilen veriler öncesinde eklenmiş ise databasedeki verileri listeleme
        else {
            setupRecyclerView(gamesList, false)
            setupPageView(gamesList)
            progressBarHome.visibility = View.GONE
        }
        searchData()
    }

    private fun init() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        progressBarHome.visibility = View.VISIBLE
        gamesDatabase = GamesDatabase.getGamesDatabase(requireContext())!!
        RecylerViewGames.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun databaseDataCheck() {
        //databasede veri olup olmama kontrolunun yapılması
        gamesList = gamesDatabase?.gamesDao()?.getAllGames() as ArrayList<GamesEntity>
        databaseDataEmpty = gamesList.size == 0
    }

    private fun getGameDataApi() {
        //api url ve keyler yazıldı.
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://rawg-video-games-database.p.rapidapi.com/games?key=6ec2f4b5dfed46238709bd2dfe1e92fd")
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
                newList = gson.fromJson(body, Games::class.java).results as ArrayList<Resultt>
                //apiden çekilen verileri gamesliste aktarma
                newList.forEach {
                    gamesList.add(
                        GamesEntity(
                            it.id,
                            it.name.toString(),
                            it.rating.toString(),
                            it.released.toString(),
                            it.background_image,
                            0
                        )
                    )
                }
                activity?.runOnUiThread(Runnable {
                    //pageview ve recylerview içerisine gameslist gönderildi.
                    setupPageView(gamesList)
                    setupRecyclerView(gamesList, false)
                    //apiden çekilen veriler database aktarıldı.
                    gamesDatabase?.gamesDao()?.insert(gamesList)
                    progressBarHome.visibility = View.GONE
                })
            }
        })
    }

    private fun setupPageView(list: ArrayList<GamesEntity>,) {
        //oyun listesi pageviewadaptere aktarılır.
        val adapter =
            PageViewAdapter(list, requireContext()) { itemModel: GamesEntity ->
                itemModelClicked(itemModel)
            }
        pageViewImage.adapter = adapter
    }

    private fun setupRecyclerView(list: ArrayList<GamesEntity>, search: Boolean) {
        //parametre ile gelen liste adaptare aktarılır.
        val adapter =
            GamesAdapter(list, search) { itemModel: GamesEntity ->
                itemModelClicked(itemModel)
            }
        RecylerViewGames.adapter = adapter

    }

    private fun searchData() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filteredList = ArrayList()
                //girilen karakter 3 harften büyük olduğunda veritabanında name alanında sorgulama yapılır.
                if (p0!!.length > 3) {
                    pageViewImage.visibility = View.GONE
                    filteredList = gamesDatabase.gamesDao()
                        .getTextSearch(p0.toString().toLowerCase() + "%") as ArrayList<GamesEntity>

                    //veritabanından gelen değer boş ise boş yazısı görünür.
                    if (filteredList.size == 0)
                        textSearchNotFound.visibility = View.VISIBLE
                    //boş değil ise gelen veriler adaptere yollanarak listelenir.
                    else {
                        textSearchNotFound.visibility = View.GONE
                        setupRecyclerView(filteredList, true)
                    }
                    //girilen karakter 3ten küçük olduğunda bütün oyunlar listelenmeye devam eder.
                } else {
                    pageViewImage.visibility = View.VISIBLE
                    textSearchNotFound.visibility = View.GONE
                    setupRecyclerView(gamesList, false)
                }
            }

        })


    }

    private fun itemModelClicked(itemModel: GamesEntity) {
        //detay sayfasına id  değerinin gönderilmesi
        val intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtra("id", itemModel.id)
        startActivity(intent)
    }
}