package com.snad.spotlight.network

import com.snad.spotlight.network.models.Movie
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("movie/{movie_id}")
    suspend fun getMovie(
        @Path("movie_id") id: Int,
        @Query("language") language: String
    ): Movie
}