package com.thetatechnolabs.networkinterceptor.utils

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.thetatechnolabs.networkinterceptor.ui.home.NetworkCallListFragment
import timber.log.Timber
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object TestUtils {
    //Just for testing purpose, register this in touch listeners
    fun FragmentActivity.showNetworkLog() {
        val fragmentManager =
            this.supportFragmentManager
        Handler(Looper.getMainLooper()).postDelayed({
            (fragmentManager.findFragmentByTag(NetworkCallListFragment.TAG) as NetworkCallListFragment?)?.let {
                Timber.tag("StackCount")
                    .e(fragmentManager.backStackEntryCount.toString())
                it.dismiss()
            } ?: run {
                NetworkCallListFragment.newInstance()
                    .show(fragmentManager, NetworkCallListFragment.TAG)
            }
        }, 100)
    }

    val currentTimeStamp: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() = ZonedDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS Z"))
}