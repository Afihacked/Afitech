package com.afitech.ui.home

import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.afitech.R
import com.afitech.data.model.HomeUiState
import com.afitech.databinding.FragmentHomeBinding
import com.afitech.ui.downloads.DownloadItem
import com.afitech.ui.downloads.DownloadProgressBottomSheet
import com.afitech.ui.slides.SlidePickerBottomSheet
import com.afitech.utils.ClipboardHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import com.afitech.utils.DownloadHelper
import com.afitech.utils.DownloadTracker
import com.afitech.data.repository.HistoryRepository
import com.afitech.ui.downloads.DownloadDetailBottomSheet
import com.afitech.ui.settings.AutoAnalyzePreferences
import com.afitech.utils.TikTokUrlHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers

class HomeFragment : androidx.fragment.app.Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recentAdapter:
            RecentDownloadsAdapter
    private var progressSheet:

            com.afitech.ui.downloads.DownloadProgressBottomSheet?

            = null

    private val savedHistoryIds =
        mutableSetOf<Long>()

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var historyRepository:
            HistoryRepository

    private var lastOfferedClipboard: String? = null

    private var clipboardListener:
            ClipboardManager.OnPrimaryClipChangedListener? = null
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)
        historyRepository =

            HistoryRepository(
                requireContext()
            )
        recentAdapter =

            RecentDownloadsAdapter { item ->

                val sheet =

                    com.afitech.ui.downloads.DownloadDetailBottomSheet(
                        item
                    )

//                sheet.onDeleted = {
//
//                    observeRecentDownloads()
//                }

                sheet.show(

                    parentFragmentManager,

                    "download_detail"
                )
            }

        binding.rvRecentDownloads.layoutManager =

            LinearLayoutManager(
                requireContext()
            )

        binding.rvRecentDownloads.adapter =
            recentAdapter
        binding.btnViewAll.setOnClickListener {

            requireActivity()
                .findViewById<BottomNavigationView>(
                    R.id.bottomNav
                )
                .selectedItemId =
                R.id.downloadsFragment
        }
        observeRecentDownloads()
        observeState()
        binding.root.post {

            checkClipboard()
        }
        binding.root.post {

            updateEndIcon()
        }
        observeEvents()
        observeDownloadEvents()
        startClipboardMonitoring()

        binding.btnAnalyze.setOnClickListener {
            val url =

                binding.edtUrl.text
                    ?.toString()
                    ?.trim()
                    .orEmpty()

            if (
                !com.afitech.utils.TikTokUrlHelper.isValidTikTokUrl(
                    url
                )
            ) {

                binding.inputLayout.error =
                    "Link TikTok tidak valid"

                return@setOnClickListener
            }
            viewModel.analyzeUrl(
                binding.edtUrl.text.toString().trim()
            )

        }

        binding.inputLayout.setEndIconOnClickListener {

            val hasText =

                !binding.edtUrl.text
                    .isNullOrBlank()

            if (hasText) {

                binding.edtUrl.setText("")

            } else {

                pasteClipboardToField()
            }
        }
        binding.edtUrl.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {

                binding.edtUrl.selectAll()
            }
        }
        binding.btnAnalyze.isEnabled = false
        binding.edtUrl.addTextChangedListener {

            val url =
                it?.toString()
                    ?.trim()
                    .orEmpty()

            if (url.isBlank()) {

                binding.inputLayout.error = null

                updateAnalyzeButton()

                updateEndIcon()

                return@addTextChangedListener
            }

            val valid =

                com.afitech.utils.TikTokUrlHelper.isValidTikTokUrl(
                    url
                )

            binding.inputLayout.error =

                if (valid) {

                    null

                } else {

                    "Link TikTok tidak valid"
                }

            updateAnalyzeButton()

            updateEndIcon()
            updateHeroCard()
        }

        updateHeroCard()
        updateAnalyzeButton()
    }

    private fun updateEndIcon() {

        val hasText =

            !binding.edtUrl.text
                .isNullOrBlank()

        if (hasText) {

            binding.inputLayout.setEndIconDrawable(
                R.drawable.ic_close
            )

        } else {

            binding.inputLayout.setEndIconDrawable(
                R.drawable.ic_content_paste
            )
        }
    }
    private fun pasteClipboardToField() {

        val clipboardText =

            com.afitech.utils.ClipboardHelper.getClipboardText(
                requireContext()
            ) ?: return

        val extractedUrl =

            com.afitech.utils.TikTokUrlHelper.extractTikTokUrl(
                clipboardText
            ) ?: return

        binding.edtUrl.setText(
            extractedUrl
        )

        lastOfferedClipboard =
            extractedUrl
    }

    private fun startClipboardMonitoring() {

        val clipboardManager =

            requireContext().getSystemService(
                android.content.Context.CLIPBOARD_SERVICE
            ) as ClipboardManager

        clipboardListener =

            ClipboardManager.OnPrimaryClipChangedListener {

                updateEndIcon()

                checkClipboard()
            }

        clipboardManager.addPrimaryClipChangedListener(

            clipboardListener
        )
    }

    private fun checkClipboard() {

        val clipboardText =

            com.afitech.utils.ClipboardHelper.getClipboardText(
                requireContext()
            ) ?: return

        val extractedUrl =

            com.afitech.utils.TikTokUrlHelper.extractTikTokUrl(
                clipboardText
            ) ?: return
        if (

            !com.afitech.ui.settings.AutoAnalyzePreferences
                .isAutoPasteEnabled(
                    requireContext()
                )

        ) {

            return
        }
        if (
            extractedUrl ==
            lastOfferedClipboard
        ) return

        binding.edtUrl.setText(
            extractedUrl
        )
        if (

            com.afitech.ui.settings.AutoAnalyzePreferences
                .isEnabled(
                    requireContext()
                )

        ) {

            viewModel.analyzeUrl(
                extractedUrl
            )
        }
        lastOfferedClipboard =
            extractedUrl

        updateHeroCard()
    }

    private fun updateAnalyzeButton() {

        val url =

            binding.edtUrl.text
                ?.toString()
                ?.trim()
                .orEmpty()

        when {

            url.isBlank() -> {

                binding.btnAnalyze.text =
                    "Paste a Link First"

                binding.btnAnalyze.isEnabled =
                    false
            }

            !com.afitech.utils.TikTokUrlHelper.isValidTikTokUrl(
                url
            ) -> {

                binding.btnAnalyze.text =
                    "Invalid TikTok Link"

                binding.btnAnalyze.isEnabled =
                    false
            }

            else -> {

                binding.btnAnalyze.text =
                    "Analyze Link"

                binding.btnAnalyze.isEnabled =
                    true
            }
        }
    }

    private fun observeState() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.uiState.collect { state ->

                    when (state) {

                        is HomeUiState.Idle -> {

                            binding.progressAnalyze.visibility =
                                View.GONE

                            updateAnalyzeButton()
                        }

                        is HomeUiState.Loading -> {

                            binding.progressAnalyze.visibility =
                                View.VISIBLE

                            binding.btnAnalyze.isEnabled = false
                            binding.btnAnalyze.text =
                                "Analyzing..."
                        }

                        is HomeUiState.Error -> {

                            binding.progressAnalyze.visibility =
                                View.GONE

                            updateAnalyzeButton()

                        }

                        is HomeUiState.Success -> {

                            binding.progressAnalyze.visibility =
                                View.GONE

                            updateAnalyzeButton()

                        }
                    }
                }
            }
        }
    }

    private fun observeEvents() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.event.collect { event ->

                    when (event) {
                        is HomeEvent.ClearUrlField -> {

                            binding.edtUrl.setText("")
                            binding.btnAnalyze.isEnabled = false
                            binding.inputLayout.error = null

                            updateEndIcon()
                        }

                        is HomeEvent.ShowError -> {

                            Snackbar.make(
                                binding.root,
                                event.message,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        is HomeEvent.OpenPreview -> {

                            val sheet =

                                PreviewBottomSheet.newInstance(

                                    event.preview.title,

                                    event.preview.author,

                                    event.preview.duration,

                                    event.preview.thumbnail,

                                    event.preview.sourceUrl,

                                    event.preview.isSlide,
                                    event.preview.previewVideoUrl
                                )

                            sheet.onDownloadClick = { downloadEvent ->

                                if (
                                    downloadEvent.format ==
                                    com.afitech.data.model.DownloadFormat.SLIDES
                                ) {

                                    viewModel.testSlides(
                                        downloadEvent.url
                                    )

                                } else {

                                    viewModel.startDownload(
                                        downloadEvent.url,
                                        downloadEvent.format
                                    )
                                }
                            }

                            sheet.show(

                                parentFragmentManager,

                                "preview"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeDownloadEvents() {
        val appContext =
            requireContext().applicationContext
        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.downloadEvent.collect {

                    when (it) {

                        is HomeDownloadEvent.StartDownload -> {

                            val fileName: String

                            val folder: String

                            when {

                                it.downloadInfo.mime.startsWith(
                                    "video"
                                ) -> {

                                    fileName =
                                        "video_${
                                            System.currentTimeMillis()
                                        }${it.downloadInfo.ext}"

                                    folder = "Videos"
                                }

                                it.downloadInfo.mime.startsWith(
                                    "audio"
                                ) -> {

                                    fileName =
                                        "music_${
                                            System.currentTimeMillis()
                                        }${it.downloadInfo.ext}"

                                    folder = "Music"
                                }

                                else -> {

                                    fileName =
                                        "cover_${
                                            System.currentTimeMillis()
                                        }${it.downloadInfo.ext}"

                                    folder = "Covers"
                                }
                            }

                            val historyType = folder

                            val downloadId =

                                com.afitech.utils.DownloadHelper.enqueueDownload(

                                    requireContext(),

                                    it.downloadInfo.url,

                                    fileName,

                                    it.downloadInfo.mime,

                                    folder
                                )

                            if (
                                progressSheet == null ||
                                progressSheet?.isAdded != true
                            ) {

                                progressSheet =
                                    com.afitech.ui.downloads.DownloadProgressBottomSheet(
                                        mutableListOf()
                                    )

                                progressSheet?.onSheetDestroyed = {
                                    progressSheet = null
                                }

                                if (
                                    parentFragmentManager.findFragmentByTag(
                                        "download_progress"
                                    ) == null
                                ) {
                                    progressSheet?.show(
                                        parentFragmentManager,
                                        "download_progress"
                                    )
                                }
                            }

                            progressSheet?.addDownload(

                                DownloadItem(

                                    downloadId = downloadId,

                                    fileName = fileName
                                )
                            )

                            com.afitech.utils.DownloadTracker.track(

                                viewLifecycleOwner.lifecycleScope,

                                appContext,

                                downloadId
                            ) { progress, completed ->

                                if (
                                    progressSheet?.isAdded == true
                                ) {

                                    progressSheet?.updateProgress(

                                        downloadId,

                                        progress,

                                        completed
                                    )}

                                if (
                                    completed &&
                                    savedHistoryIds.add(downloadId)

                                ) {
                                    Log.d(
                                        "HISTORY_DEBUG",
                                        "SAVE $downloadId $fileName"
                                    )
                                    viewLifecycleOwner.lifecycleScope.launch(

                                        Dispatchers.IO

                                    ) {

                                        val filePath =

                                            "/storage/emulated/0/Download/AfitechTok/$folder/$fileName"
                                        Log.d(
                                            "SLIDES_HISTORY",
                                            fileName
                                        )
                                        historyRepository.saveHistory(

                                            fileName = fileName,

                                            fileType = historyType,

                                            filePath = filePath
                                        )
                                    }
                                }
                            }
                        }

                        is HomeDownloadEvent.ShowSlides -> {

                            val sheet =

                                com.afitech.ui.slides.SlidePickerBottomSheet(
                                    it.images
                                )

                            sheet.onDownloadSelected = {

                                viewModel.downloadSlides(
                                    it
                                )
                            }

                            sheet.show(

                                parentFragmentManager,

                                "slides"
                            )
                        }

                        is HomeDownloadEvent.StartSlidesDownload -> {
                            if (it.images.isEmpty()) {

                                Snackbar.make(

                                    binding.root,

                                    "Tidak ada foto yang dipilih",

                                    Snackbar.LENGTH_SHORT

                                ).show()

                                return@collect
                            }
                            if (
                                progressSheet == null ||
                                progressSheet?.isAdded != true
                            ) {

                                progressSheet =
                                    com.afitech.ui.downloads.DownloadProgressBottomSheet(
                                        mutableListOf()
                                    )

                                progressSheet?.onSheetDestroyed = {

                                    progressSheet = null
                                }
                                if (
                                    parentFragmentManager.findFragmentByTag(
                                        "download_progress"
                                    ) == null
                                ) {

                                    progressSheet?.show(
                                        parentFragmentManager,
                                        "download_progress"
                                    )
                                }
                            }
                            val sessionId =
                                System.currentTimeMillis()
                            it.images.forEachIndexed {

                                    index,

                                    url ->

                                val fileName =
                                    "slide_${sessionId}_${index + 1}.jpg"

                                val downloadId =

                                    com.afitech.utils.DownloadHelper.enqueueDownload(

                                        requireContext(),

                                        url,

                                        fileName,

                                        "image/jpeg",

                                        "Slides"
                                    )

                                progressSheet?.addDownload(

                                    DownloadItem(

                                        downloadId = downloadId,

                                        fileName = fileName
                                    )
                                )

                                com.afitech.utils.DownloadTracker.track(

                                    viewLifecycleOwner.lifecycleScope,

                                    appContext,

                                    downloadId
                                ) { progress, completed ->

                                    if (
                                        progressSheet?.isAdded == true
                                    ) {

                                        progressSheet?.updateProgress(

                                            downloadId,

                                            progress,

                                            completed
                                        )}

                                    if (
                                        completed &&
                                        savedHistoryIds.add(downloadId)
                                    ) {
                                        Log.d(
                                            "HISTORY_DEBUG",
                                            "SAVE $downloadId $fileName"
                                        )
                                        viewLifecycleOwner.lifecycleScope.launch(

                                            Dispatchers.IO

                                        ) {

                                            val filePath =

                                                "/storage/emulated/0/Download/AfitechTok/Slides/$fileName"
                                            Log.d(
                                                "SLIDES_HISTORY",
                                                fileName
                                            )
                                            historyRepository.saveHistory(

                                                fileName = fileName,

                                                fileType = "Slides",

                                                filePath = filePath
                                            )
                                        }
                                    }
                                }

                            }

                        }

                        is HomeDownloadEvent.ShowError -> {

                            Snackbar.make(

                                binding.root,

                                it.message,

                                Snackbar.LENGTH_SHORT

                            ).show()
                        }
                    }
                }
            }
        }
    }
    private fun observeRecentDownloads() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                historyRepository
                    .observeRecent()
                    .collect { recent ->
                        recentAdapter.submitList(
                            recent
                        )
                        binding.layoutRecentEmpty.visibility =

                            if (recent.isEmpty())
                                View.VISIBLE
                            else
                                View.GONE

                        binding.rvRecentDownloads.visibility =

                            if (recent.isEmpty())
                                View.GONE
                            else
                                View.VISIBLE
                        binding.btnViewAll.visibility =

                            if (recent.isEmpty())
                                View.GONE
                            else
                                View.VISIBLE
                    }
            }
        }
    }

    private fun updateHeroCard() {

        val currentUrl =

            binding.edtUrl.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val clipboardText =

            com.afitech.utils.ClipboardHelper.getClipboardText(
                requireContext()
            ).orEmpty()

        val autoPasteEnabled =

            com.afitech.ui.settings.AutoAnalyzePreferences
                .isAutoPasteEnabled(
                    requireContext()
                )

        when {

            !autoPasteEnabled -> {

                binding.txtHeroSubtitle.text =
                    "Clipboard monitoring disabled"

                binding.txtHeroStatus.text =
                    "⚙ Enable Auto Paste in Settings"

                binding.txtHeroUrl.text = ""
            }

            currentUrl.isBlank() -> {

                val clipboardUrl =

                    com.afitech.utils.TikTokUrlHelper.extractTikTokUrl(
                        clipboardText
                    )

                if (clipboardUrl != null) {

                    binding.txtHeroSubtitle.text =
                        "TikTok link in clipboard"

                    binding.txtHeroStatus.text =
                        "📋 Ready to paste"

                    binding.txtHeroUrl.text =
                        clipboardUrl

                } else {

                    binding.txtHeroSubtitle.text =
                        "Waiting for TikTok link..."

                    binding.txtHeroStatus.text =
                        "Clipboard Empty"

                    binding.txtHeroUrl.text = ""
                }
            }

            currentUrl.contains(
                "tiktok",
                true
            ) ||

                    currentUrl.contains(
                        "vt.",
                        true
                    ) -> {

                val valid =

                    com.afitech.utils.TikTokUrlHelper.isValidTikTokUrl(
                        currentUrl
                    )

                if (valid) {

                    binding.txtHeroSubtitle.text =
                        "TikTok link detected"

                    binding.txtHeroStatus.text =
                        "✓ Ready to analyze"

                    binding.txtHeroUrl.text =
                        currentUrl

                } else {

                    binding.txtHeroSubtitle.text =
                        "Link needs attention"

                    binding.txtHeroStatus.text =
                        "⚠ Invalid TikTok link"

                    binding.txtHeroUrl.text =
                        currentUrl
                }
            }

            else -> {

                binding.txtHeroSubtitle.text =
                    "Waiting for TikTok link..."

                binding.txtHeroStatus.text =
                    "Ready for input"

                binding.txtHeroUrl.text = ""
            }
        }
    }
    override fun onResume() {
        super.onResume()

        checkClipboard()
        updateHeroCard()
    }
        override fun onDestroyView() {

            super.onDestroyView()

            val clipboardManager =

                requireContext().getSystemService(
                    android.content.Context.CLIPBOARD_SERVICE
                ) as ClipboardManager

            clipboardListener?.let {

                clipboardManager.removePrimaryClipChangedListener(
                    it
                )
            }

            _binding = null
        }
    }
