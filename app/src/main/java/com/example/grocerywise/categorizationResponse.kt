

package com.example.grocerywise

import com.squareup.moshi.Json

data class CtgResponse(
    @Json(name = "category") val category: String,
    @Json(name = "usdaCode") val usda: Int?,
)

data class Ingredient(
    @Json(name = "amount") val amount: Double,
    @Json(name = "unit") val unit: String?,
    @Json(name = "name") val name: String,
    @Json(name = "image") val img: String?,
    @Json(name = "originalName") val originalName: String,
    @Json(name = "aisle") val categoryAisle: String,
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

data class RecipeInfoResponse(
    @Json(name = "title") val title: String,
    @Json(name = "image") val image: String,
    @Json(name = "servings") val servings: Int,
    @Json(name = "readyInMinutes") val readyMin: Int?,
    @Json(name = "cookingMinutes") val cookMin: Int?,
    @Json(name = "preparationMinutes") val prepareMin: Int?,
    @Json(name = "cheap") val cheap: Boolean?,
    @Json(name = "dairyFree") val dairyFree: Boolean?,
    @Json(name = "glutenFree") val glutenFree: Boolean?,
    @Json(name = "sustainable") val sustainable: Boolean?,
    @Json(name = "extendedIngredients") val extendedIngredient: List<Ingredient>,
    @Json(name = "summary") val summary: String?,
)

data class SearchResponse(
    @Json(name = "results") val results: List<RcpResult>,
    @Json(name = "offset") val offset: Int,
    @Json(name = "number") val number: Int,
    @Json(name = "totalResults") val totalResults: Int,
)

data class RcpResult(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "image") val image: String?,
)
