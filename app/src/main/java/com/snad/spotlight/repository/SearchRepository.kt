package com.snad.spotlight.repository

import com.snad.spotlight.network.MovieSearchApi
import com.snad.spotlight.network.MovieSearchApiResult
import com.snad.spotlight.network.models.MovieSearchResults

class SearchRepository(
    private val movieSearchApi: MovieSearchApi
) {
    suspend fun searchMovies(title: String): SearchRepositoryResult {
        val result = movieSearchApi.searchMovie(title)
        return when(result) {
            is MovieSearchApiResult.Success -> SearchRepositoryResult.Success(
                result.searchResults
            )
            is MovieSearchApiResult.NetworkError -> SearchRepositoryResult.NetworkError
            is MovieSearchApiResult.ConnectionError -> SearchRepositoryResult.ConnectionError
            is MovieSearchApiResult.AuthenticationError -> SearchRepositoryResult.AuthenticationError
            is MovieSearchApiResult.ApiError -> SearchRepositoryResult.ApiError
            is MovieSearchApiResult.Error -> SearchRepositoryResult.Error
        }
    }
}

sealed class SearchRepositoryResult {
    class Success(val searchResults: MovieSearchResults): SearchRepositoryResult()
    object NetworkError: SearchRepositoryResult()
    object ConnectionError: SearchRepositoryResult()
    object AuthenticationError: SearchRepositoryResult()
    object ApiError: SearchRepositoryResult()
    object Error: SearchRepositoryResult()
}