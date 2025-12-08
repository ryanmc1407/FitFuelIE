package com.example.fitfuelie.data.repository

import com.example.fitfuelie.data.local.dao.GroceryItemDao
import com.example.fitfuelie.data.local.entity.GroceryItem
import com.example.fitfuelie.data.model.GroceryCategory
import kotlinx.coroutines.flow.Flow

/**
 * GroceryItemRepository
 * 
 * Handles all operations related to the grocery list.
 * Users can add items, organize by category, and check them off when purchased.
 */
class GroceryItemRepository(
    private val groceryItemDao: GroceryItemDao
) {

    /**
     * Gets all grocery items, sorted by category and name
     */
    fun getAllGroceryItems(): Flow<List<GroceryItem>> = groceryItemDao.getAllGroceryItems()

    /**
     * Gets items in a specific category
     * Useful for filtering, like "show me only meats"
     */
    fun getGroceryItemsByCategory(category: GroceryCategory): Flow<List<GroceryItem>> =
        groceryItemDao.getGroceryItemsByCategory(category)


    fun getUnpurchasedItems(): Flow<List<GroceryItem>> = groceryItemDao.getUnpurchasedItems()


    fun getPurchasedItems(): Flow<List<GroceryItem>> = groceryItemDao.getPurchasedItems()


    suspend fun getGroceryItemById(id: Long): GroceryItem? = groceryItemDao.getGroceryItemById(id)


    suspend fun insertGroceryItem(item: GroceryItem): Long = groceryItemDao.insertGroceryItem(item)


    suspend fun updateGroceryItem(item: GroceryItem) = groceryItemDao.updateGroceryItem(item)


    suspend fun deleteGroceryItem(item: GroceryItem) = groceryItemDao.deleteGroceryItem(item)


    suspend fun deleteGroceryItemById(id: Long) = groceryItemDao.deleteGroceryItemById(id)

    /**
     * Updates the purchase status of an item
     * Called when the user checks off an item in the store
     */
    suspend fun updatePurchaseStatus(id: Long, isPurchased: Boolean) =
        groceryItemDao.updatePurchaseStatus(id, isPurchased)

    /**
     * Deletes all purchased items
     *
     */
    suspend fun clearPurchasedItems() = groceryItemDao.clearPurchasedItems()

    /**
     * Counts how many items still need to be purchased
     *
     */
    fun getUnpurchasedCount(): Flow<Int> = groceryItemDao.getUnpurchasedCount()
}
