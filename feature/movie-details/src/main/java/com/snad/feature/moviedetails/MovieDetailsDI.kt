package com.snad.feature.moviedetails

import com.snad.feature.moviedetails.repository.MovieDetailsRepository
import com.snad.feature.moviedetails.repository.MovieDetailsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Scope

@Scope
annotation class MovieDetailsScope

interface MovieDetailsComponentProvider {
    fun provideMovieDetailsComponent(): MovieDetailsComponent
}

@MovieDetailsScope
@Subcomponent(modules = [MovieDetailsModule::class])
interface MovieDetailsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): MovieDetailsComponent
    }

    fun inject(movieDetailsFragment: MovieDetailsFragment)
}

internal fun MovieDetailsFragment.inject() {
    movieDetailsComponent = (requireContext().applicationContext as MovieDetailsComponentProvider)
        .provideMovieDetailsComponent()
    movieDetailsComponent.inject(this)
}

@Module
internal interface MovieDetailsModule {
    @MovieDetailsScope
    @Binds
    fun MovieDetailsRepositoryImpl.bind(): MovieDetailsRepository

    companion object {
        @MovieDetailsScope
        @Provides
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    }
}