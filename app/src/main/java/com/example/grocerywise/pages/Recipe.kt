package com.example.grocerywise.pages

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import coil.compose.AsyncImage
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
import com.example.grocerywise.models.InventoryItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive

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

    val prev = remember { mutableStateListOf<InventoryItem>() }
    var fetched by remember { mutableStateOf(false) }
    val recipeList = remember { mutableStateListOf<RecipeResponse>() }
    val search by remember { mutableStateOf(false) }
    val apiKey = BuildConfig.ApiKey
    var done by remember { mutableStateOf(false) }
    val loading by rememberLottieComposition(LottieCompositionSpec.Asset("animations/loading.json"))
    val animatable = rememberLottieAnimatable()
    val displayRowState = rememberLazyListState()
    var touchedDisplay by remember { mutableStateOf<RecipeResponse?>(null) }
    LaunchedEffect(userId, inventoryItem, search) {
        if (userId != null) {
            if (prev == inventoryItem) {
                done = true
                return@LaunchedEffect
            }
            Log.i("prev", prev.joinToString())
            Log.i("inventroyItem", inventoryItem.joinToString())
            prev.clear()
            prev.addAll(inventoryItem)
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
        } else {
            recipeList.clear()
        }
    }

    when (info) {
        WindowWidthSizeClass.COMPACT -> {
            val currentDisplay = touchedDisplay
            if (currentDisplay != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(1f).background(color = Color(0xFFD5BDAF)),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    userScrollEnabled = true,
                ) {
                    val totalIngredients = currentDisplay.usedIngredients + currentDisplay.missedIngredients
                    item {
                        Row(Modifier.fillMaxWidth().height(25.dp).padding(horizontal = 20.dp), horizontalArrangement = Arrangement.End) {
                            Button(onClick = { touchedDisplay = null }) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    contentDescription = "close",
                                    modifier = Modifier.size(25.dp),
                                )
                            }
                        }
                    }
                    item {
                        Text(
                            text = currentDisplay.title,
                            fontSize = 30.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.W600,
                            softWrap = true,
                            maxLines = 2,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    item {
                        AsyncImage(
                            modifier = Modifier.width(200.dp),
                            model = currentDisplay.image,
                            contentScale = ContentScale.FillWidth,
                            contentDescription = "Image",
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Ingredients:",
                            fontSize = 20.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.W400,
                            softWrap = true,
                            maxLines = 2,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            totalIngredients.forEachIndexed { idx, ing ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = "$idx. ${ing.originalName}",
                                        modifier = Modifier.fillMaxWidth(0.6f),
                                        fontSize = 18.sp,
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.W400,
                                        softWrap = true,
                                        maxLines = 3,
                                        textAlign = TextAlign.Start,
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    ) {
                                        Text(
                                            text = ing.amount.toString(),
                                            fontSize = 18.sp,
                                            color = Color.DarkGray,
                                            fontWeight = FontWeight.W400,
                                            softWrap = true,
                                            maxLines = 1,
                                            textAlign = TextAlign.Start,
                                        )
                                        Text(
                                            text = ing.unit.toString(),
                                            fontSize = 18.sp,
                                            color = Color.DarkGray,
                                            fontWeight = FontWeight.W400,
                                            softWrap = true,
                                            maxLines = 2,
                                            textAlign = TextAlign.Start,
                                        )
                                    }
                                }
                                if (idx != totalIngredients.size - 1) {
                                    Spacer(modifier = Modifier.height(5.dp))
                                }
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(1f).padding(vertical = 10.dp, horizontal = 5.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (done) {
                        if (!recipeList.isEmpty()) {
                            LaunchedEffect(displayRowState, recipeList) {
                                snapshotFlow { displayRowState.layoutInfo.totalItemsCount }.first { it > 0 }
                                val midway = (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % recipeList.size)
                                displayRowState.scrollToItem(midway, scrollOffset = 0)
                                while (isActive) {
                                    displayRowState.animateScrollBy(
                                        value = 600f,
                                        animationSpec =
                                        tween(
                                            6000,
                                            easing = EaseInOut,
                                        ),
                                    )
                                }
                            }
                        }

                        LazyRow(
                            state = displayRowState,
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(count = Int.MAX_VALUE, key = { it }) { idx ->
                                val index = idx % recipeList.size
                                val correctedActualIndex = if (index < 0) idx + recipeList.size else index
                                AsyncImage(
                                    model = recipeList[correctedActualIndex].image,
                                    contentDescription = "Images",
                                    modifier = Modifier.height(120.dp).clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.Fit,
                                )
                            }
                        }
                    }

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
                            columns = GridCells.Fixed(2),
                            userScrollEnabled = true,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            items(recipeList) { recipe ->
                                RecipeCard(Info = recipe, callback = { touchedDisplay = recipe })
                            }
                        }
                    }
                }
            }
        }
    }
}