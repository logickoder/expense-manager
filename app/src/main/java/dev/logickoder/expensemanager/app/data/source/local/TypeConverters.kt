package dev.logickoder.expensemanager.app.data.source.local

import androidx.room.TypeConverter
import java.time.LocalDate


class TypeConverters {
    @TypeConverter
    fun Long?.toLocalDate() = if (this == null) {
        null
    } else LocalDate.ofEpochDay(this)

    @TypeConverter
    fun LocalDate?.toLong() = this?.toEpochDay()
}