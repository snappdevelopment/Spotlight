package com.snad.feature.castdetails.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Person(
    val adult: Boolean,
    val also_known_as: List<String>,
    val biography: String,
    val birthday: String?,
    val deathday: String?,
    val gender: Int,
    val homepage: String?,
    val id: Int,
    val imdb_id: String,
    val known_for_department: String,
    val name: String,
    val place_of_birth: String?,
    val popularity: Double,
    val profile_path: String?,

    @Json(name = "movie_credits")
    val person_credits: PersonCredits
)

@JsonClass(generateAdapter = true)
internal data class PersonCredits(
    val cast: List<Cast>,
    val crew: List<Crew>
)

@JsonClass(generateAdapter = true)
internal data class Cast(
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
internal data class Crew(
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