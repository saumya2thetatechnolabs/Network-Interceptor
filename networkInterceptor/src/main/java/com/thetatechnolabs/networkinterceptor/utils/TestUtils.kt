package com.thetatechnolabs.networkinterceptor.utils

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import com.thetatechnolabs.networkinterceptor.ui.home.NetworkCallListFragment
import timber.log.Timber

object TestUtils {
    //Just for testing purpose, register this in touch listeners
    internal fun FragmentActivity.showNetworkLog() {
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
}