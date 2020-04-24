package com.snad.spotlight.network

import com.snad.spotlight.network.models.Person
import android.util.Log
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class PersonApi(
    private val personService: PersonService
) {

    private val appendToResponse = "movie_credits"

    suspend fun loadPerson(id: Int): PersonApiResult {
        return try {
            val person = personService.getPerson(id, appendToResponse)
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

sealed class PersonApiResult {
    class Success(val person: Person): PersonApiResult()
    object NetworkError: PersonApiResult()
    object ConnectionError: PersonApiResult()
    object AuthenticationError: PersonApiResult()
    object ApiError: PersonApiResult()
    object Error: PersonApiResult()
}