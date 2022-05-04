package com.thetatechnolabs.networkinterceptor.ui.home.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thetatechnolabs.networkinterceptor.R
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import com.thetatechnolabs.networkinterceptor.databinding.RowNetworkCallBinding
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.getTime
import com.thetatechnolabs.networkinterceptor.utils.NetworkItemClickCallback

internal class NetworkCallListAdapter(private val onNetworkItemClicked: NetworkItemClickCallback) :
    ListAdapter<NetworkInfo, NetworkCallListAdapter.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onNetworkItemClicked)
    }

    class ViewHolder(private val binding: RowNetworkCallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NetworkInfo, onNetworkItemClicked: NetworkItemClickCallback) {
            with(binding) {
                with(item) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        textTime.text = timeStamp.getTime("HH:mm")
                    }
                    textDuration.text = info.tookMs?.let {
                        itemView.resources.getString(
                            R.string.duration_text,
                            it.getTime()
                        )
                    } ?: run {
                        itemView.resources.getString(R.string.placeholder)
                    }
                    textUrl.text = info.url
                    textMethod.text = info.method
                    textContentType.text = info.contentType
                    constraintDecorLayout.setOnClickListener {
                        onNetworkItemClicked(item)
                    }
                    linearTimeLayout.setBackgroundResource(
                        if (response.isSuccessful) {
                            R.drawable.success_background
                        } else {
                            R.drawable.error_background
                        }
                    )
                }
            }
        }

        companion object {
            fun getInstance(parent: ViewGroup) =
                ViewHolder(
                    RowNetworkCallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }
    }

    private class DiffUtilCallback : DiffUtil.ItemCallback<NetworkInfo>() {
        override fun areItemsTheSame(
            oldItem: NetworkInfo,
            newItem: NetworkInfo
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: NetworkInfo,
            newItem: NetworkInfo
        ): Boolean {
            return oldItem == newItem
        }
    }
}