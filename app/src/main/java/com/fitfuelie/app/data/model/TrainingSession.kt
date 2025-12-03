package com.fitfuelie.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "training_sessions")
data class TrainingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val trainingType: TrainingType,
    val duration: Int, // minutes
    val intensity: TrainingIntensity,
    val dateTime: LocalDateTime,
    val completed: Boolean = false,
    val notes: String? = null
)

enum class TrainingType {
    STRENGTH,
    CARDIO,
    FLEXIBILITY,
    SPORTS_SPECIFIC, // GAA training
    RECOVERY,
    OTHER
}

enum class TrainingIntensity {
    LOW,
    MODERATE,
    HIGH,
    MAXIMUM
}
