package com.thetatechnolabs.networkinterceptor.data.database.typeconverters

import androidx.annotation.Keep
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.thetatechnolabs.networkinterceptor.data.database.entities.Info

/**
 * Type converters are used to store custom model classes to database
 */
@Keep
internal class InfoTypeConverter {

    /**
     * [fromSource] is used to convert model class into json string
     * @param info is passed to this function which eventually
     * @return returns a [String]
     */
    @TypeConverter
    fun fromSource(info: Info): String {
        return Gson().toJson(info)
    }

    /**
     * [toSource] is used to convert json String to model class
     * @param jsonString is passed to this function which eventually
     * @return returns a [Info], model class
     */
    @TypeConverter
    fun toSource(jsonString: String): Info {
        return Gson().fromJson(jsonString, Info::class.java)
    }
}