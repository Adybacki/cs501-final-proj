package com.example.grocerywise.models

data class RecipeSearched(
    var id: Int = 0,
    var title: String = "",
    var image: String = "",
)

data class SearchedResults(
    var number: Int = 0,
    var offset: Int = 0,
    var totalResults: Int = 0,
)
