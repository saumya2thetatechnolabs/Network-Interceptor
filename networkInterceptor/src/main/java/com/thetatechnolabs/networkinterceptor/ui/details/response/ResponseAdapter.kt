package com.thetatechnolabs.networkinterceptor.ui.details.response

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thetatechnolabs.networkinterceptor.databinding.RowParamsBinding
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.bind
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.getMapFromArrayList

internal class ResponseAdapter(private var headers: Map<String, Any>?) :
    RecyclerView.Adapter<ResponseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.getInstance(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        headers?.let { headerMap ->
            val key = headerMap.keys.elementAt(position)
            val value = headerMap.values.elementAt(position)
            if (key == "namesAndValues") {
                when (value) {
                    is ArrayList<*> -> headers = value.getMapFromArrayList()
                }
            } else {
                when (value) {
                    is String -> holder.bind(key, value)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return headers?.size ?: 0
    }

    class ViewHolder(private val binding: RowParamsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(key: String?, value: String?) {
            binding.bind("[$key]", value)
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