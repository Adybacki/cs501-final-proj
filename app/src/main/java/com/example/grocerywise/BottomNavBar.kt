package com.example.grocerywise

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

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
            icon = { Icon(Icons.Default.List, contentDescription = "Grocery List") },
            label = { Text("Grocery List") },
            selected = navController.currentDestination?.route == "grocery_list",
            onClick = { navController.navigate("grocery_list") }
        )
    }
}