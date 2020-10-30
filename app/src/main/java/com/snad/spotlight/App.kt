package com.snad.spotlight

import androidx.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen

class App : MultiDexApplication() {

    val appComponent = DaggerAppComponent
        .builder()
        .application(this)
        .build()

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}