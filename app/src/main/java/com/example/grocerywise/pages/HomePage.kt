package com.example.grocerywise.pages

import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.grocerywise.ApiClient
import com.example.grocerywise.AuthState
import com.example.grocerywise.AuthViewModel
import com.example.grocerywise.OfferItem
import com.example.grocerywise.ProductLookupRequest
import com.example.grocerywise.ProductLookupResponse
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            composable("grocery_list") { GroceryListScreen(authViewModel) }
            composable("add_item?name={name}&upc={upc}&prices={prices}&image={image}") { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name")
                val upc = backStackEntry.arguments?.getString("upc")
                val prices = backStackEntry.arguments?.getString("prices")?.split(",") ?: listOf()
                val image = backStackEntry.arguments?.getString("image")

                AddItemScreen(
                    navController = navController,
                    productName = name,
                    productUpc = upc,
                    productPrices = prices,
                    productImageUri = image
                )
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
            onClick = { navController.navigate("inventory") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Grocery List") },
            label = { Text("Grocery List") },
            selected = navController.currentDestination?.route == "grocery_list",
            onClick = { navController.navigate("grocery_list") }
        )
    }
}

@Composable
fun InventoryScreen(authViewModel: AuthViewModel) {
    val groceries = remember { mutableStateListOf("Apples" to 3, "Bananas" to 5, "Milk" to 1) } // placeholder vals
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Inventory", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = { authViewModel.signout() }) { Text("Sign out") }
        }
        LazyColumn {
            itemsIndexed(groceries) { index, item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(item.first, fontSize = 20.sp)
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (groceries[index].second > 0) groceries[index] = groceries[index].copy(second = groceries[index].second - 1) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Decrease")
                        }
                        Text("${item.second}", fontSize = 20.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 8.dp))
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
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Grocery List", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = { authViewModel.signout() }) { Text("Sign out") }
        }
        LazyColumn {
            itemsIndexed(groceriesList) { index, item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(item.first, fontSize = 20.sp)
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            if (groceriesList[index].second > 0) groceriesList[index] = groceriesList[index].copy(second = groceriesList[index].second - 1) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Decrease")
                        }
                        Text("${item.second}", fontSize = 20.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 8.dp))
                        IconButton(onClick = { groceriesList[index] = groceriesList[index].copy(second = groceriesList[index].second + 1) }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddItemScreen(navController: NavController, productName: String?, productUpc: String?, productPrices: List<String>?, productImageUri: String?) {
    val itemName = remember { mutableStateOf(productName ?: "") }
    val quantity = remember { mutableStateOf( "") }

    val averagePrice = if (!productPrices.isNullOrEmpty()) {
        productPrices.mapNotNull { it.toDoubleOrNull() }.average()
    } else {
        0.0
    }
    val priceEstimate = remember { mutableStateOf( averagePrice.toString()) }
    val upcCode = remember { mutableStateOf(productUpc ?: "") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(Uri.parse(productImageUri)) }

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

        OutlinedTextField(
            value = quantity.value,
            onValueChange = { quantity.value = it },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = priceEstimate.value,
            onValueChange = { priceEstimate.value = it },
            label = { Text("Price Estimate") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        OutlinedTextField(
            value = upcCode.value,
            onValueChange = { upcCode.value = it },
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
            }) {
                Text("Add to Inventory")
            }

            Button(onClick = {
                if (itemName.value.isNotEmpty() && quantity.value.isNotEmpty()) {
                    // TODO: Save to shopping list
                    navController.popBackStack()
                }
            }) {
                Text("Add to Shopping List")
            }
        }
    }
}




// Call API and handle response
fun getProductDetails(upc: String, navController: NavController) {
    val request = ProductLookupRequest(upc)
    Log.d("API Request", "Sending UPC: $request")

    ApiClient.apiService.lookupProduct(request).enqueue(object : Callback<ProductLookupResponse> {
        override fun onResponse(
            call: Call<ProductLookupResponse>,
            response: Response<ProductLookupResponse>
        ) {
            if (response.isSuccessful) {
                val product = response.body()?.items?.firstOrNull()

                if (product != null) {
                    val encodedName = Uri.encode(product.title)
                    val encodedUpc = Uri.encode(product.upc)
                    val encodedPrices = Uri.encode(product.prices.joinToString(","))
                    val encodedImage = Uri.encode(product.images.firstOrNull() ?: "")

                    navController.navigate("add_item?name=$encodedName&upc=$encodedUpc&prices=$encodedPrices&image=$encodedImage")

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
