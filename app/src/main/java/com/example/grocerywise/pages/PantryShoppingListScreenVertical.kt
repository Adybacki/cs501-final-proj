package com.example.grocerywise.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PantryShoppingListScreenVertical(
    onAvatarClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // Top pane: Pantry
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            InventoryScreen(
                onAvatarClick   = onAvatarClick,
            )
        }

        Divider(
            color    = Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
                .width(1.dp)
        )

        // Bottom pane: Grocery List
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            GroceryListScreen(
                onAvatarClick   = onAvatarClick
            )
        }
    }
}
