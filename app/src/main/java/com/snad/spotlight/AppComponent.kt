package com.snad.spotlight

import android.content.Context
import com.snad.spotlight.network.RetrofitClient
import com.snad.core.persistence.PersistenceModule
import com.snad.feature.library.LibraryComponent
import com.snad.feature.moviedetails.MovieDetailsComponent
import com.snad.feature.newmovies.NewMoviesComponent
import com.snad.feature.search.SearchComponent
import com.snad.spotlight.ui.castDetails.CastDetailsFragment
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import java.io.File
import javax.inject.Singleton


@Singleton
@Component(modules = [
    AppModule::class,
    FeaturesModule::class,
    PersistenceModule::class
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

    fun inject(castDetailsFragment: CastDetailsFragment)
}

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(cacheDir: File): Retrofit = RetrofitClient(cacheDir).get()

    @Singleton
    @Provides
    fun provideCacheDir(app: App): File = app.cacheDir

    @Singleton
    @Provides
    fun provideAppContext(app: App): Context = app.applicationContext
}

@Module(subcomponents = [
    LibraryComponent::class,
    NewMoviesComponent::class,
    SearchComponent::class,
    MovieDetailsComponent::class
])
class FeaturesModule