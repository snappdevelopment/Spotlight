package com.snad.spotlight.persistence

import androidx.room.*
import com.snad.spotlight.persistence.models.LibraryMovie
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryMovieDao {
    @Insert
    suspend fun insertMovie(movie: LibraryMovie): Long

    @Update
    suspend fun updateMovie(movie: LibraryMovie)

    @Delete
    suspend fun deleteMovie(movie: LibraryMovie)

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): Flow<List<LibraryMovie>>

    @Query("SELECT * FROM movies WHERE uid LIKE :uid")
    suspend fun getMovieById(uid: Long): LibraryMovie?
}