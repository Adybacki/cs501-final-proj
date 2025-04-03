package com.example.grocerywise

import com.squareup.moshi.Json

data class ProductLookupResponse(
    @Json(name="items") val items: List<ProductItem>
)

data class ProductItem(
    @Json(name="title") val title: String,
    @Json(name="brand") val brand: String,
    @Json(name="lowest_recorded_price") val lowestRecordedPrice: String,
    @Json(name="description") val description: String,
    @Json(name="images") val images: List<String>
)