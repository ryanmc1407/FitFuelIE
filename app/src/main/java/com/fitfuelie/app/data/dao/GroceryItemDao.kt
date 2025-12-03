package com.fitfuelie.app.data.dao

import androidx.room.*
import com.fitfuelie.app.data.model.GroceryCategory
import com.fitfuelie.app.data.model.GroceryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryItem(item: GroceryItem): Long

    @Update
    suspend fun updateGroceryItem(item: GroceryItem)

    @Delete
    suspend fun deleteGroceryItem(item: GroceryItem)

    @Query("SELECT * FROM grocery_items WHERE id = :itemId")
    suspend fun getGroceryItemById(itemId: Long): GroceryItem?

    @Query("SELECT * FROM grocery_items ORDER BY category, name")
    fun getAllGroceryItems(): Flow<List<GroceryItem>>

    @Query("SELECT * FROM grocery_items WHERE isPurchased = :purchased ORDER BY category, name")
    fun getGroceryItemsByPurchaseStatus(purchased: Boolean): Flow<List<GroceryItem>>

    @Query("SELECT * FROM grocery_items WHERE category = :category ORDER BY name")
    fun getGroceryItemsByCategory(category: GroceryCategory): Flow<List<GroceryItem>>

    @Query("DELETE FROM grocery_items WHERE isPurchased = 1")
    suspend fun deletePurchasedItems()

    @Query("UPDATE grocery_items SET isPurchased = :purchased")
    suspend fun markAllItemsAsPurchased(purchased: Boolean)
}
