package com.example.grocerywise.pages

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.grocerywise.ApiClient
import com.example.grocerywise.AuthState
import com.example.grocerywise.AuthViewModel
import com.example.grocerywise.BottomNavBar
import com.example.grocerywise.ProductLookupRequest
import com.example.grocerywise.ProductLookupResponse
import com.example.grocerywise.models.GroceryItem
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    val context = LocalContext.current
    val navigationController = rememberNavController()

    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E)
        .enableAutoZoom()
        .build()

    val scanner = GmsBarcodeScanning.getClient(context)

    // State for showing menu
    val showMenu = remember { mutableStateOf(false) }

    // Sample dummy data for grocery items
    val groceryItems = remember {
        listOf(
            GroceryItem(
                uid = "1",
                name = "Apple",
                quantity = 3,
                estimatedPrice = 1.99,
                isChecked = false,
                imageUrl = "https://example.com/apple.jpg"
            ),
            GroceryItem(
                uid = "2",
                name = "Banana",
                quantity = 5,
                estimatedPrice = 0.99,
                isChecked = false,
                imageUrl = "https://example.com/banana.jpg"
            ),
            GroceryItem(
                uid = "3",
                name = "Orange",
                quantity = 2,
                estimatedPrice = 2.49,
                isChecked = false,
                imageUrl = "https://example.com/orange.jpg"
            )
        )
    }

    Scaffold(
        bottomBar = { BottomNavBar(navigationController) },
        floatingActionButton = {
            Column {
                if (showMenu.value) {
                    Column(
                        modifier = Modifier.padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                showMenu.value = false
                                //Inside the barcode scanning success callback
                                scanner.startScan()
                                    .addOnSuccessListener { barcode ->
                                        Toast.makeText(context, "Scanned: ${barcode.rawValue}", Toast.LENGTH_LONG).show()
                                        barcode.rawValue?.let { upcCode ->
                                            getProductDetails(upcCode, navigationController)  //Passing navController to getProductDetails
                                        } ?: run {
                                            Toast.makeText(context, "Invalid barcode scanned", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Scan failed: ${e.message}", Toast.LENGTH_LONG).show()
                                    }

                            }
                        ) {
                            Icon(Icons.Default.List, contentDescription = "Scan Barcode")
                        }
                        FloatingActionButton(
                            onClick = {
                                showMenu.value = false
                                navigationController.navigate("add_item/")
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Manually")
                        }
                    }
                }
                FloatingActionButton(onClick = { showMenu.value = !showMenu.value }) {
                    Icon(
                        imageVector = if (showMenu.value) Icons.Default.Delete else Icons.Default.Add,
                        contentDescription = if (showMenu.value) "Close Menu" else "Add Item"
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(navigationController, startDestination = "inventory", Modifier.padding(paddingValues)) {
            composable("inventory") { InventoryScreen(authViewModel) }
            composable("grocery_list") {  GroceryListScreen(
                authViewModel = authViewModel,
                groceryItems = groceryItems.toMutableStateList(),
                onUpdateItem = { updatedItem ->
                    // Update the item logic here
                },
                onDeleteItem = { uid ->
                    // Delete item logic here
                },
                onAddCheckedToInventory = {
                    // Add checked items to inventory logic here
                }
            ) }
            composable("add_item?name={name}&upc={upc}&price={price}&image={image}") { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name")
                val upc = backStackEntry.arguments?.getString("upc")
                val price = backStackEntry.arguments?.getString("price")
                val image = backStackEntry.arguments?.getString("image")

                AddItemScreen(
                    navController = navController,
                    productName = name,
                    productUpc = upc,
                    productPrice = price,
                    productImageUri = image
                )
            }
        }
    }
}

// Call API and handle response
fun getProductDetails(upc: String, navController: NavController) {
    val request = ProductLookupRequest(upc)
    Log.d("API Request", "Sending UPC: $request")

    ApiClient.apiService.lookupProduct(request).enqueue(object : Callback<ProductLookupResponse> {
        @SuppressLint("DefaultLocale")
        override fun onResponse(
            call: Call<ProductLookupResponse>,
            response: Response<ProductLookupResponse>
        ) {
            if (response.isSuccessful) {
                val product = response.body()?.items?.firstOrNull()

                if (product != null) {
                    val encodedName = Uri.encode(product.title)
                    val encodedUpc = Uri.encode(product.upc)
                    val encodedImage = Uri.encode(product.images.firstOrNull() ?: "")
                    val prices = product.prices
                    val averagePrice = prices.map { it.price }.average()
                    val averageRoundedPrice = String.format("%.2f", averagePrice)
                    val encodedPrice = Uri.encode(averageRoundedPrice)

                    navController.navigate("add_item?name=$encodedName&upc=$encodedUpc&price=$encodedPrice&image=$encodedImage")

                }
            } else {
                // Handle API error
                Log.i("Error", "${response.errorBody()?.string()}")
                Log.e("API Error", "Error: ${response.code()} - ${response.message()}")
            }
        }

        override fun onFailure(call: Call<ProductLookupResponse>, t: Throwable) {
            // Handle failure
            Log.e("Network Error", "Failure: ${t.message}")
        }
    })
}
