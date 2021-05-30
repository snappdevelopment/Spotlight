package com.snad.feature.newmovies

import dagger.Subcomponent

interface NewMoviesComponentProvider {
    fun provideNewMoviesComponent(): NewMoviesComponent
}

@Subcomponent
interface NewMoviesComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): NewMoviesComponent
    }

    fun inject(newMoviesFragment: NewMoviesFragment)
}

internal fun NewMoviesFragment.inject() {
    newMoviesComponent = (requireContext().applicationContext as NewMoviesComponentProvider)
        .provideNewMoviesComponent()
    newMoviesComponent.inject(this)
}