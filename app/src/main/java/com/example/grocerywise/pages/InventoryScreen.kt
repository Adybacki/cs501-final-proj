package com.example.grocerywise.pages

import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import coil.compose.AsyncImage
import com.example.grocerywise.AuthViewModel
import com.example.grocerywise.R
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InventoryScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
) {
    // Get the current user's UID.
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // List to store inventory items from Firebase.
    val inventoryItems = remember { mutableStateListOf<InventoryItem>() }

    // NEW: which item is awaiting the “add to shopping list?” prompt?
    var pendingDelete by remember { mutableStateOf<InventoryItem?>(null) }
    val info = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

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
//    when (info) {
//        WindowWidthSizeClass.COMPACT -> {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
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
                        fontFamily = FontFamily(Font(resId = R.font.defaultfont)),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    FloatingActionButton(
                        onClick = {
                            authViewModel.signout()
                        },
                        containerColor = Color.Red,
                        modifier = Modifier.width(55.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = R.drawable.signout),
                                contentDescription = "signout",
                            )
                        }
                    }
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
                                    fontFamily = FontFamily(Font(resId = R.font.defaultfont)),
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
                    itemsIndexed(inventoryItems, key = { _, item -> item.id!! }) { index, item ->
                        val dismissState =
                            rememberDismissState(
                                // leave initialValue at default Idle
                                confirmStateChange = { state: DismissValue ->
                                    if (state == DismissValue.DismissedToStart) {
                                        // 1. Remove locally
                                        // inventoryItems.remove(item)
                                        // 2. Tell Firebase to delete
                                        // userId?.let { uid ->
                                        //    FirebaseDatabaseManager.removeInventoryItem(uid, item.id!!) { success, _ ->
                                        //        if (!success) Log.e("InventoryScreen", "Failed to remove ${item.id}")
                                        //    }
                                        // }
                                        // ask the user if they want to add it to the shopping list:
                                        pendingDelete = item
                                        true
                                    } else {
                                        false
                                    }
                                },
                            )

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(DismissDirection.EndToStart),
                            background = {
                                // Determine the background color:
                                // – RED when the user is actively swiping to the left (EndToStart)
                                // – TRANSPARENT otherwise
                                val bgColor =
                                    when (dismissState.dismissDirection) {
                                        DismissDirection.EndToStart -> Color.Red
                                        else -> Color.Transparent
                                    }

                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(bgColor)
                                        // only add padding when showing the red background
                                        .padding(end = if (bgColor == Color.Red) 20.dp else 0.dp),
                                    contentAlignment = Alignment.CenterEnd,
                                ) {
                                    if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.White,
                                        )
                                    }
                                }
                            },
                            dismissContent = {
                                Row(
                                    Modifier.fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    // show image if you have one
                                    item.imageUrl?.let { url ->
                                        AsyncImage(
                                            model = url,
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp).clip(CircleShape),
                                        )
                                        Spacer(Modifier.width(8.dp))
                                    }
                                    Text(item.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {

                                        IconButton(onClick = {
                                            val updated = item.copy(quantity = item.quantity + 1)
                                            inventoryItems[index] = updated
                                            userId?.let { FirebaseDatabaseManager.updateInventoryItem(it, updated) }
                                        }) { Icon(Icons.Default.Add, contentDescription = "Increase") }

                                        Text("${item.quantity}", fontSize = 20.sp, modifier = Modifier.padding(8.dp))

                                        IconButton(onClick = {
                                            if (item.quantity > 1) {
                                                val updated = item.copy(quantity = item.quantity - 1)
                                                inventoryItems[index] = updated
                                                userId?.let { uid ->
                                                    FirebaseDatabaseManager.updateInventoryItem(uid, updated)
                                                }
                                            } else {
                                                // 1) Remove locally first (so immediately disappears from UI)
//                                                inventoryItems.remove(item)

                                                // 2) Tell Firebase to delete
//                                                userId?.let { uid ->
//                                                    FirebaseDatabaseManager.removeInventoryItem(uid, item.id!!) { success, _ ->
//                                                        if (!success) {
//                                                            Log.e("InventoryScreen", "Failed to remove ${item.id}, rolling back")
//                                                            // rollback so the user sees it again
//                                                            inventoryItems.add(index, item)
//                                                        }
//                                                    }
//                                                }
                                                pendingDelete = item
                                            }
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Decrease")
                                        }
                                    }
                                }
                            },
                        )
                    }
                }
                // only show when an item is pending
                pendingDelete?.let { itemToDelete ->
                    AlertDialog(
                        onDismissRequest = { pendingDelete = null },
                        title = { Text("Remove “${itemToDelete.name}”?") },
                        text = { Text("Do you want to put it on your shopping list before deleting?") },
                        confirmButton = {
                            TextButton(onClick = {
                                // 1) add to grocery list
//                                FirebaseDatabaseManager.addGroceryListItem(
//                                    userId!!,
//                                    GroceryItem(
//                                        name = itemToDelete.name,
//                                        quantity = itemToDelete.quantity,
//                                        estimatedPrice = 0.0, // or pull from your InventoryItem if you track price
//                                        imageUrl = itemToDelete.imageUrl,
//                                        upc = itemToDelete.upc,
//                                    ),
//                                )
                                // 2) delete from inventory
                                FirebaseDatabaseManager.removeInventoryItem(userId!!, itemToDelete.id!!) { success, _ ->
                                    if (!success) Log.e("InventoryScreen", "delete failed")
                                }
                                // 3) navigate to your AddItemScreen, passing fields as parameters
                                navController.navigate(
                                    "add_item?" +
                                        "productName=${Uri.encode(itemToDelete.name)}" +
                                        "&productUpc=${Uri.encode(itemToDelete.upc ?: "")}" +
                                        "&productPrice=" + // leave blank or supply default
                                        "&productImageUri=", // leave blank or supply default
                                )

                                pendingDelete = null
                            }) {
                                Text("Yes")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                // just delete without adding to list
                                FirebaseDatabaseManager.removeInventoryItem(userId!!, itemToDelete.id!!) { _, _ -> }
                                pendingDelete = null
                            }) {
                                Text("No")
                            }
                        },
                    )
                //}
           // }
        }

