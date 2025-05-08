package com.example.grocerywise.pages

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grocerywise.ProfileViewModel
import com.example.grocerywise.data.FirebaseStorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroceryListScreen(
    onAvatarClick: () -> Unit
) {

    // Get the current user's UID.
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // List to store inventory items from Firebase.
    val groceryItems = remember { mutableStateListOf<GroceryItem>() }
    val showEditDialog = remember { mutableStateOf<GroceryItem?>(null) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp
    val isTabletWidth = screenWidthDp >= 600

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Listen for changes in the inventory data from the database.
    LaunchedEffect(userId) {
        if (userId != null) {
            val listener =
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        groceryItems.clear()
                        // Iterate through each child in the inventory node.
                        snapshot.children.forEach { dataSnap ->
                            val item = dataSnap.getValue(GroceryItem::class.java)
                            if (item != null) {
                                groceryItems.add(item)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                }
            FirebaseDatabaseManager.listenToGroceryList(userId, listener)
        }
    }

    val totalCost = groceryItems
        .sumOf { it.estimatedPrice * it.quantity }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Grocery List", fontSize = 30.sp, fontWeight = FontWeight.W900, modifier = Modifier.weight(1f).fillMaxWidth(), fontFamily = FontFamily(
                Font(resId = R.font.nunitobold),
            ),)
            // Get the shared ProfileViewModel to read avatarUrl
            val profileViewModel: ProfileViewModel = viewModel()
            val avatarUrl by profileViewModel.avatarUrl.collectAsState()

            // Show avatar instead of text
            if (!isTabletWidth || isLandscape) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Profile Avatar",
                    placeholder = painterResource(R.drawable.default_avatar),
                    error = painterResource(R.drawable.default_avatar),
                    fallback = painterResource(R.drawable.default_avatar),
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable {
                            onAvatarClick()
                        }
                )
            }


            Spacer(modifier = Modifier.height(8.dp))
        }

        // Check all items button
        Button(colors= ButtonDefaults.buttonColors(containerColor = Sage), onClick = {
            groceryItems.forEach { item ->
                val updatedItem = item.copy(isChecked = true)
                item.isChecked = true
                if (userId != null) {
                    FirebaseDatabaseManager.updateGroceryListItem(userId, updatedItem)
                }
            }
        }) {
            Text("Check All Items")
        }

        // display placeholder text if list is empty
        if (groceryItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Your grocery list is empty!\nAdd items using the + button on the bottom right.",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(resId = R.font.nunitobold))
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items = groceryItems, key = { it.id!! }) { item ->
                    val dismissState = rememberDismissState(
                        confirmStateChange = { state ->
                            if (state == DismissValue.DismissedToStart) {
                                FirebaseDatabaseManager.removeGroceryListItem(
                                    userId!!,
                                    item.id!!
                                )
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
                            // display all users grocery list items with swipe to delete funcitonality
                            if (userId != null) {
                                GroceryListItem(
                                    item = item,
                                    onEditClicked = {
                                        showEditDialog.value = item
                                    },
                                    onCheckedChange = { checked ->
                                        val updatedItem = item.copy(isChecked = checked)
                                        FirebaseDatabaseManager.updateGroceryListItem(
                                            userId,
                                            updatedItem
                                        )
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }

        showEditDialog.value?.let { itemToEdit ->
            // Edit grocery item dialog
            EditGroceryItemDialog(
                item = itemToEdit,
                onDismiss = { showEditDialog.value = null },
                onConfirm = { updatedItem ->
                    if (userId != null) {
                        FirebaseDatabaseManager.updateGroceryListItem(userId, updatedItem)
                    }
                    showEditDialog.value = null
                }
            )
        }

        Spacer(Modifier.padding(8.dp))

        Column(horizontalAlignment = Alignment.Start) {
            // Total cost label
            Text("Total: $${"%.2f".format(totalCost)}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))

            // Button to add all checked items to inventory list and make necessary db changes
            Button(
                onClick = {
                    groceryItems.filter { it.isChecked }.forEach { checkedItem ->
                        // 1) Reserve new inventory key
                        val invRef = FirebaseDatabaseManager
                            .getUserInventoryRef(userId!!)
                            .push()
                        val newInvId = invRef.key!!

                        // 2) Build the InventoryItem (no imageUrl yet)
                        val invItem = InventoryItem(
                            id = newInvId,
                            name = checkedItem.name,
                            quantity = checkedItem.quantity,
                            upc = checkedItem.upc,
                            expirationDate = null,
                            imageUrl = null
                        )

                        // 3) Write bare item
                        invRef.setValue(invItem).addOnCompleteListener { task ->
                            if (!task.isSuccessful) return@addOnCompleteListener

                            // 4) Re-upload image if present
                            checkedItem.imageUrl?.let { uriStr ->
                                val uri = Uri.parse(uriStr)
                                if (uri.scheme?.startsWith("http") == true) {
                                    // Download then upload
                                    coroutineScope.launch(Dispatchers.IO) {
                                        try {
                                            val conn = URL(uriStr).openConnection() as HttpURLConnection
                                            conn.inputStream.use { input ->
                                                val tmp = File(context.cacheDir, "$newInvId-temp.jpg")
                                                FileOutputStream(tmp).use { out -> input.copyTo(out) }
                                                FirebaseStorageManager.uploadItemImage(
                                                    userId, "inventory", newInvId, Uri.fromFile(tmp)
                                                ) { dlUrl ->
                                                    dlUrl?.let { FirebaseDatabaseManager.updateInventoryItem(
                                                        userId, invItem.copy(imageUrl = it)
                                                    ) }
                                                }
                                            }
                                        } catch (_: Exception) {
                                            // fallback: keep original URL
                                            FirebaseDatabaseManager.updateInventoryItem(
                                                userId, invItem.copy(imageUrl = uriStr)
                                            )
                                        }
                                    }
                                } else {
                                    // Local URI → direct upload
                                    FirebaseStorageManager.uploadItemImage(
                                        userId, "inventory", newInvId, uri
                                    ) { dlUrl ->
                                        dlUrl?.let { FirebaseDatabaseManager.updateInventoryItem(
                                            userId, invItem.copy(imageUrl = it)
                                        ) }
                                    }
                                }
                            }

                            // 5) Now delete from grocery list (and its Storage image)
                            FirebaseDatabaseManager.removeGroceryListItem(
                                userId, checkedItem.id!!, null
                            )
                        }
                    }
                },
                colors= ButtonDefaults.buttonColors(containerColor = Sage),
            ) {
                Text("Add Checked Items to Inventory")
            }

        }
    }
}


@Composable
fun GroceryListItem(
    item: GroceryItem,
    onEditClicked: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,

) {
    val checkboxChecked by rememberUpdatedState(item.isChecked)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkboxChecked,
                onCheckedChange = { checked ->
                    onCheckedChange(checked)
                }, colors = CheckboxDefaults.colors(uncheckedColor = Sage, checkedColor = Sage)
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (item.imageUrl != null) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
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
}

@Composable
fun EditGroceryItemDialog(
    item: GroceryItem,
    onDismiss: () -> Unit,
    onConfirm: (GroceryItem) -> Unit
) {
    val quantity = remember { mutableStateOf(item.quantity.toString()) }
    val price = remember { mutableStateOf(item.estimatedPrice.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit ${item.name}") },
        text = {
            Column {
                OutlinedTextField(
                    colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Sage,
                    ),
                    value = quantity.value,
                    onValueChange = { newValue ->
                        // Allow only digits and values up to 99.
                        if (newValue.all { it.isDigit() } && (newValue.isEmpty() || newValue.toInt() <= 99)) {
                            quantity.value = newValue
                        }
                    },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                OutlinedTextField(
                    colors =
                        OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Sage,
                            ),
                    value = price.value,
                    onValueChange = { newValue ->
                        // Allow only valid price formats (up to 7 digits and 2 decimal places).
                        val regex = Regex("^\\d{0,7}(\\.\\d{0,2})?$")
                        if (newValue.isEmpty() || regex.matches(newValue)) {
                            price.value = newValue
                        }
                    },
                    label = { Text("Estimated Price")}
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val updated = item.copy(
                    quantity = quantity.value.toIntOrNull() ?: item.quantity,
                    estimatedPrice = price.value.toDoubleOrNull() ?: item.estimatedPrice
                )
                onConfirm(updated)
            }, colors = ButtonDefaults.buttonColors(containerColor = Sage)) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }, containerColor = Cream
    )
}