package com.snad.spotlight.repository

import com.snad.spotlight.network.NewMoviesApi
import com.snad.spotlight.network.NewMoviesApiResult
import com.snad.spotlight.network.models.NewMovies
import io.reactivex.Single
import javax.inject.Inject

class NewMoviesRepository @Inject constructor(
    private val newMoviesApi: NewMoviesApi
) {
    fun loadNewMovies(): Single<NewMoviesResult> {
        return newMoviesApi.loadNewMovies()
            .map { it.toNewMoviesResult() }
//        return when(result) {
//            is NewMoviesApiResult.Success -> NewMoviesResult.Success(
//                result.newMovies
//            )
//            is NewMoviesApiResult.NetworkError -> NewMoviesResult.NetworkError
//            is NewMoviesApiResult.ConnectionError -> NewMoviesResult.ConnectionError
//            is NewMoviesApiResult.AuthenticationError -> NewMoviesResult.AuthenticationError
//            is NewMoviesApiResult.ApiError -> NewMoviesResult.ApiError
//            is NewMoviesApiResult.Error -> NewMoviesResult.Error
//        }
    }

    private fun NewMoviesApiResult.toNewMoviesResult(): NewMoviesResult {
        return when(this) {
            is NewMoviesApiResult.Success -> NewMoviesResult.Success(this.newMovies)
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