package com.snad.spotlight

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDexApplication

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private var instance: Application? = null
        val appContext: Context
            get() = instance!!.applicationContext
    }
}