package com.example.grocerywise.pages

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.grocerywise.R
import com.example.grocerywise.ui.theme.Sage
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
    // 1. Redirect to login if unauthenticated
    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    // 2. Detect “tablet width” by raw dp
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
// treat 600 dp+ as “tablet”
    val isTabletWidth = screenWidthDp >= 600

// 3. Detect landscape
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

// 4. Combined layout only on tablet+landscape
    val useCombinedLayout = isLandscape && isTabletWidth
    val isTabletPortrait = isTabletWidth && !isLandscape

    // 3. Create a separate NavController for the bottom tabs
    val bottomNavController = rememberNavController()

    // 4. Barcode scanner setup
    val context = LocalContext.current
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
        .enableAutoZoom()
        .build()
    val scanner = GmsBarcodeScanning.getClient(context, options)

    // 5. Floating menu state
    val showMenu = remember { mutableStateOf(false) }

    LaunchedEffect(useCombinedLayout, isTabletPortrait) {
        val currentRoute = bottomNavController.currentDestination?.route

        if (currentRoute == "recipe") {
            return@LaunchedEffect  // Do nothing if on recipe screen
        }

        if (useCombinedLayout && currentRoute != "pantry_shopping_combined") {
            bottomNavController.navigate("pantry_shopping_combined") {
                popUpTo(bottomNavController.graph.startDestinationId) { inclusive = true }
            }
        } else if (isTabletPortrait && currentRoute != "tablet_portrait") {
            bottomNavController.navigate("tablet_portrait") {
                popUpTo(bottomNavController.graph.startDestinationId) { inclusive = true }
            }
        } else if (!useCombinedLayout && !isTabletPortrait && currentRoute == "pantry_shopping_combined") {
            bottomNavController.navigate("inventory") {
                popUpTo(bottomNavController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        // Pass our flag into BottomNavBar so it can switch between 2-item and 3-item modes
        bottomBar = {
            BottomNavBar(
                navController = bottomNavController,
            )
        },
        // FAB menu will float over whichever screen is active
        floatingActionButton = {
            Column {
                if (showMenu.value) {
                    Column(
                        modifier = Modifier.padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        // Scan barcode button
                        FloatingActionButton(
                            containerColor = Sage,
                            contentColor = Color.White,
                            onClick = {
                                showMenu.value = false
                                scanner.startScan()
                                    .addOnSuccessListener { barcode ->
                                        barcode.rawValue?.let { upc ->
                                            getProductDetails(upc, bottomNavController)
                                        } ?: run {
                                            Toast.makeText(context, "Invalid barcode", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Scan failed: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.icons8_barcode_96),
                                contentDescription = "Scan Barcode"
                            )
                        }

                        // Manual add button
                        FloatingActionButton(
                            containerColor = Sage,
                            contentColor = Color.White,
                            onClick = {
                                showMenu.value = false
                                bottomNavController.navigate("add_item")
                            }
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Add Manually")
                        }
                    }
                }

                // Toggle the mini-menu
                FloatingActionButton(
                    containerColor = Sage,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    onClick = { showMenu.value = !showMenu.value }
                ) {
                    Icon(
                        imageVector = if (showMenu.value) Icons.Default.Delete else Icons.Default.Add,
                        contentDescription = if (showMenu.value) "Close Menu" else "Open Menu"
                    )
                }
            }
        }
    ) { innerPadding ->
        // 6. NavHost for bottom tabs (includes our new combined screen)
        NavHost(
            navController = bottomNavController,
            startDestination = when {
                useCombinedLayout -> "pantry_shopping_combined"
                isTabletPortrait -> "tablet_portrait"
                else -> "inventory" },
            modifier = Modifier.padding(innerPadding)
        ) {
            // Inventory / Pantry (single-pane)
            composable("inventory") {
                InventoryScreen(
                    onAvatarClick = { navController.navigate("profile") },

                )
            }

            // Grocery List (single-pane)
            composable("grocery_list") {
                GroceryListScreen(
                    onAvatarClick = { navController.navigate("profile") }
                )
            }

            // Recipes (always full-screen)
            composable("recipe") {
                Recipe(
                )
            }

            // Combined Pantry & Shopping List (tablet-landscape only)
            composable("pantry_shopping_combined") {
                PantryAndShoppingListScreen(
                    onAvatarClick      = { navController.navigate("profile") }

                )
            }

            composable("tablet_portrait") {
                PantryShoppingListScreenVertical(
                    onAvatarClick = {navController.navigate("profile")}
                )
            }

            // Add-Item route (shared)
            composable(
                route = "add_item?productName={productName}&productUpc={productUpc}" +
                        "&productPrice={productPrice}&productImageUri={productImageUri}",
                arguments = listOf(
                    navArgument("productName") { nullable = true; defaultValue = null },
                    navArgument("productUpc")   { nullable = true; defaultValue = null },
                    navArgument("productPrice") { nullable = true; defaultValue = null },
                    navArgument("productImageUri") { nullable = true; defaultValue = null }
                )
            ) { backStack ->
                AddItemScreen(
                    navController     = bottomNavController,
                    productName       = backStack.arguments?.getString("productName"),
                    productUpc        = backStack.arguments?.getString("productUpc"),
                    productPrice      = backStack.arguments?.getString("productPrice"),
                    productImageUri   = backStack.arguments?.getString("productImageUri")
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
                        val trimmedTitle = if (product.title.length > 30) {
                            product.title.substring(0, 30) + "..."
                        } else {
                            product.title
                        }
                        val encodedName = Uri.encode(trimmedTitle)
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
                }
            }

            override fun onFailure(
                call: Call<ProductLookupResponse>,
                t: Throwable,
            ) {
            }
        },
    )
}
