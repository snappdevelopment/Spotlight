package com.snad.feature.castdetails

import com.snad.feature.castdetails.repository.PersonRepository
import com.snad.feature.castdetails.repository.PersonRepositoryImpl
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
annotation class CastDetailsScope

interface CastDetailsComponentProvider {
    fun provideCastDetailsComponent(): CastDetailsComponent
}

@CastDetailsScope
@Subcomponent(modules = [CastDetailsModule::class])
interface CastDetailsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): CastDetailsComponent
    }

    fun inject(castDetailsFragment: CastDetailsFragment)
}

internal fun CastDetailsFragment.inject() {
    castDetailsComponent = (requireContext().applicationContext as CastDetailsComponentProvider)
        .provideCastDetailsComponent()
    castDetailsComponent.inject(this)
}

@Module
internal interface CastDetailsModule {
    @CastDetailsScope
    @Binds
    fun PersonRepositoryImpl.binds(): PersonRepository

    companion object {
        @CastDetailsScope
        @Provides
        fun provideDateTimeFormatter(): DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    }
}