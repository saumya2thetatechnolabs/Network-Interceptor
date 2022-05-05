package com.thetatechnolabs.networkinterceptor.data.repositories

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.thetatechnolabs.networkinterceptor.data.database.NetworkDatabase
import com.thetatechnolabs.networkinterceptor.data.database.entities.Info
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import com.thetatechnolabs.networkinterceptor.data.database.entities.Request
import com.thetatechnolabs.networkinterceptor.data.database.entities.Response
import kotlinx.coroutines.flow.Flow
import okhttp3.Headers
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal class NetworkRepo(context: Context) {
    // Repo to manage data access to the views
    private val database = NetworkDatabase.getInstance(context)
    private val networkCallDao = database.networkCallDao()
    private lateinit var info: Info
    private lateinit var request: Request
    private lateinit var response: Response

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addNetworkCall() {
        networkCallDao.insertItem(
            NetworkInfo(
                info = info, response = response, request = request, timeStamp = ZonedDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS Z"))
            )
        )
    }

    fun addInfo(
        url: String,
        method: String,
        status: Int?,
        requestTimeStamp: String?,
        tookMs: Long?,
        responseTimeStamp: String?,
        contentType: String?,
        timeOut: Int?
    ) {
        info = Info(
            url = url,
            method = method,
            status = status,
            requestTimeStamp = requestTimeStamp,
            tookMs = tookMs,
            responseTimeStamp = responseTimeStamp,
            contentType = contentType,
            timeOut = timeOut
        )
    }

    fun addRequest(
        headers: Headers,
        contentLength: String?,
        body: String?,
        sentRequestAtMillis: Long,
        curlUrl: String
    ) {
        request = Request(
            contentLength = contentLength,
            body = body,
            sentRequestAtMillis = sentRequestAtMillis,
            curlUrl = curlUrl
        ).putHeader(headers)
    }

    fun addResponse(
        headers: Headers?,
        body: String?,
        receivedResponseAtMillis: Long,
        isSuccessful: Boolean = false,
        contentLength: String?
    ) {
        response = Response(
            body = body,
            receivedResponseAtMillis = receivedResponseAtMillis,
            isSuccessful = isSuccessful,
            contentLength = contentLength
        ).putHeader(headers)
    }

    fun getNetworkCallList(): Flow<List<NetworkInfo>> {
        return networkCallDao.getNetworkCallList()
    }

    companion object {
        private var INSTANCE: NetworkRepo? = null

        fun getInstance(context: Context): NetworkRepo = INSTANCE ?: synchronized(this) {
            return INSTANCE ?: NetworkRepo(context).also { INSTANCE = it }
        }
    }
}