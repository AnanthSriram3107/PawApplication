package com.example.paw.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.paw.utils.ImageListConverter

@Database(
    entities = [FavoriteDog::class,
        DogCollection::class,
        CollectionImage::class,
        DogOfTheDay::class],
    version = 3
)
@TypeConverters(ImageListConverter::class)
abstract class PawDataBase : RoomDatabase() {
    abstract fun favoriteDogDao(): FavoriteDogDao
}