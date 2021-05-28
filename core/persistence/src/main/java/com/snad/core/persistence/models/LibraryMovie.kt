package com.snad.core.persistence.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.snad.core.persistence.DatabaseTypeConverter
import com.squareup.moshi.JsonClass
import java.util.Calendar

@Entity(tableName = "movies")
data class LibraryMovie(
    @PrimaryKey
    val id : Int,
    val added_at: Calendar?,
    val updated_at: Calendar?,
    val adult : Boolean,
    val backdrop_path : String?,
    @TypeConverters(DatabaseTypeConverter::class)
    val backdrops: List<Image>,
    val budget : Int,
    @TypeConverters(DatabaseTypeConverter::class)
    val cast: List<CastMember>,
    val genres : String,
    val has_been_watched: Boolean,
    val imdb_id : String?,
    val overview : String?,
    val popularity : Double,
    val poster_path : String?,
    val release_date : String,
    val revenue : Int,
    @TypeConverters(DatabaseTypeConverter::class)
    val reviews: List<Review>,
    val runtime : Int?,
    val tagline : String?,
    val title : String,
    val trailer: String?,
    val video : Boolean,
    val vote_average : Double,
    val vote_count : Int
)

@JsonClass(generateAdapter = true)
class Review(
    val id: String,
    val author: String,
    val content: String,
    val url: String
)

@JsonClass(generateAdapter = true)
class Image(
    val aspect_ratio: Double,
    val file_path: String,
    val height: Int,
    val iso_639_1: String?,
    val vote_average: Double,
    val vote_count: Int,
    val width: Int
)

@JsonClass(generateAdapter = true)
class CastMember(
    val cast_id: Int,
    val character: String,
    val credit_id: String,
    val gender: Int?,
    val id: Int,
    val name: String,
    val order: Int,
    val profile_path: String?
)