package com.fitfuelie.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val quantity: String, // e.g., "2 lbs", "1 dozen", "500g"
    val category: GroceryCategory,
    val isPurchased: Boolean = false,
    val notes: String? = null
)

enum class GroceryCategory {
    PROTEIN, // Chicken, beef, fish, eggs, etc.
    DAIRY, // Milk, cheese, yogurt
    GRAINS, // Bread, rice, pasta, oats
    FRUITS,
    VEGETABLES,
    FATS_OILS, // Olive oil, nuts, avocado
    BEVERAGES,
    SNACKS,
    CONDIMENTS,
    OTHER
}
