package com.snad.spotlight.ui.movieDetails

import com.snad.spotlight.App

fun MovieDetailsFragment.inject() {
    (requireContext().applicationContext as App)
        .appComponent.inject(this)
}