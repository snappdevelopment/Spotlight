package com.snad.spotlight.network

import android.util.Log
import com.snad.spotlight.apiKey
import com.snad.spotlight.network.models.NewMovies
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class NewMoviesApi(
    private val newMoviesService: NewMoviesService
) {
    private val language = "en-US"
    private val page = 1

    suspend fun loadNewMovies(): NewMoviesApiResult {
        return try {
            val newMovies = newMoviesService.getNewMovies(language, page)
            Log.d("NewMoviesApi", newMovies.toString())
            NewMoviesApiResult.Success(newMovies)
        }
        catch (error: Exception) {
            when(error) {
                is SocketTimeoutException -> {
                    Log.d("NewMoviesApi", "Timeout: ${error.message} ${error.cause}")
                    NewMoviesApiResult.NetworkError
                }
                is IOException -> {
                    Log.d("NewMoviesApi", "IOException: ${error.message} ${error.cause}")
                    NewMoviesApiResult.ConnectionError
                }
                is HttpException -> when(error.code()) {
                    401 -> {
                        Log.d("NewMoviesApi", "${error.code()} ${error.response().toString()}")
                        NewMoviesApiResult.AuthenticationError
                    }
                    404 -> {
                        Log.d("NewMoviesApi", "${error.code()} ${error.response().toString()}")
                        NewMoviesApiResult.ApiError
                    }
                    else -> NewMoviesApiResult.Error
                }
                else -> NewMoviesApiResult.Error
            }
        }
    }
}

sealed class NewMoviesApiResult {
    class Success(val newMovies: NewMovies): NewMoviesApiResult()
    object NetworkError: NewMoviesApiResult()
    object ConnectionError: NewMoviesApiResult()
    object AuthenticationError: NewMoviesApiResult()
    object ApiError: NewMoviesApiResult()
    object Error: NewMoviesApiResult()
}