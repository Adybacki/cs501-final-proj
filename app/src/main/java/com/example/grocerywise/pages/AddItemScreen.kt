package com.example.grocerywise.pages

import android.net.Uri
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@Composable
fun AddItemScreen(navController: NavController, productName: String?, productUpc: String?, productPrice: String?, productImageUri: String?) {
    val itemName = remember { mutableStateOf(productName ?: "") }
    val quantity = remember { mutableStateOf( "") }
    Log.d("PRODUCT PRICE AVG", productPrice.toString())
    val priceEstimate = remember { mutableStateOf( productPrice ?: "") }
    val upcCode = remember { mutableStateOf(productUpc ?: "") }
    val selectedImageUri = remember {
        mutableStateOf<Uri?>(productImageUri?.let { Uri.parse(it) })
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri.value = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add Item", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = itemName.value,
            onValueChange = { itemName.value = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Row (modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = priceEstimate.value,
                onValueChange = { newValue ->
                    val regex = Regex("^\\d{0,7}(\\.\\d{0,2})?$")
                    if (newValue.isEmpty() || regex.matches(newValue)) {
                        priceEstimate.value = newValue
                    }
                },
                label = { Text("Price Estimate Per item") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(2f / 3f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = quantity.value,
                onValueChange = { newValue ->
                    // Allow only digits and values up to 99
                    if (newValue.all { it.isDigit() } && (newValue.isEmpty() || newValue.toInt() <= 99)) {
                        quantity.value = newValue
                    }
                },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        OutlinedTextField(
            value = upcCode.value,
            onValueChange = { newValue ->
                // Allow only digits and max 12 characters
                if (newValue.all { it.isDigit() } && newValue.length <= 12) {
                    upcCode.value = newValue
                }
            },
            label = { Text("UPC Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Upload Photo")
        }

        selectedImageUri.value?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (itemName.value.isNotEmpty() && quantity.value.isNotEmpty()) {
                    // TODO: Save to inventory
                    navController.popBackStack()
                }
            }, enabled = itemName.value.isNotEmpty() && quantity.value.isNotEmpty()) {
                Text("Add to Inventory")
            }

            Button(onClick = {
                if (itemName.value.isNotEmpty() && quantity.value.isNotEmpty()) {
                    // TODO: Save to shopping list
                    navController.popBackStack()
                }
            }, enabled = itemName.value.isNotEmpty() && quantity.value.isNotEmpty()) {
                Text("Add to Shopping List")
            }
        }
    }
}