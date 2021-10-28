package com.ramazanaksoy.games.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.ramazanaksoy.games.R
import com.ramazanaksoy.games.database.GamesDatabase
import com.ramazanaksoy.games.database.GamesEntity
import com.ramazanaksoy.games.models.GamesAdapter
import com.ramazanaksoy.games.ui.activity.DetailsActivity
import kotlinx.android.synthetic.main.fragment_favorite.*

class FavoriteFragment : Fragment() {
    lateinit var gamesDatabase: GamesDatabase
    lateinit var favoriteGamesList: ArrayList<GamesEntity>
    lateinit var gamesList: ArrayList<GamesEntity>
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_favorite, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        dbGetAllFavoriteGames()

        //favori sayfasının açıldığına dair event gönderme
        firebaseAnalytics.logEvent("favori_fragment_open",null)
    }

    private fun dbGetAllFavoriteGames() {
        //veritabanında ki verilerinin listeye aktarılması
        favoriteGamesList= arrayListOf()
       gamesList = gamesDatabase?.gamesDao()?.getAllGames() as ArrayList<GamesEntity>

        //veritabanınından çekilen listede favori alanı 1 olan verilerin favori listesine aktarılması
            gamesList.forEach {
                if (it.favorite == 1) {
                    favoriteGamesList.add(it)
                }
            }
        //favori listesi boş değil ise verilerin adaptere gönderilerek listelenmesi
        if(favoriteGamesList.size!=0)
        {
            textFavoriNotFound.visibility=View.GONE
            setupRecyclerView(favoriteGamesList)
        }
        //favori listesi boş ise boş yazının gözükmesi
        else
            textFavoriNotFound.visibility=View.VISIBLE
    }

    private fun setupRecyclerView(list: ArrayList<GamesEntity>) {
        //listenin adapter ile entegre edilmesi
        val adapter =
            GamesAdapter(list,true) { itemModel: GamesEntity ->
                itemModelClicked(itemModel)
            }
        recylerviewFavorite.adapter = adapter
    }

    private fun itemModelClicked(itemModel: GamesEntity) {
        //detay sayfasına id  değerinin gönderilmesi
        val intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtra("id", itemModel.id)
        startActivity(intent)
    }

    private fun init() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        gamesDatabase =GamesDatabase.getGamesDatabase(requireContext())!!
        recylerviewFavorite.layoutManager = LinearLayoutManager(requireContext())
    }

}
