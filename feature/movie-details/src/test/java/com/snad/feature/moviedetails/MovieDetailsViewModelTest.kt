package com.snad.feature.moviedetails

import app.cash.turbine.test
import com.snad.core.persistence.models.LibraryMovie
import com.snad.feature.moviedetails.repository.MovieDetailsRepository
import com.snad.feature.moviedetails.repository.MovieDetailsResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

        repository.result = MovieDetailsResult.Success(movie = movie, isInLibrary = true)

        underTest.state.test {
            assertEquals(MovieDetailsState.LoadingState, expectItem())
            underTest.loadMovie(movie.id)
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

        repository.result = MovieDetailsResult.Success(movie = movie, isInLibrary = false)

        underTest.state.test {
            underTest.loadMovie(movie.id)
            assertEquals(MovieDetailsState.LoadingState, expectItem())
            assertEquals(MovieDetailsState.DoneState(movie = movie, isInLibrary = false), expectItem())
            underTest.addOrRemoveMovie()
            //load movie again to get state update from fake repository (should be fixed by using a stateFlow or similar)
            underTest.loadMovie(movie.id)
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

        repository.result = MovieDetailsResult.Success(movie = movie, isInLibrary = true)

        underTest.state.test {
            underTest.loadMovie(movie.id)
            assertEquals(MovieDetailsState.LoadingState, expectItem())
            assertEquals(MovieDetailsState.DoneState(movie = movie, isInLibrary = true), expectItem())
            underTest.addOrRemoveMovie()
            //load movie again to get state update from fake repository (should be fixed by using a stateFlow or similar)
            underTest.loadMovie(movie.id)
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

        repository.result = MovieDetailsResult.Success(movie = movie, isInLibrary = true)

        underTest.state.test {
            underTest.loadMovie(movie.id)
            assertEquals(MovieDetailsState.LoadingState, expectItem())
            assertEquals(MovieDetailsState.DoneState(movie = movie, isInLibrary = true), expectItem())
            underTest.toggleHasBeenWatched()
            //load movie again to get state update from fake repository (should be fixed by using a stateFlow or similar)
            underTest.loadMovie(movie.id)
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
        repository.result = MovieDetailsResult.Success(
            movie = outdatedMovie,
            isInLibrary = true
        )

        underTest.state.test {
            underTest.loadMovie(movie.id)
            assertEquals(MovieDetailsState.LoadingState, expectItem())
            assertEquals(MovieDetailsState.DoneState(movie = outdatedMovie, isInLibrary = true), expectItem())
            //load movie again to get state update from fake repository (should be fixed by using a stateFlow or similar)
            underTest.loadMovie(movie.id)
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

        repository.result = MovieDetailsResult.Success(
            movie = upToDateMovie,
            isInLibrary = true
        )

        underTest.state.test {
            underTest.loadMovie(movie.id)
            assertEquals(MovieDetailsState.LoadingState, expectItem())
            assertEquals(MovieDetailsState.DoneState(movie = upToDateMovie, isInLibrary = true), expectItem())
            //load movie again to get state update from fake repository (should be fixed by using a stateFlow or similar)
            underTest.loadMovie(movie.id)
            //no new state expected
            expectNoEvents()
        }
    }
}

private class FakeRepository(
    val clock: Clock
): MovieDetailsRepository {

    var result: MovieDetailsResult? = null

    override suspend fun loadMovie(id: Int): Flow<MovieDetailsResult> {
        return flowOf(result!!)
    }

    override suspend fun addMovie(movie: LibraryMovie) {
        result = MovieDetailsResult.Success(movie, true)
    }

    override suspend fun deleteMovie(movie: LibraryMovie) {
        result = MovieDetailsResult.Success(movie, false)
    }

    override suspend fun updateMovie(movie: LibraryMovie) {
        result = MovieDetailsResult.Success(movie, true)
    }

    override suspend fun updateMovieData(movie: LibraryMovie) {
        result = MovieDetailsResult.Success(
            movie.copy(genres = "fiction", updated_at = LocalDate.now(clock)),
            isInLibrary = true
        )
    }
}