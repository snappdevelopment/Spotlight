package com.snad.feature.newmovies.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class NewMovies(
    @Json(name = "results")
    val movies : List<ListMovie>,
    val page : Int,
    val total_results : Int,
    val dates : Dates,
    val total_pages : Int
)

@JsonClass(generateAdapter = true)
internal data class Dates (
    val maximum : String,
    val minimum : String
)

@JsonClass(generateAdapter = true)
internal data class ListMovie(
    val popularity : Double,
    val vote_count : Int,
    val video : Boolean,
    val poster_path : String?,
    val id : Int,
    val adult : Boolean,
    val backdrop_path : String?,
    val original_language : String,
    val original_title : String,
    val genre_ids : List<Int>,
    val title : String,
    val vote_average : Double,
    val overview : String,
    val release_date : String?
)