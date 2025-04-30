package com.example.grocerywise.pages

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.google.firebase.auth.FirebaseAuth

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
            Button(colors= ButtonDefaults.buttonColors(containerColor = Sage),
                onClick = {
                    if (itemName.value.isNotEmpty() && quantity.value.isNotEmpty() && userId != null) {
                        // Create an InventoryItem object.
                        val newItem =
                            InventoryItem(
                                name = itemName.value,
                                quantity = quantity.value.toIntOrNull() ?: 0,
                                upc = upcCode.value,
                                imageUrl = selectedImageUri.value?.toString(),
                                // Note: Price and expirationDate are not stored for inventory items.
                            )
                        FirebaseDatabaseManager.addInventoryItem(userId, newItem) { success, exception ->
                            if (success) {
                                navController.navigate("grocery_list")
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "Failed to add item: ${exception?.message}",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }
                        }
                        // parse the data and added to the FireDatabase
                    } else {
                        Toast
                            .makeText(
                                context,
                                "Please fill out all required fields",
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                },
                enabled = itemName.value.isNotEmpty() && quantity.value.isNotEmpty(),
            ) {
                Text("Add to Inventory")
            }

            Button(colors= ButtonDefaults.buttonColors(containerColor = Sage),
                onClick = {
                    if (itemName.value.isNotEmpty() && quantity.value.isNotEmpty() && userId != null) {
                        // Create a GroceryListItem object in db, price 默认 0.0
                        val qty = quantity.value.toIntOrNull() ?: 0
                        val price = priceEstimate.value.toDoubleOrNull() ?: 0.0
                        val newItem =
                            GroceryItem(
                                name = itemName.value,
                                quantity = qty,
                                upc = upcCode.value,
                                imageUrl = selectedImageUri.value?.toString(),
                                estimatedPrice = price,
                            )
                        FirebaseDatabaseManager.addGroceryListItem(userId, newItem) { success, exception ->
                            if (success) {
                                navController.navigate("grocery_list")
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "Failed to add item: ${exception?.message}",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }
                        }
                    }
                },
                enabled = itemName.value.isNotEmpty() && quantity.value.isNotEmpty(),
            ) {
                Text("Add to Grocery List")
            }
        }
    }
}
