package com.example.grocerywise.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerywise.AuthViewModel
import com.example.grocerywise.data.FirebaseDatabaseManager
import com.example.grocerywise.models.InventoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

// Dummy color list for usage bar visualization.
val colors: List<String> =
    listOf(
        "#FFFF00",
        "#FFC0CB",
        "#00FF00",
        "#0000FF",
        "#FFA500",
        "#800080",
        "#ADD8E6",
        "#90EE90",
        "#FFB6C1",
        "#FFFFE0",
        "#40E0D0",
        "#E6E6FA",
        "#FF7F50",
        "#98FF98",
        "#FFDAB9",
        "#87CEEB",
        "#32CD32",
        "#FF00FF",
        "#00FFFF",
        "#FFFF33",
        "#FF6EC7",
        "#39FF14",
        "#FF5F1F",
        "#FDFD96",
        "#FFD1DC",
        "#77DD77",
        "#AEC6CF",
        "#B19CD9",
    )

// Convert a hex string to a Color.
fun hexToColor(hex: String): Color {
    val color = hex.removePrefix("#").toLong(16)
    return Color(color or 0xFF000000L)
}

@Composable
fun InventoryScreen(authViewModel: AuthViewModel) {
    // Get the current user's UID.
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // List to store inventory items from Firebase.
    val inventoryItems = remember { mutableStateListOf<InventoryItem>() }

    // Listen for changes in the inventory data from the database.
    LaunchedEffect(userId) {
        if (userId != null) {
            val listener =
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        inventoryItems.clear()
                        // Iterate through each child in the inventory node.
                        snapshot.children.forEach { dataSnap ->
                            val item = dataSnap.getValue(InventoryItem::class.java)
                            if (item != null) {
                                inventoryItems.add(item)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("InventoryScreen", "Data listener error: ${error.message}")
                    }
                }
            FirebaseDatabaseManager.listenToInventory(userId, listener)
        }
    }

    // Compute total quantity for the usage bar.
    val totalQuantity = inventoryItems.sumOf { it.quantity }
    // Create a list of (item name, percentage of total) pairs.
    val percentageList =
        if (totalQuantity == 0) {
            emptyList<Pair<String, Float>>()
        } else {
            inventoryItems.map { it.name to (it.quantity.toFloat() / totalQuantity) }
        }
    // Shuffle colors for visual variety.
    val shuffledColors = remember { colors.shuffled() }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Top bar with title and sign out button.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Inventory",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { authViewModel.signout() }) { Text("Sign out") }
        }
        Spacer(modifier = Modifier.height(20.dp))
        // Display current usage as a horizontal progress bar.
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Current Usage:",
                fontSize = 14.sp,
                color = Color(0xFF29b34e),
            )
            Row(modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight()) {
                percentageList.forEachIndexed { index, (name, per) ->
                    val color = hexToColor(shuffledColors[index % shuffledColors.size])
                    Box(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(per)
                                .background(color = color),
                    ) {
                        Text(
                            text = name,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(2.dp),
                        )
                    }
                }
            }
        }
        // List the inventory items with update buttons.
        LazyColumn {
            itemsIndexed(inventoryItems) { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = item.name, fontSize = 20.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            if (item.quantity > 0) {
                                val updatedItem = item.copy(quantity = item.quantity - 1)
                                inventoryItems[index] = updatedItem
                                if (userId != null) {
                                    FirebaseDatabaseManager.updateInventoryItem(userId, updatedItem)
                                }
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Decrease")
                        }
                        Text(
                            text = "${item.quantity}",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                        IconButton(onClick = {
                            val updatedItem = item.copy(quantity = item.quantity + 1)
                            inventoryItems[index] = updatedItem
                            if (userId != null) {
                                FirebaseDatabaseManager.updateInventoryItem(userId, updatedItem)
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            }
        }
    }
}
