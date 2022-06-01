package com.thetatechnolabs.networkinterceptor.network.retrofitHelpers

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class NetworkResponseCallAdapterFactory private constructor() : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }
        val callType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (getRawType(callType) != BaseResponse::class.java) {
            return null
        }

        val resultType = getParameterUpperBound(0, callType as ParameterizedType)
        return NetworkResponseCallAdapter(resultType)
    }

    companion object {
        fun create(): NetworkResponseCallAdapterFactory = NetworkResponseCallAdapterFactory()
    }
}