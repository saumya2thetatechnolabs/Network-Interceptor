package com.thetatechnolabs.networkinterceptor.ui.details.request

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thetatechnolabs.networkinterceptor.databinding.RowParamsBinding
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.bind
import okhttp3.Headers

internal class RequestAdapter(private val headers: Headers) :
    RecyclerView.Adapter<RequestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(headers)
    }

    override fun getItemCount(): Int {
        return headers.size()
    }

    class ViewHolder(private val binding: RowParamsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(headers: Headers) {
            for (index in 0 until headers.size()) {
                with(binding) {
                    this.bind(headers.name(index), headers.value(index))
                }
            }
        }

        companion object {
            fun getInstance(parent: ViewGroup) = ViewHolder(
                RowParamsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}

