package com.example.fitfuelie.data.model

/**
 * Intensity Enum
 * 
 * Represents how intense a workout is.
 * Used when creating training sessions to indicate difficulty level.
 * This helps calculate calories burned and determine recovery time needed.
 */
enum class Intensity {
    LOW,      // Light activity, easy pace
    MODERATE, // Moderate effort, steady pace
    HIGH,     // Hard effort, challenging
    MAXIMUM   // Maximum effort, very intense
}
