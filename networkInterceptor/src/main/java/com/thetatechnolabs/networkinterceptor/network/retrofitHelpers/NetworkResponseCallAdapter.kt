package com.thetatechnolabs.networkinterceptor.network.retrofitHelpers

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class NetworkResponseCallAdapter(private val resultType: Type) :
    CallAdapter<Type, Call<BaseResponse<Type>>> {
    override fun responseType(): Type = resultType

    override fun adapt(call: Call<Type>): Call<BaseResponse<Type>> = NetworkResponseCall(call)
}