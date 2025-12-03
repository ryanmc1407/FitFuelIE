package com.fitfuelie.app.ui.grocery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitfuelie.app.data.DatabaseProvider
import com.fitfuelie.app.data.model.GroceryCategory
import com.fitfuelie.app.data.model.GroceryItem
import com.fitfuelie.app.data.repository.GroceryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class GroceryViewModel(
    private val groceryRepository: GroceryRepository
) : ViewModel() {

    // UI state
    private val _filterPurchased = MutableStateFlow(false)
    val filterPurchased: StateFlow<Boolean> = _filterPurchased

    // All grocery items
    val allGroceryItems: Flow<List<GroceryItem>> = groceryRepository.getAllGroceryItems()

    // Filtered grocery items based on purchase status
    val groceryItems: Flow<List<GroceryItem>> = combine(
        allGroceryItems,
        _filterPurchased
    ) { items, showPurchased ->
        if (showPurchased) {
            items.filter { it.isPurchased }
        } else {
            items.filter { !it.isPurchased }
        }
    }

    // Items count by category
    val itemsByCategory: Flow<Map<GroceryCategory, Int>> = allGroceryItems.combine(
        groceryRepository.getAllGroceryItems()
    ) { items, _ ->
        items.groupBy { it.category }.mapValues { it.value.size }
    }

    fun setFilterPurchased(showPurchased: Boolean) {
        _filterPurchased.value = showPurchased
    }

    fun addGroceryItem(item: GroceryItem) {
        viewModelScope.launch {
            groceryRepository.insertGroceryItem(item)
        }
    }

    fun updateGroceryItem(item: GroceryItem) {
        viewModelScope.launch {
            groceryRepository.updateGroceryItem(item)
        }
    }

    fun toggleItemPurchased(item: GroceryItem) {
        viewModelScope.launch {
            groceryRepository.markItemAsPurchased(item, !item.isPurchased)
        }
    }

    fun deleteGroceryItem(item: GroceryItem) {
        viewModelScope.launch {
            groceryRepository.deleteGroceryItem(item)
        }
    }

    fun clearPurchasedItems() {
        viewModelScope.launch {
            groceryRepository.deletePurchasedItems()
        }
    }

    fun markAllItemsAsPurchased(purchased: Boolean) {
        viewModelScope.launch {
            groceryRepository.markAllItemsAsPurchased(purchased)
        }
    }

    suspend fun getGroceryItemById(itemId: Long): GroceryItem? {
        return groceryRepository.getGroceryItemById(itemId)
    }
}
