package com.afitech.ui.status

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.afitech.R
import com.afitech.data.model.WhatsappStatus
import com.afitech.databinding.FragmentStatusBinding
import com.afitech.ui.player.VideoPreviewActivity
import com.afitech.utils.StatusPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class StatusFragment : Fragment(
    R.layout.fragment_status
) {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StatusAdapter

    private val viewModel: StatusViewModel by viewModels()

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                viewModel.loadStatuses()
            }
        }

    private val folderPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->

            uri ?: return@registerForActivityResult

            try {

                requireContext()
                    .contentResolver
                    .takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )

                StatusPreferences.saveUri(
                    requireContext(),
                    uri
                )

                updateFolderStatus()

                viewModel.loadStatuses()

            } catch (e: Exception) {

                Snackbar.make(
                    binding.root,
                    e.message ?: "Failed to connect folder",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    private var allStatuses =
        emptyList<WhatsappStatus>()

    private var currentFilter = "All"

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        _binding = FragmentStatusBinding.bind(view)

        updateFolderStatus()

        setupRecyclerView()
        binding.swipeRefresh.isEnabled = false
        observeEvents()

        observeStatuses()

        binding.btnSelectFolder.setOnClickListener {

            folderPicker.launch(null)
        }
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->

            currentFilter = when {

                checkedIds.contains(
                    binding.chipVideos.id
                ) -> "Videos"

                checkedIds.contains(
                    binding.chipImages.id
                ) -> "Images"

                else -> "All"
            }

            applyFilter(currentFilter)
        }

        viewModel.loadStatuses()
    }

    private fun setupRecyclerView() {

        adapter = StatusAdapter { status ->

            if (status.isVideo) {

                val intent = Intent(
                    requireContext(),
                    VideoPreviewActivity::class.java
                )

                intent.putExtra(
                    "video_uri",
                    status.uri.toString()
                )
                intent.putExtra(
                    "file_name",
                    status.name
                )
                startActivity(intent)

            } else {

                val intent = Intent(
                    requireContext(),
                    ImagePreviewActivity::class.java
                )

                intent.putExtra(
                    "uri",
                    status.uri.toString()
                )

                intent.putExtra(
                    "name",
                    status.name
                )

                startActivity(intent)
            }
        }

        binding.rvStatuses.layoutManager =
            GridLayoutManager(
                requireContext(),
                3
            )

        binding.rvStatuses.adapter =
            adapter

        binding.rvStatuses.setHasFixedSize(
            true
        )

        binding.rvStatuses.itemAnimator =
            null
    }

    private fun observeStatuses() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.statuses.collect { list ->

                    allStatuses = list

                    applyFilter(currentFilter)

                    binding.layoutEmpty.visibility =
                        if (list.isEmpty())
                            View.VISIBLE
                        else
                            View.GONE

                    binding.rvStatuses.visibility =
                        if (list.isEmpty())
                            View.GONE
                        else
                            View.VISIBLE

                    val videos =
                        list.count {
                            it.isVideo
                        }

                    val images =
                        list.size - videos

                    binding.txtStatusCount.text =
                        "Available Statuses: ${list.size}"

                    binding.txtStatusInfo.text =
                        "Videos $videos • Images $images"
                }
            }
        }
    }

    private fun updateFolderStatus() {

        val uri =
            StatusPreferences.getUri(
                requireContext()
            )

        binding.txtFolderStatus.text =

            if (uri == null)
                "Not Connected"
            else
                "Connected"
    }

    private fun observeEvents() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.event.collect {

                    when (it) {

                        is StatusEvent.Saved -> {

                            Snackbar.make(
                                binding.root,
                                "Saved: ${it.fileName}",
                                Snackbar.LENGTH_SHORT
                            ).show()

                            viewModel.loadStatuses()
                        }

                        is StatusEvent.Error -> {

                            Snackbar.make(
                                binding.root,
                                it.message,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun applyFilter(
        type: String
    ) {

        val filtered = when(type) {

            "Videos" -> {

                allStatuses.filter {
                    it.isVideo
                }
            }

            "Images" -> {

                allStatuses.filter {
                    !it.isVideo
                }
            }

            else -> {

                allStatuses
            }
        }

        adapter.submitList(
            filtered.toList()
        )

        binding.layoutEmpty.visibility =

            if (filtered.isEmpty())
                View.VISIBLE
            else
                View.GONE

        binding.rvStatuses.visibility =

            if (filtered.isEmpty())
                View.GONE
            else
                View.VISIBLE
    }

    private fun syncFilterChip() {

        when (currentFilter) {

            "Videos" -> {

                binding.chipVideos.isChecked = true
            }

            "Images" -> {

                binding.chipImages.isChecked = true
            }

            else -> {

                binding.chipAll.isChecked = true
            }
        }

        applyFilter(currentFilter)
    }

    override fun onResume() {
        super.onResume()

        updateFolderStatus()

        syncFilterChip()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}