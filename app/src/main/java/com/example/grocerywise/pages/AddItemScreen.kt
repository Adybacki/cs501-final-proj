package com.example.grocerywise.pages

import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.grocerywise.R
import com.example.grocerywise.data.FirebaseDatabaseManager
import com.example.grocerywise.models.GroceryItem
import com.example.grocerywise.models.InventoryItem
import com.example.grocerywise.ui.theme.Sage
import com.example.grocerywise.data.FirebaseStorageManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


@Composable
fun AddItemScreen(
    navController: NavController,
    productName: String?,
    productUpc: String? = null,
    productPrice: String? = null,
    productImageUri: String? = null,
) {
    // Pre-populate fields if product details are provided.
    val itemName = remember { mutableStateOf(productName ?: "") }
    val quantity = remember { mutableStateOf("1") }
    val priceEstimate = remember { mutableStateOf(productPrice ?: "") }
    val upcCode = remember { mutableStateOf(productUpc ?: "") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(productImageUri?.let { Uri.parse(it) }) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp
    val isTabletWidth = screenWidthDp >= 600

    // Launcher for picking an image.
    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            selectedImageUri.value = uri
        }

    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // For downloading API images off the network
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Add Item", fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily(
            Font(resId = R.font.nunitobold)
        ))

        OutlinedTextField(
            value = itemName.value,
            onValueChange = {
                if (it.length <= 33) {
                    itemName.value = it
                }
            },
            label = { Text("Item Name", fontFamily = FontFamily(
                Font(resId = R.font.nunito))) },
            modifier = Modifier.fillMaxWidth(),
            colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Sage,
            ),
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = priceEstimate.value,
                onValueChange = { newValue ->
                    // Allow only valid price formats (up to 7 digits and 2 decimal places).
                    val regex = Regex("^\\d{0,7}(\\.\\d{0,2})?$")
                    if (newValue.isEmpty() || regex.matches(newValue)) {
                        priceEstimate.value = newValue
                    }
                },
                label = { Text("Price Estimate Per Item", fontFamily = FontFamily(
                    Font(resId = R.font.nunito))) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(2f / 3f),
                colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Sage,
                ),
            )
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = quantity.value,
                onValueChange = { newValue ->
                    // Allow only digits and values up to 99.
                    if (newValue.all { it.isDigit() } && (newValue.isEmpty() || newValue.toInt() <= 99)) {
                        quantity.value = newValue
                    }
                },
                label = { Text("Quantity", fontFamily = FontFamily(
                    Font(resId = R.font.nunito))) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Sage,
                ),
            )
        }

        Row ( modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically ) {
            OutlinedTextField(
                value = upcCode.value,
                onValueChange = { newValue ->
                    // Allow only digits and a maximum of 12 characters.
                    if (newValue.all { it.isDigit() } && newValue.length <= 12) {
                        upcCode.value = newValue
                    }
                },
                label = { Text("UPC Code", fontFamily = FontFamily(
                    Font(resId = R.font.nunito))) }, colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Sage,
                ),
            )
            Button(onClick = { getProductDetails(upcCode.value, navController)}, modifier = Modifier.padding(4.dp), colors= ButtonDefaults.buttonColors(containerColor = Sage),) {
                Icon(Icons.Default.Search, contentDescription = "Search UPC")
            }
        }

        Button(onClick = { imagePickerLauncher.launch("image/*") }, colors= ButtonDefaults.buttonColors(containerColor = Sage),) {
            Text("Upload Photo")
        }

        selectedImageUri.value?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected Image",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Button(
                onClick = {
                    if (itemName.value.isNotEmpty() && quantity.value.isNotEmpty() && userId != null) {
                        // 1) Reserve a new DB key for the inventory item
                        val inventoryRef = FirebaseDatabaseManager
                            .getUserInventoryRef(userId)
                            .push()
                        val itemId = inventoryRef.key ?: return@Button

                        // 2) Create the item without imageUrl
                        val newItem = InventoryItem(
                            id = itemId,
                            name = itemName.value,
                            quantity = quantity.value.toIntOrNull() ?: 0,
                            upc = upcCode.value,
                            expirationDate = null,
                            imageUrl = null
                        )

                        // 3) Write the item to Realtime Database
                        inventoryRef.setValue(newItem)
                            .addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Toast
                                        .makeText(context, "Failed to add item: ${task.exception?.message}", Toast.LENGTH_SHORT)
                                        .show()
                                    return@addOnCompleteListener
                                }

                                // 4) Upload image (local URI or API URL)
                                selectedImageUri.value?.let { uri ->
                                    if (uri.scheme?.startsWith("http") == true) {
                                        // Download from API then upload to Storage
                                        coroutineScope.launch(Dispatchers.IO) {
                                            try {
                                                val conn = URL(uri.toString()).openConnection() as HttpURLConnection
                                                conn.inputStream.use { input ->
                                                    val tmp = File(context.cacheDir, "$itemId-api.jpg")
                                                    FileOutputStream(tmp).use { out -> input.copyTo(out) }
                                                    val fileUri = Uri.fromFile(tmp)
                                                    FirebaseStorageManager.uploadItemImage(userId, "inventory", itemId, fileUri) { dl ->
                                                        dl?.let { url ->
                                                            FirebaseDatabaseManager.updateInventoryItem(userId, newItem.copy(imageUrl = url))
                                                        }
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                // Fallback: save original API URL
                                                FirebaseDatabaseManager.updateInventoryItem(userId, newItem.copy(imageUrl = uri.toString()))
                                            }
                                        }
                                    } else {
                                        // Local URI → upload directly
                                        FirebaseStorageManager.uploadItemImage(userId, "inventory", itemId, uri) { dl ->
                                            dl?.let { url ->
                                                FirebaseDatabaseManager.updateInventoryItem(userId, newItem.copy(imageUrl = url))
                                            }
                                        }
                                    }
                                }

                                // 6) Navigate based on layout
                                when {
                                    !isLandscape && !isTabletWidth -> navController.navigate("inventory")
                                    isLandscape -> navController.navigate("pantry_shopping_combined")
                                    else -> navController.navigate("tablet_portrait")
                                }
                            }
                    } else {
                        Toast
                            .makeText(context, "Please fill out all required fields", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                enabled = itemName.value.isNotEmpty() && quantity.value.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Sage)
            ) {
                Text("Add to Inventory")
            }

            // --- Add to Grocery List Button ---
            Button(
                onClick = {
                    if (itemName.value.isNotEmpty() && quantity.value.isNotEmpty() && userId != null) {
                        // 1) Reserve a new DB key for the grocery item
                        val groceryRef = FirebaseDatabaseManager
                            .getUserGroceryListRef(userId)
                            .push()
                        val itemId = groceryRef.key ?: return@Button

                        // 2) Create the grocery item without imageUrl
                        val qty = quantity.value.toIntOrNull() ?: 0
                        val price = priceEstimate.value.toDoubleOrNull() ?: 0.0
                        val newItem = GroceryItem(
                            id = itemId,
                            name = itemName.value,
                            quantity = qty,
                            upc = upcCode.value,
                            imageUrl = null,
                            estimatedPrice = price
                        )

                        // 3) Write the item to Realtime Database
                        groceryRef.setValue(newItem)
                            .addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Toast
                                        .makeText(context, "Failed to add item: ${task.exception?.message}", Toast.LENGTH_SHORT)
                                        .show()
                                    return@addOnCompleteListener
                                }

                                // 4) Upload image or fallback
                                selectedImageUri.value?.let { uri ->
                                    if (uri.scheme?.startsWith("http") == true) {
                                        coroutineScope.launch(Dispatchers.IO) {
                                            try {
                                                val conn = URL(uri.toString()).openConnection() as HttpURLConnection
                                                conn.inputStream.use { input ->
                                                    val tmp = File(context.cacheDir, "$itemId-api.jpg")
                                                    FileOutputStream(tmp).use { out -> input.copyTo(out) }
                                                    val fileUri = Uri.fromFile(tmp)
                                                    FirebaseStorageManager.uploadItemImage(userId, "groceryList", itemId, fileUri) { dl ->
                                                        dl?.let { url ->
                                                            FirebaseDatabaseManager.updateGroceryListItem(userId, newItem.copy(imageUrl = url))
                                                        }
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                FirebaseDatabaseManager.updateGroceryListItem(userId, newItem.copy(imageUrl = uri.toString()))
                                            }
                                        }
                                    } else {
                                        FirebaseStorageManager.uploadItemImage(userId, "groceryList", itemId, uri) { dl ->
                                            dl?.let { url ->
                                                FirebaseDatabaseManager.updateGroceryListItem(userId, newItem.copy(imageUrl = url))
                                            }
                                        }
                                    }
                                }

                                // 6) Navigate based on layout
                                when {
                                    !isLandscape && !isTabletWidth -> navController.navigate("grocery_list")
                                    isLandscape -> navController.navigate("pantry_shopping_combined")
                                    else -> navController.navigate("tablet_portrait")
                                }
                            }
                    }
                },
                enabled = itemName.value.isNotEmpty() && quantity.value.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Sage)
            ) {
                Text("Add to Grocery List")
            }
        }
    }
}
