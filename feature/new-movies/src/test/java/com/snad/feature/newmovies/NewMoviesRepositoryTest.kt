package com.snad.feature.newmovies

import com.snad.feature.newmovies.model.Dates
import com.snad.feature.newmovies.model.NewMovies
import com.snad.feature.newmovies.repository.*
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class NewMoviesRepositoryTest {

    private val newMovies = NewMovies(
        movies = listOf(),
        page = 0,
        total_results = 0,
        dates = Dates(maximum = "", minimum = ""),
        total_pages = 0
    )

    private val api: NewMoviesApi = mock()

    private val underTest = NewMoviesRepositoryImpl(newMoviesApi = api)

    @Test
    fun `loading succeeds`() = runBlocking {
        whenever(api.loadNewMovies()).thenReturn(NewMoviesApiResult.Success(newMovies))

        val expectedResult = NewMoviesResult.Success(newMovies)

        val result = underTest.loadNewMovies()

        assertEquals(expectedResult, result)
    }

    @Test
    fun `loading fails with network error`() = runBlocking {
        whenever(api.loadNewMovies()).thenReturn(NewMoviesApiResult.NetworkError)

        val expectedResult = NewMoviesResult.NetworkError

        val result = underTest.loadNewMovies()

        assertEquals(expectedResult, result)
    }

    @Test
    fun `loading fails with connection error`() = runBlocking {
        whenever(api.loadNewMovies()).thenReturn(NewMoviesApiResult.ConnectionError)

        val expectedResult = NewMoviesResult.ConnectionError

        val result = underTest.loadNewMovies()

        assertEquals(expectedResult, result)
    }

    @Test
    fun `loading fails with authentication error`() = runBlocking {
        whenever(api.loadNewMovies()).thenReturn(NewMoviesApiResult.AuthenticationError)

        val expectedResult = NewMoviesResult.AuthenticationError

        val result = underTest.loadNewMovies()

        assertEquals(expectedResult, result)
    }

    @Test
    fun `loading fails with api error`() = runBlocking {
        whenever(api.loadNewMovies()).thenReturn(NewMoviesApiResult.ApiError)

        val expectedResult = NewMoviesResult.ApiError

        val result = underTest.loadNewMovies()

        assertEquals(expectedResult, result)
    }

    @Test
    fun `loading fails with generic error`() = runBlocking {
        whenever(api.loadNewMovies()).thenReturn(NewMoviesApiResult.Error)

        val expectedResult = NewMoviesResult.Error

        val result = underTest.loadNewMovies()

        assertEquals(expectedResult, result)
    }
}