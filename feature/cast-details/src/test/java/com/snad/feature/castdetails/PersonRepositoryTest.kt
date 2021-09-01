package com.snad.feature.castdetails

import com.snad.feature.castdetails.model.Person
import com.snad.feature.castdetails.model.PersonCredits
import com.snad.feature.castdetails.repository.PersonApi
import com.snad.feature.castdetails.repository.PersonApiResult
import com.snad.feature.castdetails.repository.PersonRepositoryImpl
import com.snad.feature.castdetails.repository.PersonResult
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class PersonRepositoryTest {

    private val person = Person(
        adult = false,
        also_known_as = listOf(),
        biography = "",
        birthday = null,
        deathday = null,
        gender = 0,
        homepage = null,
        id = 0,
        imdb_id = "",
        known_for_department = "",
        name = "",
        place_of_birth = null,
        popularity = 0.0,
        profile_path = null,
        person_credits = PersonCredits(
            cast = listOf(),
            crew = listOf()
        )
    )

    private val api: PersonApi = mock()

    private val underTest = PersonRepositoryImpl(personApi = api)

    @Test
    fun `load person succeeds`() = runBlocking {
        whenever(api.loadPerson(any())).thenReturn(PersonApiResult.Success(person))

        val expectedResult = PersonResult.Success(person)

        val result = underTest.loadPerson(id = 0)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `load person fails with network error`() = runBlocking {
        whenever(api.loadPerson(any())).thenReturn(PersonApiResult.NetworkError)

        val expectedResult = PersonResult.NetworkError

        val result = underTest.loadPerson(id = 0)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `load person fails with authentication error`() = runBlocking {
        whenever(api.loadPerson(any())).thenReturn(PersonApiResult.AuthenticationError)

        val expectedResult = PersonResult.AuthenticationError

        val result = underTest.loadPerson(id = 0)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `load person fails with api error`() = runBlocking {
        whenever(api.loadPerson(any())).thenReturn(PersonApiResult.ApiError)

        val expectedResult = PersonResult.ApiError

        val result = underTest.loadPerson(id = 0)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `load person fails with connection error`() = runBlocking {
        whenever(api.loadPerson(any())).thenReturn(PersonApiResult.ConnectionError)

        val expectedResult = PersonResult.ConnectionError

        val result = underTest.loadPerson(id = 0)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `load person fails with generic error`() = runBlocking {
        whenever(api.loadPerson(any())).thenReturn(PersonApiResult.Error)

        val expectedResult = PersonResult.Error

        val result = underTest.loadPerson(id = 0)

        assertEquals(expectedResult, result)
    }
}