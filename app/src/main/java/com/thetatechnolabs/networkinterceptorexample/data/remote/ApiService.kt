package com.thetatechnolabs.networkinterceptorexample.data.remote

import android.content.Context
import com.google.gson.GsonBuilder
import com.thetatechnolabs.networkinterceptor.network.NetworkInterceptor
import com.thetatechnolabs.networkinterceptor.network.retrofitHelpers.BaseResponse
import com.thetatechnolabs.networkinterceptor.network.retrofitHelpers.NetworkResponseCallAdapterFactory
import com.thetatechnolabs.networkinterceptorexample.data.models.Weather
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("astro.php?lon=113.2&lat=23.1&ac=0&unit=metric&output=json&shift=0")
    suspend fun getWeather(): BaseResponse<Weather>
}

class WeatherRemoteDataSource(
    private val apiService: ApiService
) {
    suspend operator fun invoke(): BaseResponse<Weather> {
        return apiService.getWeather()
    }
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
        .addCallAdapterFactory(NetworkResponseCallAdapterFactory.create())
        .build()
        .create(ApiService::class.java)

    companion object {
        private var INSTANCE: ApiClient? = null

        val Context.networkService: ApiClient
            get() = INSTANCE ?: synchronized(this) {
                return INSTANCE ?: ApiClient(this).also {
                    INSTANCE = it
                }
            }
    }
}