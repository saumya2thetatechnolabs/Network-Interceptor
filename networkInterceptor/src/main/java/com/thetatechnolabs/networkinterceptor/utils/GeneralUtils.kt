package com.thetatechnolabs.networkinterceptor.utils

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.thetatechnolabs.networkinterceptor.databinding.RowParamsBinding
import com.thetatechnolabs.networkinterceptor.ui.details.adapters.CallDetails
import java.io.BufferedWriter
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

internal object GeneralUtils {
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
        this.visibility = View.VISIBLE
    }

    // An extension to make view gone
    fun View.hide() {
        this.visibility = View.GONE
    }

    // Extension to help binding headers to pagers
    fun RowParamsBinding.bind(header: String?, value: String?) {
        this.textHeader.text = header
        this.textValue.text = value
    }

    fun RowParamsBinding.hide() {
        this.textHeader.hide()
        this.textValue.hide()
    }

    fun RowParamsBinding.show() {
        this.textHeader.show()
        this.textValue.show()
    }

    // Write a line in file
    fun BufferedWriter.writeALine(line: String) {
        this.write(line)
        this.newLine()
    }
}