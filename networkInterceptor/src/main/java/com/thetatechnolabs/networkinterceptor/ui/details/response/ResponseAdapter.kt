package com.thetatechnolabs.networkinterceptor.ui.details.response

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thetatechnolabs.networkinterceptor.databinding.RowParamsBinding
import okhttp3.Headers

internal class ResponseAdapter(private val headers: Headers?) :
    RecyclerView.Adapter<ResponseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(headers?.name(position), headers?.value(position))
    }

    override fun getItemCount(): Int {
        return headers?.size() ?: 0
    }

    class ViewHolder(private val binding: RowParamsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String?, value: String?) {
            binding.bind("[$name]", value)
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