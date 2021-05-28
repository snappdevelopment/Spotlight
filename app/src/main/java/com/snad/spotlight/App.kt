package com.snad.spotlight

import androidx.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen
import com.snad.feature.library.LibraryComponent
import com.snad.feature.library.LibraryComponentProvider

class App : MultiDexApplication(), LibraryComponentProvider {

    val appComponent = DaggerAppComponent
        .builder()
        .application(this)
        .build()

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

    override fun provideLibraryComponent(): LibraryComponent = appComponent.libraryComponent().create()
}