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
import com.afitech.utils.StatusFolderValidator
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

                val result =

                    StatusFolderValidator.validate(
                        requireContext(),
                        uri
                    )

                when (result) {

                    StatusFolderValidator.Result.VALID -> {

                        StatusPreferences.saveUri(
                            requireContext(),
                            uri
                        )

                        updateFolderStatus()

                        viewModel.clearCache()

                        viewModel.loadStatuses(
                            forceRefresh = true
                        )
                    }

                    StatusFolderValidator.Result.EMPTY_STATUS_FOLDER -> {

                        StatusPreferences.saveUri(
                            requireContext(),
                            uri
                        )

                        updateFolderStatus()

                        showNoStatusState()
                    }

                    StatusFolderValidator.Result.NOT_WHATSAPP -> {

                        WrongFolderBottomSheet(

                            title = "Wrong Folder Selected",

                            message =
                                "Please select your WhatsApp Status folder.",

                            onRetry = {

                                openFolderPicker()
                            },

                            onHelp = {

                                HiddenFolderHelpBottomSheet()
                                    .show(
                                        childFragmentManager,
                                        "hidden_help"
                                    )
                            }

                        ).show(

                            childFragmentManager,

                            "wrong_folder"
                        )
                    }

                    StatusFolderValidator.Result.NOT_STATUSES -> {

                        WrongFolderBottomSheet(

                            title = "Almost There",

                            message =
                                "Open Media → .Statuses and select that folder.",

                            onRetry = {

                                openFolderPicker()
                            },

                            onHelp = {

                                HiddenFolderHelpBottomSheet()
                                    .show(
                                        childFragmentManager,
                                        "hidden_help"
                                    )
                            }

                        ).show(

                            childFragmentManager,

                            "statuses_folder"
                        )
                    }
                }

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
        if (
            StatusPreferences.getUri(
                requireContext()
            ) == null
        ) {

            showNotConnectedState()

            StatusSetupBottomSheet {

                openFolderPicker()

            }.show(

                childFragmentManager,

                "setup"
            )
        }

        setupRecyclerView()

        binding.swipeRefresh.isEnabled = false

        observeEvents()

        observeLoading()

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

        adapter = StatusAdapter(

            onClick = { status ->

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

                    openImageViewer(status)
                }
            },

            onDownload = { status ->

                viewModel.saveStatus(status)
            }
        )

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
                    val uri =
                        StatusPreferences.getUri(
                            requireContext()
                        )

                    if (uri == null) {

                        showNotConnectedState()

                    } else if (list.isEmpty()) {

                        showNoStatusState()

                    } else {

                        showContentState()

                        applyFilter(currentFilter)
                    }

                    val videos =
                        list.count {
                            it.isVideo
                        }

                    val images =
                        list.size - videos

                    binding.txtStatusInfo.text =
                        "${list.size} Status • $videos Videos • $images Images"
                }
            }
        }
    }

    private fun observeLoading() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.isLoading.collect { loading ->

                    binding.layoutSkeleton.visibility =

                        if (loading)
                            View.VISIBLE
                        else
                            View.GONE

                    binding.rvStatuses.visibility =

                        if (loading)
                            View.INVISIBLE
                        else
                            View.VISIBLE
                }
            }
        }
    }

    private fun updateFolderStatus() {

        val uri =
            StatusPreferences.getUri(
                requireContext()
            )

        val connected =

            uri != null &&
                    requireContext()
                        .contentResolver
                        .persistedUriPermissions
                        .any { it.uri == uri }

        binding.txtFolderStatus.text =

            if (connected)
                "WhatsApp Connected"
            else
                "Connect WhatsApp Folder"

        binding.btnSelectFolder.visibility =

            if (connected)
                View.GONE
            else
                View.VISIBLE
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
                                "Status saved successfully",
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

    private fun openImageViewer(
        status: WhatsappStatus
    ) {

        val intent = Intent(
            Intent.ACTION_VIEW
        ).apply {

            setDataAndType(
                status.uri,
                "image/*"
            )

            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        startActivity(intent)
    }

    private fun showNotConnectedState() {

        binding.layoutEmpty.visibility = View.VISIBLE
        binding.rvStatuses.visibility = View.GONE

        binding.txtEmptyTitle.text =
            "Connect WhatsApp Status"

        binding.txtEmptyDesc.text =
            "One-time setup required"

        binding.btnEmptyAction.text =
            "Connect WhatsApp"

        binding.btnEmptyAction.setOnClickListener {

            StatusSetupBottomSheet {

                openFolderPicker()

            }.show(

                childFragmentManager,

                "setup"
            )
        }
    }
    private fun showNoStatusState() {

        binding.layoutEmpty.visibility = View.VISIBLE
        binding.rvStatuses.visibility = View.GONE

        binding.txtEmptyTitle.text =
            "No Recent Statuses"

        binding.txtEmptyDesc.text =
            "Open WhatsApp and view a friend's status first"

        binding.btnEmptyAction.text =
            "Open WhatsApp"

        binding.btnEmptyAction.setOnClickListener {

            val intent =
                requireContext()
                    .packageManager
                    .getLaunchIntentForPackage(
                        "com.whatsapp"
                    )

            if (intent != null) {

                startActivity(intent)
            }
        }
    }

    private fun openFolderPicker() {

        folderPicker.launch(null)
    }
    private fun showContentState() {

        binding.layoutEmpty.visibility =
            View.GONE

        binding.rvStatuses.visibility =
            View.VISIBLE
    }
    override fun onResume() {
        super.onResume()

        updateFolderStatus()

        syncFilterChip()

        viewModel.clearCache()

        viewModel.loadStatuses(
            forceRefresh = true
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}