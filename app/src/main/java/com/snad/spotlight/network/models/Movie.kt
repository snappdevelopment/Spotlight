package com.snad.spotlight.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Movie (
	val adult : Boolean,
	val backdrop_path : String?,
	val belongs_to_collection : MovieCollection?,
	val budget : Int,
	val genres : List<Genre>,
	val homepage : String?,
	val id : Int,
	val imdb_id : String?,
	val original_language : String,
	val original_title : String,
	val overview : String?,
	val popularity : Double,
	val poster_path : String?,
	val production_companies : List<ProductionCompany>,
	val production_countries : List<ProductionCountry>,
	val release_date : String,
	val revenue : Int,
	val runtime : Int?,
	val spoken_languages : List<SpokenLanguage>,
	val status : String,
	val tagline : String?,
	val title : String,
	val video : Boolean,
	val vote_average : Double,
	val vote_count : Int,

	val images: Images,
	val videos: Videos,
	val credits: Credits
)

@JsonClass(generateAdapter = true)
data class Genre (
	val id : Int,
	val name : String
)

@JsonClass(generateAdapter = true)
data class MovieCollection (
	val id : Int,
	val name : String,
	val poster_path : String?,
	val backdrop_path : String?
)

@JsonClass(generateAdapter = true)
data class ProductionCompany (
	val id : Int,
	val logo_path : String?,
	val name : String,
	val origin_country : String
)

@JsonClass(generateAdapter = true)
data class ProductionCountry (
	val iso_3166_1 : String,
	val name : String
)

@JsonClass(generateAdapter = true)
data class SpokenLanguage (
	val iso_639_1 : String,
	val name : String
)