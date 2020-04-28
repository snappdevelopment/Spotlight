package com.snad.spotlight.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PersonCredits(
    val cast: List<Cast>,
    val crew: List<Crew>
)

@JsonClass(generateAdapter = true)
data class Cast(
    val character: String,
    val credit_id: String,
    val release_date: String?,
    val vote_count: Int,
    val video: Boolean,
    val adult: Boolean,
    val vote_average: Double,
    val title: String,
    val genre_ids: List<Int>,
    val original_language: String,
    val original_title: String,
    val popularity: Double,
    val id: Int,
    val backdrop_path: String?,
    val overview: String,
    val poster_path: String?
)

@JsonClass(generateAdapter = true)
data class Crew(
    val id: Int,
    val department: String,
    val original_language: String,
    val original_title: String,
    val job: String,
    val overview: String,
    val vote_count: Int,
    val video: Boolean,
    val poster_path: String?,
    val backdrop_path: String?,
    val title: String,
    val popularity: Double,
    val genre_ids: List<Int>,
    val vote_average: Double,
    val adult: Boolean,
    val release_date: String?,
    val credit_id: String
)