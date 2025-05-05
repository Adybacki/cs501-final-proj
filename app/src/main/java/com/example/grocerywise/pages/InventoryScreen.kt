package com.example.grocerywise.pages

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.grocerywise.ProfileViewModel
import com.example.grocerywise.R
import com.example.grocerywise.data.FirebaseDatabaseManager
import com.example.grocerywise.models.GroceryItem
import com.example.grocerywise.models.InventoryItem
import com.example.grocerywise.ui.theme.Cream
import com.example.grocerywise.ui.theme.Sage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import com.example.grocerywise.ApiClient
import com.example.grocerywise.ProductLookupRequest
import com.example.grocerywise.ProductLookupResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
    onAvatarClick: () -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val inventoryItems = remember { mutableStateListOf<InventoryItem>() }
    var pendingDelete by remember { mutableStateOf<InventoryItem?>(null) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Listen to Firebase – use snapshot.key as unique ID
    LaunchedEffect(userId) {
        if (userId == null) {
            inventoryItems.clear()
            return@LaunchedEffect
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fresh = snapshot.children.mapNotNull { ds ->
                    ds.getValue(InventoryItem::class.java)
                        ?.copy(id = ds.key)
                }
                inventoryItems.apply {
                    clear()
                    addAll(fresh)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("InventoryScreen", "listenToInventory failed: ${error.message}")
            }
        }
        FirebaseDatabaseManager.listenToInventory(userId, listener)
    }

    // Compute usage-bar
    val total = inventoryItems.sumOf { it.quantity }
    val percList = if (total == 0) emptyList() else
        inventoryItems.map { it.name to (it.quantity.toFloat() / total) }
    val shuffled = remember { colors.shuffled() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top row: title + avatar
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Pantry",
                fontSize = 30.sp,
                fontFamily = FontFamily(Font(R.font.nunitobold)),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            // User account icon and button
            if (!isLandscape) {
                AsyncImage(
                    model = viewModel<ProfileViewModel>().avatarUrl.collectAsState().value,
                    contentDescription = "Avatar",
                    placeholder = painterResource(R.drawable.default_avatar),
                    error = painterResource(R.drawable.default_avatar),
                    fallback = painterResource(R.drawable.default_avatar),
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onAvatarClick() }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Usage bar
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(50)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                percList.forEachIndexed { i, (name, frac) ->
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(frac)
                            .background(hexToColor(shuffled[i % shuffled.size])),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            name,
                            fontSize = 10.sp,
                            fontFamily = FontFamily(Font(R.font.nunitobold)),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
            }

        // Placeholder text for empty inventory list
        if (inventoryItems.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Your pantry is empty!\nAdd items using the + button.",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.nunitobold))
                )
            }
        }

        // Display all inventoryList items
        else {
            LazyColumn {
                itemsIndexed(
                    inventoryItems,
                    key = { _, item -> item.id!! }
                ) { _, item ->
                    InventoryListItem(
                        item = item,
                        userId = userId,
                        onDeleteRequest = { pendingDelete = it }
                    )
                }
            }
        }

        // Delete confirmation dialog to add removed items back into grocery list
        pendingDelete?.let { target ->
            AlertDialog(
                onDismissRequest = { pendingDelete = null },
                title = {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Remove “${target.name}”?",
                            fontFamily = FontFamily(Font(resId = R.font.nunitobold))
                        )
                        IconButton(onClick = { pendingDelete = null }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                            )
                        }
                    }
                },
                text = { Text("Add it to your grocery list before deleting?") },
                confirmButton = {
                    TextButton(onClick = {
                        userId?.let { uid ->
                            val grocery = GroceryItem(
                                id             = target.id,
                                name           = target.name,
                                quantity       = 1,
                                upc            = target.upc,
                                imageUrl       = target.imageUrl,
                                estimatedPrice = 0.0
                            )
                            if (target.upc != null) {
                                getPrices(target.upc) { price ->
                                    if (price != null) {
                                        grocery.estimatedPrice = price.toDouble()
                                    }
                                    FirebaseDatabaseManager.addGroceryListItem(uid, grocery)
                                    FirebaseDatabaseManager.removeInventoryItem(uid, target.id!!)
                                }
                            } else {
                                FirebaseDatabaseManager.addGroceryListItem(uid, grocery)
                                FirebaseDatabaseManager.removeInventoryItem(uid, target.id!!)
                            }
                        }
                        pendingDelete = null

                    }, colors = ButtonDefaults.textButtonColors(contentColor = Sage)) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        userId?.let { FirebaseDatabaseManager.removeInventoryItem(it, target.id!!) }
                        pendingDelete = null
                    }, colors = ButtonDefaults.textButtonColors(contentColor = Sage)) {
                        Text("No")
                    }
                },
                containerColor = Cream,
            )
        }
    }
}

// Helper function to get updated price info when adding item from inventory to grocerylist
fun getPrices(upc: String, onResult: (String?) -> Unit) {
    val request = ProductLookupRequest(upc)
    Log.d("API Request", "Sending UPC: $request")

    ApiClient.apiService.lookupProduct(request).enqueue(
        object : Callback<ProductLookupResponse> {
            @SuppressLint("DefaultLocale")
            override fun onResponse(
                call: Call<ProductLookupResponse>,
                response: Response<ProductLookupResponse>,
            ) {
                if (response.isSuccessful) {
                    val product = response.body()?.items?.firstOrNull()

                    if (product != null) {
                        val prices = product.prices
                        val averagePrice = prices.map { it.price }.average()
                        val averageRoundedPrice = String.format("%.2f", averagePrice)
                        onResult(averageRoundedPrice)
                    } else {
                        onResult(null)
                    }
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<ProductLookupResponse>, t: Throwable) {
                onResult(null)
            }
        }
    )
}


// inventory list item composable
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InventoryListItem(
    item: InventoryItem,
    userId: String?,
    onDeleteRequest: (InventoryItem) -> Unit
) {
    val dismissState = rememberDismissState { value ->
        if (value == DismissValue.DismissedToStart) {
            onDeleteRequest(item)
            true
        } else false
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            val bg = if (dismissState.dismissDirection == DismissDirection.EndToStart)
                Color.Red else Color.Transparent
            Box(
                Modifier
                    .fillMaxSize()
                    .background(bg)
                    .padding(end = if (bg == Color.Red) 20.dp else 0.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                }
            }
        }
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Cream),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                item.imageUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                }

                Text(
                    item.name,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                        .padding(1.dp) // padding inside the border
                ) {
                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            val updated = item.copy(quantity = item.quantity + 1)
                            userId?.let {
                                FirebaseDatabaseManager.updateInventoryItem(
                                    it,
                                    updated
                                )
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }

                        Text(
                            item.quantity.toString(),
                            fontSize = 20.sp,
                        )

                        IconButton(onClick = {
                            if (item.quantity > 1) {
                                val updated = item.copy(quantity = item.quantity - 1)
                                userId?.let {
                                    FirebaseDatabaseManager.updateInventoryItem(
                                        it,
                                        updated
                                    )
                                }
                            } else {
                                onDeleteRequest(item)
                            }
                        }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.remove),
                                contentDescription = "Minus quantity"
                            )
                        }
                    }
                }
            }
        }
    }
}

