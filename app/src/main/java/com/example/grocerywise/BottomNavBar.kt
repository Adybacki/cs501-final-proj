package com.example.grocerywise

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grocerywise.ui.theme.Brown
import com.example.grocerywise.ui.theme.Sage

@Composable
fun BottomNavBar(navController: NavController, useCombinedLayout: Boolean) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp
// treat 600 dp+ as “tablet”
    val isTabletWidth = screenWidthDp >= 600
    BottomNavigation(backgroundColor = Sage) {

        if (isLandscape || isTabletWidth) {
            // — Tablet in landscape mode: only show Recipes and the combined Pantry&ShoppingList.
            BottomNavigationItem(
                icon = {
                    // Use a “list” icon to represent the merged pantry+shopping view
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Pantry & Shopping",
                        tint = Color.White
                    )
                },
                label = { Text("Pantry & Grocery List", color = Color.White) },
                // This route must match the one you registered in NavHost
                selected = navController.currentDestination?.route == "pantry_shopping_combined",
                onClick = {
                    if (isTabletWidth && !isLandscape)
                    {
                        navController.navigate("tablet_portrait")
                    } else {
                        navController.navigate("pantry_shopping_combined")
                    }
                }
            )
            BottomNavigationItem(
                icon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.recipe),
                        contentDescription = "Recipes",
                        tint = Color.White
                    )
                },
                label = { Text("Recipes", color = Color.White) },
                selected = navController.currentDestination?.route == "recipe",
                onClick = { navController.navigate("recipe") }
            )

        } else {
            // — Phone or portrait: show Pantry, Grocery List, and Recipes as before.

            BottomNavigationItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Inventory", tint = Color.White) },
                label = { Text("Pantry", color = Color.White) },
                selected = navController.currentDestination?.route == "inventory",
                onClick = { navController.navigate("inventory") }
            )

            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Grocery List",
                        tint = Color.White
                    )
                },
                label = { Text("Grocery List", color = Color.White) },
                selected = navController.currentDestination?.route == "grocery_list",
                onClick = { navController.navigate("grocery_list") }
            )

            BottomNavigationItem(
                icon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.recipe),
                        contentDescription = "Recipes",
                        tint = Color.White
                    )
                },
                label = { Text("Recipes", color = Color.White) },
                selected = navController.currentDestination?.route == "recipe",
                onClick = { navController.navigate("recipe") }
            )
        }
    }
}