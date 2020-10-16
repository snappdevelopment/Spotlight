package com.snad.spotlight.network

import android.util.Log
import com.snad.spotlight.network.models.MovieSearchResults
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class MovieSearchApi @Inject constructor(
    private val retrofit: Retrofit
) {
    private val page = 1
    private val service = retrofit.create(SearchService::class.java)

    suspend fun searchMovie(title: String): MovieSearchApiResult {
        return try {
            val searchResults = service.searchMovie(title, page)
            MovieSearchApiResult.Success(searchResults)
        }
        catch (error: Exception) {
            Log.d("MovieApi", error.toString())
            when(error) {
                is SocketTimeoutException -> {
                    Log.d("MovieSearchApi", "Timeout: ${error.message} ${error.cause}")
                    MovieSearchApiResult.NetworkError
                }
                is IOException -> {
                    Log.d("MovieSearchApi", "IOException: ${error.message} ${error.cause}")
                    MovieSearchApiResult.ConnectionError
                }
                is HttpException -> when(error.code()) {
                    401 -> {
                        Log.d("MovieSearchApi", "${error.code()} ${error.response().toString()}")
                        MovieSearchApiResult.AuthenticationError
                    }
                    404 -> {
                        Log.d("MovieSearchApi", "${error.code()} ${error.response().toString()}")
                        MovieSearchApiResult.ApiError
                    }
                    else -> MovieSearchApiResult.Error
                }
                else -> MovieSearchApiResult.Error
            }
        }
    }
}

sealed class MovieSearchApiResult {
    class Success(val searchResults: MovieSearchResults): MovieSearchApiResult()
    object NetworkError:MovieSearchApiResult()
    object ConnectionError: MovieSearchApiResult()
    object AuthenticationError: MovieSearchApiResult()
    object ApiError: MovieSearchApiResult()
    object Error: MovieSearchApiResult()
}