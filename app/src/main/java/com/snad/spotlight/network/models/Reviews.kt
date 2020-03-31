package com.snad.spotlight.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Reviews(
    @Json(name = "results")
    val reviews: List<Review>,
    val page: Int,
    val total_pages: Int,
    val total_results: Int
)

@JsonClass(generateAdapter = true)
class Review(
    val id: String,
    val author: String,
    val content: String,
    val url: String
)