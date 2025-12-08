package com.example.fitfuelie.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitfuelie.data.model.MealType
import java.util.Date

/**
 * Meal Entity
 * 
 * This is a data class that represents a meal in my database.
 * I use Room  to store meals, and this class tells Room
 *
 * 
 * I used a data class because it automatically gives me useful functions like
 * equals(), hashCode(), and toString().
 */
@Entity(tableName = "meals")
data class Meal(
    // Primary key, this is like the meal's unique ID number
    // autoGenerate means Room will automatically create a new ID when I add a meal
    // I don't have to manually count meals or worry about duplicate IDs
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // The name of the meal (e.g., "Grilled Chicken Salad")
    val name: String,
    
    // What type of meal this is - breakfast, lunch, dinner, or snack
    // I use an enum (MealType) so I can only use valid meal types
    val type: MealType,
    
    // Nutritional information
    val calories: Int,      // Total calories in the meal
    val protein: Float,    // Protein in grams (float because I might have decimals like 25.5g)
    val carbs: Float,      // Carbohydrates in grams
    val fat: Float,        // Fat in grams
    
    // When this meal was eaten, I use Date so I can track meals by day
    val date: Date,
    
    //  notes, the ? means this can be null if the user doesn't add notes
    //  like "Added extra cheese" or "Half portion"
    val notes: String? = null
)
