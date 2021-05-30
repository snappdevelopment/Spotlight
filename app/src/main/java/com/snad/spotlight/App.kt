package com.snad.spotlight

import androidx.multidex.MultiDexApplication
import com.snad.feature.castdetails.CastDetailsComponent
import com.snad.feature.castdetails.CastDetailsComponentProvider
import com.snad.feature.library.LibraryComponent
import com.snad.feature.library.LibraryComponentProvider
import com.snad.feature.moviedetails.MovieDetailsComponent
import com.snad.feature.moviedetails.MovieDetailsComponentProvider
import com.snad.feature.newmovies.NewMoviesComponent
import com.snad.feature.newmovies.NewMoviesComponentProvider
import com.snad.feature.search.SearchComponent
import com.snad.feature.search.SearchComponentProvider

class App :
    MultiDexApplication(),
    LibraryComponentProvider,
    NewMoviesComponentProvider,
    SearchComponentProvider,
    MovieDetailsComponentProvider,
    CastDetailsComponentProvider {

    val appComponent = DaggerAppComponent
        .builder()
        .application(this)
        .build()

    override fun provideLibraryComponent(): LibraryComponent = appComponent.libraryComponent().create()
    override fun provideNewMoviesComponent(): NewMoviesComponent = appComponent.newMoviesComponent().create()
    override fun provideSearchComponent(): SearchComponent = appComponent.searchComponent().create()
    override fun provideMovieDetailsComponent(): MovieDetailsComponent = appComponent.movieDetailsComponent().create()
    override fun provideCastDetailsComponent(): CastDetailsComponent = appComponent.castDetailsComponent().create()
}