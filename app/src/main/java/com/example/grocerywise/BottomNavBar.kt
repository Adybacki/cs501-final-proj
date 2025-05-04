package com.example.grocerywise

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grocerywise.ui.theme.Brown
import com.example.grocerywise.ui.theme.Sage

@Composable
fun BottomNavBar(navController: NavController, useCombinedLayout: Boolean) {
    BottomNavigation(backgroundColor = Sage) {

        if (useCombinedLayout) {
            // — Tablet in landscape mode: only show Recipes and the combined Pantry&ShoppingList.

            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Recipes",
                        tint = Color.White
                    )
                },
                label = { Text("Recipes", color = Color.White) },
                selected = navController.currentDestination?.route == "recipe",
                onClick = { navController.navigate("recipe") }
            )

            BottomNavigationItem(
                icon = {
                    // Use a “list” icon to represent the merged pantry+shopping view
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Pantry & Shopping",
                        tint = Color.White
                    )
                },
                label = { Text("Pantry & List", color = Color.White) },
                // This route must match the one you registered in NavHost
                selected = navController.currentDestination?.route == "pantry_shopping_combined",
                onClick = { navController.navigate("pantry_shopping_combined") }
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