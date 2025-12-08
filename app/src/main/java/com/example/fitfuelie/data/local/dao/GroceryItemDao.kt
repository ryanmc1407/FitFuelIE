package com.example.fitfuelie.data.local.dao

import androidx.room.*
import com.example.fitfuelie.data.local.entity.GroceryItem
import com.example.fitfuelie.data.model.GroceryCategory
import kotlinx.coroutines.flow.Flow

/**
 * GroceryItemDao
 * 
 * Handles all database operations for grocery items.
 * This lets users create shopping lists and check off items as they buy them.
 * 
 * I organized the queries to filter by category and purchase status because
 * that's how users typically want to see their shopping list.
 */
@Dao
interface GroceryItemDao {

    /**
     * Gets all grocery items, sorted by category then name
     * This makes the list organized and easy to read
     */
    @Query("SELECT * FROM grocery_items ORDER BY category ASC, name ASC")
    fun getAllGroceryItems(): Flow<List<GroceryItem>>

    /**
     * Gets items in a specific category
     * Useful for filtering, like "show me only fruits"
     */
    @Query("SELECT * FROM grocery_items WHERE category = :category ORDER BY name ASC")
    fun getGroceryItemsByCategory(category: GroceryCategory): Flow<List<GroceryItem>>

    // Gets only items that haven't been purchased yet

    @Query("SELECT * FROM grocery_items WHERE isPurchased = 0 ORDER BY category ASC, name ASC")
    fun getUnpurchasedItems(): Flow<List<GroceryItem>>

    /**
     * Gets only items that have been purchased
     *
     */
    @Query("SELECT * FROM grocery_items WHERE isPurchased = 1 ORDER BY category ASC, name ASC")
    fun getPurchasedItems(): Flow<List<GroceryItem>>

    /**
     * Gets a single item by ID
     */
    @Query("SELECT * FROM grocery_items WHERE id = :id")
    suspend fun getGroceryItemById(id: Long): GroceryItem?

    /**
     * Inserts a new grocery item
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryItem(item: GroceryItem): Long

    /**
     * Updates an existing grocery item
     */
    @Update
    suspend fun updateGroceryItem(item: GroceryItem)

    /**
     * Deletes a grocery item
     */
    @Delete
    suspend fun deleteGroceryItem(item: GroceryItem)

    /**
     * Deletes an item by ID
     */
    @Query("DELETE FROM grocery_items WHERE id = :id")
    suspend fun deleteGroceryItemById(id: Long)

    /**
     * Updates just the purchase status
     * Used when the user checks off an item in the store
     *
     */
    @Query("UPDATE grocery_items SET isPurchased = :isPurchased WHERE id = :id")
    suspend fun updatePurchaseStatus(id: Long, isPurchased: Boolean)

    /**
     * Deletes all purchased items
     * Useful for cleaning up the list after shopping is done
     */
    @Query("DELETE FROM grocery_items WHERE isPurchased = 1")
    suspend fun clearPurchasedItems()

    /**
     * Counts how many items still need to be purchased
     *
     */
    @Query("SELECT COUNT(*) FROM grocery_items WHERE isPurchased = 0")
    fun getUnpurchasedCount(): Flow<Int>
}
