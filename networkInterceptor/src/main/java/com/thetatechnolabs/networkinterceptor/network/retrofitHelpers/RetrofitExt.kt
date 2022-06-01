package com.thetatechnolabs.networkinterceptor.network.retrofitHelpers

import retrofit2.HttpException
import retrofit2.Response
import kotlin.Exception

/**
 * @param networkCall function which returns [Response] is invoked and response is wrapped to our desired type [BaseResponse]
 */
internal fun <T> safeNetworkCall(networkCall: () -> Response<T>): BaseResponse<T> {
    return try {
        val response = networkCall()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            Success(body)
        } else {
            Error(response.message())
        }
    } catch (httpException: HttpException) {
        Error(httpException.message())
    } catch (exception: Exception) {
        BaseException(exception)
    }
}