package com.thetatechnolabs.networkinterceptor.data.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import okhttp3.Headers

@Parcelize
@Entity(tableName = "response")
internal data class Response(
    @ColumnInfo(name = "headers")
    var headers: String? = "",
    @ColumnInfo(name = "body")
    val body: String?,
    @PrimaryKey
    @ColumnInfo(name = "receivedResponseAtMillis")
    val receivedResponseAtMillis: Long,
    @ColumnInfo(name = "isSuccessful")
    val isSuccessful: Boolean = false,
    @ColumnInfo(name = "contentType")
    val contentLength: String?
) : Parcelable {
    fun putHeader(headers: Headers?): Response {
        this.headers = Gson().toJson(headers)
        return this
    }

    fun getHeader(headers: String?): Headers? {
        return Gson().fromJson(headers, Headers::class.java)
    }
}
