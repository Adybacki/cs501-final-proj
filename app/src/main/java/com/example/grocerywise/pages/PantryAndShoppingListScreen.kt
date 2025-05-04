package com.example.grocerywise.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.grocerywise.AuthViewModel

@Composable
fun PantryAndShoppingListScreen(
    authViewModel: AuthViewModel,
    navController: NavController // pass NavController if needed for actions
) {
    // Use a Row to place two content panels side by side
    Row(modifier = Modifier.fillMaxSize()) {

        // Left pane: Pantry (Inventory) Screen content
        Box(
            modifier = Modifier
                .weight(1f)                // take half width
                .fillMaxHeight()
        ) {
            // InventoryScreen is the existing composable for Pantry content
            InventoryScreen(
                authViewModel = authViewModel,
                navController   = navController,
                onAvatarClick   = { navController.navigate("profile") },
            )
        }
        Divider(
            color    = Color.LightGray,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )

        // Right pane: Grocery List Screen content
        Box(
            modifier = Modifier
                .weight(1f)                // take half width
                .fillMaxHeight()
        ) {
            // GroceryListScreen is the existing composable for Grocery List content
            GroceryListScreen(
                authViewModel = authViewModel,
                navController   = navController,
                onAvatarClick   = { navController.navigate("profile") }
            )

            // Floating Action Button – only on the right pane for adding new grocery items
//            FloatingActionButton(
//                onClick = { /* handle add new grocery item */ },
//                modifier = Modifier
//                    .align(LineHeightStyle.Alignment.BottomEnd)   // anchor to bottom-right of the right pane
//                    .padding(16.dp)
//            ) {
//                Icon(Icons.Filled.Add, contentDescription = "Add Item")
//            }
        }
    }
}
