package com.snad.core.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.snad.core.persistence.models.LibraryMovie

@Database(entities = [LibraryMovie::class], version = 1)
@TypeConverters(DatabaseTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun libraryMovieDao(): LibraryMovieDao
}