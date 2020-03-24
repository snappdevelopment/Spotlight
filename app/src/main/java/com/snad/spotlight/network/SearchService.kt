package com.snad.spotlight.network

import com.snad.spotlight.network.models.MovieSearchResults
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    @GET("search/movie")
    suspend fun searchMovie(
        @Query("query") title: String,
        @Query("page") page: Int
    ): MovieSearchResults
}