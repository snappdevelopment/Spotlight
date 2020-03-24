package com.snad.spotlight.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MovieSearchResults(
    val page: Int,
    val total_results: Int,
    val total_pages: Int,
    val results: List<ListMovie>
)
