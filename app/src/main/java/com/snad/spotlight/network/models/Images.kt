package com.snad.spotlight.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Images(
    val backdrops: List<Backdrop>,
    val posters: List<Poster>
)

@JsonClass(generateAdapter = true)
class Backdrop(
    val aspect_ratio: Double,
    val file_path: String,
    val height: Int,
    val iso_639_1: String?,
    val vote_average: Double,
    val vote_count: Int,
    val width: Int
)

@JsonClass(generateAdapter = true)
class Poster(
    val aspect_ratio: Double,
    val file_path: String,
    val height: Int,
    val iso_639_1: String?,
    val vote_average: Double,
    val vote_count: Int,
    val width: Int
)
