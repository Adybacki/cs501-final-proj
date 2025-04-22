package com.example.grocerywise

import com.squareup.moshi.Json

data class ProductLookupResponse(
    @Json(name = "items") val items: List<ProductItem>,
)

class ProductItem(
    @Json(name = "title") val title: String,
    @Json(name = "upc") val upc: String,
    @Json(name = "offers") val prices: List<OfferItem>,
    @Json(name = "images") val images: List<String>,
)

data class OfferItem(
    @Json(name = "price") val price: Float,
)
