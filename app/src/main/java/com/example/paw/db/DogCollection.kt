package com.example.paw.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "collections")
data class DogCollection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

@Entity
data class CollectionImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val collectionId: Int,
    val imageUrl: String,
    val breed: String?
)
