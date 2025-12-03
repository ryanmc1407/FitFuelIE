package com.example.fitfuelie.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitfuelie.data.model.GroceryCategory

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
