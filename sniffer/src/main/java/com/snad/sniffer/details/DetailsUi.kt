package com.snad.sniffer.details

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.Navigation
import kotlin.math.max

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
private fun Content(
    networkRequestDetailsItem: NetworkRequestDetailsItem
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Headline(text = "URL")

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = networkRequestDetailsItem.url,
            style = MaterialTheme.typography.body2,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Headline(text = "Request Headers")

        Spacer(modifier = Modifier.height(8.dp))

        Headers(headers = networkRequestDetailsItem.requestHeaders)

        Spacer(modifier = Modifier.height(16.dp))

        Headline(text = "Request Body")

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 2),
            text = networkRequestDetailsItem.requestBody ?: "Empty Request Body",
            style = MaterialTheme.typography.body2,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Headline(text = "Response Headers")

        Spacer(modifier = Modifier.height(8.dp))

        Headers(headers = networkRequestDetailsItem.responseHeaders)

        Spacer(modifier = Modifier.height(16.dp))

        Headline(text = "Response Body")

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 2),
            text = networkRequestDetailsItem.responseBody ?: "Empty Response Body",
            style = MaterialTheme.typography.body2,
            color = Color.DarkGray
        )
    }
}

@Composable
private fun Headline(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.h6,
        color = Color.Black
    )
}

@Composable
private fun Headers(
    headers: Map<String, String>?
) {
    if(headers.isNullOrEmpty()) {
        Text(
            text = "Empty Headers",
            style = MaterialTheme.typography.body2,
            color = Color.DarkGray
        )
    } else {
        headers.forEach {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = it.key,
                    style = MaterialTheme.typography.body2,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    modifier = Modifier.weight(1F),
                    text = it.value,
                    style = MaterialTheme.typography.body2,
                    color = Color.DarkGray
                )
            }
        }
    }
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
            url = "www.example.com",
            requestBody = """
                {
                    "data": [
                        {
                            "id": "62d19ae39874bff5d95efb89",
                            "index": 0,
                            "isActive": false,
                        }
                    ]
                }
            """.trimIndent(),
            responseBody = """
                {
                    "data": [
                        {
                            "id": "62d19ae39874bff5d95efb89",
                            "index": 0,
                            "isActive": false,
                        }
                    ]
                }
            """.trimIndent(),
            requestHeaders = mapOf(
                "Content-Type" to "image/jpeg",
                "Accept-Encoding" to "gzip"
            ),
            responseHeaders = mapOf(
                "Content-Type" to "image/jpeg",
                "Accept-Encoding" to "gzip"
            ),
        )
    )
}