package com.snad.feature.newmovies

import com.snad.feature.newmovies.repository.NewMoviesRepository
import com.snad.feature.newmovies.repository.NewMoviesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.time.Clock
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Scope

@Scope
annotation class NewMoviesScope

interface NewMoviesComponentProvider {
    fun provideNewMoviesComponent(): NewMoviesComponent
}

@NewMoviesScope
@Subcomponent(modules = [NewMoviesModule::class])
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

@Module
internal interface NewMoviesModule {
    @NewMoviesScope
    @Binds
    fun NewMoviesRepositoryImpl.binds(): NewMoviesRepository
}