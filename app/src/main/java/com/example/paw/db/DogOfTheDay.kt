package com.example.paw.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dog_of_the_day")
data class DogOfTheDay(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val month: Int,
    val day: Int,
    val imageUrl: String?,
    val breed: String?
)
