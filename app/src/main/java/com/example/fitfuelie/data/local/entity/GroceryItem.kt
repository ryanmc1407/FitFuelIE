package com.example.fitfuelie.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitfuelie.data.model.GroceryCategory

/**
 * GroceryItem Entity
 * 
 * This represents an item on the user's grocery list.
 * Users can add items, organize them by category, and check them off when purchased.
 * 
 * I made this to help users plan their meals by keeping track of what they need to buy.
 */
@Entity(tableName = "grocery_items")
data class GroceryItem(
    // Unique ID for each grocery item
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Name of the item (e.g., "Chicken Breast", "Bananas", "Milk")
    val name: String,
    
    // How much to buy, I use String because quantities can be different formats
    // Using String gives me flexibility instead of forcing a number
    val quantity: String,
    
    // What category it belongs to, fruits, vegetables, meat, dairy.
    // This helps organize the shopping list
    val category: GroceryCategory,
    
    // Whether the user has bought this item yet
    // Starts as false, user checks it off when they get it
    val isPurchased: Boolean = false,
    
    // notes like "organic only" or "check date"
    val notes: String? = null
)
