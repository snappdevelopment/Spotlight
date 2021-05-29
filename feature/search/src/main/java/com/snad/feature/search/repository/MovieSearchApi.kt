package com.snad.feature.search.repository

import android.util.Log
import com.snad.feature.search.model.MovieSearchResults
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

internal class MovieSearchApi @Inject constructor(
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

internal interface SearchService {
    @GET("search/movie")
    suspend fun searchMovie(
        @Query("query") title: String,
        @Query("page") page: Int
    ): MovieSearchResults
}

internal sealed class MovieSearchApiResult {
    class Success(val searchResults: MovieSearchResults): MovieSearchApiResult()
    object NetworkError:MovieSearchApiResult()
    object ConnectionError: MovieSearchApiResult()
    object AuthenticationError: MovieSearchApiResult()
    object ApiError: MovieSearchApiResult()
    object Error: MovieSearchApiResult()
}