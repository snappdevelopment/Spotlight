package com.snad.feature.castdetails.repository

import com.snad.feature.castdetails.model.Person
import javax.inject.Inject

internal interface PersonRepository {
    suspend fun loadPerson(id: Int): PersonResult
}

internal class PersonRepositoryImpl @Inject constructor(
    private val personApi: PersonApi
): PersonRepository {

    override suspend fun loadPerson(id: Int): PersonResult {
        val result = personApi.loadPerson(id)
        return when(result) {
            is PersonApiResult.Success -> PersonResult.Success(result.person)
            is PersonApiResult.NetworkError -> PersonResult.NetworkError
            is PersonApiResult.ConnectionError -> PersonResult.ConnectionError
            is PersonApiResult.AuthenticationError -> PersonResult.AuthenticationError
            is PersonApiResult.ApiError -> PersonResult.ApiError
            is PersonApiResult.Error -> PersonResult.Error
        }
    }
}

internal sealed class PersonResult {
    data class Success(val person: Person): PersonResult()
    object NetworkError: PersonResult()
    object ConnectionError: PersonResult()
    object AuthenticationError: PersonResult()
    object ApiError: PersonResult()
    object Error: PersonResult()
}