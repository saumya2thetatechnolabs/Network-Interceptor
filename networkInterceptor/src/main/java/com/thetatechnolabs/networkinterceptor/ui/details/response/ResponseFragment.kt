package com.thetatechnolabs.networkinterceptor.ui.details.response

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thetatechnolabs.networkinterceptor.R
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import com.thetatechnolabs.networkinterceptor.databinding.FragmentResponseBinding
import com.thetatechnolabs.networkinterceptor.ui.home.NetworkCallListFragment
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.hide
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.show

internal class ResponseFragment : Fragment() {

    private var _binding: FragmentResponseBinding? = null
    private val binding: FragmentResponseBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResponseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val clickedItem =
                checkNotNull(arguments?.getParcelable<NetworkInfo>(NetworkCallListFragment.TAG))
            with(binding) {
                textHeaderResponse.show()
                textBodyHeaderResponse.show()
                RVHeadersListResponse.adapter =
                    ResponseAdapter(clickedItem.response.getHeader(clickedItem.response.headers))
                textBodyResponse.text =
                    clickedItem.response.body ?: getString(R.string.empty_response_body_text)
            }
        } catch (exception: IllegalArgumentException) {
            with(binding) {
                textHeaderResponse.hide()
                textBodyHeaderResponse.hide()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ResponseFragment()
    }
}