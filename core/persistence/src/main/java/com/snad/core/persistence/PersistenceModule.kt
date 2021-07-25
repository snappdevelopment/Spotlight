package com.snad.core.persistence

import android.content.Context
import androidx.room.Room
import dagger.*
import javax.inject.Singleton

@Module
interface PersistenceModule {

    companion object {
        @Singleton
        @Provides
        fun provideDatabaseInstance(appContext: Context): AppDatabase =
            Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "app_database"
            )
                .build()
    }

    @Singleton
    @Binds
    fun LibraryDbImpl.bind(): LibraryDb
}