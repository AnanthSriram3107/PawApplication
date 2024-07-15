package com.example.paw.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(tableName = "collections")
data class DogCollection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)




data class DogCollectionWithImages(
    @Embedded val collection: DogCollection,
    @Relation(
        parentColumn = "id",
        entityColumn = "collectionId"
    )
    val images: List<CollectionImage>
)

@Entity
data class CollectionImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val collectionId: Int,
    val imageUrl: String, // Add the imageUrl property here
    val breed: String?
)
