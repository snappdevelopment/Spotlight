package com.snad.spotlight

import com.snad.spotlight.network.MovieApi
import com.snad.spotlight.network.MovieApiResult
import com.snad.spotlight.persistence.LibraryDb
import com.snad.spotlight.persistence.LibraryDbResult
import com.snad.spotlight.persistence.models.LibraryMovie
import com.snad.spotlight.persistence.toLibraryMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieDetailsRepository(
    private val libraryDb: LibraryDb,
    private val movieApi: MovieApi
) {
    suspend fun loadMovie(id: Int): Flow<MovieDetailsResult> {
        //Todo: load changes from Api and write to db
        val result = libraryDb.getMovieById(id)
        return result.map { libraryDbResult ->
            when(libraryDbResult) {
                is LibraryDbResult.SuccessMovieById -> MovieDetailsResult.Success(libraryDbResult.libraryMovie, true)
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

    private suspend fun loadMovieFromApi(id: Int): MovieDetailsResult {
        val result = movieApi.loadMovie(id)
        return when(result) {
            is MovieApiResult.Success -> MovieDetailsResult.Success(result.movie.toLibraryMovie(), false)
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