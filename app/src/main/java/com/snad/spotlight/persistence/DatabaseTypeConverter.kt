package com.snad.spotlight.persistence

import androidx.room.TypeConverter
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
}
