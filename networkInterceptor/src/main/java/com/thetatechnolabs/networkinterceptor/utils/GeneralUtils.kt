package com.thetatechnolabs.networkinterceptor.utils

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.thetatechnolabs.networkinterceptor.databinding.RowParamsBinding
import com.thetatechnolabs.networkinterceptor.ui.details.adapters.CallDetails
import java.io.BufferedWriter
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

internal object GeneralUtils {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    // Takes formatter string and converts string to date time according to formatter string
    @RequiresApi(Build.VERSION_CODES.O)
    fun String.getTime(formatString: String): String =
        ZonedDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS Z"))
            .format(DateTimeFormatter.ofPattern(formatString))

    // Returns seconds in string on a long date time stamp
    @SuppressLint("SimpleDateFormat")
    fun Long.getTime(): String? {
        return try {
            SimpleDateFormat("ss").format(Date(this * 1000))
        } catch (e: Exception) {
            e.toString()
        }
    }

    // Used to map fragments to view pager and tab layout mediator
    fun Int.getPage(): CallDetails = when (this) {
        0 -> CallDetails.INFO
        1 -> CallDetails.REQUEST
        2 -> CallDetails.RESPONSE
        else -> CallDetails.INFO
    }

    // An extension to make view visible
    fun View.show() {
        visibility = View.VISIBLE
    }

    // An extension to make view gone
    fun View.hide() {
        visibility = View.GONE
    }

    // Extension to help binding headers to pagers
    fun RowParamsBinding.bind(header: String?, value: String?) {
        textHeader.text = header
        textValue.text = value
    }

    fun RowParamsBinding.hide() {
        textHeader.hide()
        textValue.hide()
    }

    fun RowParamsBinding.show() {
        textHeader.show()
        textValue.show()
    }

    // Write a line in file
    fun BufferedWriter.writeALine(line: String) {
        write(line)
        newLine()
    }

    // uses arraylist returned by okhttp headers and converts it to usable map
    fun ArrayList<*>.getMapFromArrayList(): Map<String, String>? = if (isEmpty()) {
        null
    } else {
        chunked(2) {
            it[0] as String to it[1] as String
        }.toMap()
    }

    val String.beautifyString: String get() = gson.toJson(JsonParser.parseString(this))
}