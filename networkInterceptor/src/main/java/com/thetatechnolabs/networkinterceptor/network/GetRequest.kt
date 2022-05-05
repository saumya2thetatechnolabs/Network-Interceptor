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
import com.google.gson.JsonSyntaxException
import com.thetatechnolabs.networkinterceptor.BuildConfig
import com.thetatechnolabs.networkinterceptor.data.repositories.NetworkRepo
import com.thetatechnolabs.networkinterceptor.utils.FailureCallback
import com.thetatechnolabs.networkinterceptor.utils.SuccessCallback
import com.thetatechnolabs.networkinterceptor.utils.TestUtils.currentTimeStamp
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * Make a GET request and return a parsed object from JSON.
 *
 * @param url URL of the request to make
 * @param modelClass Relevant class object, for Gson's reflection
 * @param headers Map of request headers
 * @param listener takes in a callback eventually notifying a successful network call
 * @param errorListener takes in a callback eventually notifying a failed network call
 * @author Saumya Macwan (Created on 5th May '22)
 */
class GetRequest<T> constructor(
    private val context: Context,
    url: String,
    private val modelClass: Class<T>,
    private val headers: MutableMap<String, String>?,
    private val listener: Response.Listener<T>,
    errorListener: Response.ErrorListener
) : Request<T>(Method.GET, url, errorListener) {
    private lateinit var requestTimeStamp: String
    private var requestTime by Delegates.notNull<Long>()
    private lateinit var responseTimeStamp: String
    private var responseTime by Delegates.notNull<Long>()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getHeaders(): MutableMap<String, String> =
        headers?.let {
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

    override fun deliverResponse(response: T) = listener.onResponse(response)

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
                scope.launch {
                    with(NetworkRepo.getInstance(context)) {
                        addInfo(
                            url,
                            "GET",
                            statusCode,
                            requestTimeStamp,
                            networkTimeMs,
                            responseTimeStamp,
                            if (headers != null && headers!!.containsKey("Content-Type")) {
                                headers!!["Content-Type"]
                            } else {
                                null
                            },
                            timeOut = if (headers != null
                                && headers!!.containsKey("Keep-Alive")
                                && headers!!["Keep-Alive"]!!.contains(
                                    "timeout",
                                    ignoreCase = true
                                )
                            ) {
                                headers!!["Keep-Alive"]!!.substring(8, 9).toInt()
                            } else {
                                null
                            }
                        )
                        addRequest(
                            this@GetRequest.headers,
                            null,
                            "Request body is empty",
                            requestTime,
                            ""
                        )
                        addResponse(
                            headers,
                            body = data.contentToString(),
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
        } catch (e: UnsupportedEncodingException) {
            responseTime = TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - requestTime)
            responseTimeStamp = currentTimeStamp
            scope.launch {
                with(NetworkRepo.getInstance(context)) {
                    addInfo(
                        url,
                        "GET",
                        null,
                        requestTimeStamp,
                        responseTime,
                        responseTimeStamp,
                        if (headers?.contains("Content-Type") == true) {
                            headers["Content-Type"]
                        } else {
                            null
                        },
                        timeOut = null
                    )
                    addRequest(
                        headers,
                        null,
                        "Request body is empty",
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
        } catch (e: JsonSyntaxException) {
            Response.error(ParseError(e))
        }
    }

    private constructor(builder: Builder<T>) : this(
        context = builder.context,
        url = builder.url,
        modelClass = builder.modelClass,
        headers = builder.headers,
        listener = builder.listener,
        errorListener = builder.errorListener
    )

    companion object {
        inline fun <T> Context.makeAGetRequest(block: Builder<T>.() -> Unit): GetRequest<T> =
            Builder<T>(this).apply(block).build()
    }

    class Builder<T>(val context: Context) {
        lateinit var url: String
        var headers: MutableMap<String, String>? = null
        lateinit var modelClass: Class<T>
        lateinit var onSuccess: SuccessCallback<T>
        lateinit var onFailure: FailureCallback
        internal val listener: Response.Listener<T> = Response.Listener {
            onSuccess(it)
        }
        internal val errorListener: Response.ErrorListener = Response.ErrorListener {
            with(it) {
                onFailure(stackTraceToString(), localizedMessage, message, networkTimeMs)
            }
        }

        fun build() = GetRequest(this@Builder)
    }
}