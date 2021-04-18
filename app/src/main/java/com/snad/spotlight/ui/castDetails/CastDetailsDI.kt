package com.snad.spotlight.ui.castDetails

import com.snad.spotlight.App

fun CastDetailsFragment.inject() {
    (requireContext().applicationContext as App)
        .appComponent.inject(this)
}