package com.snad.sniffer.feed

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snad.sniffer.logging.NetworkDataRepository

@Composable
internal fun FeedUi(
    viewModel: FeedViewModel,
    onBackClick: () -> Unit
) {
    val state = viewModel.state.collectAsState()
    Text(text = state.value.toString())
}

//@Preview
//@Composable
//private fun FeedUiPreview() {
//    FeedUi(
//        viewModel = FeedViewModel(object : NetworkDataRepository {}),
//        onBackClick = {}
//    )
//}