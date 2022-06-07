package com.thetatechnolabs.networkinterceptor.network.retrofitHelpers

/**
 * To leverage kotlin chaining railway, and easily handle response with TypeChecks
 *
 * @param block takes in a suspend scoped function to execute if [BaseResponse] turns out to be [Success]
 */
@ResponseDelegate
suspend inline fun <T> BaseResponse<T>.onSuccess(
    crossinline block: suspend (T) -> Unit
): BaseResponse<T> =
    apply {
        if (this is Success<T>) {
            block(data)
        }
    }

/**
 * @param block takes in a suspend scoped function to execute if [BaseResponse] turns out to be [Error]
 */
@ResponseDelegate
suspend inline fun <T> BaseResponse<T>.onError(
    block: suspend (String?) -> Unit
): BaseResponse<T> =
    apply {
        if (this is Error<T>) {
            block(message)
        }
    }

/**
 * @param block takes in a suspend scoped function to execute if [BaseResponse] turns out to be [Exception]
 */
@ResponseDelegate
suspend inline fun <T> BaseResponse<T>.onException(
    block: suspend (Throwable) -> Unit
): BaseResponse<T> =
    apply {
        if (this is Exception<T>) {
            block(exception)
        }
    }

/**
 * Transformer function to map the response to [R]
 *
 * @param transformer takes [T] and returns [R], is executed only if response is turned out to be a [Success]
 */
@ResponseDelegate
@Suppress("UNCHECKED_CAST")
inline fun <T, R> BaseResponse<T>.map(
    transformer: (T) -> R
): BaseResponse<R> {
    return if (this is Success) {
        Success(transformer(data))
    } else {
        this as BaseResponse<R>
    }
}