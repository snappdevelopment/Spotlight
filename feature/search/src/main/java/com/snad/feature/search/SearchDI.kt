package com.snad.feature.search

import com.snad.feature.search.repository.SearchRepository
import com.snad.feature.search.repository.SearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.time.Clock
import javax.inject.Scope

@Scope
annotation class SearchScope

interface SearchComponentProvider {
    fun provideSearchComponent(): SearchComponent
}

@SearchScope
@Subcomponent(modules = [SearchModule::class])
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

@Module
internal interface SearchModule {
    @SearchScope
    @Binds
    fun SearchRepositoryImpl.bind(): SearchRepository
}