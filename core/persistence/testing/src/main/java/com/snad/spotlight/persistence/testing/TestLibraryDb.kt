package com.snad.spotlight.persistence.testing

import com.snad.core.persistence.LibraryDb
import com.snad.core.persistence.LibraryDbResult
import com.snad.core.persistence.models.LibraryMovie
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect

class TestLibraryDb: LibraryDb {

    val result = MutableSharedFlow<LibraryDbResult>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun getAllMovies(): Flow<LibraryDbResult> {
       return flow {
           result.collect { emit(it) }
       }
    }

    override fun getMovieById(id: Int): Flow<LibraryDbResult> {
        return flow {
            result.collect { emit(it) }
        }
    }

    override suspend fun insertMovie(movie: LibraryMovie) {
        //noop
    }

    override suspend fun updateMovie(movie: LibraryMovie) {
        //noop
    }

    override suspend fun deleteMovie(movie: LibraryMovie) {
        //noop
    }
}