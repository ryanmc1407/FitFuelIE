package com.example.fitfuelie.data.model

/**
 * MealType Enum
 * 
 * Represents the different types of meals a user can log.
 * I use an enum instead of a String because:
 * - It prevents typos (can't accidentally use "breakfast" instead of "BREAKFAST")
 * - The compiler checks that I'm using valid values
 * - It's easier to refactor if I need to change the names
 * 
 * Enums are stored in the database as their name (String)
 */
enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}
