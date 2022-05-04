package com.thetatechnolabs.networkinterceptorexample.utils

import android.content.Context
import com.thetatechnolabs.networkinterceptorexample.ApiClient
import com.thetatechnolabs.networkinterceptorexample.ApiService

object NetworkUtils {
    fun Context.networkService(): ApiService = ApiClient.sharedInstance(this).service
}