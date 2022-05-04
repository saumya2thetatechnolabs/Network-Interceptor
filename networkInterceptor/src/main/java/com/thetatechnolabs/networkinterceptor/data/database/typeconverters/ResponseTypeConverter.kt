package com.thetatechnolabs.networkinterceptor.data.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.thetatechnolabs.networkinterceptor.data.database.entities.Request
import com.thetatechnolabs.networkinterceptor.data.database.entities.Response

internal class ResponseTypeConverter {

    /**
     * [fromSource] is used to convert model class into json string
     * @param response is passed to this function which eventually
     * @return returns a [String]
     */
    @TypeConverter
    fun fromSource(response: Response): String {
        return Gson().toJson(response)
    }

    /**
     * [toSource] is used to convert json String to model class
     * @param jsonString is passed to this function which eventually
     * @return returns a [Response], model class
     */
    @TypeConverter
    fun toSource(jsonString: String): Response {
        return Gson().fromJson(jsonString, Response::class.java)
    }
}