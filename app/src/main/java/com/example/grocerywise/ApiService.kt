package com.example.grocerywise

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class ProductLookupRequest(val upc: String)

// Retrofit API service interface
interface ApiService {
    @Headers(
        "Content-Type: application/json",
    )
    @POST("prod/trial/lookup")
    fun lookupProduct(
        @Body request: ProductLookupRequest
    ): Call<ProductLookupResponse>
}

// Singleton object to provide Retrofit API client
object ApiClient {
    private const val BASE_URL = "https://api.upcitemdb.com/" // Base URL for GitHub's API

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Moshi adapter for Kotlin
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi)) // Use Moshi for JSON parsing
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}