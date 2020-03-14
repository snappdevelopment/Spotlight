package com.snad.spotlight

import com.snad.spotlight.persistence.LibraryDb
import com.snad.spotlight.persistence.LibraryDbResult
import com.snad.spotlight.persistence.models.LibraryMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibraryRepository(
    private val libraryDb: LibraryDb
) {
    suspend fun loadLibraryMovies(): Flow<LibraryRepositoryResult> {
        return libraryDb.getAllMovies().map { result ->
            when(result) {
                is LibraryDbResult.SuccessAllMovies -> LibraryRepositoryResult.Success(result.libraryMovies)
                else -> LibraryRepositoryResult.DbError
            }
        }
    }

    suspend fun updateMovie(movie: LibraryMovie) {
        libraryDb.updateMovie(movie)
    }

    suspend fun deleteMovie(movie: LibraryMovie) {
        libraryDb.deleteMovie(movie)
    }
}

sealed class LibraryRepositoryResult {
    data class Success(val libraryMovies: List<LibraryMovie>): LibraryRepositoryResult()
    object DbError: LibraryRepositoryResult()
}