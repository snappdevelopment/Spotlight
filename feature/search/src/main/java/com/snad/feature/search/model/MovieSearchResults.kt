package com.snad.feature.search.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class MovieSearchResults(
    val page: Int,
    val total_results: Int,
    val total_pages: Int,
    val results: List<ListMovie>
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