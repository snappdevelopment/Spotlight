package com.snad.spotlight.persistence.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "movies")
data class LibraryMovie(
    @PrimaryKey
    val id : Int,
    val added_at: Calendar,
    val has_been_watched: Boolean,
    val adult : Boolean,
    val backdrop_path : String?,
    val budget : Int,
    val genres : String,
    val imdb_id : String?,
    val overview : String?,
    val popularity : Double,
    val poster_path : String?,
    val release_date : String,
    val revenue : Int,
    val runtime : Int?,
    val tagline : String?,
    val title : String,
    val video : Boolean,
    val vote_average : Double,
    val vote_count : Int
    )