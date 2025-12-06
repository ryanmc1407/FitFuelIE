package com.example.fitfuelie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfuelie.data.local.entity.GroceryItem
import com.example.fitfuelie.data.model.GroceryCategory
import com.example.fitfuelie.data.repository.GroceryItemRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class GroceryListViewModel(
    private val groceryRepository: GroceryItemRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<GroceryCategory?>(null)
    val selectedCategory: StateFlow<GroceryCategory?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val allGroceryItems = groceryRepository.getAllGroceryItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredGroceryItems = combine(
        allGroceryItems,
        _selectedCategory
    ) { items, category ->
        if (category != null) {
            items.filter { it.category == category }
        } else {
            items
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unpurchasedItems = groceryRepository.getUnpurchasedItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val purchasedItems = groceryRepository.getPurchasedItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unpurchasedCount = groceryRepository.getUnpurchasedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun selectCategory(category: GroceryCategory?) {
        _selectedCategory.value = category
    }

    fun addGroceryItem(
        name: String,
        quantity: String,
        category: GroceryCategory,
        notes: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val item = GroceryItem(
                    name = name,
                    quantity = quantity,
                    category = category,
                    notes = notes
                )
                groceryRepository.insertGroceryItem(item)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateGroceryItem(item: GroceryItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                groceryRepository.updateGroceryItem(item)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteGroceryItem(item: GroceryItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                groceryRepository.deleteGroceryItem(item)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun togglePurchaseStatus(itemId: Long, isPurchased: Boolean) {
        viewModelScope.launch {
            groceryRepository.updatePurchaseStatus(itemId, isPurchased)
        }
    }

    fun clearPurchasedItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                groceryRepository.clearPurchasedItems()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getGroceryItemById(id: Long): Flow<GroceryItem?> {
        return flow {
            val item = groceryRepository.getGroceryItemById(id)
            emit(item)
        }
    }
}
