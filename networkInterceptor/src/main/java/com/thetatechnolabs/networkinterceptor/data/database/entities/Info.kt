package com.thetatechnolabs.networkinterceptor.data.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "info")
internal data class Info(
    @PrimaryKey
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "method")
    val method: String,
    @ColumnInfo(name = "status")
    val status: Int?,
    @ColumnInfo(name = "requestTimeStamp")
    val requestTimeStamp: String?,
    @ColumnInfo(name = "responseTimeStamp")
    val responseTimeStamp: String?,
    @ColumnInfo(name = "timeTakenForResponse")
    val tookMs: Long?,
    @ColumnInfo(name = "contentType")
    val contentType: String? = "",
    @ColumnInfo(name = "connectionTimeOut")
    val timeOut: Int?
) : Parcelable
