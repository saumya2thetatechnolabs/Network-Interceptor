package com.thetatechnolabs.networkinterceptor.network

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.thetatechnolabs.networkinterceptor.data.repositories.NetworkRepo
import kotlinx.coroutines.*
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import timber.log.Timber
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * Custom interceptor class to maintain log of network calls
 * @param context is required to use database for network call entries to be logged.
 * @throws Exception when network call fails, catch this exception while firing network call
 */
class NetworkInterceptor constructor(private val context: Context?) : Interceptor {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body()
        val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        var requestContentLength = ""
        var requestContentType = "Request content-type is empty"
        val requestHeaders = request.headers()

        if (requestBody != null) {
            requestBody.contentType()?.let {
                requestContentType = if (requestHeaders["Content-Type"] == null) {
                    "${it.type()}/${it.subtype()}"
                } else {
                    requestHeaders["Content-Type"].toString()
                }
            }
            if (requestBody.contentLength() != -1L) {
                if (requestHeaders["Content-Length"] == null) {
                    requestContentLength = "${requestBody.contentLength()}"
                }
            }
        }

        val headers = StringBuilder()
        for (index in 0 until requestHeaders.size()) {
            headers.append(
                " -H ' ${requestHeaders.name(index)} : ${
                    requestHeaders.value(index)
                } ' \n"
            )
        }

        //Preparing curl URL before requesting API
        val requestBuffer = Buffer()
        requestBody?.writeTo(requestBuffer)
        val params = requestBody?.contentType()?.charset(Charset.forName("UTF-8"))
            ?.let { requestBuffer.readString(it) }
        val curlUrl = "curl -I $headers -X${request.method()} ${request.url()} ${params ?: ""}"

        val startNs = System.nanoTime()
        val requestTimeStamp =
            ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS Z"))
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (exception: Exception) {
            scope.launch {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (context != null) {
                        with(NetworkRepo.getInstance(context)) {
                            addInfo(
                                request.url().toString(),
                                request.method(),
                                null,
                                requestTimeStamp,
                                null,
                                null,
                                requestContentType,
                                chain.connectTimeoutMillis()
                            )
                            addRequest(
                                requestHeaders,
                                requestContentLength,
                                "${requestBody ?: "Request body is empty"}",
                                startNs,
                                curlUrl
                            )
                            addResponse(
                                headers = null,
                                exception.message,
                                startNs,
                                contentLength = "Content-Length for response is unknown"
                            )
                            addNetworkCall()
                        }
                    }
                }
                this.cancel()
            }
            Timber.tag("Network Interceptor")
                .e("Request failed with error ${exception.message} - ${exception.localizedMessage}")
            throw exception
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseTimeStamp =
            ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS Z"))

        val responseBody = response.body()!!
        val contentLength = responseBody.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"

        val responseHeaders = response.headers()
        val responseTemp = responseBody.source()
            .apply { request(Long.MAX_VALUE) }.buffer.clone()
            .readString(
                responseBody.contentType()
                    ?.charset(StandardCharsets.UTF_8)
                    ?: StandardCharsets.UTF_8
            )

        scope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (context != null) {
                    NetworkRepo.getInstance(context).apply {
                        addInfo(
                            url = request.url().toString(),
                            method = request.method(),
                            status = response.code(),
                            requestTimeStamp = requestTimeStamp,
                            responseTimeStamp = responseTimeStamp,
                            contentType = "${
                                response.body()?.contentType()?.type()
                            }/${
                                response.body()?.contentType()?.subtype()
                            }",
                            timeOut = chain.connectTimeoutMillis(),
                            tookMs = tookMs
                        )
                        addRequest(
                            requestHeaders,
                            contentLength = requestContentLength,
                            body = "${requestBody ?: "Request Body is Empty"}",
                            sentRequestAtMillis = response.sentRequestAtMillis() - startNs,
                            curlUrl = curlUrl
                        )
                        addResponse(
                            responseHeaders,
                            body = GsonBuilder().setPrettyPrinting().create()
                                .toJson(JsonParser.parseString(responseTemp)),
                            receivedResponseAtMillis = tookMs,
                            isSuccessful = response.isSuccessful,
                            contentLength = bodySize
                        )
                        addNetworkCall()
                    }
                }
            }
            this.cancel()
        }
        Timber.tag("Network Interceptor")
            .d("Request was successful with code ${response.code()}")
        return response
    }
}