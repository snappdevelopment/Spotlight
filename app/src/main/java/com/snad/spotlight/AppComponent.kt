package com.snad.spotlight

import android.content.Context
import com.snad.core.network.NetworkModule
import com.snad.core.persistence.PersistenceModule
import com.snad.feature.castdetails.CastDetailsComponent
import com.snad.feature.library.LibraryComponent
import com.snad.feature.moviedetails.MovieDetailsComponent
import com.snad.feature.newmovies.NewMoviesComponent
import com.snad.feature.search.SearchComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import java.io.File
import java.time.Clock
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    FeaturesModule::class,
    PersistenceModule::class,
    NetworkModule::class
])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: App): Builder
        fun build(): AppComponent
    }

    fun libraryComponent(): LibraryComponent.Factory
    fun newMoviesComponent(): NewMoviesComponent.Factory
    fun searchComponent(): SearchComponent.Factory
    fun movieDetailsComponent(): MovieDetailsComponent.Factory
    fun castDetailsComponent(): CastDetailsComponent.Factory
}

@Module
class AppModule {
    @Singleton
    @Provides
    fun provideCacheDir(app: App): File = app.cacheDir

    @Singleton
    @Provides
    fun provideAppContext(app: App): Context = app.applicationContext

    @Singleton
    @Provides
    fun provideClock(): Clock = Clock.systemDefaultZone()
}

@Module(subcomponents = [
    LibraryComponent::class,
    NewMoviesComponent::class,
    SearchComponent::class,
    MovieDetailsComponent::class,
    CastDetailsComponent::class
])
class FeaturesModule