package com.example.grocerywise

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

data class ProductLookupRequest(
    val upc: String,
)

data class ClassifyRequestBody(
    val title: String,
)

// data class IgRequest(val upc:String, val name: String? )
// Retrofit API service interface
interface ApiService {
    @Headers(
        "Content-Type: application/json",
    )
    @POST("prod/trial/lookup")
    fun lookupProduct(
        @Body request: ProductLookupRequest,
    ): Call<ProductLookupResponse>
}

interface Categorization {
    @Headers("Content-Type: application/json")
    @POST("food/products/classify")
    suspend fun getIg(
        @Body requestBody: ClassifyRequestBody,
        @Query("apiKey") apikey: String,
    ): CtgResponse
}

// Singleton object to provide Retrofit API client
object ApiClient {
    private const val BASE_URL = "https://api.upcitemdb.com/" // Base URL for GitHub's API
    private const val Category_url = "https://api.spoonacular.com/" // for fetching Ingredients
    private val moshi =
        Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory()) // Moshi adapter for Kotlin
            .build()
    private val ctgRetrofit: Retrofit =
        Retrofit
            .Builder()
            .baseUrl(Category_url)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    moshi,
                ),
            ).build()

    private val retrofit: Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // Use Moshi for JSON parsing
            .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
    val ctgService: Categorization = ctgRetrofit.create(Categorization::class.java)
}
