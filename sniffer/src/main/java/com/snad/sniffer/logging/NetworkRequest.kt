package com.snad.sniffer.logging

internal data class NetworkRequest(
    val timestampMillis: Long,
    val url: String,
    val method: String,
    val statusCode: Int,
    val durationMillis: Long,
    val requestBody: String?,
    val responseBody: String?
)