package com.snad.feature.moviedetails

import app.cash.turbine.test
import com.snad.core.persistence.models.LibraryMovie
import com.snad.feature.moviedetails.repository.MovieDetailsRepository
import com.snad.feature.moviedetails.repository.MovieDetailsResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
internal class MovieDetailsViewModelTest {

    private val testDispatcher = TestCoroutineDispatcher()
    private val testClock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault())

    private val repository = FakeRepository(testClock)
    private val underTest = MovieDetailsViewModel(
        movieDetailsRepository = repository,
        ioDispatcher = testDispatcher,
        clock = testClock
    )

    private val movie = LibraryMovie(
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
    fun `loadMovie produces correct state`() = runBlockingTest {
        val expectedState = MovieDetailsState.DoneState(movie = movie, isInLibrary = true)

        underTest.state.test {
            assertEquals(MovieDetailsState.LoadingState, expectItem())

            underTest.handleAction(LoadMovie(movie.id))
            repository.result.emit(MovieDetailsResult.Success(movie = movie, isInLibrary = true))

            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `adding a movie produces correct state`() = runBlockingTest {
        val expectedState = MovieDetailsState.DoneState(
            movie = movie.copy(
                added_at = LocalDate.now(testClock),
                updated_at = LocalDate.now(testClock)
            ),
            isInLibrary = true
        )

        underTest.state.test {
            underTest.handleAction(LoadMovie(movie.id))
            repository.result.emit(MovieDetailsResult.Success(movie = movie, isInLibrary = false))

            assertEquals(MovieDetailsState.LoadingState, expectItem())
            assertEquals(MovieDetailsState.DoneState(movie = movie, isInLibrary = false), expectItem())

            underTest.handleAction(CtaClicked)
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `removing a movie produces correct state`() = runBlockingTest {
        val expectedState = MovieDetailsState.DoneState(
            movie = movie,
            isInLibrary = false
        )

        underTest.state.test {
            underTest.handleAction(LoadMovie(movie.id))
            repository.result.emit(MovieDetailsResult.Success(movie = movie, isInLibrary = true))

            assertEquals(MovieDetailsState.LoadingState, expectItem())
            assertEquals(MovieDetailsState.DoneState(movie = movie, isInLibrary = true), expectItem())

            underTest.handleAction(CtaClicked)
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `clicking the watched button produces correct state`() = runBlockingTest {
        val expectedState = MovieDetailsState.DoneState(
            movie = movie.copy(has_been_watched = true),
            isInLibrary = true
        )

        underTest.state.test {
            underTest.handleAction(LoadMovie(movie.id))
            repository.result.emit(MovieDetailsResult.Success(movie = movie, isInLibrary = true))

            assertEquals(MovieDetailsState.LoadingState, expectItem())
            assertEquals(MovieDetailsState.DoneState(movie = movie, isInLibrary = true), expectItem())

            underTest.handleAction(WatchedClicked)
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `data of movie in library gets updated after two days`() = runBlockingTest {
        val expectedState = MovieDetailsState.DoneState(
            movie = movie.copy(
                genres = "fiction",
                updated_at = LocalDate.now(testClock)
            ),
            isInLibrary = true
        )

        val outdatedMovie = movie.copy(updated_at = LocalDate.now(testClock).minusDays(3))

        underTest.state.test {
            underTest.handleAction(LoadMovie(movie.id))
            repository.result.emit(MovieDetailsResult.Success(movie = outdatedMovie, isInLibrary = true))

            assertEquals(MovieDetailsState.LoadingState, expectItem())
            assertEquals(MovieDetailsState.DoneState(movie = outdatedMovie, isInLibrary = true), expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `data of movie in library does not get updated within two days`() = runBlockingTest {
        val upToDateMovie = movie.copy(
            genres = "fiction",
            updated_at = LocalDate.now(testClock).minusDays(1)
        )

        underTest.state.test {
            underTest.handleAction(LoadMovie(movie.id))
            repository.result.emit(MovieDetailsResult.Success(movie = upToDateMovie, isInLibrary = true))

            assertEquals(MovieDetailsState.LoadingState, expectItem())
            assertEquals(MovieDetailsState.DoneState(movie = upToDateMovie, isInLibrary = true), expectItem())
            //no new state expected
            expectNoEvents()
        }
    }
}

private class FakeRepository(
    val clock: Clock
): MovieDetailsRepository {

    val result = MutableSharedFlow<MovieDetailsResult>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override suspend fun loadMovie(id: Int): Flow<MovieDetailsResult> {
        return flow {
            result.collect { emit(it) }
        }
    }

    override suspend fun addMovie(movie: LibraryMovie) {
        result.emit(MovieDetailsResult.Success(movie, true))
    }

    override suspend fun deleteMovie(movie: LibraryMovie) {
        result.emit(MovieDetailsResult.Success(movie, false))
    }

    override suspend fun updateMovie(movie: LibraryMovie) {
        result.emit(MovieDetailsResult.Success(movie, true))
    }

    override suspend fun updateMovieData(movie: LibraryMovie) {
        result.emit(
            MovieDetailsResult.Success(
                movie.copy(genres = "fiction", updated_at = LocalDate.now(clock)),
                isInLibrary = true
            )
        )
    }
}