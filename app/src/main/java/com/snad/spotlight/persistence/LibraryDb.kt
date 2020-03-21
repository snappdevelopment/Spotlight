package com.snad.spotlight.persistence

import com.snad.spotlight.persistence.models.LibraryMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibraryDb(
    private val db: AppDatabase
) {
    private val dao = db.libraryMovieDao()

    fun getAllMovies(): Flow<LibraryDbResult> {
        return dao.getAllMovies().map { list ->
            LibraryDbResult.SuccessAllMovies(list)
        }
    }

    fun getMovieById(id: Int): Flow<LibraryDbResult> {
        val movie = dao.getMovieById(id)
        return movie.map { libraryMovie ->
            when(libraryMovie) {
                null -> LibraryDbResult.ErrorMovieById
                else -> LibraryDbResult.SuccessMovieById(libraryMovie)
            }
        }
    }

    suspend fun insertMovie(movie: LibraryMovie) {
        dao.insertMovie(movie)
    }

    suspend fun updateMovie(movie: LibraryMovie) {
        dao.updateMovie(movie)
    }

    suspend fun deleteMovie(movie: LibraryMovie) {
        dao.deleteMovie(movie)
    }
}

sealed class LibraryDbResult {
    data class SuccessAllMovies(val libraryMovies: List<LibraryMovie>): LibraryDbResult()
    data class SuccessMovieById(val libraryMovie: LibraryMovie): LibraryDbResult()
    object ErrorMovieById: LibraryDbResult()
}