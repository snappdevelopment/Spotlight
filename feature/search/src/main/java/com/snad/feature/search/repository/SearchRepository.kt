package com.snad.feature.search.repository

import com.snad.feature.search.model.MovieSearchResults
import javax.inject.Inject

internal interface SearchRepository {
    suspend fun searchMovies(title: String): SearchRepositoryResult
}

internal class SearchRepositoryImpl @Inject constructor(
    private val movieSearchApi: MovieSearchApi
): SearchRepository {

    override suspend fun searchMovies(title: String): SearchRepositoryResult {
        val result = movieSearchApi.searchMovie(title)
        return when(result) {
            is MovieSearchApiResult.Success -> SearchRepositoryResult.Success(result.searchResults)
            is MovieSearchApiResult.NetworkError -> SearchRepositoryResult.NetworkError
            is MovieSearchApiResult.ConnectionError -> SearchRepositoryResult.ConnectionError
            is MovieSearchApiResult.AuthenticationError -> SearchRepositoryResult.AuthenticationError
            is MovieSearchApiResult.ApiError -> SearchRepositoryResult.ApiError
            is MovieSearchApiResult.Error -> SearchRepositoryResult.Error
        }
    }
}

internal sealed class SearchRepositoryResult {
    data class Success(val searchResults: MovieSearchResults): SearchRepositoryResult()
    object NetworkError: SearchRepositoryResult()
    object ConnectionError: SearchRepositoryResult()
    object AuthenticationError: SearchRepositoryResult()
    object ApiError: SearchRepositoryResult()
    object Error: SearchRepositoryResult()
}