package com.snad.feature.newmovies

import app.cash.turbine.test
import com.snad.feature.newmovies.model.Dates
import com.snad.feature.newmovies.model.NewMovies
import com.snad.feature.newmovies.repository.NewMoviesRepository
import com.snad.feature.newmovies.repository.NewMoviesResult
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
internal class NewMoviesViewModelTest {

    private val newMovies = NewMovies(
        movies = listOf(),
        page = 0,
        total_results = 0,
        dates = Dates(maximum = "", minimum = ""),
        total_pages = 0
    )

    private val repository = TestNewMoviesRepository()

    private val underTest = NewMoviesViewModel(
        newMoviesRepository = repository,
        ioDispatcher = TestCoroutineDispatcher()
    )

    @Test
    fun `loading succeeds`() = runBlocking {
        val expectedState = NewMoviesState.DoneState(newMovies)

        underTest.state.test {
            underTest.handleAction(LoadMovies)
            repository.setResult(NewMoviesResult.Success(newMovies))

            assertEquals(NewMoviesState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `loading fails with network error`() = runBlocking {
        val expectedState = NewMoviesState.NetworkErrorState

        underTest.state.test {
            underTest.handleAction(LoadMovies)
            repository.setResult(NewMoviesResult.NetworkError)

            assertEquals(NewMoviesState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `loading fails with autentication error`() = runBlocking {
        val expectedState = NewMoviesState.AuthenticationErrorState

        underTest.state.test {
            underTest.handleAction(LoadMovies)
            repository.setResult(NewMoviesResult.AuthenticationError)

            assertEquals(NewMoviesState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `loading fails with api error`() = runBlocking {
        val expectedState = NewMoviesState.ErrorState

        underTest.state.test {
            underTest.handleAction(LoadMovies)
            repository.setResult(NewMoviesResult.ApiError)

            assertEquals(NewMoviesState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }
}

private class TestNewMoviesRepository: NewMoviesRepository {

    private val state = MutableSharedFlow<NewMoviesResult>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    suspend fun setResult(result: NewMoviesResult) {
        state.emit(result)
    }

    override suspend fun loadNewMovies(): NewMoviesResult {
        return state.first()
    }
}