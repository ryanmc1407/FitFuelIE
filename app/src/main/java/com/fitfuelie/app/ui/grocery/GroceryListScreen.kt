package com.fitfuelie.app.ui.grocery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitfuelie.app.data.DatabaseProvider
import com.fitfuelie.app.data.model.GroceryCategory
import com.fitfuelie.app.data.model.GroceryItem
import com.fitfuelie.app.data.repository.GroceryRepository

@Composable
fun GroceryListScreen() {
    val context = LocalContext.current
    val viewModel: GroceryViewModel = viewModel {
        val database = DatabaseProvider.getDatabase(context)
        val repository = GroceryRepository(database.groceryItemDao())
        GroceryViewModel(repository)
    }
    val groceryItems by viewModel.groceryItems.collectAsState(initial = emptyList())
    val showPurchased by viewModel.filterPurchased.collectAsState()
    val itemsByCategory by viewModel.itemsByCategory.collectAsState(initial = emptyMap())

    var showAddItemDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddItemDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Grocery Item")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Grocery List") },
                actions = {
                    IconButton(onClick = { viewModel.setFilterPurchased(!showPurchased) }) {
                        Icon(
                            imageVector = if (showPurchased) Icons.Default.Clear else Icons.Default.Check,
                            contentDescription = if (showPurchased) "Show unpurchased" else "Show purchased"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Filter indicator
            Text(
                text = if (showPurchased) "Purchased Items" else "Shopping List",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Category summary
            if (!showPurchased) {
                CategorySummaryRow(itemsByCategory)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Grocery items list
            if (groceryItems.isEmpty()) {
                EmptyGroceryView(showPurchased)
            } else {
                GroceryItemsList(
                    items = groceryItems.sortedBy { it.category },
                    onTogglePurchased = { viewModel.toggleItemPurchased(it) },
                    onDeleteItem = { viewModel.deleteGroceryItem(it) }
                )

                if (showPurchased && groceryItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { viewModel.clearPurchasedItems() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Clear All Purchased Items")
                    }
                }
            }
        }

        if (showAddItemDialog) {
            AddGroceryItemDialog(
                onDismiss = { showAddItemDialog = false },
                onItemAdded = { item ->
                    viewModel.addGroceryItem(item)
                    showAddItemDialog = false
                }
            )
        }
    }
}

@Composable
fun CategorySummaryRow(itemsByCategory: Map<GroceryCategory, Int>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        GroceryCategory.entries.forEach { category ->
            val count = itemsByCategory[category] ?: 0
            if (count > 0) {
                Text(
                    text = "${category.name}: $count",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun GroceryItemsList(
    items: List<GroceryItem>,
    onTogglePurchased: (GroceryItem) -> Unit,
    onDeleteItem: (GroceryItem) -> Unit
) {
    LazyColumn {
        val itemsByCategory = items.groupBy { it.category }

        itemsByCategory.forEach { (category, categoryItems) ->
            item {
                Text(
                    text = category.name.replace("_", " "),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }

            items(categoryItems) { item ->
                GroceryItemRow(
                    item = item,
                    onTogglePurchased = { onTogglePurchased(item) },
                    onDelete = { onDeleteItem(item) }
                )
            }
        }
    }
}

@Composable
fun GroceryItemRow(
    item: GroceryItem,
    onTogglePurchased: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPurchased)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = item.quantity,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (item.notes != null) {
                    Text(
                        text = item.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onTogglePurchased) {
                    Icon(
                        imageVector = if (item.isPurchased) Icons.Default.Check else Icons.Default.Clear,
                        contentDescription = if (item.isPurchased) "Mark as unpurchased" else "Mark as purchased",
                        tint = if (item.isPurchased) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete item"
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyGroceryView(showPurchased: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (showPurchased) "No purchased items" else "Your shopping list is empty",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        if (!showPurchased) {
            Text(
                text = "Tap the + button to add your first grocery item",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun AddGroceryItemDialog(
    onDismiss: () -> Unit,
    onItemAdded: (GroceryItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(GroceryCategory.OTHER) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Grocery Item") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity (e.g., 2 lbs, 1 dozen)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category selector
                Text("Category", style = MaterialTheme.typography.bodyMedium)
                GroceryCategory.entries.forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                        Text(
                            category.name.replace("_", " "),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val item = GroceryItem(
                        name = name,
                        quantity = quantity,
                        category = selectedCategory,
                        notes = if (notes.isBlank()) null else notes
                    )
                    onItemAdded(item)
                },
                enabled = name.isNotBlank()
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
