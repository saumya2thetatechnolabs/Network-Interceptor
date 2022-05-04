package com.thetatechnolabs.networkinterceptor.ui.details.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thetatechnolabs.networkinterceptor.R
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import com.thetatechnolabs.networkinterceptor.databinding.FragmentRequestBinding
import com.thetatechnolabs.networkinterceptor.ui.home.NetworkCallListFragment
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.bind
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.hide
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.show

internal class RequestFragment : Fragment() {

    private var _binding: FragmentRequestBinding? = null
    private val binding: FragmentRequestBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val clickedItem =
                checkNotNull(arguments?.getParcelable<NetworkInfo>(NetworkCallListFragment.TAG))
            with(binding) {
                textHeaderRequest.show()
                textBodyHeaderRequest.show()

                clickedItem.request.apply {
                    if (contentLength.isNullOrEmpty()) {
                        textContentLength.hide()
                    } else {
                        textContentLength.show()
                        textContentLength.bind(
                            getString(R.string.content_length_header),
                            contentLength
                        )
                    }
                    textBodyRequest.text =
                        body ?: getString(R.string.empty_request_body_text)
                    RVHeadersListRequest.adapter =
                        getHeader(clickedItem.request.headers)
                            ?.let { RequestAdapter(it) }
                }
            }
        } catch (exception: IllegalStateException) {
            with(binding) {
                textHeaderRequest.hide()
                textBodyHeaderRequest.hide()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = RequestFragment()
    }
}