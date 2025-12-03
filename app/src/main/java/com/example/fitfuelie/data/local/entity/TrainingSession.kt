package com.example.fitfuelie.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitfuelie.data.model.Intensity
import com.example.fitfuelie.data.model.TrainingType
import java.util.Date

@Entity(tableName = "training_sessions")
data class TrainingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: TrainingType,
    val intensity: Intensity,
    val duration: Int, // in minutes
    val date: Date,
    val isCompleted: Boolean = false,
    val notes: String? = null
)
