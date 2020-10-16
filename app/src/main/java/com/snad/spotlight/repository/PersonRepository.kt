package com.snad.spotlight.repository

import com.snad.spotlight.network.PersonApi
import com.snad.spotlight.network.PersonApiResult
import com.snad.spotlight.network.models.Person
import javax.inject.Inject

class PersonRepository @Inject constructor(
    private val personApi: PersonApi
) {
    suspend fun loadPerson(id: Int): PersonResult {
        val result = personApi.loadPerson(id)
        return when(result) {
            is PersonApiResult.Success -> PersonResult.Success(
                result.person
            )
            is PersonApiResult.NetworkError -> PersonResult.NetworkError
            is PersonApiResult.ConnectionError -> PersonResult.ConnectionError
            is PersonApiResult.AuthenticationError -> PersonResult.AuthenticationError
            is PersonApiResult.ApiError -> PersonResult.ApiError
            is PersonApiResult.Error -> PersonResult.Error
        }
    }
}

sealed class PersonResult {
    class Success(val person: Person): PersonResult()
    object NetworkError: PersonResult()
    object ConnectionError: PersonResult()
    object AuthenticationError: PersonResult()
    object ApiError: PersonResult()
    object Error: PersonResult()
}