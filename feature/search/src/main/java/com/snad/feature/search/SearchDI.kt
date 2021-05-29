package com.snad.feature.search

import dagger.Subcomponent

interface SearchComponentProvider {
    fun provideSearchComponent(): SearchComponent
}

@Subcomponent
interface SearchComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SearchComponent
    }

    fun inject(searchFragment: SearchFragment)
}

internal fun SearchFragment.inject() {
    searchComponent = (requireContext().applicationContext as SearchComponentProvider)
        .provideSearchComponent()
    searchComponent.inject(this)
}