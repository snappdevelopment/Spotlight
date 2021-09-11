package com.snad.core.arch

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

fun <T: Flow<R>, R> T.observeWithLifecycle(
    fragment: Fragment,
    block: (state: R) -> Unit
) {
    fragment.viewLifecycleOwner.lifecycleScope.launch {
        this@observeWithLifecycle
            .flowWithLifecycle(fragment.lifecycle, Lifecycle.State.STARTED)
            .distinctUntilChanged()
            .collect { state ->
                block(state)
            }
    }
}