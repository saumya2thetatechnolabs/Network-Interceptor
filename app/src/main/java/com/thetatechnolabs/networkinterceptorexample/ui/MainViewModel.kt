package com.thetatechnolabs.networkinterceptorexample.ui

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thetatechnolabs.networkinterceptor.network.volley.GetRequest.Companion.makeAGetRequest
import com.thetatechnolabs.networkinterceptor.network.volley.Queue.Companion.volleyRequestQueue
import com.thetatechnolabs.networkinterceptor.network.retrofitHelpers.onError
import com.thetatechnolabs.networkinterceptor.network.retrofitHelpers.onException
import com.thetatechnolabs.networkinterceptor.network.retrofitHelpers.onSuccess
import com.thetatechnolabs.networkinterceptorexample.data.models.Weather
import com.thetatechnolabs.networkinterceptorexample.data.remote.ApiClient.Companion.networkService
import com.thetatechnolabs.networkinterceptorexample.data.remote.WeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainViewModel(private val application: Application) : ViewModel() {

    private val _getWeather = MutableStateFlow<String?>(null)
    val getWeather: StateFlow<String?> get() = _getWeather.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun makeVolleyGetRequest() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            try {
                application.volleyRequestQueue.add(
                    application.makeAGetRequest<Weather> {
                        url =
                            "https://www.7timer.info/bin/astro.php?lon=113.2&lat=23.1&ac=0&unit=metric&output=json&shift=0"
                        headers =
                            mutableMapOf("Content-Type" to "application/json, charset utf-8")
                        modelClass = Weather::class.java
                        onSuccess = {
                            _getWeather.value = it.toString()
                        }
                        onFailure = { localizedMessage, message, networkTimeMs ->
                            _getWeather.value =
                                "$message took ${networkTimeMs}ms with $localizedMessage"
                        }
                    }
                )
            } catch (exception: Exception) {
                Timber.e(exception.message)
            }
        }
    }

    fun makeRetrofitRequest() = viewModelScope.launch(Dispatchers.IO) {
        try {
            /*application.networkService.service.getWeather().apply {
                _getWeather.value = body()?.toString()
            }*/
            val response = WeatherRemoteDataSource(application.networkService.service).invoke()

            response.onSuccess {
                val stringResponse = it.toString()
                Log.e("TAG", stringResponse)
                _getWeather.emit(stringResponse)
            }.onError {
                Log.e("TAG", it.toString())
                _getWeather.emit(it)
            }.onException {
                Log.e("TAG", it.message.toString())
                _getWeather.emit(it.message)
            }


        } catch (exception: Exception) {
            Log.e("TAG", exception.message.toString())
            _getWeather.emit(exception.message)
        }
    }
}