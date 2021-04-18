package com.snad.spotlight.ui.search

import com.snad.spotlight.App

fun SearchFragment.inject() {
    (requireContext().applicationContext as App)
        .appComponent.inject(this)
}