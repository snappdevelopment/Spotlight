package com.snad.spotlight.network

import com.snad.spotlight.network.models.NewMovies
import retrofit2.http.GET
import retrofit2.http.Query

interface NewMoviesService {
    @GET("movie/now_playing")
    suspend fun getNewMovies(
        @Query("language") language: String,
        @Query("page") page: Int
    ): NewMovies
}