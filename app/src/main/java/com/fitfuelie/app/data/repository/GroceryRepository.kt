package com.fitfuelie.app.data.repository

import com.fitfuelie.app.data.dao.GroceryItemDao
import com.fitfuelie.app.data.model.GroceryCategory
import com.fitfuelie.app.data.model.GroceryItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroceryRepository @Inject constructor(
    private val groceryItemDao: GroceryItemDao
) {
    // Create
    suspend fun insertGroceryItem(item: GroceryItem): Long =
        groceryItemDao.insertGroceryItem(item)

    // Read
    fun getAllGroceryItems(): Flow<List<GroceryItem>> =
        groceryItemDao.getAllGroceryItems()

    fun getGroceryItemsByPurchaseStatus(purchased: Boolean): Flow<List<GroceryItem>> =
        groceryItemDao.getGroceryItemsByPurchaseStatus(purchased)

    fun getGroceryItemsByCategory(category: GroceryCategory): Flow<List<GroceryItem>> =
        groceryItemDao.getGroceryItemsByCategory(category)

    suspend fun getGroceryItemById(itemId: Long): GroceryItem? =
        groceryItemDao.getGroceryItemById(itemId)

    // Update
    suspend fun updateGroceryItem(item: GroceryItem) =
        groceryItemDao.updateGroceryItem(item)

    suspend fun markItemAsPurchased(item: GroceryItem, purchased: Boolean) {
        groceryItemDao.updateGroceryItem(item.copy(isPurchased = purchased))
    }

    suspend fun markAllItemsAsPurchased(purchased: Boolean) =
        groceryItemDao.markAllItemsAsPurchased(purchased)

    // Delete
    suspend fun deleteGroceryItem(item: GroceryItem) =
        groceryItemDao.deleteGroceryItem(item)

    suspend fun deletePurchasedItems() =
        groceryItemDao.deletePurchasedItems()
}
