package com.snad.feature.moviedetails

import dagger.Subcomponent

interface MovieDetailsComponentProvider {
    fun provideMovieDetailsComponent(): MovieDetailsComponent
}

@Subcomponent
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