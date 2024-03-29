package com.snad.feature.castdetails.repository

import com.snad.feature.castdetails.model.Person
import android.util.Log
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

internal class PersonApi @Inject constructor(
    private val retrofit: Retrofit
) {

    private val appendToResponse = "movie_credits"
    private val service = retrofit.create(PersonService::class.java)

    suspend fun loadPerson(id: Int): PersonApiResult {
        return try {
            val person = service.getPerson(id, appendToResponse)
            PersonApiResult.Success(person)
        }
        catch (error: Exception) {
            Log.d("PersonApi", error.toString())
            when(error) {
                is SocketTimeoutException -> {
                    Log.d("PersonApi", "Timeout: ${error.message} ${error.cause}")
                    PersonApiResult.NetworkError
                }
                is IOException -> {
                    Log.d("PersonApi", "IOException: ${error.message} ${error.cause}")
                    PersonApiResult.ConnectionError
                }
                is HttpException -> when(error.code()) {
                    401 -> {
                        Log.d("PersonApi", "${error.code()} ${error.response().toString()}")
                        PersonApiResult.AuthenticationError
                    }
                    404 -> {
                        Log.d("PersonApi", "${error.code()} ${error.response().toString()}")
                        PersonApiResult.ApiError
                    }
                    else -> PersonApiResult.Error
                }
                else -> PersonApiResult.Error
            }
        }
    }
}

internal interface PersonService {
    @GET("person/{person_id}")
    suspend fun getPerson(
        @Path("person_id") id: Int,
        @Query("append_to_response") appendToResponse: String
    ): Person
}

internal sealed class PersonApiResult {
    data class Success(val person: Person): PersonApiResult()
    object NetworkError: PersonApiResult()
    object ConnectionError: PersonApiResult()
    object AuthenticationError: PersonApiResult()
    object ApiError: PersonApiResult()
    object Error: PersonApiResult()
}