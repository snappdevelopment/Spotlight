package com.snad.feature.search

import app.cash.turbine.test
import com.snad.feature.search.model.ListMovie
import com.snad.feature.search.model.MovieSearchResults
import com.snad.feature.search.repository.SearchRepository
import com.snad.feature.search.repository.SearchRepositoryResult
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
internal class SearchViewModelTest {

    private val listMovie = ListMovie(
        popularity = 0.0,
        vote_count = 0,
        video = false,
        poster_path = null,
        id = 0,
        adult = false,
        backdrop_path = null,
        original_language = "",
        original_title = "",
        genre_ids = listOf(),
        title = "",
        vote_average = 0.0,
        overview = "",
        release_date = null
    )

    private val movieSearchResults = MovieSearchResults(
        page = 0,
        total_results = 1,
        total_pages = 0,
        results = listOf(listMovie)
    )

    private val repository = TestSearchRepository()

    private val underTest = SearchViewModel(
        searchRepository = repository,
        ioDispatcher = TestCoroutineDispatcher()
    )

    @Test
    fun `searching succeeds and has results`() = runBlocking {
        val expectedState = SearchState.DoneState(listOf(listMovie))

        underTest.state.test {
            underTest.handleAction(SearchMovies(title = ""))
            repository.setResult(SearchRepositoryResult.Success(movieSearchResults))

            assertEquals(SearchState.InitialState, expectItem())
            assertEquals(SearchState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `searching succeeds and has no results`() = runBlocking {
        val expectedState = SearchState.NoResultsState

        underTest.state.test {
            underTest.handleAction(SearchMovies(title = ""))
            repository.setResult(
                SearchRepositoryResult.Success(movieSearchResults.copy(total_results = 0))
            )

            assertEquals(SearchState.InitialState, expectItem())
            assertEquals(SearchState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `searching fails with network error`() = runBlocking {
        val expectedState = SearchState.NetworkErrorState

        underTest.state.test {
            underTest.handleAction(SearchMovies(title = ""))
            repository.setResult(SearchRepositoryResult.NetworkError)

            assertEquals(SearchState.InitialState, expectItem())
            assertEquals(SearchState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `searching fails with authentication error`() = runBlocking {
        val expectedState = SearchState.AuthenticationErrorState

        underTest.state.test {
            underTest.handleAction(SearchMovies(title = ""))
            repository.setResult(SearchRepositoryResult.AuthenticationError)

            assertEquals(SearchState.InitialState, expectItem())
            assertEquals(SearchState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `searching fails with api error`() = runBlocking {
        val expectedState = SearchState.ErrorState

        underTest.state.test {
            underTest.handleAction(SearchMovies(title = ""))
            repository.setResult(SearchRepositoryResult.ApiError)

            assertEquals(SearchState.InitialState, expectItem())
            assertEquals(SearchState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }
}

private class TestSearchRepository: SearchRepository {

    private val result = MutableSharedFlow<SearchRepositoryResult>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    suspend fun setResult(searchRepositoryResult: SearchRepositoryResult) {
        result.emit(searchRepositoryResult)
    }

    override suspend fun searchMovies(title: String): SearchRepositoryResult {
       return result.first()
    }
}