//        WindowWidthSizeClass.EXPANDED -> {
//            Row(
//                modifier =
//                    Modifier
//                        .fillMaxSize(1f)
//                        .padding(vertical = 5.dp, horizontal = 20.dp),
//                horizontalArrangement = Arrangement.spacedBy(10.dp),
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Column(
//                    modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    Text(
//                        "Inventory",
//                        fontSize = 30.sp,
//                        fontFamily = FontFamily(Font(resId = R.font.defaultfont)),
//                        fontWeight = FontWeight.W600,
//                    )
//                    Spacer(modifier = Modifier.height(10.dp))
//
//                    Row(
//                        modifier =
//                            Modifier
//                                .fillMaxWidth()
//                                .height(20.dp),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                    ) {
//                        Text(
//                            "Current Usage:",
//                            fontSize = 18.sp,
//                            fontFamily = FontFamily(Font(resId = R.font.defaultfont)),
//                            color = Color(0xFF29b34e),
//                        )
//                        Row(modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight()) {
//                            percentageList.forEachIndexed { index, (name, per) ->
//                                val color = hexToColor(shuffledColors[index % shuffledColors.size])
//                                Box(
//                                    modifier =
//                                        Modifier
//                                            .fillMaxHeight()
//                                            .fillMaxWidth(per)
//                                            .background(color = color),
//                                ) {
//                                }
//                            }
//                        }
//                    }
//// lazy Column for lists
//                    @OptIn(ExperimentalMaterialApi::class)
//                    LazyColumn {
//                        itemsIndexed(inventoryItems, key = { _, item -> item.id!! }) { index, item ->
//                            // 1) remember a SwipeToDismiss state
//                            val dismissState =
//                                rememberDismissState(
//                                    confirmStateChange = { state ->
//                                        if (state == DismissValue.DismissedToStart) {
//                                            // 1. Remove locally
//                                            inventoryItems.remove(item)
//                                            // 2. Remove remotely
//                                            userId?.let { uid ->
//                                                FirebaseDatabaseManager.removeInventoryItem(uid, item.id!!) { success, _ ->
//                                                    if (!success) Log.e("InventoryScreen", "Failed to remove ${item.id}")
//                                                }
//                                            }
//                                            true
//                                        } else {
//                                            false
//                                        }
//                                    },
//                                )
//
//                            SwipeToDismiss(
//                                state = dismissState,
//                                directions = setOf(DismissDirection.EndToStart),
//                                background = { /* unchanged */ },
//                                dismissContent = {
//                                    Row(
//                                        Modifier.fillMaxWidth(),
//                                        horizontalArrangement = Arrangement.SpaceBetween,
//                                        verticalAlignment = Alignment.CenterVertically,
//                                    ) {
//                                        // … your AsyncImage + Text …
//
//                                        // 2) “–” button:
//                                        IconButton(onClick = {
//                                            if (item.quantity > 1) {
//                                                val updated = item.copy(quantity = item.quantity - 1)
//                                                inventoryItems[index] = updated
//                                                userId?.let { uid ->
//                                                    FirebaseDatabaseManager.updateInventoryItem(uid, updated)
//                                                }
//                                            } else {
//                                                // 1) Remove locally first (so immediately disappears from UI)
//                                                inventoryItems.remove(item)
//
//                                                // 2) Tell Firebase to delete
//                                                userId?.let { uid ->
//                                                    FirebaseDatabaseManager.removeInventoryItem(uid, item.id!!) { success, _ ->
//                                                        if (!success) {
//                                                            Log.e("InventoryScreen", "Failed to remove ${item.id}, rolling back")
//                                                            // rollback so the user sees it again
//                                                            inventoryItems.add(index, item)
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }) {
//                                            Icon(Icons.Default.Delete, contentDescription = "Decrease")
//                                        }
//
//                                        Text("${item.quantity}", fontSize = 20.sp, modifier = Modifier.padding(8.dp))
//
//                                        IconButton(onClick = {
//                                            val updated = item.copy(quantity = item.quantity + 1)
//                                            inventoryItems[index] = updated
//                                            userId?.let { FirebaseDatabaseManager.updateInventoryItem(it, updated) }
//                                        }) {
//                                            Icon(Icons.Default.Add, contentDescription = "Increase")
//                                        }
//                                    }
//                                },
//                            )
//                        }
//                    }
//                }
//
//                Column(
//                    modifier = Modifier.fillMaxWidth(1f).fillMaxHeight().padding(top = 16.dp),
//                    verticalArrangement = Arrangement.Top,
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    FloatingActionButton(
//                        onClick = {
//                            authViewModel.signout()
//                        },
//                        containerColor = Color.Red,
//                        modifier = Modifier.fillMaxWidth(0.4f),
//                    ) {
//                        Icon(
//                            modifier = Modifier.size(24.dp),
//                            painter = painterResource(id = R.drawable.signout),
//                            contentDescription = "signout",
//                        )
//                    }
//                }
//            }
//        }
    }
}
