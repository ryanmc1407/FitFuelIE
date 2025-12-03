package com.example.fitfuelie.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.fitfuelie.data.local.entity.GroceryItem
import com.example.fitfuelie.data.model.GroceryCategory
import com.example.fitfuelie.ui.viewmodel.GroceryListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListScreen(
    viewModel: GroceryListViewModel,
    onNavigateBack: () -> Unit
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val groceryItems by viewModel.filteredGroceryItems.collectAsState()
    val unpurchasedCount by viewModel.unpurchasedCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grocery List") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (unpurchasedCount > 0) {
                        IconButton(onClick = { viewModel.clearPurchasedItems() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear purchased")
                        }
                    }
                    IconButton(onClick = { showAddItemDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add item")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category filter
            CategoryFilter(
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.selectCategory(it) }
            )

            // Items list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (groceryItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (selectedCategory != null) {
                                    "No items in this category.\nTap the + button to add your first item!"
                                } else {
                                    "Your grocery list is empty.\nTap the + button to add your first item!"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    val groupedItems = groceryItems.groupBy { it.category }

                    groupedItems.forEach { (category, items) ->
                        item {
                            CategoryHeader(category = category)
                        }

                        items(items.sortedBy { it.name }) { item ->
                            GroceryItemCard(
                                item = item,
                                onTogglePurchased = { purchased ->
                                    viewModel.togglePurchaseStatus(item.id, purchased)
                                }
                            )
                        }
                    }
                }
            }

            // Summary footer
            if (unpurchasedCount > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$unpurchasedCount items remaining to purchase",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    // Add item dialog
    if (showAddItemDialog) {
        AddGroceryItemDialog(
            onDismiss = { showAddItemDialog = false },
            onSave = { name, quantity, category, notes ->
                viewModel.addGroceryItem(name, quantity, category, notes)
                showAddItemDialog = false
            }
        )
    }

    // Loading overlay
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun CategoryFilter(
    selectedCategory: GroceryCategory?,
    onCategorySelected: (GroceryCategory?) -> Unit
) {
    val categories = listOf(null) + GroceryCategory.values() // null = All categories

    ScrollableTabRow(
        selectedTabIndex = categories.indexOf(selectedCategory),
        modifier = Modifier.fillMaxWidth()
    ) {
        categories.forEach { category ->
            Tab(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = category?.name?.replace("_", " ")?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "All"
                    )
                }
            )
        }
    }
}

@Composable
private fun CategoryHeader(category: GroceryCategory) {
    Text(
        text = category.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun GroceryItemCard(
    item: GroceryItem,
    onTogglePurchased: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPurchased)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = if (item.isPurchased)
                        MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = TextDecoration.LineThrough
                        )
                    else
                        MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = item.quantity,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                item.notes?.let { notes ->
                    if (notes.isNotBlank()) {
                        Text(
                            text = notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Checkbox(
                checked = item.isPurchased,
                onCheckedChange = onTogglePurchased
            )
        }
    }
}

@Composable
private fun AddGroceryItemDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, GroceryCategory, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(GroceryCategory.OTHER) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Grocery Item") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., 2 lbs, 1 dozen, 500g") }
                )

                // Category selection
                Text("Category", style = MaterialTheme.typography.bodyMedium)
                GroceryCategory.values().forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory = category },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(category.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() })
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalNotes = notes.takeIf { it.isNotBlank() }
                    onSave(name, quantity, selectedCategory, finalNotes)
                },
                enabled = name.isNotBlank() && quantity.isNotBlank()
            ) {
                Text("Add Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
