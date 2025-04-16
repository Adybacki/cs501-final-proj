package com.example.grocerywise.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerywise.AuthViewModel
import com.example.grocerywise.R

val colors: List<String> =
    listOf(
        "#FFFF00",
        "#FFC0CB",
        "#00FF00",
        "#0000FF",
        "#FFA500",
        "#800080",
        "#ADD8E6",
        "#90EE90",
        "#FFB6C1",
        "#FFFFE0",
        "#40E0D0",
        "#E6E6FA",
        "#FF7F50",
        "#98FF98",
        "#FFDAB9",
        "#87CEEB",
        "#32CD32",
        "#FF00FF",
        "#00FFFF",
        "#FFFF33",
        "#FF6EC7",
        "#39FF14",
        "#FF5F1F",
        "#FDFD96",
        "#FFD1DC",
        "#77DD77",
        "#AEC6CF",
        "#B19CD9",
    )

fun hexToColor(hex: String): Color {
    val color = hex.removePrefix("#").toLong(16)
    return Color(color or 0xFF000000L)
}

@Composable
fun InventoryScreen(authViewModel: AuthViewModel) {
    val groceries = remember { mutableStateListOf("Apples" to 3, "Bananas" to 5, "Milk" to 1) } // placeholder vals
    val total: Int = groceries.sumOf { it.second }
    val shuffledColor = remember { colors.shuffled() }
    val percentage: List<Pair<String, Float>> =
        if (total == 0) {
            emptyList()
        } else {
            groceries.map { (name, quantity) ->
                name to (quantity.toFloat() / total)
            }
        }
    Column(modifier = Modifier.fillMaxSize().padding(6.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Inventory", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Button(onClick = { authViewModel.signout() }) { Text("Sign out") }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth().height(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Current Usage:",
                fontSize = 14.sp,
                color = Color(0xFF29b34e),
                fontFamily =
                    FontFamily(
                        Font(resId = R.font.defaultfont),
                    ),
            )

            Row(modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight()) {
                percentage.forEachIndexed { index, (name, per) ->
                    val color = hexToColor(shuffledColor[index % shuffledColor.size])
                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(per).background(color = color)) {
                        Text("$name")
                    }
                }
            }
        }
        LazyColumn {
            itemsIndexed(groceries) { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(item.first, fontSize = 20.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            if (groceries[index].second >
                                0
                            ) {
                                groceries[index] = groceries[index].copy(second = groceries[index].second - 1)
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Decrease")
                        }
                        Text(
                            "${item.second}",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                        IconButton(onClick = { groceries[index] = groceries[index].copy(second = groceries[index].second + 1) }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            }
        }
    }
}
