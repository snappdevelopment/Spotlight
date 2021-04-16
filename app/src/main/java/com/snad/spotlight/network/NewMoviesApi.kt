package com.snad.spotlight.network

import android.util.Log
import com.snad.spotlight.network.models.NewMovies
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class NewMoviesApi @Inject constructor(
    private val retrofit: Retrofit
) {
    private val language = "en-US"
    private val page = 1
    private val service = retrofit.create(NewMoviesService::class.java)

    fun loadNewMovies(): Single<NewMoviesApiResult> {
        return service.getNewMovies(language, page)
            .subscribeOn(Schedulers.io())
            .map { it.toNewMoviesApiResult() }
//        return try {
//            val newMovies = service.getNewMovies(language, page)
//                .subscribeOn(Schedulers.io())
//                .map {
//                    it.toNewMoviesApiResult()
//                }
//            NewMoviesApiResult.Success(newMovies)
//        }
//        catch (error: Exception) {
//            when(error) {
//                is SocketTimeoutException -> {
//                    Log.d("NewMoviesApi", "Timeout: ${error.message} ${error.cause}")
//                    NewMoviesApiResult.NetworkError
//                }
//                is IOException -> {
//                    Log.d("NewMoviesApi", "IOException: ${error.message} ${error.cause}")
//                    NewMoviesApiResult.ConnectionError
//                }
//                is HttpException -> when(error.code()) {
//                    401 -> {
//                        Log.d("NewMoviesApi", "${error.code()} ${error.response().toString()}")
//                        NewMoviesApiResult.AuthenticationError
//                    }
//                    404 -> {
//                        Log.d("NewMoviesApi", "${error.code()} ${error.response().toString()}")
//                        NewMoviesApiResult.ApiError
//                    }
//                    else -> NewMoviesApiResult.Error
//                }
//                else -> NewMoviesApiResult.Error
//            }
//        }
    }

    private fun Result<NewMovies>.toNewMoviesApiResult(): NewMoviesApiResult {
        return if(isSuccess) {
            NewMoviesApiResult.Success(this.getOrThrow())
        } else {
            when(val exception = this.exceptionOrNull()) {
                is SocketTimeoutException -> NewMoviesApiResult.NetworkError
                is IOException -> NewMoviesApiResult.ConnectionError
                is HttpException -> {
                    when(exception.code()) {
                        401 -> NewMoviesApiResult.AuthenticationError
                        404 -> NewMoviesApiResult.ApiError
                        else -> NewMoviesApiResult.Error
                    }
                }
                else -> NewMoviesApiResult.Error
            }
        }
    }
}

interface NewMoviesService {
    @GET("movie/now_playing")
    fun getNewMovies(
        @Query("language") language: String,
        @Query("page") page: Int
    ): Single<Result<NewMovies>>
}
//Todo: Retrofit kann Result nicht als return type erstellen. CallAdapter kann nicht implementiert werden weil Kotlin.Result niemals returned werden kann
sealed class NewMoviesApiResult {
    class Success(val newMovies: NewMovies): NewMoviesApiResult()
    object NetworkError: NewMoviesApiResult()
    object ConnectionError: NewMoviesApiResult()
    object AuthenticationError: NewMoviesApiResult()
    object ApiError: NewMoviesApiResult()
    object Error: NewMoviesApiResult()
}