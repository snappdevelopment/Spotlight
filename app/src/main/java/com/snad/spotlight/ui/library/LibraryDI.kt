package com.snad.spotlight.ui.library

import com.snad.spotlight.App

fun LibraryFragment.inject() {
    (requireContext().applicationContext as App)
        .appComponent.inject(this)
}