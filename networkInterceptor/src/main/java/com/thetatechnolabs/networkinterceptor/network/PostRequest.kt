package com.thetatechnolabs.networkinterceptor.network

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.thetatechnolabs.networkinterceptor.BuildConfig
import com.thetatechnolabs.networkinterceptor.data.repositories.NetworkRepo
import com.thetatechnolabs.networkinterceptor.utils.FailureCallback
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.beautifyString
import com.thetatechnolabs.networkinterceptor.utils.SuccessCallback
import com.thetatechnolabs.networkinterceptor.utils.TestUtils.currentTimeStamp
import kotlinx.coroutines.*
import org.json.JSONObject
import timber.log.Timber
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * Make a POST request and return a parsed object from JSON.
 * @param context required to register data to database
 * @param url URL of the request to make
 * @param modelClass Relevant class object, for Gson's reflection
 * @param requestHeaders Map of request headers
 * @param requestBody json request body
 * @param listener takes in a callback eventually notifying a successful network call
 * @param errorListener takes in a callback eventually notifying a failed network call
 * @author Saumya Macwan (Created on 6th May '22)
 */
class PostRequest<T> constructor(
    private val context: Context,
    url: String,
    private val modelClass: Class<T>,
    private val requestHeaders: MutableMap<String, String>?,
    private val requestParams: MutableMap<String, String>?,
    private val requestBody: JSONObject?,
    private val listener: Response.Listener<T>,
    errorListener: Response.ErrorListener
) : Request<T>(Method.POST, url, errorListener) {
    private lateinit var requestTimeStamp: String
    private var requestTime by Delegates.notNull<Long>()
    private lateinit var responseTimeStamp: String
    private var responseTime by Delegates.notNull<Long>()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getHeaders(): MutableMap<String, String> {
        return requestHeaders?.let {
            requestTime = System.currentTimeMillis()
            requestTimeStamp = currentTimeStamp
            if (BuildConfig.DEBUG) {
                it.forEach { (t, u) ->
                    Timber.tag("Request Header").d("$t : $u")
                }
            }
            it
        } ?: run {
            requestTime = System.currentTimeMillis()
            requestTimeStamp = currentTimeStamp
            val headers = super.getHeaders()
            if (BuildConfig.DEBUG) {
                headers.forEach { (t, u) ->
                    Timber.tag("Default Request Header").d("$t : $u")
                }
            }
            headers
        }
    }

    override fun getParams(): MutableMap<String, String>? {
        return requestParams ?: super.getParams()
    }

    override fun getBody(): ByteArray {
        return requestBody?.toString()?.toByteArray() ?: super.getBody()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {
        return try {
            responseTime = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - requestTime)
            responseTimeStamp = currentTimeStamp
            if (BuildConfig.DEBUG) {
                with(response) {
                    this?.allHeaders?.forEach {
                        Timber.tag("Response Header").d("${it.name} : ${it.value}")
                    }
                    Timber.d("Status Code ${this?.statusCode}")
                    Timber.d("Data - ${String(this?.data!!, Charsets.UTF_8)}")
                    Timber.d("Took time ${this.networkTimeMs}")
                    Timber.d("Modification Flag ${this.notModified}")
                }
            }


            response?.apply {
                val responseHeaders = headers
                scope.launch {
                    with(NetworkRepo.getInstance(context)) {
                        addInfo(
                            url,
                            "POST",
                            statusCode,
                            requestTimeStamp,
                            networkTimeMs,
                            responseTimeStamp,
                            if (responseHeaders != null && responseHeaders.containsKey("Content-Type")) {
                                responseHeaders["Content-Type"]
                            } else {
                                null
                            },
                            timeOut = if (responseHeaders != null
                                && responseHeaders.containsKey("Keep-Alive")
                                && responseHeaders["Keep-Alive"]!!.contains(
                                    "timeout",
                                    ignoreCase = true
                                )
                            ) {
                                responseHeaders["Keep-Alive"]!!.substring(8, 9).toInt()
                            } else {
                                null
                            }
                        )
                        addRequest(
                            requestHeaders,
                            null,
                            requestBody?.toString()?.beautifyString
                                ?: requestParams?.toString()?.beautifyString,
                            requestTime,
                            ""
                        )
                        addResponse(
                            headers,
                            body = data?.let { String(it, Charsets.UTF_8).beautifyString },
                            receivedResponseAtMillis = responseTime,
                            contentLength = data.size.toString(),
                            isSuccessful = true
                        )
                        addNetworkCall()
                    }
                    scope.cancel()
                }
            }

            Response.success(
                Gson().fromJson(
                    String(
                        response?.data ?: ByteArray(0),
                        Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
                    ), modelClass
                ),
                HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch (e: Exception) {
            responseTime = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - requestTime)
            responseTimeStamp = currentTimeStamp
            scope.launch {
                with(NetworkRepo.getInstance(context)) {
                    addInfo(
                        url,
                        "POST",
                        null,
                        requestTimeStamp,
                        responseTime,
                        responseTimeStamp,
                        if (headers.contains("Content-Type")) {
                            headers["Content-Type"]
                        } else {
                            null
                        },
                        timeOut = null
                    )
                    addRequest(
                        headers,
                        null,
                        requestBody?.toString()?.beautifyString
                            ?: requestParams?.toString()?.beautifyString,
                        requestTime,
                        ""
                    )
                    addResponse(
                        mutableMapOf("" to ""),
                        body = null,
                        receivedResponseAtMillis = responseTime,
                        contentLength = "Content-Length for response is unknown"
                    )
                    addNetworkCall()
                }
                scope.cancel()
            }
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(response: T) = listener.onResponse(response)

    private constructor(builder: Builder<T>) : this(
        context = builder.context,
        url = builder.url,
        requestHeaders = builder.headers,
        requestParams = builder.requestParams,
        requestBody = builder.requestBody,
        modelClass = builder.modelClass,
        listener = builder.listener,
        errorListener = builder.errorListener
    )

    companion object {
        inline fun <T> Context.makeAPostRequest(block: Builder<T>.() -> Unit): PostRequest<T> =
            Builder<T>(this).apply(block).build()
    }

    class Builder<T>(val context: Context) {
        lateinit var url: String
        lateinit var modelClass: Class<T>
        lateinit var onSuccess: SuccessCallback<T>
        lateinit var onFailure: FailureCallback
        private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        var headers: MutableMap<String, String>? = null
        var requestParams: MutableMap<String, String>? = null
        var requestBody: JSONObject? = null
        internal val listener: Response.Listener<T> = Response.Listener {
            onSuccess(it)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        internal val errorListener: Response.ErrorListener = Response.ErrorListener {
            with(it) {
                scope.launch {
                    with(NetworkRepo.getInstance(context)) {
                        addInfo(
                            url,
                            "POST",
                            null,
                            null,
                            null,
                            null,
                            if (headers != null && headers?.contains("Content-Type") == true) {
                                headers!!["Content-Type"]
                            } else {
                                null
                            },
                            timeOut = null
                        )
                        addRequest(
                            headers,
                            null,
                            requestBody?.toString()?.beautifyString
                                ?: requestParams?.toString()?.beautifyString,
                            System.currentTimeMillis(),
                            ""
                        )
                        addResponse(
                            volleyHeaders = null,
                            body = null,
                            receivedResponseAtMillis = System.currentTimeMillis(),
                            contentLength = "Content-Length for response is unknown"
                        )
                        addNetworkCall()
                    }
                    scope.cancel()
                }
                onFailure(localizedMessage, message, networkTimeMs)
            }
        }

        fun build() = PostRequest(this@Builder)
    }
}