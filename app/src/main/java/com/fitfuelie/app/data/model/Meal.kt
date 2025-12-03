package com.fitfuelie.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val mealType: MealType, // Breakfast, Lunch, Dinner, Snack
    val calories: Int,
    val protein: Double, // grams
    val carbs: Double, // grams
    val fat: Double, // grams
    val fiber: Double = 0.0, // grams
    val sugar: Double = 0.0, // grams
    val sodium: Double = 0.0, // mg
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val notes: String? = null
)

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}
