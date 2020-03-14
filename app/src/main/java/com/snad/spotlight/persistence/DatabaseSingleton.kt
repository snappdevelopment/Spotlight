package com.snad.spotlight.persistence

import androidx.room.Room
import com.snad.spotlight.App

object DatabaseSingleton {
    var instance: AppDatabase? = null
        get() {
            if (field == null) {
                field = Room.databaseBuilder(
                    App.appContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .build()
            }
            return field
        }
        private set
}