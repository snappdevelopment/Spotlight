package com.snad.feature.library

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
internal fun LibraryUi(
    state: LibraryState,
    sendAction: (LibraryAction) -> Unit
) {
    MaterialTheme {
        Text(text = "$state")
    }
}