package com.thetatechnolabs.networkinterceptor.data.database.entities

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@Entity(tableName = "network_call")
internal data class NetworkInfo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,
    @ColumnInfo(name = "info")
    val info: Info,
    @ColumnInfo(name = "request")
    val request: Request,
    @ColumnInfo(name = "response")
    val response: Response,
    @ColumnInfo(name = "network_call_timeStamp")
    val timeStamp: String
) : Parcelable
