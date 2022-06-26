package com.snad.sniffer.details

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun DetailsUi(
    requestId: Long,
    viewModelFactory: DetailsViewModel.Factory,
    onBackClicked: () -> Unit
) {
    val viewModel: DetailsViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state(requestId).collectAsState()

    when(val currentState = state) {
        is DetailsState.Initial -> {}
        is DetailsState.Content -> Content(currentState.networkRequestDetailsItem)
        is DetailsState.Error -> Error()
    }
}

@Composable
private fun Content(networkRequestDetailsItem: NetworkRequestDetailsItem) {
    Text(
        text = networkRequestDetailsItem.url,
        style = MaterialTheme.typography.body2,
        color = Color.DarkGray
    )
}

@Composable
private fun Error() {
    Text(
        text = "Error",
        style = MaterialTheme.typography.body2,
        color = Color.DarkGray
    )
}

@Composable
@Preview
private fun ContentPreview() {
    Content(
        networkRequestDetailsItem = NetworkRequestDetailsItem(
            url = "www.example.com"
        )
    )
}