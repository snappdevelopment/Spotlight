package com.snad.spotlight.persistence

import androidx.room.*
import com.snad.spotlight.persistence.models.LibraryMovie
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryMovieDao {
    @Insert
    suspend fun insertMovie(movie: LibraryMovie)

    @Update
    suspend fun updateMovie(movie: LibraryMovie)

    @Delete
    suspend fun deleteMovie(movie: LibraryMovie)

    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<LibraryMovie>>

    @Query("SELECT * FROM movies WHERE id LIKE :id")
    suspend fun getMovieById(id: Int): LibraryMovie?
}