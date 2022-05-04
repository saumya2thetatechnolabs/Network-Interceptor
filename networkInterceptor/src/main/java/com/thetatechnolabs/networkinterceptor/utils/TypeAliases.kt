package com.thetatechnolabs.networkinterceptor.utils

import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import retrofit2.Response

internal typealias NetworkItemClickCallback = (item: NetworkInfo) -> Unit
internal typealias NetworkCall<T> = suspend () -> Response<T>