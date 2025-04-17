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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.grocerywise.ApiClient
import com.example.grocerywise.AuthState
import com.example.grocerywise.AuthViewModel
import com.example.grocerywise.BottomNavBar
import com.example.grocerywise.ProductLookupRequest
import com.example.grocerywise.ProductLookupResponse
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    val context = LocalContext.current
    val navigationController = rememberNavController()

    val options =
        GmsBarcodeScannerOptions
            .Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
            ).enableAutoZoom()
            .build()

    val scanner = GmsBarcodeScanning.getClient(context)

    // State for showing menu
    val showMenu = remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomNavBar(navigationController) },
        floatingActionButton = {
            Column {
                if (showMenu.value) {
                    Column(
                        modifier = Modifier.padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        FloatingActionButton(
                            onClick = {
                                showMenu.value = false
                                // Inside the barcode scanning success callback
                                scanner
                                    .startScan()
                                    .addOnSuccessListener { barcode ->
                                        Toast.makeText(context, "Scanned: ${barcode.rawValue}", Toast.LENGTH_LONG).show()
                                        barcode.rawValue?.let { upcCode ->
                                            getProductDetails(upcCode, navigationController) // Passing navController to getProductDetails
                                        } ?: run {
                                            Toast.makeText(context, "Invalid barcode scanned", Toast.LENGTH_LONG).show()
                                        }
                                    }.addOnFailureListener { e ->
                                        Toast.makeText(context, "Scan failed: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            },
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Scan Barcode")
                        }
                        FloatingActionButton(
                            onClick = {
                                showMenu.value = false
                                navigationController.navigate("add_item")
                            },
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Add Manually")
                        }
                    }
                }
                FloatingActionButton(onClick = { showMenu.value = !showMenu.value }) {
                    Icon(
                        imageVector = if (showMenu.value) Icons.Default.Delete else Icons.Default.Add,
                        contentDescription = if (showMenu.value) "Close Menu" else "Add Item",
                    )
                }
            }
        },
    ) { paddingValues ->
        NavHost(navigationController, startDestination = "inventory", Modifier.padding(paddingValues)) {
            composable("inventory") {
                InventoryScreen(
                    navController = navigationController,
                    authViewModel = authViewModel,
                )
            }
            composable("grocery_list") {
                GroceryListScreen(
                    authViewModel = authViewModel,
                )
            }
            composable(
                route = "add_item?productName={productName}&productUpc={productUpc}&productPrice={productPrice}&productImageUri={productImageUri}",
                arguments =
                    listOf(
                        navArgument("productName") {
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("productUpc") {
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("productPrice") {
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("productImageUri") {
                            nullable = true
                            defaultValue = null
                        },
                    ),
            ) { backStackEntry ->
                AddItemScreen(
                    navController,
                    productName = backStackEntry.arguments?.getString("productName"),
                    productUpc = backStackEntry.arguments?.getString("productUpc"),
                    productPrice = backStackEntry.arguments?.getString("productPrice"),
                    productImageUri = backStackEntry.arguments?.getString("productImageUri"),
                )
            }
        }
    }
}

// Call API and handle response
fun getProductDetails(
    upc: String,
    navController: NavController,
) {
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
                        val encodedName = Uri.encode(product.title)
                        val encodedUpc = Uri.encode(product.upc)
                        val encodedImage = Uri.encode(product.images.firstOrNull() ?: "")
                        val prices = product.prices
                        val averagePrice = prices.map { it.price }.average()
                        val averageRoundedPrice = String.format("%.2f", averagePrice)
                        val encodedPrice = Uri.encode(averageRoundedPrice)

                        navController.navigate(
                            "add_item?productName=$encodedName&productUpc=$encodedUpc&productPrice=$encodedPrice&productImageUri=$encodedImage",
                        )
                    }
                } else {
                    // Handle API error
                    Log.i("Error", "${response.errorBody()?.string()}")
                    Log.e("API Error", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(
                call: Call<ProductLookupResponse>,
                t: Throwable,
            ) {
                // Handle failure
                Log.e("Network Error", "Failure: ${t.message}")
            }
        },
    )
}
