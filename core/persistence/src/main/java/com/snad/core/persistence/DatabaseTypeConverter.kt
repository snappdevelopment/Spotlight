package com.snad.core.persistence

import androidx.room.TypeConverter
import com.snad.core.persistence.models.CastMember
import com.snad.core.persistence.models.Image
import com.snad.core.persistence.models.Review
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type
import java.util.Calendar


class DatabaseTypeConverter {
    @TypeConverter
    fun calendarFromTimestamp(timeInMillis: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return calendar
    }

    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar): Long {
        return calendar.timeInMillis
    }

    @TypeConverter
    fun backdropsToJson(backdrops: List<Image>): String {
        val moshi = Moshi.Builder().build()
        val posterType: Type = Types.newParameterizedType(
            List::class.java,
            Image::class.java
        )
        val backdropsAdapter: JsonAdapter<List<Image>> = moshi.adapter(posterType)

        return backdropsAdapter.toJson(backdrops)
    }

    @TypeConverter
    fun backdropsFromJson(backdrops: String): List<Image>? {
        val moshi = Moshi.Builder().build()
        val backdropType: Type = Types.newParameterizedType(
            List::class.java,
            Image::class.java
        )
        val backdropsAdapter: JsonAdapter<List<Image>> = moshi.adapter(backdropType)

        return backdropsAdapter.fromJson(backdrops)
    }

    @TypeConverter
    fun castToJson(cast: List<CastMember>): String {
        val moshi = Moshi.Builder().build()
        val castMemberType: Type = Types.newParameterizedType(
            List::class.java,
            CastMember::class.java
        )
        val castAdapter: JsonAdapter<List<CastMember>> = moshi.adapter(castMemberType)

        return castAdapter.toJson(cast)
    }

    @TypeConverter
    fun castFromJson(cast: String): List<CastMember>? {
        val moshi = Moshi.Builder().build()
        val castType: Type = Types.newParameterizedType(
            List::class.java,
            CastMember::class.java
        )
        val castAdapter: JsonAdapter<List<CastMember>> = moshi.adapter(castType)

        return castAdapter.fromJson(cast)
    }

    @TypeConverter
    fun reviewsToJson(reviews: List<Review>): String {
        val moshi = Moshi.Builder().build()
        val reviewType: Type = Types.newParameterizedType(
            List::class.java,
            Review::class.java
        )
        val reviewsAdapter: JsonAdapter<List<Review>> = moshi.adapter(reviewType)

        return reviewsAdapter.toJson(reviews)
    }

    @TypeConverter
    fun reviewsFromJson(reviews: String): List<Review>? {
        val moshi = Moshi.Builder().build()
        val reviewType: Type = Types.newParameterizedType(
            List::class.java,
            Review::class.java
        )
        val reviewsAdapter: JsonAdapter<List<Review>> = moshi.adapter(reviewType)

        return reviewsAdapter.fromJson(reviews)
    }
}
