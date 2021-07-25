package com.snad.feature.library.repository

import com.snad.core.persistence.LibraryDb
import com.snad.core.persistence.LibraryDbResult
import com.snad.core.persistence.models.LibraryMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class LibraryRepository @Inject constructor(
    private val libraryDb: LibraryDb
) {
    fun loadLibraryMovies(): Flow<LibraryRepositoryResult> {
        return libraryDb.getAllMovies().map { result ->
            when(result) {
                is LibraryDbResult.Success -> LibraryRepositoryResult.Success(result.libraryMovies)
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

internal sealed class LibraryRepositoryResult {
    data class Success(val libraryMovies: List<LibraryMovie>): LibraryRepositoryResult()
    object DbError: LibraryRepositoryResult()
}