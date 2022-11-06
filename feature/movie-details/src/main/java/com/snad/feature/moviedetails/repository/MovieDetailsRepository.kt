package com.snad.feature.moviedetails.repository

import com.snad.core.persistence.LibraryDb
import com.snad.core.persistence.LibraryDbResult
import com.snad.core.persistence.models.LibraryMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

internal interface MovieDetailsRepository {
    suspend fun loadMovie(id: Int): Flow<MovieDetailsResult>
    suspend fun addMovie(movie: LibraryMovie)
    suspend fun deleteMovie(movie: LibraryMovie)
    suspend fun updateMovie(movie: LibraryMovie)
    suspend fun updateMovieData(movie: LibraryMovie)
}

internal class MovieDetailsRepositoryImpl @Inject constructor(
    private val libraryDb: LibraryDb,
    private val movieApi: MovieApi
): MovieDetailsRepository {

    override suspend fun loadMovie(id: Int): Flow<MovieDetailsResult> {
        val result = libraryDb.getMovieById(id)
        return result.map { libraryDbResult ->
            when(libraryDbResult) {
                is LibraryDbResult.Success -> MovieDetailsResult.Success(
                    movie = libraryDbResult.libraryMovies.first(),
                    isInLibrary = true
                )
                is LibraryDbResult.Error -> loadMovieFromApi(id)
            }
        }
    }

    override suspend fun addMovie(movie: LibraryMovie) {
        libraryDb.insertMovie(movie)
    }

    override suspend fun deleteMovie(movie: LibraryMovie) {
        libraryDb.deleteMovie(movie)
    }

    override suspend fun updateMovie(movie: LibraryMovie) {
        libraryDb.updateMovie(movie)
    }

    override suspend fun updateMovieData(movie: LibraryMovie) {
        val result = movieApi.loadMovie(movie.id)
        when(result) {
            is MovieApiResult.Success -> {
                val updatedMovie = result.movie.toLibraryMovie(
                    addedAt = movie.added_at,
                    updatedAt = LocalDate.now(),
                    hasBeenWatched = movie.has_been_watched
                )
                libraryDb.updateMovie(updatedMovie)
            }
            else -> { /* Do nothing */ }
        }
    }

    private suspend fun loadMovieFromApi(id: Int): MovieDetailsResult {
        val result = movieApi.loadMovie(id)
        return when(result) {
            is MovieApiResult.Success -> MovieDetailsResult.Success(
                movie = result.movie.toLibraryMovie(),
                isInLibrary = false
            )
            is MovieApiResult.NetworkError -> MovieDetailsResult.NetworkError
            is MovieApiResult.ConnectionError -> MovieDetailsResult.ConnectionError
            is MovieApiResult.AuthenticationError -> MovieDetailsResult.AuthenticationError
            is MovieApiResult.ApiError -> MovieDetailsResult.ApiError
            is MovieApiResult.Error -> MovieDetailsResult.Error
        }
    }
}

internal sealed class MovieDetailsResult {
    data class Success(val movie: LibraryMovie, val isInLibrary: Boolean): MovieDetailsResult()
    object NetworkError: MovieDetailsResult()
    object ConnectionError: MovieDetailsResult()
    object AuthenticationError: MovieDetailsResult()
    object ApiError: MovieDetailsResult()
    object Error: MovieDetailsResult()
}