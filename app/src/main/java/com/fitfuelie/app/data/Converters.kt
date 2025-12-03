package com.fitfuelie.app.data

import androidx.room.TypeConverter
import com.fitfuelie.app.data.model.DietaryPreference
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun fromDietaryPreferences(value: Set<DietaryPreference>?): String? {
        return value?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toDietaryPreferences(value: String?): Set<DietaryPreference>? {
        return value?.split(",")?.mapNotNull {
            try {
                DietaryPreference.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }?.toSet()
    }
}
