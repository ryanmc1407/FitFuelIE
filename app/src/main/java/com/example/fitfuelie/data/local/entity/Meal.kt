package com.example.fitfuelie.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitfuelie.data.model.MealType
import java.util.Date

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: MealType,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val date: Date,
    val notes: String? = null
)
