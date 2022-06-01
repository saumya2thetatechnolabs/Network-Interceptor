package com.thetatechnolabs.networkinterceptor.network.retrofitHelpers

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class NetworkResponseCall<T>(private val call: Call<T>) : Call<BaseResponse<T>> {
    override fun clone(): Call<BaseResponse<T>> = NetworkResponseCall(call.clone())

    override fun execute(): Response<BaseResponse<T>> = throw NotImplementedError()

    override fun enqueue(callback: Callback<BaseResponse<T>>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val networkResponse = safeNetworkCall { response }
                callback.onResponse(this@NetworkResponseCall, Response.success(networkResponse))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val networkResponse = BaseException<T>(t)
                callback.onResponse(this@NetworkResponseCall, Response.success(networkResponse))
            }
        })
    }

    override fun isExecuted(): Boolean = call.isExecuted

    override fun cancel() {
        call.cancel()
    }

    override fun isCanceled(): Boolean = call.isCanceled

    override fun request(): Request = call.request()

    override fun timeout(): Timeout = call.timeout()
}