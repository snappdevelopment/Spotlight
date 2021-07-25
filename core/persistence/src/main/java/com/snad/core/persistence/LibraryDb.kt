package com.snad.core.persistence

import com.snad.core.persistence.models.LibraryMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface LibraryDb {
    fun getAllMovies(): Flow<LibraryDbResult>
    fun getMovieById(id: Int): Flow<LibraryDbResult>
    suspend fun insertMovie(movie: LibraryMovie)
    suspend fun updateMovie(movie: LibraryMovie)
    suspend fun deleteMovie(movie: LibraryMovie)
}

class LibraryDbImpl @Inject constructor(
    private val db: AppDatabase
): LibraryDb {
    private val dao = db.libraryMovieDao()

    override fun getAllMovies(): Flow<LibraryDbResult> {
        return dao.getAllMovies().map { list ->
            LibraryDbResult.Success(list)
        }
    }

    override fun getMovieById(id: Int): Flow<LibraryDbResult> {
        val movie = dao.getMovieById(id)
        return movie.map { libraryMovie ->
            when(libraryMovie) {
                null -> LibraryDbResult.Error
                else -> LibraryDbResult.Success(listOf(libraryMovie))
            }
        }
    }

    override suspend fun insertMovie(movie: LibraryMovie) {
        dao.insertMovie(movie)
    }

    override suspend fun updateMovie(movie: LibraryMovie) {
        dao.updateMovie(movie)
    }

    override suspend fun deleteMovie(movie: LibraryMovie) {
        dao.deleteMovie(movie)
    }
}

sealed class LibraryDbResult {
    data class Success(val libraryMovies: List<LibraryMovie>): LibraryDbResult()
    object Error: LibraryDbResult()
}