package com.snad.feature.moviedetails

import app.cash.turbine.test
import com.snad.core.persistence.LibraryDbResult
import com.snad.core.persistence.models.LibraryMovie
import com.snad.feature.moviedetails.model.*
import com.snad.feature.moviedetails.repository.MovieApi
import com.snad.feature.moviedetails.repository.MovieApiResult
import com.snad.feature.moviedetails.repository.MovieDetailsRepositoryImpl
import com.snad.feature.moviedetails.repository.MovieDetailsResult
import com.snad.spotlight.persistence.testing.TestLibraryDb
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class MovieDetailsRepositoryTest {

    private val apiMovie = Movie(
        adult = false,
        backdrop_path = null,
        belongs_to_collection = null,
        budget = 0,
        genres = listOf(),
        homepage = null,
        id = 0,
        imdb_id = null,
        original_language = "",
        original_title = "",
        overview = null,
        popularity = 0.0,
        poster_path = null,
        production_companies = listOf(),
        production_countries = listOf(),
        release_date = "",
        revenue = 0,
        runtime = null,
        spoken_languages = listOf(),
        status = "",
        tagline = null,
        title = "",
        video = false,
        vote_average = 0.0,
        vote_count = 0,
        images = Images(
            backdrops = listOf(),
            posters = listOf()
        ),
        videos = Videos(emptyList()),
        credits = Credits(emptyList(), emptyList()),
        reviews = Reviews(emptyList(), 1, 1, 1)
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

    private val libraryDb = spy(TestLibraryDb())
    private val movieApi = mock<MovieApi>()

    private val underTest = MovieDetailsRepositoryImpl(
        libraryDb = libraryDb,
        movieApi = movieApi
    )

    @Test
    fun `loadMovie from db succeeds`() = runBlocking {
        underTest.loadMovie(0).test {
            libraryDb.result.emit(LibraryDbResult.Success(listOf(libraryMovie)))

            assertEquals(
                MovieDetailsResult.Success(movie = libraryMovie, isInLibrary = true),
                expectItem()
            )
            expectNoEvents()
        }
    }

    @Test
    fun `loadMovie from db fails and succeeds from api`() = runBlocking {
        whenever(movieApi.loadMovie(any())).thenReturn(MovieApiResult.Success(apiMovie))

        underTest.loadMovie(0).test {
            libraryDb.result.emit(LibraryDbResult.Error)

            assertEquals(
                MovieDetailsResult.Success(movie = libraryMovie, isInLibrary = false),
                expectItem()
            )
            expectNoEvents()
        }
    }

    @Test
    fun `loadMovie from db and api fails`() = runBlocking {
        whenever(movieApi.loadMovie(any())).thenReturn(MovieApiResult.ApiError)

        underTest.loadMovie(0).test {
            libraryDb.result.emit(LibraryDbResult.Error)

            assertEquals(
                MovieDetailsResult.ApiError,
                expectItem()
            )
            expectNoEvents()
        }
    }

    @Test
    fun `addMovie adds movie to db`() = runBlocking {
        underTest.addMovie(libraryMovie)
        verify(libraryDb).insertMovie(libraryMovie)
    }

    @Test
    fun `deleteMovie deletes movie from db`() = runBlocking {
        underTest.deleteMovie(libraryMovie)
        verify(libraryDb).deleteMovie(libraryMovie)
    }

    @Test
    fun `updateMovie updates movie in db`() = runBlocking {
        underTest.updateMovie(libraryMovie)
        verify(libraryDb).updateMovie(libraryMovie)
    }
}


