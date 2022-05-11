package com.thetatechnolabs.networkinterceptor.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * @param context is used to initialize requestQueue on applicationContext
 * @author Saumya Macwan (created on 5th May 22)
 *
 * Modularized class to execute network request using volley with [add] and [cancelAll] which uses [requestQueue]
 */
class Queue constructor(context: Context) {
    companion object {
        // Creating a single instance of [VolleyRequestQueue] to use globally
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

    fun cancelAll(tag: String? = null) {
        requestQueue.cancelAll(tag)
    }
}