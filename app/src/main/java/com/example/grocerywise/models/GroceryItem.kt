package com.example.grocerywise.models

data class GroceryItem(
    val uid: String = "",
    val name: String = "",
    val quantity: Int = 1,
    val estimatedPrice: Double = 0.0,
    val upc: String = "",
    val isChecked: Boolean = false,
    val imageUrl: String? = null
)
