package com.thetatechnolabs.networkinterceptorexample.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thetatechnolabs.networkinterceptor.network.GetRequest.Companion.makeAGetRequest
import com.thetatechnolabs.networkinterceptor.network.PostRequest.Companion.makeAPostRequest
import com.thetatechnolabs.networkinterceptor.network.Queue.Companion.volleyRequestQueue
import com.thetatechnolabs.networkinterceptorexample.ApiClient.Companion.networkService
import com.thetatechnolabs.networkinterceptorexample.models.Test
import com.thetatechnolabs.networkinterceptorexample.models.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber

class MainViewModel(private val application: Application) : ViewModel() {

    private val _getWeather by lazy { MutableLiveData<String?>(null) }
    val getWeather: LiveData<String?> get() = _getWeather

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


    fun makeVolleyPostRequest(name: String, gender: String, email: String, status: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val postRequest = application.makeAPostRequest<Test> {
                url = "https://gorest.co.in/public/v2/users"
                headers = mutableMapOf(
                    "Content-Type" to "application/json",
                    "Authorization" to "Bearer cc6a5de2498aca191c3181598126c8fc49628e860b7b8143c9439f3d87fdb819"
                )
                requestBody = JSONObject().also {
                    with(it) {
                        put("name", name)
                        put("gender", gender)
                        put("email", email)
                        put("status", status)
                    }
                }
                modelClass = Test::class.java
                onSuccess = { _getWeather.value = it.toString() }
                onFailure = { localizedMessage, message, networkTimeMs ->
                    _getWeather.value =
                        "$message took ${networkTimeMs}ms with $localizedMessage"
                }
            }
            application.volleyRequestQueue.add(postRequest)
        }

    fun makeRetrofitRequest() = viewModelScope.launch(Dispatchers.IO) {
        try {
            application.networkService.service.getWeather().apply {
                _getWeather.value = body()?.toString()
            }
        } catch (_: Exception) {

        }
    }
}