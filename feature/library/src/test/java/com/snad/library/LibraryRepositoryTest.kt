package com.snad.library

import app.cash.turbine.test
import com.snad.core.persistence.LibraryDbResult
import com.snad.core.persistence.models.LibraryMovie
import com.snad.feature.library.repository.LibraryRepositoryImpl
import com.snad.feature.library.repository.LibraryRepositoryResult
import com.snad.spotlight.persistence.testing.TestLibraryDb
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class LibraryRepositoryTest {

    private val libraryDb = spy(TestLibraryDb())

    private val underTest = LibraryRepositoryImpl(
        libraryDb = libraryDb
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
    fun `load movie from library succeeds`() = runBlocking {
        val expectedResult = LibraryRepositoryResult.Success(listOf(libraryMovie))

        underTest.loadLibraryMovies().test {
            libraryDb.result.emit(LibraryDbResult.Success(listOf(libraryMovie)))
            assertEquals(expectedResult, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `load movie from library fails`() = runBlocking {
        val expectedResult = LibraryRepositoryResult.DbError

        underTest.loadLibraryMovies().test {
            libraryDb.result.emit(LibraryDbResult.Error)
            assertEquals(expectedResult, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `update movie succeeds`() = runBlocking {
        val expectedResult = LibraryRepositoryResult.Success(listOf(libraryMovie.copy(tagline = "")))

        underTest.loadLibraryMovies().test {
            libraryDb.result.emit(LibraryDbResult.Success(listOf(libraryMovie)))
            underTest.updateMovie(libraryMovie.copy(tagline = ""))

            verify(libraryDb).updateMovie(libraryMovie.copy(tagline = ""))
            libraryDb.result.emit(LibraryDbResult.Success(listOf(libraryMovie.copy(tagline = ""))))

            assertEquals(LibraryRepositoryResult.Success(listOf(libraryMovie)), expectItem())
            assertEquals(expectedResult, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `delete movie succeeds`() = runBlocking {
        val expectedResult = LibraryRepositoryResult.Success(listOf())

        underTest.loadLibraryMovies().test {
            underTest.deleteMovie(libraryMovie)
            verify(libraryDb).deleteMovie(libraryMovie)

            libraryDb.result.emit(LibraryDbResult.Success(listOf()))
            assertEquals(expectedResult, expectItem())
            expectNoEvents()
        }
    }
}