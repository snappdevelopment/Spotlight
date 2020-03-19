package com.snad.spotlight.persistence

import com.snad.spotlight.persistence.models.LibraryMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibraryDb(
    private val db: AppDatabase
) {
    private val dao = db.libraryMovieDao()

    suspend fun getAllMovies(): Flow<LibraryDbResult> {
        return dao.getAllMovies().map { list ->
            LibraryDbResult.SuccessAllMovies(list)
            //Todo: Error einbauen wenn nötig
        }
    }

    suspend fun getMovieById(id: Int): LibraryDbResult {
        val movie = dao.getMovieById(id)
        return if(movie != null) {
            LibraryDbResult.SuccessMovieById(movie)
        }
        else {
            LibraryDbResult.ErrorMovieById
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