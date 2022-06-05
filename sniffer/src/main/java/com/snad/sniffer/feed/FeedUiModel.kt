package com.snad.sniffer.feed

internal data class FeedUiModel(
    val dateTime: String,
    val method: String,
    val statusCode: Int,
    val duration: String,
    val url: String
)