package com.snad.spotlight.ui.newMovies

import com.snad.spotlight.App

fun NewMoviesFragment.inject() {
    (requireContext().applicationContext as App)
        .appComponent.inject(this)
}