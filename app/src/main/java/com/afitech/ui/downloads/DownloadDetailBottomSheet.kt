package com.afitech.ui.downloads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afitech.data.local.room.DownloadHistoryEntity
import com.afitech.databinding.BottomsheetDownloadDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import java.io.File
import androidx.lifecycle.lifecycleScope
import com.afitech.R
import com.afitech.data.repository.HistoryRepository
import com.afitech.ui.player.VideoPreviewActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadDetailBottomSheet(

    private val item: DownloadHistoryEntity

) : com.google.android.material.bottomsheet.BottomSheetDialogFragment() {

    private var _binding:
            BottomsheetDownloadDetailBinding? = null

    private val binding
        get() = _binding!!

    private val historyRepository by lazy {

        HistoryRepository(
            requireContext()
        )
    }

    var onDeleted: (() -> Unit)? = null

    override fun onCreateView(

        inflater: LayoutInflater,

        container: ViewGroup?,

        savedInstanceState: Bundle?

    ): View {

        _binding =

            BottomsheetDownloadDetailBinding.inflate(

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
        val iconRes = when (item.fileType.lowercase()) {

            "videos" ->
                R.drawable.ic_videos

            "music" ->
                R.drawable.ic_note

            "slides" ->
                R.drawable.ic_slides

            "covers" ->
                R.drawable.ic_covers

            else ->
                R.drawable.ic_file_save
        }

        binding.imgType.setImageResource(iconRes)
        binding.txtFileName.text =
            item.fileName

        binding.txtType.text =
            item.fileType
        binding.btnOpen.text =
            if (
                item.fileType.equals(
                    "Videos",
                    true
                )
            ) {
                "Preview Video"
            } else {
                "Open File"
            }
        binding.txtTime.text =

            SimpleDateFormat(

                "dd/MM/yyyy HH:mm",

                Locale.getDefault()

            ).format(
                Date(item.savedAt)
            )

        binding.btnOpen.setOnClickListener {

            val file = File(item.filePath)
            if (

                item.fileType.equals(
                    "Videos",
                    true
                )

            ) {

                startActivity(

                    Intent(
                        requireContext(),
                        VideoPreviewActivity::class.java
                    ).putExtra(

                        "video_url",

                        file.toURI().toString()
                    )
                )

                return@setOnClickListener
            }
            if (!file.exists()) {

                Snackbar.make(

                    binding.root,

                    "File not found",

                    Snackbar.LENGTH_SHORT

                ).show()

                return@setOnClickListener
            }

            try {

                val uri = FileProvider.getUriForFile(

                    requireContext(),

                    "${requireContext().packageName}.provider",

                    file
                )

                val intent = Intent(
                    Intent.ACTION_VIEW
                ).apply {

                    setDataAndType(

                        uri,

                        when {

                            item.fileType.equals(
                                "Videos",
                                true
                            ) -> "video/*"

                            item.fileType.equals(
                                "Music",
                                true
                            ) -> "audio/*"

                            else -> "image/*"
                        }
                    )

                    addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                startActivity(intent)

            } catch (e: Exception) {

                Snackbar.make(

                    binding.root,

                    "No application found",

                    Snackbar.LENGTH_SHORT

                ).show()
            }
        }

        binding.btnShare.setOnClickListener {

            val file = File(item.filePath)

            if (!file.exists()) {

                Snackbar.make(

                    binding.root,

                    "File not found",

                    Snackbar.LENGTH_SHORT

                ).show()

                return@setOnClickListener
            }

            try {

                val uri = FileProvider.getUriForFile(

                    requireContext(),

                    "${requireContext().packageName}.provider",

                    file
                )

                val shareIntent = Intent(
                    Intent.ACTION_SEND
                ).apply {

                    type = when {

                        item.fileType.equals(
                            "Videos",
                            true
                        ) -> "video/*"

                        item.fileType.equals(
                            "Music",
                            true
                        ) -> "audio/*"

                        else -> "image/*"
                    }

                    putExtra(
                        Intent.EXTRA_STREAM,
                        uri
                    )

                    addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                startActivity(

                    Intent.createChooser(

                        shareIntent,

                        "Share File"
                    )
                )

            } catch (e: Exception) {

                Snackbar.make(

                    binding.root,

                    "Unable to share file",

                    Snackbar.LENGTH_SHORT

                ).show()
            }
        }
        binding.btnDelete.setOnClickListener {

            DeleteConfirmBottomSheet(
                count = 1
            ) {

                deleteFile()

            }.show(
                parentFragmentManager,
                "delete_confirm"
            )
        }
    }


    private fun deleteFile() {

        lifecycleScope.launch(
            Dispatchers.IO
        ) {

            try {

                val file = File(
                    item.filePath
                )

                if (file.exists()) {
                    file.delete()
                }

                historyRepository.deleteHistory(
                    item.id
                )

                withContext(
                    Dispatchers.Main
                ) {

                    onDeleted?.invoke()

                    dismiss()
                }

            } catch (e: Exception) {

                withContext(
                    Dispatchers.Main
                ) {

                    Snackbar.make(
                        requireView(),
                        "Delete failed",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}