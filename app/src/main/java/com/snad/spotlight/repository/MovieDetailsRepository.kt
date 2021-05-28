package com.snad.spotlight.repository

import com.snad.spotlight.network.MovieApi
import com.snad.spotlight.network.MovieApiResult
import com.snad.core.persistence.LibraryDb
import com.snad.core.persistence.LibraryDbResult
import com.snad.core.persistence.models.LibraryMovie
import com.snad.spotlight.persistence.toLibraryMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class MovieDetailsRepository @Inject constructor(
    private val libraryDb: LibraryDb,
    private val movieApi: MovieApi
) {
    suspend fun loadMovie(id: Int): Flow<MovieDetailsResult> {
        val result = libraryDb.getMovieById(id)
        return result.map { libraryDbResult ->
            when(libraryDbResult) {
                is LibraryDbResult.SuccessMovieById -> MovieDetailsResult.Success(
                    libraryDbResult.libraryMovie,
                    true
                )
                is LibraryDbResult.ErrorMovieById -> loadMovieFromApi(id)
                else -> MovieDetailsResult.Error
            }
        }
    }

    suspend fun addMovie(movie: LibraryMovie) {
        libraryDb.insertMovie(movie)
    }

    suspend fun deleteMovie(movie: LibraryMovie) {
        libraryDb.deleteMovie(movie)
    }

    suspend fun updateMovie(movie: LibraryMovie) {
        libraryDb.updateMovie(movie)
    }

    suspend fun updateMovieData(movie: LibraryMovie) {
        if(movie.updated_at != null) {
            var daysDifference = Calendar.getInstance().timeInMillis - movie.updated_at!!.timeInMillis
            daysDifference = daysDifference / (1000 * 60 * 60 * 24)

            if(daysDifference >= 2) {
                val result = movieApi.loadMovie(movie.id)
                when(result) {
                    is MovieApiResult.Success -> {
                        val updatedMovie = result.movie.toLibraryMovie(
                            addedAt = movie.added_at,
                            updatedAt = Calendar.getInstance(),
                            hasBeenWatched = movie.has_been_watched
                        )
                        libraryDb.updateMovie(updatedMovie)
                    }
                }
            }
        }
    }

    private suspend fun loadMovieFromApi(id: Int): MovieDetailsResult {
        val result = movieApi.loadMovie(id)
        return when(result) {
            is MovieApiResult.Success -> MovieDetailsResult.Success(
                result.movie.toLibraryMovie(),
                false
            )
            is MovieApiResult.NetworkError -> MovieDetailsResult.NetworkError
            is MovieApiResult.ConnectionError -> MovieDetailsResult.ConnectionError
            is MovieApiResult.AuthenticationError -> MovieDetailsResult.AuthenticationError
            is MovieApiResult.ApiError -> MovieDetailsResult.ApiError
            is MovieApiResult.Error -> MovieDetailsResult.Error
        }
    }
}

sealed class MovieDetailsResult {
    data class Success(val movie: LibraryMovie, val isInLibrary: Boolean): MovieDetailsResult()
    object NetworkError: MovieDetailsResult()
    object ConnectionError: MovieDetailsResult()
    object AuthenticationError: MovieDetailsResult()
    object ApiError: MovieDetailsResult()
    object Error: MovieDetailsResult()
}