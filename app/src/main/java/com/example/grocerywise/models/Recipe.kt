package com.example.grocerywise.models

import java.util.Date

data class Ingredients(
    val category: String?,
    val name: String?,
    val quality: Int?,
    val unit: Int?,
    val expiration: Date?,
    val nutrition: Float?,
)
