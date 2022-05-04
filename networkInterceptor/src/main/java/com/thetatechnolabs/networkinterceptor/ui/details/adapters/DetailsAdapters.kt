package com.thetatechnolabs.networkinterceptor.ui.details.adapters

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import com.thetatechnolabs.networkinterceptor.ui.details.info.InfoFragment
import com.thetatechnolabs.networkinterceptor.ui.details.request.RequestFragment
import com.thetatechnolabs.networkinterceptor.ui.details.response.ResponseFragment
import com.thetatechnolabs.networkinterceptor.ui.home.NetworkCallListFragment
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.getPage

internal class DetailsAdapters(
    private val networkInfo: NetworkInfo?,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position.getPage()) {
            CallDetails.INFO -> InfoFragment.newInstance().also {
                it.arguments = bundleOf(NetworkCallListFragment.TAG to networkInfo)
            }
            CallDetails.REQUEST -> RequestFragment.newInstance().also {
                it.arguments = bundleOf(NetworkCallListFragment.TAG to networkInfo)
            }
            CallDetails.RESPONSE -> ResponseFragment.newInstance().also {
                it.arguments = bundleOf(NetworkCallListFragment.TAG to networkInfo)
            }
        }
    }
}