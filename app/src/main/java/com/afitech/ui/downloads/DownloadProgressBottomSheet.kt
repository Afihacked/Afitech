package com.afitech.ui.downloads

import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.afitech.databinding.BottomsheetDownloadProgressBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DownloadProgressBottomSheet(

   private val downloads:
    MutableList<DownloadItem>

) : com.google.android.material.bottomsheet.BottomSheetDialogFragment() {

    var onSheetDestroyed: (() -> Unit)? = null
    private var _binding:
            BottomsheetDownloadProgressBinding? = null

    private val binding
        get() = _binding!!

    private var adapter:
            DownloadAdapter? = null

    private var completedDownloads = 0

    private val lastProgressMap =
        mutableMapOf<Long, Int>()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        isCancelable = false
    }

    override fun onCreateView(

        inflater: LayoutInflater,

        container: ViewGroup?,

        savedInstanceState: Bundle?

    ): View {

        _binding =

            BottomsheetDownloadProgressBinding.inflate(

                inflater,

                container,

                false
            )

        return binding.root
    }

    override fun onViewCreated(

        view: View,

        savedInstanceState: Bundle?

    ) {

        adapter =
            DownloadAdapter()

        adapter?.submitList(
            downloads.toList()
        )

        binding.rvDownloads.layoutManager =

            LinearLayoutManager(
                requireContext()
            )

        binding.rvDownloads.adapter =
            adapter
        binding.rvDownloads.setHasFixedSize(
            true
        )
        updateTitle()

        binding.btnClose.setOnClickListener {

            dismiss()
        }

        binding.btnOpenFolder.setOnClickListener {

            val intent = Intent(

                DownloadManager.ACTION_VIEW_DOWNLOADS
            )

            startActivity(intent)
        }
    }

    fun addDownload(
        item: DownloadItem
    ) {

        downloads.add(item)

        adapter?.submitList(
            downloads.toList()
        )

        updateTitle()
    }

    private fun updateTitle() {

        if (_binding == null) return

        binding.txtTitle.text =
            "Downloads (${downloads.size})"
    }

    fun updateProgress(

        downloadId: Long,

        progress: Int,

        completed: Boolean

    ) {
        if (_binding == null) return

        val oldProgress =
            lastProgressMap[downloadId]

        if (
            oldProgress == progress &&
            !completed
        ) {
            return
        }

        lastProgressMap[downloadId] =
            progress

        if (_binding == null) return
        val target =

            downloads.find {

                it.downloadId == downloadId
            }

        if (
            completed &&
            target != null &&
            !target.completed
        ) {

            completedDownloads++

            lastProgressMap.remove(
                downloadId
            )
        }

        val index = downloads.indexOfFirst {
            it.downloadId == downloadId
        }

        if (index != -1) {

            downloads[index] =

                downloads[index].copy(

                    progress = progress,

                    completed = completed
                )

            adapter?.submitList(
                downloads.toList()
            )
        }

        if (
            downloads.isNotEmpty() &&
            completedDownloads == downloads.size
        ) {

            _binding?.let {

                it.txtTitle.text =

                    "All Downloads Completed\n\n${downloads.size} files saved"
            }
            _binding?.btnClose?.visibility =
                View.VISIBLE

            _binding?.btnOpenFolder?.visibility =
                View.VISIBLE

            isCancelable = true
//            autoDismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        onSheetDestroyed?.invoke()

        _binding = null
    }
}