package com.snad.spotlight.network

import android.util.Log
import com.snad.spotlight.network.models.Movie
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class MovieApi @Inject constructor(
    private val retrofit: Retrofit
) {
    private val appendToResponse = "images,videos,credits,reviews"
    private val service = retrofit.create(MovieService::class.java)

    suspend fun loadMovie(id: Int): MovieApiResult {
        return try {
            val movie = service.getMovie(id, appendToResponse)
            MovieApiResult.Success(movie)
        }
        catch (error: Exception) {
            Log.d("MovieApi", error.toString())
            when(error) {
                is SocketTimeoutException -> {
                    Log.d("MovieApi", "Timeout: ${error.message} ${error.cause}")
                    MovieApiResult.NetworkError
                }
                is IOException -> {
                    Log.d("MovieApi", "IOException: ${error.message} ${error.cause}")
                    MovieApiResult.ConnectionError
                }
                is HttpException -> when(error.code()) {
                    401 -> {
                        Log.d("MovieApi", "${error.code()} ${error.response().toString()}")
                        MovieApiResult.AuthenticationError
                    }
                    404 -> {
                        Log.d("MovieApi", "${error.code()} ${error.response().toString()}")
                        MovieApiResult.ApiError
                    }
                    else -> MovieApiResult.Error
                }
                else -> MovieApiResult.Error
            }
        }
    }
}

interface MovieService {
    @GET("movie/{movie_id}")
    suspend fun getMovie(
        @Path("movie_id") id: Int,
        @Query("append_to_response") appendToResponse: String
    ): Movie
}

sealed class MovieApiResult {
    class Success(val movie: Movie): MovieApiResult()
    object NetworkError: MovieApiResult()
    object ConnectionError: MovieApiResult()
    object AuthenticationError: MovieApiResult()
    object ApiError: MovieApiResult()
    object Error: MovieApiResult()
}