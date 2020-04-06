package com.snad.spotlight.persistence.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.snad.spotlight.network.models.Backdrop
import com.snad.spotlight.network.models.CastMember
import com.snad.spotlight.network.models.Review
import com.snad.spotlight.network.models.Video
import com.snad.spotlight.persistence.DatabaseTypeConverter
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
    val backdrops: List<Backdrop>,
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