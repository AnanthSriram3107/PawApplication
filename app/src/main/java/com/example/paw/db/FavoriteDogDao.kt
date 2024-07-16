package com.example.paw.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDogDao {
    @Query("SELECT * FROM favorite_dog LIMIT 1")
    suspend fun getFavoriteDog(): FavoriteDog?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFavoriteDog(dog: FavoriteDog)
    @Insert
    suspend fun insertCollection(collection: DogCollection): Long
    @Insert
    suspend fun insertImages(collectionImage: CollectionImage)
    @Query("SELECT COUNT(*) FROM collections")
    suspend fun getCollectionCount(): Int
    @Query("SELECT * FROM collections")
    fun getAllCollections(): Flow<List<DogCollection>>

    @Query("SELECT * FROM CollectionImage WHERE collectionId = :collectionId")
    fun getImagesForCollection(collectionId: Long): Flow<List<CollectionImage>>
    @Insert
    suspend fun insertDogOfTheDay(dogOfTheDay: DogOfTheDay)
    @Query("SELECT * FROM dog_of_the_day WHERE month = :month AND day = :day")
    suspend fun getDogOfTheDay(month: Int, day: Int): DogOfTheDay?
    @Query("DELETE FROM collections WHERE id = :collectionId")
    suspend fun deleteCollectionById(collectionId: Long)
}