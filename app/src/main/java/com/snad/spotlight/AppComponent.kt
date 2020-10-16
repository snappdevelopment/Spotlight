package com.snad.spotlight

import android.app.Application
import com.snad.spotlight.network.RetrofitClient
import com.snad.spotlight.persistence.AppDatabase
import com.snad.spotlight.persistence.DatabaseSingleton
import com.snad.spotlight.ui.castDetails.CastDetailsFragment
import com.snad.spotlight.ui.library.LibraryFragment
import com.snad.spotlight.ui.movieDetails.MovieDetailsFragment
import com.snad.spotlight.ui.newMovies.NewMoviesFragment
import com.snad.spotlight.ui.search.SearchFragment
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import java.io.File
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: App): Builder
        fun build(): AppComponent
    }

    fun inject(newMoviesFragment: NewMoviesFragment)
    fun inject(searchFragment: SearchFragment)
    fun inject(libraryFragment: LibraryFragment)
    fun inject(movieDetailsFragment: MovieDetailsFragment)
    fun inject(castDetailsFragment: CastDetailsFragment)
}

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(cacheDir: File) = RetrofitClient(cacheDir).get()

    @Singleton
    @Provides
    fun provideCacheDir(app: App): File = app.cacheDir

    @Singleton
    @Provides
    fun provideDatabaseInstance(): AppDatabase = DatabaseSingleton.instance!!
}