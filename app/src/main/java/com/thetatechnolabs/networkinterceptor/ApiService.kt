package com.thetatechnolabs.networkinterceptor

import android.content.Context
import com.google.gson.GsonBuilder
import com.thetatechnolabs.networkinterceptor.models.Weather
import com.thetatechnolabs.networkinterceptor.network.NetworkInterceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("astro.php?lon=113.2&lat=23.1&ac=0&unit=metric&output=json&tzshift=0")
    suspend fun getWeather(): Response<Weather>
}

class ApiClient(context: Context) {
    var service: ApiService = Retrofit
        .Builder()
        .client(OkHttpClient.Builder().apply {
            addInterceptor(NetworkInterceptor(context))
        }.build())
        .baseUrl("https://www.7timer.info/bin/")
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