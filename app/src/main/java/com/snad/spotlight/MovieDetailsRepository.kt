package com.snad.spotlight

import com.snad.spotlight.persistence.LibraryDb
import com.snad.spotlight.persistence.LibraryDbResult
import com.snad.spotlight.persistence.models.LibraryMovie

class MovieDetailsRepository(
    private val libraryDb: LibraryDb
) {
    suspend fun loadMovie(id: Int): MovieDetailsResult {
        val result = libraryDb.getMovieById(id)
        return when(result) {
            is LibraryDbResult.SuccessMovieById -> MovieDetailsResult.Success(result.libraryMovie, true)
            is LibraryDbResult.ErrorMovieById -> loadMovieFromApi()
            else -> MovieDetailsResult.ErrorUnknown
        }
    }

    suspend fun addMovie(movie: LibraryMovie) {
        libraryDb.insertMovie(movie)
    }

    private suspend fun loadMovieFromApi(): MovieDetailsResult {
        //todo: get movie from api if not in db
        return MovieDetailsResult.ErrorUnknown
    }
}

sealed class MovieDetailsResult {
    data class Success(val movie: LibraryMovie, val isInLibrary: Boolean): MovieDetailsResult()
    object ErrorUnknown: MovieDetailsResult()
}