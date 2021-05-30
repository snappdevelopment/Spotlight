package com.snad.feature.moviedetails.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class Videos(
    @Json(name = "results")
    val videos: List<Video>
)

@JsonClass(generateAdapter = true)
internal class Video(
    val id: String,
    val iso_639_1: String,
    val iso_3166_1: String,
    val key: String,
    val name: String,
    val site: String,
    val size: Int,
    val type: String
)