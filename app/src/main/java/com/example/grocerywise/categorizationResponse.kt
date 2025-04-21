

package com.example.grocerywise

import com.squareup.moshi.Json

data class CtgResponse(
    @Json(name = "category") val category: String,
    @Json(name = "usdaCode") val catergory: Int?,
)
