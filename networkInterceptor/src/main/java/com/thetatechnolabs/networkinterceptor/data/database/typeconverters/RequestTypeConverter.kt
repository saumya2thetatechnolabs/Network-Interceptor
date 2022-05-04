package com.thetatechnolabs.networkinterceptor.data.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.thetatechnolabs.networkinterceptor.data.database.entities.Info
import com.thetatechnolabs.networkinterceptor.data.database.entities.Request

internal class RequestTypeConverter {
    /**
     * [fromSource] is used to convert model class into json string
     * @param request is passed to this function which eventually
     * @return returns a [String]
     */
    @TypeConverter
    fun fromSource(request: Request): String {
        return Gson().toJson(request)
    }

    /**
     * [toSource] is used to convert json String to model class
     * @param jsonString is passed to this function which eventually
     * @return returns a [Request], model class
     */
    @TypeConverter
    fun toSource(jsonString: String): Request {
        return Gson().fromJson(jsonString, Request::class.java)
    }
}