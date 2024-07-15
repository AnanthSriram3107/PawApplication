package com.example.paw.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_dog")
data class FavoriteDog(
    @PrimaryKey val id: Int = 0,
    val imageUrl: String,
    val breed: String?
)
