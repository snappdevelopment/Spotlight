package com.snad.spotlight.persistence

import androidx.room.TypeConverter
import com.snad.spotlight.network.models.Backdrop
import com.snad.spotlight.network.models.CastMember
import com.snad.spotlight.network.models.Poster
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
    fun backdropsToJson(backdrops: List<Backdrop>): String {
        val moshi = Moshi.Builder().build()
        val posterType: Type = Types.newParameterizedType(
            List::class.java,
            Backdrop::class.java
        )
        val backdropsAdapter: JsonAdapter<List<Backdrop>> = moshi.adapter(posterType)

        return backdropsAdapter.toJson(backdrops)
    }

    @TypeConverter
    fun backdropsFromJson(backdrops: String): List<Backdrop>? {
        val moshi = Moshi.Builder().build()
        val backdropType: Type = Types.newParameterizedType(
            List::class.java,
            Backdrop::class.java
        )
        val backdropsAdapter: JsonAdapter<List<Backdrop>> = moshi.adapter(backdropType)

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
}
