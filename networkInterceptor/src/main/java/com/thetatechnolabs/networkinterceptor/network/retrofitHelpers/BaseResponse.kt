package com.thetatechnolabs.networkinterceptor.network.retrofitHelpers

/**
 * Base sealed interface to have command over generic types
 *
 * Three response handling delegates as below [Success], [Error] and [BaseException]
 */
sealed interface BaseResponse<T>

class Success<T>(val data: T) : BaseResponse<T>
class Error<T>(val message: String?) : BaseResponse<T>
class Exception<T>(val exception: Throwable) : BaseResponse<T>

/**
 * TypeAlias to distinguish between [kotlin.Exception] and [Exception]
 */
typealias BaseException<T> = Exception<T>