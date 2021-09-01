package com.snad.feature.search

import com.snad.feature.search.model.MovieSearchResults
import com.snad.feature.search.repository.MovieSearchApi
import com.snad.feature.search.repository.MovieSearchApiResult
import com.snad.feature.search.repository.SearchRepositoryImpl
import com.snad.feature.search.repository.SearchRepositoryResult
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class SearchRepositoryTest {

    private val movieSearchResults = MovieSearchResults(
        page = 0,
        total_results = 0,
        total_pages = 0,
        results = listOf()
    )

    private val api: MovieSearchApi = mock()

    private val underTest = SearchRepositoryImpl(movieSearchApi = api)

    @Test
    fun `search movie succeeds`() = runBlocking {
        whenever(api.searchMovie(any())).thenReturn(MovieSearchApiResult.Success(movieSearchResults))

        val expectedResult = SearchRepositoryResult.Success(movieSearchResults)
        val result = underTest.searchMovies(title = "")

        assertEquals(expectedResult, result)
    }

    @Test
    fun `search fails with network error`() = runBlocking {
        whenever(api.searchMovie(any())).thenReturn(MovieSearchApiResult.NetworkError)

        val expectedResult = SearchRepositoryResult.NetworkError
        val result = underTest.searchMovies(title = "")

        assertEquals(expectedResult, result)
    }

    @Test
    fun `search fails with authentication error`() = runBlocking {
        whenever(api.searchMovie(any())).thenReturn(MovieSearchApiResult.AuthenticationError)

        val expectedResult = SearchRepositoryResult.AuthenticationError
        val result = underTest.searchMovies(title = "")

        assertEquals(expectedResult, result)
    }

    @Test
    fun `search fails with api error`() = runBlocking {
        whenever(api.searchMovie(any())).thenReturn(MovieSearchApiResult.ApiError)

        val expectedResult = SearchRepositoryResult.ApiError
        val result = underTest.searchMovies(title = "")

        assertEquals(expectedResult, result)
    }

    @Test
    fun `search fails with connection error`() = runBlocking {
        whenever(api.searchMovie(any())).thenReturn(MovieSearchApiResult.ConnectionError)

        val expectedResult = SearchRepositoryResult.ConnectionError
        val result = underTest.searchMovies(title = "")

        assertEquals(expectedResult, result)
    }

    @Test
    fun `search fails with generic error`() = runBlocking {
        whenever(api.searchMovie(any())).thenReturn(MovieSearchApiResult.Error)

        val expectedResult = SearchRepositoryResult.Error
        val result = underTest.searchMovies(title = "")

        assertEquals(expectedResult, result)
    }
}