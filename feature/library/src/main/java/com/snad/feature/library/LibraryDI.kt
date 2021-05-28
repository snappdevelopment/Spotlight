package com.snad.feature.library

import dagger.Subcomponent

interface LibraryComponentProvider {
    fun provideLibraryComponent(): LibraryComponent
}

@Subcomponent
interface LibraryComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LibraryComponent
    }

    fun inject(libraryFragment: LibraryFragment)
}

internal fun LibraryFragment.inject() {
    libraryComponent = (requireContext().applicationContext as LibraryComponentProvider)
        .provideLibraryComponent()
    libraryComponent.inject(this)
}