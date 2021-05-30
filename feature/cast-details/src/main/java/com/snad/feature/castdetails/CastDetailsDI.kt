package com.snad.feature.castdetails

import dagger.Subcomponent

interface CastDetailsComponentProvider {
    fun provideCastDetailsComponent(): CastDetailsComponent
}

@Subcomponent
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