package com.snad.spotlight

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.snad.spotlight.persistence.AppDatabase
import com.snad.spotlight.persistence.DatabaseSingleton

class App : MultiDexApplication() {

    lateinit var appDb: AppDatabase

    override fun onCreate() {
        super.onCreate()
        instance = this

        appDb = DatabaseSingleton.instance!!
    }

    companion object {
        private var instance: Application? = null
        val appContext: Context
            get() = instance!!.applicationContext
    }
}