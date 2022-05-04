package com.thetatechnolabs.networkinterceptor.ui.details.menu

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thetatechnolabs.networkinterceptor.R
import com.thetatechnolabs.networkinterceptor.data.database.entities.NetworkInfo
import com.thetatechnolabs.networkinterceptor.databinding.BottomSheetShareBinding
import com.thetatechnolabs.networkinterceptor.utils.GeneralUtils.writeALine
import java.io.File


internal class ShareBottomSheet : BottomSheetDialogFragment() {
    private var clickedItem: NetworkInfo? = null
    private var logFile: File? = null
    private var _binding: BottomSheetShareBinding? = null
    private val binding: BottomSheetShareBinding get() = _binding!!

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clickedItem = arguments?.getParcelable(NETWORK_INFO)
        logFile = getFile()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            shareCurlUrl.setOnClickListener {
                clickedItem?.request?.curlUrl?.let { shareCurlUrl(it) } ?: run {
                    Toast.makeText(
                        requireContext(),
                        "Curl URL is not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            shareLog.setOnClickListener {
                shareLogFile()
            }
        }

        prepareFileToShare()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun shareCurlUrl(textToBeShared: String) {
        startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToBeShared)
            type = "text/plain"
        }, null))
        this.dismiss()
    }

    private fun getFile(): File {
        File.createTempFile(getString(R.string.file_name), ".txt", requireContext().cacheDir)
        return File(requireContext().cacheDir, getString(R.string.file_name))
    }

    private fun prepareFileToShare() {
        logFile?.let { file ->
            file.bufferedWriter().use { writer ->
                with(writer) {

                    // INFO DETAILS
                    newLine()
                    writeALine("** INFO **")
                    newLine()
                    clickedItem?.info?.apply {
                        url.let {
                            writeALine(getString(R.string.url_header))
                            writeALine(it)
                            newLine()
                        }
                        method.let {
                            writeALine(getString(R.string.method_header))
                            writeALine(it)
                            newLine()
                        }
                        status?.let {
                            writeALine(getString(R.string.status_header))
                            writeALine(it.toString())
                            newLine()
                        }
                        requestTimeStamp?.let {
                            writeALine(getString(R.string.request_time_header))
                            writeALine(it)
                            newLine()
                        }
                        responseTimeStamp?.let {
                            writeALine(getString(R.string.response_time_header))
                            writeALine(it)
                            newLine()
                        }
                        timeOut?.let {
                            writeALine(getString(R.string.time_out_header))
                            writeALine(it.toString())
                            newLine()
                        }
                        contentType?.let {
                            writeALine(getString(R.string.content_type_header))
                            writeALine(it)
                            newLine()
                        }
                    }

                    // REQUEST DETAILS
                    newLine()
                    writeALine("** REQUEST **")
                    newLine()
                    clickedItem?.request?.apply {
                        getHeader(headers)?.let {
                            writeALine("-- Headers --")
                            for (index in 0 until it.size()) {
                                writeALine("[${it.name(index)}]")
                                writeALine(it.value(index))
                                newLine()
                            }
                        }
                        contentLength?.let {
                            if (it.isNotEmpty()) {
                                writeALine("Content-Length")
                                writeALine(it)
                            }
                        }
                        newLine()
                        writeALine("-- Body --")
                        writeALine(
                            body
                                ?: getString(R.string.empty_request_body_text)
                        )
                    }

                    // RESPONSE DETAILS
                    newLine()
                    newLine()
                    writeALine("** RESPONSE **")
                    newLine()
                    clickedItem?.response?.apply {
                        getHeader(headers)?.let {
                            writeALine("-- Headers --")
                            for (index in 0 until it.size()) {
                                writeALine("[${it.name(index)}]")
                                writeALine(it.value(index))
                                newLine()
                            }
                        }
                        newLine()
                        writeALine("-- Body --")
                        writeALine(
                            body
                                ?: getString(R.string.empty_response_body_text)
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun shareLogFile() {
        if (logFile != null && logFile!!.exists()) {
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                logFile!!
            )
            val chooser = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/*"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                putExtra(Intent.EXTRA_SUBJECT, clickedItem?.info?.url)
                putExtra(Intent.EXTRA_STREAM, uri)
            }, "Share Simple Log")

            val resInfoList: List<ResolveInfo> =
                requireContext().packageManager
                    .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                requireContext().grantUriPermission(
                    packageName,
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            startActivity(chooser)
            this@ShareBottomSheet.dismiss()
        }
    }

    companion object {
        val TAG: String = ShareBottomSheet::class.java.simpleName
        const val NETWORK_INFO = "NETWORK_INFO"

        fun newInstance(clickedItem: NetworkInfo?): ShareBottomSheet {
            return ShareBottomSheet().apply {
                arguments = bundleOf(NETWORK_INFO to clickedItem)
            }
        }
    }
}