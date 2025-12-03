package com.example.fitfuelie.data.local.dao

import androidx.room.*
import com.example.fitfuelie.data.local.entity.GroceryItem
import com.example.fitfuelie.data.model.GroceryCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryItemDao {

    @Query("SELECT * FROM grocery_items ORDER BY category ASC, name ASC")
    fun getAllGroceryItems(): Flow<List<GroceryItem>>

    @Query("SELECT * FROM grocery_items WHERE category = :category ORDER BY name ASC")
    fun getGroceryItemsByCategory(category: GroceryCategory): Flow<List<GroceryItem>>

    @Query("SELECT * FROM grocery_items WHERE isPurchased = 0 ORDER BY category ASC, name ASC")
    fun getUnpurchasedItems(): Flow<List<GroceryItem>>

    @Query("SELECT * FROM grocery_items WHERE isPurchased = 1 ORDER BY category ASC, name ASC")
    fun getPurchasedItems(): Flow<List<GroceryItem>>

    @Query("SELECT * FROM grocery_items WHERE id = :id")
    suspend fun getGroceryItemById(id: Long): GroceryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryItem(item: GroceryItem): Long

    @Update
    suspend fun updateGroceryItem(item: GroceryItem)

    @Delete
    suspend fun deleteGroceryItem(item: GroceryItem)

    @Query("DELETE FROM grocery_items WHERE id = :id")
    suspend fun deleteGroceryItemById(id: Long)

    @Query("UPDATE grocery_items SET isPurchased = :isPurchased WHERE id = :id")
    suspend fun updatePurchaseStatus(id: Long, isPurchased: Boolean)

    @Query("DELETE FROM grocery_items WHERE isPurchased = 1")
    suspend fun clearPurchasedItems()

    @Query("SELECT COUNT(*) FROM grocery_items WHERE isPurchased = 0")
    fun getUnpurchasedCount(): Flow<Int>
}
