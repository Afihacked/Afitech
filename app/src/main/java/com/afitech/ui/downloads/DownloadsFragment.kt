package com.afitech.ui.downloads

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afitech.R
import com.afitech.databinding.FragmentDownloadsBinding
import com.afitech.ui.history.HistoryAdapter
import kotlinx.coroutines.launch
import java.io.File
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Canvas
import com.afitech.data.local.room.DownloadHistoryEntity
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar

class DownloadsFragment :
    androidx.fragment.app.Fragment(R.layout.fragment_downloads) {

    private var _binding:
            FragmentDownloadsBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel:

            DownloadsViewModel by viewModels {

        DownloadsViewModelFactory(
            requireContext()
        )
    }
    private var emptyAnimator:
            ObjectAnimator? = null
    private lateinit var historyAdapter: com.afitech.ui.history.HistoryAdapter
    private var pendingDeletedItem:
            DownloadHistoryEntity? = null
    private var pendingDeletedPosition =
        -1

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        _binding =
            FragmentDownloadsBinding.bind(view)

        binding.btnSearch.setOnClickListener {

            toggleSearch()
        }
        binding.btnCancelSelection.setOnClickListener {

            historyAdapter.clearSelection()
        }
        binding.btnSelectAll.setOnClickListener {

            historyAdapter.toggleSelectAll()
        }


        binding.rvHistory.layoutManager =

            LinearLayoutManager(
                requireContext()
            )
        historyAdapter =
            com.afitech.ui.history.HistoryAdapter(

                onItemClick = { item ->

                    val sheet =

                        DownloadDetailBottomSheet(
                            item
                        )

                    sheet.onDeleted = {

                        viewModel.loadHistory()
                    }

                    sheet.show(

                        parentFragmentManager,

                        "download_detail"
                    )
                },

                onItemLongClick = { item ->

                    historyAdapter.toggleSelection(
                        item
                    )
                },

                onSelectionChanged = { count ->
                    if (
                        historyAdapter.isAllSelected()
                    ) {

                        binding.btnSelectAll.setImageResource(
                            R.drawable.ic_deselect_all
                        )

                    } else {

                        binding.btnSelectAll.setImageResource(
                            R.drawable.ic_select_all
                        )
                    }
                    if (count > 0) {

                        binding.txtTitle.text =
                            "$count Selected"

                        binding.btnSearch.visibility =
                            View.GONE

                        binding.btnDeleteSelected.visibility =
                            View.VISIBLE

                        binding.btnShareSelected.visibility =
                            View.VISIBLE

                        binding.btnCancelSelection.visibility =
                            View.VISIBLE
                        binding.btnSelectAll.visibility =
                            View.VISIBLE

                    } else {

                        binding.txtTitle.text =
                            "Downloads"

                        binding.btnSearch.visibility =
                            View.VISIBLE

                        binding.btnDeleteSelected.visibility =
                            View.GONE

                        binding.btnShareSelected.visibility =
                            View.GONE

                        binding.btnCancelSelection.visibility =
                            View.GONE
                        binding.btnSelectAll.visibility =
                            View.GONE
                    }
                }
            )
        binding.btnDeleteSelected.setOnClickListener {

            val selectedItems =

                historyAdapter
                    .getSelectedItems()

            if (
                selectedItems.isEmpty()
            ) return@setOnClickListener

            DeleteConfirmBottomSheet(

                selectedItems.size

            ) {

                animateDeleteSelected(
                    selectedItems
                )

            }.show(

                parentFragmentManager,

                "delete_confirm"
            )
        }
        binding.btnShareSelected.setOnClickListener {

            val selectedItems =

                historyAdapter
                    .getSelectedItems()

            if (
                selectedItems.isEmpty()
            ) return@setOnClickListener

            shareSelectedItems(
                selectedItems
            )
        }

        binding.rvHistory.adapter =
            historyAdapter
        viewModel.loadHistory()
        attachSwipeToDelete()
        binding.edtSearch.addTextChangedListener(

            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    viewModel.searchHistory(

                        s?.toString()
                            ?: ""
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )

        binding.chipAll.setOnClickListener {

            viewModel.filterByType(
                "All"
            )
        }

        binding.chipVideos.setOnClickListener {

            viewModel.filterByType(
                "Videos"
            )
        }

        binding.chipMusic.setOnClickListener {

            viewModel.filterByType(
                "Music"
            )
        }

        binding.chipSlides.setOnClickListener {

            viewModel.filterByType(
                "Slides"
            )
        }

        binding.chipCovers.setOnClickListener {

            viewModel.filterByType(
                "Covers"
            )
        }
        binding.chipStatusVideo.setOnClickListener {

            viewModel.filterByType(
                "Status Video"
            )
        }

        binding.chipStatusImage.setOnClickListener {

            viewModel.filterByType(
                "Status Image"
            )
        }

        binding.swipeRefresh.setOnRefreshListener {

            viewModel.loadHistory(
                syncFiles = true
            )

            binding.swipeRefresh.isRefreshing =
                false
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

            viewModel.history.collect { history ->
                binding.swipeRefresh.isEnabled =
                    history.isNotEmpty()
                updateStats(history)

                if (history.isEmpty()) {

                    binding.layoutEmpty.visibility =
                        View.VISIBLE
                    binding.layoutEmpty.apply {

                        alpha = 0f

                        translationY = 30f

                        animate()

                            .alpha(1f)

                            .translationY(0f)

                            .setDuration(350)

                            .start()
                    }
                    emptyAnimator?.cancel()

                    emptyAnimator =
                        ObjectAnimator.ofFloat(

                            binding.imgEmpty,

                            "translationY",

                            0f,

                            -12f,

                            0f

                        ).apply {

                            duration = 2200

                            repeatCount =
                                ValueAnimator.INFINITE

                            start()
                        }
                    binding.rvHistory.visibility =
                        View.GONE

                } else {

                    emptyAnimator?.cancel()

                    emptyAnimator = null

                    binding.layoutEmpty.visibility =
                        View.GONE

                    binding.rvHistory.visibility =
                        View.VISIBLE

                    historyAdapter.updateData(
                        history
                    )
                }
            }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.removedFilesEvent.collect {

                        count ->

                    Snackbar.make(

                        binding.root,

                        "$count file sudah dihapus dari perangkat",

                        Snackbar.LENGTH_LONG

                    ).show()
                }
            }
        }
    }

    private fun toggleSearch() {

        if (binding.searchLayout.visibility == View.GONE) {

            binding.searchLayout.apply {

                alpha = 0f

                translationY = -30f

                visibility = View.VISIBLE

                animate()

                    .alpha(1f)

                    .translationY(0f)

                    .setDuration(250)

                    .setInterpolator(
                        AccelerateDecelerateInterpolator()
                    )

                    .start()
            }

            binding.edtSearch.requestFocus()

        } else {

            binding.searchLayout.animate()

                .alpha(0f)

                .translationY(-30f)

                .setDuration(200)

                .withEndAction {

                    binding.searchLayout.visibility =
                        View.GONE
                }

                .start()
        }
    }

    private fun updateStats(

        history: List<com.afitech.data.local.room.DownloadHistoryEntity>

    ) {

        val videos =

            history.count {

                it.fileType.equals(
                    "Videos",
                    true
                )
            }

        val music =

            history.count {

                it.fileType.equals(
                    "Music",
                    true
                )
            }

        val slides =

            history.count {

                it.fileType.equals(
                    "Slides",
                    true
                )
            }

        val covers =

            history.count {

                it.fileType.equals(
                    "Covers",
                    true
                )
            }

        val statusVideos =

            history.count {

                it.fileType.equals(
                    "Status Video",
                    true
                )
            }

        val statusImages =

            history.count {

                it.fileType.equals(
                    "Status Image",
                    true
                )
            }

        binding.txtTotalFiles.text =

            "Total Files: ${history.size}"

        binding.txtStats.text =

            "Videos $videos • " +
                    "Music $music • " +
                    "Slides $slides • " +
                    "Covers $covers • " +
                    "Status Video $statusVideos • " +
                    "Status Image $statusImages"

        var totalBytes = 0L

        history.forEach {

            val file = File(it.filePath)

            if (file.exists()) {

                totalBytes += file.length()
            }
        }

        val storageText = when {

            totalBytes >= 1024L * 1024L * 1024L ->

                String.format(

                    "%.2f GB",

                    totalBytes / 1024f / 1024f / 1024f
                )

            totalBytes >= 1024L * 1024L ->

                String.format(

                    "%.2f MB",

                    totalBytes / 1024f / 1024f
                )

            else ->

                String.format(

                    "%.2f KB",

                    totalBytes / 1024f
                )
        }

        binding.txtStorageUsed.text =

            "Storage Used: $storageText"
    }

    private fun attachSwipeToDelete() {

        val callback =

            object : ItemTouchHelper.SimpleCallback(

                0,

                ItemTouchHelper.LEFT

            ) {

                override fun onChildDraw(

                    c: Canvas,

                    recyclerView: RecyclerView,

                    viewHolder: RecyclerView.ViewHolder,

                    dX: Float,

                    dY: Float,

                    actionState: Int,

                    isCurrentlyActive: Boolean

                ) {

                    val itemView =
                        viewHolder.itemView

                    val bgPaint = Paint().apply {

                        color =
                            MaterialColors.getColor(

                                recyclerView,

                                com.google.android.material.R.attr.colorSurfaceContainerHigh

                            )
                    }

                    c.drawRoundRect(

                        itemView.right + dX,

                        itemView.top + 12f,

                        itemView.right.toFloat(),

                        itemView.bottom - 12f,

                        24f,

                        24f,

                        bgPaint
                    )

                    super.onChildDraw(

                        c,

                        recyclerView,

                        viewHolder,

                        dX,

                        dY,

                        actionState,

                        isCurrentlyActive

                    )
                }

                override fun onMove(

                    recyclerView: RecyclerView,

                    viewHolder: RecyclerView.ViewHolder,

                    target: RecyclerView.ViewHolder

                ) = false

                override fun onSwiped(

                    viewHolder: RecyclerView.ViewHolder,

                    direction: Int

                ) {

                    val position =
                        viewHolder.bindingAdapterPosition

                    pendingDeletedPosition =
                        position

                    val item =
                        historyAdapter.getItemAt(
                            position
                        )

                    pendingDeletedItem =
                        item

                    viewHolder.itemView.animate()

                        .alpha(0f)

                        .setDuration(120)

                        .withEndAction {

                            historyAdapter.removeItem(item)
                        }

                        .start()

                    val snackbar = Snackbar.make(

                        binding.root,

                        "${item.fileName} deleted",

                        Snackbar.LENGTH_LONG

                    )

                    snackbar.setAction("UNDO") {

                        pendingDeletedItem?.let {

                            historyAdapter.restoreItem(

                                it,

                                pendingDeletedPosition

                            )
                        }

                        pendingDeletedItem = null


                    }

                    snackbar.setAnchorView(

                        requireActivity().findViewById(
                            R.id.bottomNav
                        )
                    )

                    snackbar.animationMode =
                        Snackbar.ANIMATION_MODE_SLIDE

                    snackbar.addCallback(

                        object : Snackbar.Callback() {

                            override fun onDismissed(

                                transientBottomBar: Snackbar?,

                                event: Int

                            ) {

                                if (
                                    event == DISMISS_EVENT_ACTION
                                ) {
                                    return
                                }

                                pendingDeletedItem?.let { item ->

                                    lifecycleScope.launch {

                                        try {

                                            val file =
                                                File(item.filePath)

                                            if (
                                                file.exists()
                                            ) {

                                                file.delete()
                                            }

                                        } catch (_: Exception) {
                                        }

                                        viewModel.deleteHistory(
                                            item.id
                                        )
                                    }
                                }

                                pendingDeletedItem = null
                            }
                        }
                    )

                    snackbar.show()
                }
            }

        ItemTouchHelper(
            callback
        ).attachToRecyclerView(
            binding.rvHistory
        )
    }

    private fun deleteSelectedItems(

        items: List<DownloadHistoryEntity>

    ) {

        viewLifecycleOwner.lifecycleScope.launch {

            items.forEach { item ->

                try {

                    val file =
                        File(item.filePath)

                    if (
                        file.exists()
                    ) {

                        file.delete()
                    }

                } catch (_: Exception) {
                }
            }

            viewModel.deleteManyHistory(

                items.map {
                    it.id
                }
            )

            historyAdapter.clearSelection()
        }
    }
    private fun shareSelectedItems(

        items: List<DownloadHistoryEntity>

    ) {

        val uris = mutableListOf<Uri>()

        items.forEach { item ->

            try {

                val file =
                    File(item.filePath)

                if (
                    file.exists()
                ) {

                    val uri =

                        FileProvider.getUriForFile(

                            requireContext(),

                            "${requireContext().packageName}.provider",

                            file
                        )

                    uris.add(uri)
                }

            } catch (_: Exception) {
            }
        }

        if (
            uris.isEmpty()
        ) return

        val shareIntent = Intent(

            Intent.ACTION_SEND_MULTIPLE

        ).apply {

            type = "*/*"

            putParcelableArrayListExtra(

                Intent.EXTRA_STREAM,

                ArrayList(uris)
            )

            addFlags(

                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        startActivity(

            Intent.createChooser(

                shareIntent,

                "Share Files"
            )
        )
    }
    private fun animateDeleteSelected(

        items: List<DownloadHistoryEntity>

    ) {

        binding.rvHistory.animate()

            .alpha(0.7f)

            .setDuration(150)

            .withEndAction {

                deleteSelectedItems(items)

                binding.rvHistory.alpha = 1f
            }

            .start()
    }
    override fun onResume() {

        super.onResume()

        viewModel.loadHistory(
            syncFiles = true
        )
    }

    override fun onDestroyView() {

        emptyAnimator?.cancel()
        emptyAnimator = null

        super.onDestroyView()

        _binding = null
    }
}