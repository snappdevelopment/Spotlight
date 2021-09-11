package com.snad.library

import app.cash.turbine.test
import com.snad.core.persistence.models.LibraryMovie
import com.snad.feature.library.*
import com.snad.feature.library.repository.LibraryRepository
import com.snad.feature.library.repository.LibraryRepositoryResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
internal class LibraryViewModelTest {

    private val ioDispatcher = TestCoroutineDispatcher()
    private val repository = FakeLibraryRepository()

    private val underTest = LibraryViewModel(
        libraryRepository = repository,
        ioDispatcher = ioDispatcher,
    )

    private val libraryMovie = LibraryMovie(
        id = 0,
        added_at = null,
        updated_at = null,
        adult = false,
        backdrop_path = null,
        backdrops = listOf(),
        budget = 0,
        cast = listOf(),
        genres = "",
        has_been_watched = false,
        imdb_id = null,
        overview = null,
        popularity = 0.0,
        poster_path = null,
        release_date = "",
        revenue = 0,
        reviews = listOf(),
        runtime = null,
        tagline = null,
        title = "",
        trailer = null,
        video = false,
        vote_average = 0.0,
        vote_count = 0
    )

    @Test
    fun `loading a movie succeeds - returns movie`() = runBlocking {
        val expectedState = LibraryState.DoneState(listOf(libraryMovie))

        underTest.state.test {
            assertEquals(LibraryState.LoadingState, expectItem())

            underTest.handleAction(LoadMovies)
            repository.result.emit(LibraryRepositoryResult.Success(listOf(libraryMovie)))

            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `loading a movie succeeds - returns empty list`() = runBlocking {
        val expectedState = LibraryState.EmptyState

        underTest.state.test {
            assertEquals(LibraryState.LoadingState, expectItem())

            underTest.handleAction(LoadMovies)
            repository.result.emit(LibraryRepositoryResult.Success(emptyList()))

            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `loading a movie fails`() = runBlocking {
        val expectedState = LibraryState.ErrorState

        underTest.state.test {
            assertEquals(LibraryState.LoadingState, expectItem())

            underTest.handleAction(LoadMovies)
            repository.result.emit(LibraryRepositoryResult.DbError)

            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `update movie succeeds`() = runBlocking {
        val updatedMovie = libraryMovie.copy(tagline = "new tagline")
        val expectedState = LibraryState.DoneState(listOf(updatedMovie))

        underTest.state.test {
            assertEquals(LibraryState.LoadingState, expectItem())

            underTest.handleAction(LoadMovies)
            repository.result.emit(LibraryRepositoryResult.Success(listOf(libraryMovie)))
            assertEquals(LibraryState.DoneState(listOf(libraryMovie)), expectItem())

            underTest.handleAction(UpdateMovie(updatedMovie))

            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `delete movie succeeds`() = runBlocking {
        val expectedState = LibraryState.EmptyState

        underTest.state.test {
            assertEquals(LibraryState.LoadingState, expectItem())

            underTest.handleAction(LoadMovies)
            repository.result.emit(LibraryRepositoryResult.Success(listOf(libraryMovie)))
            assertEquals(LibraryState.DoneState(listOf(libraryMovie)), expectItem())

            underTest.handleAction(DeleteMovie(libraryMovie))

            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }
}

private class FakeLibraryRepository: LibraryRepository {

    val result = MutableSharedFlow<LibraryRepositoryResult>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun loadLibraryMovies(): Flow<LibraryRepositoryResult> {
        return flow {
            result.collect { emit(it) }
        }
    }

    override suspend fun updateMovie(movie: LibraryMovie) {
        result.emit(LibraryRepositoryResult.Success(listOf(movie)))
    }

    override suspend fun deleteMovie(movie: LibraryMovie) {
        result.emit(LibraryRepositoryResult.Success(emptyList()))
    }
}