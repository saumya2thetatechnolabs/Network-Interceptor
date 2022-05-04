package com.thetatechnolabs.networkinterceptor.network

internal sealed class NetworkResponse<out R> {
    data class Success<out T>(val networkResponse: T) : NetworkResponse<T>()
    data class Error<out T>(val error: String) : NetworkResponse<T>()
}
