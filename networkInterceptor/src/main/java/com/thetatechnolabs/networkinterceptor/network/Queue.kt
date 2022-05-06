package com.thetatechnolabs.networkinterceptor.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class Queue constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: Queue? = null
        val Context.volleyRequestQueue: Queue
            get() = INSTANCE ?: synchronized(this) {
                return INSTANCE ?: Queue(this).also {
                    INSTANCE = it
                }
            }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> add(request: Request<T>) {
        requestQueue.add(request)
    }

    fun <T> cancelAll(tag: String? = null) {
        requestQueue.cancelAll(tag)
    }
}