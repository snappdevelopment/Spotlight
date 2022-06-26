package com.snad.sniffer.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun FeedUi(
    viewModelFactory: FeedViewModel.Factory,
    onBackClick: () -> Unit,
    onRequestClick: (Long) -> Unit
) {
    val viewModel: FeedViewModel = viewModel(factory = viewModelFactory)
    val state = viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
    ) {
        itemsIndexed(state.value) { index, item ->
            Request(item = item, onRequestClick = onRequestClick)

            if(index < state.value.size - 1) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
private fun Request(
    item: NetworkRequestListItem,
    onRequestClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = item !is NetworkRequestListItem.Ongoing) { onRequestClick(item.id) }
            .alpha(alpha = if (item is NetworkRequestListItem.Ongoing) 0.3F else 1F)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Text(
                text = item.dateTime,
                style = MaterialTheme.typography.body2,
                color = Color.DarkGray,
            )

            Spacer(modifier = Modifier.width(8.dp))

            VerticalDivider()

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = item.method,
                style = MaterialTheme.typography.body2,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.width(8.dp))

            VerticalDivider()

            Spacer(modifier = Modifier.width(8.dp))

            when(item) {
                is NetworkRequestListItem.Ongoing -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(14.dp),
                        color = Color.DarkGray,
                        strokeWidth = 2.dp
                    )
                }
                is NetworkRequestListItem.Finished -> {
                    Text(
                        text = item.statusCode.toString(),
                        style = MaterialTheme.typography.body2,
                        color = if(item.statusCode >= 400) Color.Red else Color.DarkGray
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    VerticalDivider()

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = item.duration,
                        style = MaterialTheme.typography.body2,
                        color = Color.DarkGray
                    )
                }
                is NetworkRequestListItem.Failed -> {}
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.url,
            style = MaterialTheme.typography.body1,
            color = Color.Black,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        if(item is NetworkRequestListItem.Failed) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.errorMessage,
                style = MaterialTheme.typography.body2,
                color = Color.Red,
            )
        }
    }
}

@Composable
private fun VerticalDivider() {
    Divider(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp),
        color = Color.DarkGray
    )
}

@Preview(backgroundColor = 0xFFFFFF, showBackground = true)
@Composable
private fun OngoingRequestPreview() {
    Request(
        NetworkRequestListItem.Ongoing(
            id = 0L,
            dateTime = "15:13:24",
            method = "GET",
            url = "www.example.com/api?id=5&text=Lorem ipsum dolor sit amet, consectetur adipiscing" +
                    " elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        ),
        onRequestClick = {}
    )
}

@Preview(backgroundColor = 0xFFFFFF, showBackground = true)
@Composable
private fun FinishedRequestPreview() {
    Request(
        NetworkRequestListItem.Finished(
            id = 0L,
            dateTime = "15:13:24",
            method = "GET",
            statusCode = 200,
            duration = "25ms",
            url = "www.example.com/api?id=5&text=Lorem ipsum dolor sit amet, consectetur adipiscing " +
                    "elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        ),
        onRequestClick = {}
    )
}

@Preview(backgroundColor = 0xFFFFFF, showBackground = true)
@Composable
private fun FailedRequestPreview() {
    Request(
        NetworkRequestListItem.Failed(
            id = 0L,
            dateTime = "15:13:24",
            method = "GET",
            url = "www.example.com/api?id=5&text=Lorem ipsum dolor sit amet, consectetur adipiscing" +
                    " elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            errorMessage = "This is an error message, because the api call failed",
        ),
        onRequestClick = {}
    )
}