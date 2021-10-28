package com.ramazanaksoy.games.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GamesEntity::class], version = 1)
abstract class GamesDatabase : RoomDatabase() {

    abstract fun gamesDao(): GamesDao

    companion object {
        private var instance: GamesDatabase? = null

        fun getGamesDatabase(context: Context): GamesDatabase? {

            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    GamesDatabase::class.java,
                    "game.db"
                ).allowMainThreadQueries()
                    .build()
            }
            return instance
        }
    }
}