package com.example.grocerywise.pages

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.grocerywise.ApiClient
import com.example.grocerywise.AuthViewModel
import com.example.grocerywise.BuildConfig
import com.example.grocerywise.ClassifyRequestBody
import com.example.grocerywise.InventoryViewModel
import com.example.grocerywise.R
import com.example.grocerywise.RecipeResponse
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Recipe(
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    val info = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    var searchVal by remember { mutableStateOf("") }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val categorization: MutableList<String> = remember { mutableStateListOf() }
    val activity = LocalActivity.current as ComponentActivity
    val inventoryViewModel: InventoryViewModel = viewModel(viewModelStoreOwner = activity)
    val inventoryItem by inventoryViewModel.inventoryItems.collectAsState()
    var fetched by remember { mutableStateOf(false) }
    val recipeList = remember { mutableStateListOf<RecipeResponse>() }
    val search by remember { mutableStateOf(false) }
    val apiKey = BuildConfig.ApiKey
    var done by remember { mutableStateOf(false) }
    val loading by rememberLottieComposition(LottieCompositionSpec.Asset("animations/loading.json"))
    val animatable = rememberLottieAnimatable()
    LaunchedEffect(userId, inventoryItem, search) {
        if (userId != null) {
            inventoryItem.listIterator().forEach { item ->
                val itemName = item.name
//                val upc = item.upc
// get the classification based
                val requestBody = ClassifyRequestBody(title = itemName)
                val catgoryname = ApiClient.ctgService.getIg(requestBody = requestBody, apikey = apiKey)
                Log.i("category", itemName)
                val ctgryName = catgoryname.category
                if (catgoryname.category != "unknown") {
                    categorization.add(ctgryName)
                }
            }

            // get the recipe
            val fetchString = categorization.joinToString(separator = ",+")
            Log.i("fetched String", fetchString)
            val rcpResponse = ApiClient.rcpService.getRecipe(apikey = apiKey, ingredients = fetchString, number = 10)
            Log.i("rcpResponseList:", rcpResponse.toString())
            recipeList.clear()
            recipeList.addAll(rcpResponse)
            done = true
        }
    }

    when (info) {
        WindowWidthSizeClass.COMPACT -> {
            Column(
                modifier = Modifier.fillMaxSize(1f).padding(vertical = 20.dp, horizontal = 5.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Recipe Finder",
                    fontWeight = FontWeight.W600,
                    fontFamily = FontFamily(Font(R.font.defaultfont)),
                    fontSize = 30.sp,
                    color = Color(0xFF101210),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(1f).height(60.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.search),
                                contentDescription = "search",
                            )
                        },
                        textStyle =
                            TextStyle(
                                color = Color.DarkGray,
                                fontSize = 18.sp,
                                fontFamily =
                                    FontFamily(
                                        Font(R.font.defaultfont),
                                    ),
                            ),
                        modifier =
                            Modifier
                                .fillMaxWidth(
                                    0.8f,
                                ).height(50.dp),
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF44B863),
                                focusedLabelColor = Color(0xFF615fd4),
                            ),
                        shape = RoundedCornerShape(50),
                        value = searchVal,
                        onValueChange = { value: String ->
                            searchVal =
                                value
                        },
                        placeholder = {
                            Text(
                                "search your recipe",
                                fontSize = 18.sp,
                                fontFamily =
                                    FontFamily(
                                        Font(R.font.defaultfont),
                                    ),
                                color = Color.LightGray,
                            )
                        },
                        singleLine = true,
                    )
                    Button(
                        onClick = {
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier =
                            Modifier.fillMaxWidth().height(
                                40.dp,
                            ),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF022e2d)),
                    ) {
                        Text(
                            "Search",
                            softWrap = true,
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.defaultfont)),
                            color = Color.LightGray,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (!done) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        LaunchedEffect(loading) {
                            if (loading != null) {
                                animatable.animate(
                                    loading,
                                    iterations = LottieConstants.IterateForever,
                                )
                            }
                        }

                        LottieAnimation(composition = loading, progress = animatable.progress, modifier = Modifier.size(150.dp))
                    }
                } else {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                        contentPadding = PaddingValues(horizontal = 3.dp, vertical = 5.dp),
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        userScrollEnabled = true,
                    ) {
                        items(recipeList) { recipe ->
                            RecipeCard(Info = recipe)
                        }
                    }
                }
            }
        }
    }
}
