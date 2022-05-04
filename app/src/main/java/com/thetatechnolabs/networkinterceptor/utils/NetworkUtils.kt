package com.thetatechnolabs.networkinterceptor.utils

import android.content.Context
import com.thetatechnolabs.networkinterceptor.ApiClient
import com.thetatechnolabs.networkinterceptor.ApiService

object NetworkUtils {
    fun Context.networkService(): ApiService = ApiClient.sharedInstance(this).service
}