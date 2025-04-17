package com.example.grocerywise

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun BottomNavBar(navController: NavController) {
    BottomNavigation {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inventory", tint = Color.White) },
            label = { Text("Inventory", color = Color.White) },
            selected = navController.currentDestination?.route == "inventory",
            onClick = { navController.navigate("inventory") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Grocery List", tint = Color.White) },
            label = { Text("Grocery List", color = Color.White) },
            selected = navController.currentDestination?.route == "grocery_list",
            onClick = { navController.navigate("grocery_list") }
        )
    }
}