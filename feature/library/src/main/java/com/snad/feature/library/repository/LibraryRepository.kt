package com.snad.feature.library.repository

import com.snad.core.persistence.LibraryDb
import com.snad.core.persistence.LibraryDbResult
import com.snad.core.persistence.models.LibraryMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal interface LibraryRepository {
    fun loadLibraryMovies(): Flow<LibraryRepositoryResult>
    suspend fun updateMovie(movie: LibraryMovie)
    suspend fun deleteMovie(movie: LibraryMovie)
}

internal class LibraryRepositoryImpl @Inject constructor(
    private val libraryDb: LibraryDb
): LibraryRepository {
    override fun loadLibraryMovies(): Flow<LibraryRepositoryResult> {
        return libraryDb.getAllMovies().map { result ->
            when(result) {
                is LibraryDbResult.Success -> LibraryRepositoryResult.Success(result.libraryMovies)
                else -> LibraryRepositoryResult.DbError
            }
        }
    }

    override suspend fun updateMovie(movie: LibraryMovie) {
        libraryDb.updateMovie(movie)
    }

    override suspend fun deleteMovie(movie: LibraryMovie) {
        libraryDb.deleteMovie(movie)
    }
}

internal sealed class LibraryRepositoryResult {
    data class Success(val libraryMovies: List<LibraryMovie>): LibraryRepositoryResult()
    object DbError: LibraryRepositoryResult()
}