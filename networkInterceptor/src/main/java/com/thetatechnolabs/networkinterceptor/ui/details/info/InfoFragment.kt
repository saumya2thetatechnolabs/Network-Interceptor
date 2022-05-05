package com.thetatechnolabs.networkinterceptor.ui.details.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thetatechnolabs.networkinterceptor.R
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import com.thetatechnolabs.networkinterceptor.databinding.FragmentInfoBinding
import com.thetatechnolabs.networkinterceptor.ui.home.NetworkCallListFragment
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.bind
import java.util.concurrent.TimeUnit

internal class InfoFragment : Fragment() {

    private val binding: FragmentInfoBinding get() = _binding!!
    private var _binding: FragmentInfoBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val clickedItem =
                checkNotNull(arguments?.getParcelable<NetworkInfo>(NetworkCallListFragment.TAG))
            with(binding) {
                with(clickedItem.info) {
                    textUrlInfo.bind(getString(R.string.url_header), url)
                    textMethodInfo.bind(getString(R.string.method_header), method)
                    textStatusInfo.bind(
                        getString(R.string.status_header),
                        status?.toString() ?: getString(R.string.placeholder)
                    )
                    textResponseTimeInfo.bind(
                        getString(R.string.response_time_header),
                        responseTimeStamp ?: getString(R.string.placeholder)
                    )
                    textRequestTimeInfo.bind(
                        getString(R.string.request_time_header),
                        requestTimeStamp ?: getString(R.string.placeholder)
                    )
                    textTimeOut.bind(
                        getString(R.string.time_out_header),
                        timeOut?.let { "${TimeUnit.MILLISECONDS.toSeconds(it.toLong())}s" }
                            ?: run { getString(R.string.placeholder) }
                    )
                    textContentType.bind(
                        getString(R.string.content_type_header),
                        contentType ?: getString(R.string.placeholder)
                    )
                }
            }
        } catch (exception: IllegalStateException) {
            with(binding) {
                textUrlInfo.bind(exception.localizedMessage, getString(R.string.placeholder))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = InfoFragment()
    }
}