package com.snad.feature.library

import com.snad.feature.library.repository.LibraryRepository
import com.snad.feature.library.repository.LibraryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Scope

@Scope
annotation class LibraryScope

interface LibraryComponentProvider {
    fun provideLibraryComponent(): LibraryComponent
}

@LibraryScope
@Subcomponent(modules = [LibraryModule::class])
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

@Module
internal interface LibraryModule {
    @LibraryScope
    @Binds
    fun LibraryRepositoryImpl.binds(): LibraryRepository

    companion object {
        @LibraryScope
        @Provides
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    }
}