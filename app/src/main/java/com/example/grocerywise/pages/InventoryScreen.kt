package com.example.grocerywise.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerywise.AuthViewModel

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