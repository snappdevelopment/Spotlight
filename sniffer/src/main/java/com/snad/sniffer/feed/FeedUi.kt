package com.snad.sniffer.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun FeedUi(
    viewModel: FeedViewModel,
    onBackClick: () -> Unit
) {
    val state = viewModel.state.collectAsState(emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
    ) {
        itemsIndexed(state.value) { index, item ->
            Request(
                dateTime = item.dateTime,
                method = item.method,
                statusCode = item.statusCode,
                duration = item.duration,
                url = item.url,
            )

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
    dateTime: String,
    method: String,
    statusCode: Int,
    duration: String,
    url: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Text(
                text = dateTime,
                style = MaterialTheme.typography.body2,
                color = Color.DarkGray,

            )

            Spacer(modifier = Modifier.width(8.dp))

            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = method,
                style = MaterialTheme.typography.body2,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.width(8.dp))

            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = statusCode.toString(),
                style = MaterialTheme.typography.body2,
                color = if(statusCode >= 400) Color.Red else Color.DarkGray
            )

            Spacer(modifier = Modifier.width(8.dp))

            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = duration,
                style = MaterialTheme.typography.body2,
                color = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = url,
            style = MaterialTheme.typography.body1,
            color = Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun RequestPreview() {
    Request(
        dateTime = "15:13:24",
        method = "GET",
        statusCode = 200,
        duration = "25ms",
        url = "www.example.com/api?id=5"
    )
}