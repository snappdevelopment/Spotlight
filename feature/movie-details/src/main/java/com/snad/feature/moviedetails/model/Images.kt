package com.snad.feature.moviedetails.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class Images(
    val backdrops: List<Backdrop>,
    val posters: List<Poster>
)

@JsonClass(generateAdapter = true)
internal class Backdrop(
    val aspect_ratio: Double,
    val file_path: String,
    val height: Int,
    val iso_639_1: String?,
    val vote_average: Double,
    val vote_count: Int,
    val width: Int
)

@JsonClass(generateAdapter = true)
internal class Poster(
    val aspect_ratio: Double,
    val file_path: String,
    val height: Int,
    val iso_639_1: String?,
    val vote_average: Double,
    val vote_count: Int,
    val width: Int
)
