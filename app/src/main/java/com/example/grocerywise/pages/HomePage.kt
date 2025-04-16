package com.example.grocerywise.pages

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.grocerywise.ApiClient
import com.example.grocerywise.AuthState
import com.example.grocerywise.AuthViewModel
import com.example.grocerywise.ProductLookupRequest
import com.example.grocerywise.ProductLookupResponse
import com.example.grocerywise.R
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 20.dp),
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
                            Icon(Icons.Default.List, contentDescription = "Scan Barcode")
                        }
                        FloatingActionButton(
                            onClick = {
                                showMenu.value = false
                                navigationController.navigate("add_item/")
                            },
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Manually")
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
            composable("inventory") { InventoryScreen(authViewModel) }
            composable("grocery_list") { GroceryListScreen(authViewModel) }
            composable("add_item/{productName}") { backStackEntry ->
                val productName = backStackEntry.arguments?.getString("productName")
                AddItemScreen(navigationController, productName)
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    BottomNavigation {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inventory") },
            label = { Text("Inventory") },
            selected = navController.currentDestination?.route == "inventory",
            onClick = { navController.navigate("inventory") },
        )
        BottomNavigationItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Grocery List") },
            label = { Text("Grocery List") },
            selected = navController.currentDestination?.route == "grocery_list",
            onClick = { navController.navigate("grocery_list") },
        )
    }
}

fun hexToColor(hex: String): Color {
    val color = hex.removePrefix("#").toLong(16)
    return Color(color or 0xFF000000L)
}

@Composable
fun InventoryScreen(authViewModel: AuthViewModel) {
    val groceries = remember { mutableStateListOf("Apples" to 3, "Bananas" to 5, "Milk" to 1) } // placeholder vals
    val total: Int = groceries.sumOf { it.second }
    val shuffledColor = remember { colors.shuffled() }
    val percentage: List<Pair<String, Float>> =
        if (total == 0) {
            emptyList()
        } else {
            groceries.map { (name, quantity) ->
                name to (quantity.toFloat() / total)
            }
        }
    Column(modifier = Modifier.fillMaxSize().padding(6.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Inventory", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Button(onClick = { authViewModel.signout() }) { Text("Sign out") }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth().height(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Current Usage:", fontFamily = FontFamily(Font(resId = R.font.defaultfont)), fontSize = 14.sp, color = Color(0xFF29b34e))

            Row(modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight()) {
                percentage.forEachIndexed { index, (name, per) ->
                    val color = hexToColor(shuffledColor[index % shuffledColor.size])
                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(per).background(color = color)) {
                        Text("$name")
                    }
                }
            }
        }
        LazyColumn {
            itemsIndexed(groceries) { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(item.first, fontSize = 20.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            if (groceries[index].second >
                                0
                            ) {
                                groceries[index] = groceries[index].copy(second = groceries[index].second - 1)
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Decrease")
                        }
                        Text(
                            "${item.second}",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                        IconButton(onClick = { groceries[index] = groceries[index].copy(second = groceries[index].second + 1) }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroceryListScreen(authViewModel: AuthViewModel) {
    val groceriesList = remember { mutableStateListOf("Apples" to 3, "Bananas" to 5, "Milk" to 1) } // placeholder vals
    Column(modifier = Modifier.fillMaxSize().padding(6.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Grocery List", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = { authViewModel.signout() }) { Text("Sign out") }
        }
        LazyColumn {
            itemsIndexed(groceriesList) { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(item.first, fontSize = 20.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            if (groceriesList[index].second >
                                0
                            ) {
                                groceriesList[index] = groceriesList[index].copy(second = groceriesList[index].second - 1)
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Decrease")
                        }
                        Text(
                            "${item.second}",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                        IconButton(
                            onClick = { groceriesList[index] = groceriesList[index].copy(second = groceriesList[index].second + 1) },
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddItemScreen(
    navController: NavController,
    productName: String?,
) {
    // Use an empty string as a fallback if productName is null
    val itemName = remember { mutableStateOf(productName ?: "") }
    val quantity = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Add Item", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = itemName.value,
            onValueChange = { itemName.value = it },
            label = { Text("Item Name") },
        )

        OutlinedTextField(
            value = quantity.value,
            onValueChange = { quantity.value = it },
            label = { Text("Quantity") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Ensure that the name is not empty before allowing the user to proceed
            if (itemName.value.isNotEmpty() && quantity.value.isNotEmpty()) {
                // TODO: Save the item to inventory or grocery list in state or db
                navController.popBackStack()
            }
        }) {
            Text("Add Item")
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
            override fun onResponse(
                call: Call<ProductLookupResponse>,
                response: Response<ProductLookupResponse>,
            ) {
                if (response.isSuccessful) {
                    val product = response.body()?.items?.firstOrNull()

                    if (product != null) {
                        val productName = product.title
                        val lowest_recorded_price = product.lowestRecordedPrice
                        val image = product.images[0] // TODO: Add images into grocery lists also investigate using ViewModel?
                        // Navigate to the AddItemScreen and pass the productName
                        navController.navigate("add_item/$productName")
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
