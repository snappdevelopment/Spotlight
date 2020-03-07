package com.snad.spotlight.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewMovies(
    @Json(name = "results")
    val movies : List<NewMovie>,
    val page : Int,
    val total_results : Int,
    val dates : Dates,
    val total_pages : Int
)

@JsonClass(generateAdapter = true)
data class Dates (
    val maximum : String,
    val minimum : String
)