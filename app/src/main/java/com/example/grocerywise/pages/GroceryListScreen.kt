package com.example.grocerywise.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.grocerywise.AuthViewModel
import com.example.grocerywise.models.GroceryItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroceryListScreen(
    authViewModel: AuthViewModel,
    groceryItems: SnapshotStateList<GroceryItem>,
    onUpdateItem: (GroceryItem) -> Unit,
    onDeleteItem: (String) -> Unit,
    onAddCheckedToInventory: () -> Unit
) {
    val totalCost = groceryItems
        .sumOf { it.estimatedPrice * it.quantity }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Grocery List", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = { authViewModel.signout() }) { Text("Sign out") }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items = groceryItems, key = { it.uid }) { item ->
                val dismissState = rememberDismissState(
                    confirmStateChange = { state ->
                        if (state == DismissValue.DismissedToStart) {
                            onDeleteItem(item.uid)
                            true
                        } else false
                    }
                )

                //Swipe to remove item feature
                androidx.compose.material.SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    background = {
                        val color = when (dismissState.dismissDirection) {
                            DismissDirection.EndToStart -> Color.Red
                            else -> Color.Transparent
                        }

                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(end = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    dismissContent = {
                        GroceryListItem(
                            item = item,
                            onCheckedChange = { checked ->
                                onUpdateItem(item.copy(isChecked = checked))
                            },
                            onEditClicked = {
                                //TODO: edit to show bottom sheet or dialog to update quantity/price
                            }
                        )
                    }
                )
            }
        }

        Spacer(Modifier.padding(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total: $${"%.2f".format(totalCost)}", fontWeight = FontWeight.Bold)
            Button(onClick = { onAddCheckedToInventory() }) {
                Text("Add to Inventory")
            }
        }
    }
}


@Composable
fun GroceryListItem(
    item: GroceryItem,
    onCheckedChange: (Boolean) -> Unit,
    onEditClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (item.imageUrl != null) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(48.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.SemiBold)
            Text("Qty: ${item.quantity} · $${"%.2f".format(item.estimatedPrice)}")
        }
        IconButton(onClick = onEditClicked) {
            Icon(Icons.Default.MoreVert, contentDescription = "Edit item")
        }
    }
}