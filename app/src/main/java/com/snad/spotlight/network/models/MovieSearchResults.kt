package com.snad.spotlight.network.models

class SearchResults(
    val page: Int,
    val total_results: Int,
    val total_pages: Int,
    val results: List<ListMovie>
)
