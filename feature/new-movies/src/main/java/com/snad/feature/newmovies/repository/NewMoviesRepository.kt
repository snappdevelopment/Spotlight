package com.snad.feature.newmovies.repository

import com.snad.feature.newmovies.model.NewMovies
import javax.inject.Inject

internal class NewMoviesRepository @Inject constructor(
    private val newMoviesApi: NewMoviesApi
) {
    suspend fun loadNewMovies(): NewMoviesResult {
        val result = newMoviesApi.loadNewMovies()
        return when(result) {
            is NewMoviesApiResult.Success -> NewMoviesResult.Success(
                result.newMovies
            )
            is NewMoviesApiResult.NetworkError -> NewMoviesResult.NetworkError
            is NewMoviesApiResult.ConnectionError -> NewMoviesResult.ConnectionError
            is NewMoviesApiResult.AuthenticationError -> NewMoviesResult.AuthenticationError
            is NewMoviesApiResult.ApiError -> NewMoviesResult.ApiError
            is NewMoviesApiResult.Error -> NewMoviesResult.Error
        }
    }
}

internal sealed class NewMoviesResult {
    class Success(val newMovies: NewMovies): NewMoviesResult()
    object NetworkError: NewMoviesResult()
    object ConnectionError: NewMoviesResult()
    object AuthenticationError: NewMoviesResult()
    object ApiError: NewMoviesResult()
    object Error: NewMoviesResult()
}