package com.snad.spotlight

import com.snad.spotlight.network.NewMoviesApi
import com.snad.spotlight.network.NewMoviesApiResult
import com.snad.spotlight.network.models.NewMovies

class NewMoviesRepository(
    private val newMoviesApi: NewMoviesApi
) {
    suspend fun loadNewMovies(): NewMoviesResult {
        val result = newMoviesApi.loadNewMovies()
        return when(result) {
            is NewMoviesApiResult.Success -> NewMoviesResult.Success(result.newMovies)
            is NewMoviesApiResult.NetworkError -> NewMoviesResult.NetworkError
            is NewMoviesApiResult.ConnectionError -> NewMoviesResult.ConnectionError
            is NewMoviesApiResult.AuthenticationError -> NewMoviesResult.AuthenticationError
            is NewMoviesApiResult.ApiError -> NewMoviesResult.ApiError
            is NewMoviesApiResult.Error -> NewMoviesResult.Error
        }
    }
}

sealed class NewMoviesResult {
    class Success(val newMovies: NewMovies): NewMoviesResult()
    object NetworkError: NewMoviesResult()
    object ConnectionError: NewMoviesResult()
    object AuthenticationError: NewMoviesResult()
    object ApiError: NewMoviesResult()
    object Error: NewMoviesResult()
}