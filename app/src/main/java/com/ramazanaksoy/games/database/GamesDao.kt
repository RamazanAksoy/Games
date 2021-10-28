package com.ramazanaksoy.games.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GamesDao {

    //veri eklenmesi
    @Insert
    fun insert(games: ArrayList<GamesEntity>)

    //veri silinmesi
    @Delete
    fun delete(games: GamesEntity)

    //tüm verilerin çekilmesi
    @Query("SELECT*FROM games")
    fun getAllGames(): List<GamesEntity>

    //gelen id değerine göre veri çekilmesi
    @Query("SELECT * FROM games WHERE id = :id")
    fun getIdGames(id: Int):List<GamesEntity>

    //gelen favorite değerini idye göre güncelleme
    @Query("UPDATE games SET favorite=:favorite WHERE id = :id")
    fun update(favorite: Int?, id: Int)

         //gelen texti name alanında arama
        @Query("SELECT * FROM games WHERE name LIKE :search")
        fun getTextSearch(search:String):List<GamesEntity>




}