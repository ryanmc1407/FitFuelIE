package com.example.fitfuelie.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitfuelie.data.model.Intensity
import com.example.fitfuelie.data.model.TrainingType
import java.util.Date

/**
 * TrainingSession Entity
 * 
 * This represents a workout session that the user has planned or completed.
 * I store all the details so they can track their training over time.
 * 
 * I used this to build the training calendar feature where users can see
 * their workout schedule and mark sessions as complete.
 */
@Entity(tableName = "training_sessions")
data class TrainingSession(
    // Unique ID for each training session
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Name of the workout (e.g., "Morning Run", "Leg Day", "Gym Workout")
    val title: String,
    
    // What kind of training - cardio, strength, flexibility, etc.
    val type: TrainingType,
    
    // How hard the workout is, light, moderate, or intense
    // This helps me calculate calories burned and recovery time
    val intensity: Intensity,
    
    // How long the workout lasts, in minutes
    // I use Int because I don't need seconds precision
    val duration: Int,
    
    // When the workout is scheduled or was completed
    val date: Date,
    
    // Whether the user actually did the workout
    // Starts as false, user marks it true when they finish
    val isCompleted: Boolean = false,
    
    //  notes about the workout
    //  like "Felt great today!" or "Skipped last 2 sets"
    val notes: String? = null
)
