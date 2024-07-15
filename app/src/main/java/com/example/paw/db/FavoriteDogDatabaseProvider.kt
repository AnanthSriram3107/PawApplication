package com.example.paw.db

import android.content.Context
import androidx.room.Room

class FavoriteDogDatabaseProvider(context: Context) : FavoriteDogDaoProvider {

    private val database by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            PawDataBase::class.java,
            "paw_database"
        ).fallbackToDestructiveMigration().build()
    }

    override fun getFavoriteDogDao(): FavoriteDogDao {
        return database.favoriteDogDao()
    }

    fun providePawDatabase(): PawDataBase {
        return database
    }


}