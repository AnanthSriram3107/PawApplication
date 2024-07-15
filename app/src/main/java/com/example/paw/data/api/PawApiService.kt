package com.example.paw.data.api

import com.example.paw.model.BreedsList
import com.example.paw.model.PawBreedResponse
import com.example.paw.model.PawResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PawApiService {
    @GET("breeds/image/random")
    suspend fun getRandomDogImage(): PawResponse

    @GET("breed/{breed}/images")
    suspend fun getImagesByBreed(
        @Path("breed") breed: String
    ): PawBreedResponse

    @GET("breed/{breed}/images/random")
    suspend fun getRandomDogImageByBreed(
        @Path("breed") breed: String
    ): PawResponse

    @GET("breeds/list/all")
    suspend fun getAllBreads(
    ): BreedsList
}