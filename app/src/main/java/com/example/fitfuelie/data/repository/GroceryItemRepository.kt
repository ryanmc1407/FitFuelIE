package com.example.fitfuelie.data.repository

import com.example.fitfuelie.data.local.dao.GroceryItemDao
import com.example.fitfuelie.data.local.entity.GroceryItem
import com.example.fitfuelie.data.model.GroceryCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroceryItemRepository @Inject constructor(
    private val groceryItemDao: GroceryItemDao
) {

    fun getAllGroceryItems(): Flow<List<GroceryItem>> = groceryItemDao.getAllGroceryItems()

    fun getGroceryItemsByCategory(category: GroceryCategory): Flow<List<GroceryItem>> =
        groceryItemDao.getGroceryItemsByCategory(category)

    fun getUnpurchasedItems(): Flow<List<GroceryItem>> = groceryItemDao.getUnpurchasedItems()

    fun getPurchasedItems(): Flow<List<GroceryItem>> = groceryItemDao.getPurchasedItems()

    suspend fun getGroceryItemById(id: Long): GroceryItem? = groceryItemDao.getGroceryItemById(id)

    suspend fun insertGroceryItem(item: GroceryItem): Long = groceryItemDao.insertGroceryItem(item)

    suspend fun updateGroceryItem(item: GroceryItem) = groceryItemDao.updateGroceryItem(item)

    suspend fun deleteGroceryItem(item: GroceryItem) = groceryItemDao.deleteGroceryItem(item)

    suspend fun deleteGroceryItemById(id: Long) = groceryItemDao.deleteGroceryItemById(id)

    suspend fun updatePurchaseStatus(id: Long, isPurchased: Boolean) =
        groceryItemDao.updatePurchaseStatus(id, isPurchased)

    suspend fun clearPurchasedItems() = groceryItemDao.clearPurchasedItems()

    fun getUnpurchasedCount(): Flow<Int> = groceryItemDao.getUnpurchasedCount()
}
