package com.thetatechnolabs.networkinterceptor.ui.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thetatechnolabs.networkinterceptor.R
import com.thetatechnolabs.networkinterceptor.databinding.FragmentNetworkCallListBinding
import com.thetatechnolabs.networkinterceptor.ui.details.DetailsActivity
import com.thetatechnolabs.networkinterceptor.ui.home.adapters.NetworkCallListAdapter
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.hide
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.show
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch

internal class NetworkCallListFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNetworkCallListBinding? = null
    private val binding get() = _binding!!

    private lateinit var networkCallListAdapter: NetworkCallListAdapter

    private val viewModel by viewModels<NetworkCallListViewModel>()

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNetworkCallListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.apply {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            Handler(Looper.getMainLooper()).post {
                ((dialog as? BottomSheetDialog)?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout)?.let {
                    BottomSheetBehavior.from(
                        it
                    ).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkCallListAdapter = NetworkCallListAdapter { clickedItem ->
            startActivity(Intent(requireContext(), DetailsActivity::class.java).also {
                it.putExtra(
                    TAG, clickedItem
                )
            })
        }
        with(binding) {
            RVNetworkCallList.adapter = networkCallListAdapter
            progressBar.show()
            etSearchView.addTextChangedListener { text: Editable? ->
                lifecycleScope.launch {
                    viewModel.getFilteredList(text.toString())
                        .collect { networkCallListAdapter.submitList(it) }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.networkCallList.cancellable().collect { list ->
                binding.progressBar.show()
                networkCallListAdapter.submitList(list)
                binding.progressBar.hide()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String by lazy { NetworkCallListFragment::class.java.simpleName }
        fun newInstance() = NetworkCallListFragment()
    }
}