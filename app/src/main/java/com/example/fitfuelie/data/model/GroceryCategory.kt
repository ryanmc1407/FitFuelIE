package com.example.fitfuelie.data.model

/**
 * GroceryCategory Enum
 * 
 * Represents categories for organizing grocery items.
 * Used in the grocery list to group items together, making shopping easier.
 * Items are sorted by category in the list view.
 */
enum class GroceryCategory {
    PROTEIN,    // Meat, fish, eggs, beans, etc.
    DAIRY,      // Milk, cheese, yogurt, etc.
    GRAINS,     // Bread, rice, pasta, etc.
    FRUITS,     // Apples, bananas, berries, etc.
    VEGETABLES, // Carrots, broccoli, lettuce, etc.
    OTHER       // Anything that doesn't fit the above categories
}
