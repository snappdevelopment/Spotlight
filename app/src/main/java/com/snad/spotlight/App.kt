package com.snad.spotlight

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen
import com.snad.spotlight.network.RetrofitClient
import com.snad.spotlight.persistence.AppDatabase
import com.snad.spotlight.persistence.DatabaseSingleton
import retrofit2.Retrofit

class App : MultiDexApplication() {

    lateinit var appDb: AppDatabase
    lateinit var retrofit: Retrofit

    override fun onCreate() {
        super.onCreate()
        instance = this
        AndroidThreeTen.init(this)

        retrofit = RetrofitClient(this.cacheDir).get()
        appDb = DatabaseSingleton.instance!!
    }

    companion object {
        private var instance: Application? = null
        val appContext: Context
            get() = instance!!.applicationContext
    }
}