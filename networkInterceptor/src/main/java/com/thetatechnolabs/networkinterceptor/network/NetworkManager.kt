package com.thetatechnolabs.networkinterceptor.network

import android.content.Context
import android.os.Build
import com.google.gson.GsonBuilder
import com.thetatechnolabs.networkinterceptor.data.database.entities.Info
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import com.thetatechnolabs.networkinterceptor.data.database.entities.Request
import com.thetatechnolabs.networkinterceptor.data.database.entities.Response
import com.thetatechnolabs.networkinterceptor.data.repositories.NetworkRepo
import com.thetatechnolabs.networkinterceptor.utils.NetworkCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private abstract class NetworkManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    protected suspend fun <T> traceableNetworkCall(networkCall: NetworkCall<T>): NetworkResponse<T> {
        runCatching {
            networkCall.invoke()
        }.onSuccess { response ->
            if (response.isSuccessful) {
                coroutineScope.launch(Dispatchers.IO) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NetworkRepo.getInstance(context).addNetworkCall(
                            NetworkInfo(
                                info = Info(
                                    url = response.raw().request().url().url().toString(),
                                    method = response.raw().request().method(),
                                    status = response.raw().code(),
                                    requestTimeStamp = response.raw().sentRequestAtMillis()
                                        .toString(),
                                    responseTimeStamp = response.raw().receivedResponseAtMillis()
                                        .toString(),
                                    contentType = "${
                                        response.raw().body()?.contentType()?.type()
                                    }/${
                                        response.raw().body()?.contentType()?.subtype()
                                    }",
                                    tookMs = response.raw().receivedResponseAtMillis(),
                                    timeOut = 10
                                ),
                                request = Request(
                                    contentLength = response.raw().body()?.contentLength()
                                        .toString(),
                                    body = response.raw().request().body()?.toString(),
                                    sentRequestAtMillis = response.raw().sentRequestAtMillis(),
                                    curlUrl = ""
                                ).putHeader(response.raw().request().headers()),
                                response = Response(
                                    body = GsonBuilder().setPrettyPrinting().create()
                                        .toJson(response.body()),
                                    receivedResponseAtMillis = response.raw()
                                        .receivedResponseAtMillis(),
                                    isSuccessful = response.isSuccessful,
                                    contentLength = ""
                                ).putHeader(response.headers()),
                                timeStamp = LocalDateTime.now()
                                    .format(DateTimeFormatter.ISO_DATE_TIME)
                            )
                        )
                    }
                }
                return NetworkResponse.Success(response.body()!!)
            }

            var errorBody: String? = null
            runCatching {
                response.errorBody()?.string()
            }.onSuccess {
                errorBody = it
            }.onFailure {
                errorBody = it.localizedMessage
            }

            coroutineScope.launch(Dispatchers.IO) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NetworkRepo.getInstance(context).addNetworkCall(
                        NetworkInfo(
                            info = Info(
                                url = response.raw().request().url().url().toString(),
                                method = response.raw().request().method(),
                                status = response.raw().code(),
                                requestTimeStamp = response.raw().sentRequestAtMillis().toString(),
                                responseTimeStamp = response.raw().receivedResponseAtMillis()
                                    .toString(),
                                contentType = "${
                                    response.raw().body()?.contentType()?.type()
                                }/${
                                    response.raw().body()?.contentType()?.subtype()
                                }", timeOut = 10, tookMs = response.raw().receivedResponseAtMillis()
                            ),
                            request = Request(
                                contentLength = response.raw().body()?.contentLength().toString(),
                                body = response.raw().request().body()?.toString(),
                                sentRequestAtMillis = response.raw().sentRequestAtMillis(),
                                curlUrl = ""
                            ).putHeader(response.raw().request().headers()),
                            response = Response(
                                body = GsonBuilder().setPrettyPrinting().create()
                                    .toJson(response.body()),
                                receivedResponseAtMillis = response.raw()
                                    .receivedResponseAtMillis(),
                                isSuccessful = response.isSuccessful,
                                contentLength = ""
                            ).putHeader(response.headers()),
                            timeStamp = LocalDateTime.now()
                                .format(DateTimeFormatter.ISO_DATE_TIME)
                        )
                    )
                }
            }
            return NetworkResponse.Error(
                "Network call failed with status ${response.code()}, $errorBody, ${response.message()}"
            )
        }.onFailure { exception ->
            /*coroutineScope.launch(Dispatchers.IO) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NetworkRepo.getInstance(context).addNetworkCall(
                        NetworkInfo(
                            info = Info(
                                url = response.raw().request().url().url().toString(),
                                method = response.raw().request().method(),
                                status = response.raw().code(),
                                requestTimeTaken = response.raw().sentRequestAtMillis(),
                                responseTimeTaken = response.raw().receivedResponseAtMillis(),
                                contentType = "${
                                    response.raw().body()?.contentType()?.type()
                                }/${
                                    response.raw().body()?.contentType()?.subtype()
                                }"
                            ),
                            request = Request(
                                contentLength = response.raw().body()?.contentLength(),
                                body = response.raw().request().body()?.toString(),
                                sentRequestAtMillis = response.raw().sentRequestAtMillis()
                            ).putHeader(response.raw().request().headers()),
                            response = Response(
                                body = GsonBuilder().setPrettyPrinting().create()
                                    .toJson(response.body()),
                                receivedResponseAtMillis = response.raw()
                                    .receivedResponseAtMillis(),
                                isSuccessful = response.isSuccessful
                            ).putHeader(response.headers()),
                            timeStamp = LocalDateTime.now()
                                .format(DateTimeFormatter.ISO_DATE_TIME)
                        )
                    )
                }
            }*/
            return NetworkResponse.Error("Network call failed ${exception.localizedMessage}")
        }

        coroutineScope.launch(Dispatchers.IO) {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NetworkRepo.getInstance(context).addNetworkCall(
                    NetworkInfo(
                        info = Info(
                            url = response.raw().request().url().url().toString(),
                            method = response.raw().request().method(),
                            status = response.raw().code(),
                            requestTimeTaken = response.raw().sentRequestAtMillis(),
                            responseTimeTaken = response.raw().receivedResponseAtMillis(),
                            contentType = "${
                                response.raw().body()?.contentType()?.type()
                            }/${
                                response.raw().body()?.contentType()?.subtype()
                            }"
                        ),
                        request = Request(
                            contentLength = response.raw().body()?.contentLength(),
                            body = response.raw().request().body()?.toString(),
                            sentRequestAtMillis = response.raw().sentRequestAtMillis()
                        ).putHeader(response.raw().request().headers()),
                        response = Response(
                            body = GsonBuilder().setPrettyPrinting().create()
                                .toJson(response.body()),
                            receivedResponseAtMillis = response.raw()
                                .receivedResponseAtMillis(),
                            isSuccessful = response.isSuccessful
                        ).putHeader(response.headers()),
                        timeStamp = LocalDateTime.now()
                            .format(DateTimeFormatter.ISO_DATE_TIME)
                    )
                )
            }*/
        }
        return NetworkResponse.Error("Network call failed with unknown behaviour.")
    }
}
