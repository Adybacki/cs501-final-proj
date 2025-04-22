

package com.example.grocerywise

import com.squareup.moshi.Json

data class CtgResponse(
    @Json(name = "category") val category: String,
    @Json(name = "usdaCode") val usda: Int?,
)

data class Ingredient(
    @Json(name = "amount") val amount: Int,
    @Json(name = "unit") val unit: String?,
    @Json(name = "name") val name: String,
)

data class RecipeResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "image") val image: String,
    @Json(name = "usedIngredientCount") val usedIngredientsCount: Int,
    @Json(name = "missedIngredientCount") val missedIngredientsCount: Int,
    @Json(name = "usedIngredients") val usedIngredients: List<Ingredient>,
    @Json(name = "missedIngredients") val missedIngredients: List<Ingredient>,
)
