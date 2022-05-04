package com.thetatechnolabs.networkinterceptor

import android.content.Context
import com.google.gson.GsonBuilder
import com.thetatechnolabs.networkinterceptor.network.NetworkInterceptor
import com.thetatechnolabs.networkinterceptor.news.NewsResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("everything")
    suspend fun getNews(
        @Query("q") query: String = "apple",
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY
    ): Response<NewsResponse>
}

class ApiClient(context: Context) {
    var service: ApiService = Retrofit
        .Builder()
        .client(OkHttpClient.Builder().apply {
            addInterceptor(NetworkInterceptor(context))
        }.build())
        .baseUrl("https://newsapi.org/v2/")
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setPrettyPrinting()
                    .setLenient()
                    .create()
            )
        )
        .build()
        .create(ApiService::class.java)

    companion object {
        private var INSTANCE: ApiClient? = null

        fun sharedInstance(context: Context) = INSTANCE ?: synchronized(this) {
            return INSTANCE ?: ApiClient(context).also {
                INSTANCE = it
            }
        }
    }
}