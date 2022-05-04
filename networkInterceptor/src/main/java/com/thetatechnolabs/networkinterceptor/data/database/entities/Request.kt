package com.thetatechnolabs.networkinterceptor.data.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import okhttp3.Headers

@Parcelize
@Entity(tableName = "request")
internal data class Request(
    @ColumnInfo(name = "headers")
    var headers: String? = "",
    @ColumnInfo(name = "contentLength")
    val contentLength: String?,
    @ColumnInfo(name = "body")
    val body: String?,
    @PrimaryKey
    @ColumnInfo(name = "sentRequestAtMillis")
    val sentRequestAtMillis: Long,
    @ColumnInfo(name = "curlUrl")
    val curlUrl: String
) : Parcelable {
    fun putHeader(headers: Headers): Request {
        this.headers = Gson().toJson(headers)
        return this
    }

    fun getHeader(headers: String?): Headers? {
        return Gson().fromJson(headers, Headers::class.java)
    }
}
