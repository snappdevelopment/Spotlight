package com.snad.core.persistence

import androidx.room.*
import com.snad.core.persistence.models.LibraryMovie
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
    fun getMovieById(id: Int): Flow<LibraryMovie?>
}