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

@Composable
fun BottomNavBar(navController: NavController) {
    BottomNavigation {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inventory", tint = Color.White) },
            label = { Text("Inventory", color = Color.White) },
            selected = navController.currentDestination?.route == "inventory",
            onClick = { navController.navigate("inventory") },
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Grocery List", tint = Color.White) },
            label = { Text("Grocery List", color = Color.White) },
            selected = navController.currentDestination?.route == "grocery_list",
            onClick = { navController.navigate("grocery_list") },
        )

        BottomNavigationItem(
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.recipe),
                    contentDescription = "Recipe",
                    tint = Color.White,
                )
            },
            label = { Text("Recipes", color = Color.White) },
            onClick = { navController.navigate("recipe") },
            selected = navController.currentDestination?.route == "recipe",
        )
    }
}
