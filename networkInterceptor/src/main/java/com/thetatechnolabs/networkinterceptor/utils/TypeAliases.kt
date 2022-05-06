package com.thetatechnolabs.networkinterceptor.utils

import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo

internal typealias NetworkItemClickCallback = (item: NetworkInfo) -> Unit
internal typealias SuccessCallback<T> = (response: T) -> Unit
internal typealias FailureCallback = (
    localizedMessage: String?,
    message: String?,
    networkTimeMs: Long
) -> Unit