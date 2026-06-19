package com.afitech.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afitech.databinding.BottomsheetPreviewBinding
import com.afitech.ui.player.VideoPreviewActivity
import com.afitech.ui.settings.AutoAnalyzePreferences
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PreviewBottomSheet : com.google.android.material.bottomsheet.BottomSheetDialogFragment() {

    var onDownloadClick:

            ((DownloadEvent.StartDownload) -> Unit)?

            = null
    companion object {

        private const val KEY_TITLE = "title"
        private const val KEY_AUTHOR = "author"
        private const val KEY_DURATION = "duration"
        private const val KEY_URL = "url"
        private const val KEY_THUMBNAIL = "thumbnail"
        private const val KEY_IS_SLIDE = "is_slide"

        private const val KEY_VIDEO_URL =
            "video_url"
        fun newInstance(
            title: String,
            author: String,
            duration: String,
            thumbnail: String,
            url: String,
            isSlide: Boolean,
            videoUrl: String?
        ): PreviewBottomSheet {

            return PreviewBottomSheet().apply {

                arguments = Bundle().apply {

                    putString(KEY_TITLE, title)
                    putString(KEY_AUTHOR, author)
                    putString(KEY_DURATION, duration)
                    putString(KEY_THUMBNAIL, thumbnail)
                    putString(KEY_URL, url)
                    putBoolean(
                        KEY_IS_SLIDE,
                        isSlide
                    )
                    putString(
                        KEY_VIDEO_URL,
                        videoUrl
                    )
                }
            }
        }
    }

    private var _binding: BottomsheetPreviewBinding? = null
    private val binding get() = _binding!!

    override fun getTheme(): Int {
        return com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            BottomsheetPreviewBinding.inflate(
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
        super.onViewCreated(view, savedInstanceState)

        val sourceUrl =

            arguments?.getString(
                KEY_URL
            ) ?: return
        val videoUrl =

            arguments?.getString(
                KEY_VIDEO_URL
            )

        binding.txtTitle.text =
            arguments?.getString(KEY_TITLE)
                ?: "-"

        val author =
            arguments?.getString(KEY_AUTHOR)
                ?: "-"

        val duration =
            arguments?.getString(KEY_DURATION)
                ?: "-"

        binding.txtMeta.text =
            "$author • $duration"

        binding.btnVideo.setOnClickListener {

            onDownloadClick?.invoke(

                DownloadEvent.StartDownload(

                    sourceUrl,

                    com.afitech
                        .data.model
                        .DownloadFormat
                        .VIDEO
                )
            )

            closePreviewIfNeeded()
        }

        binding.btnMusic.setOnClickListener {

            onDownloadClick?.invoke(

                DownloadEvent.StartDownload(

                    sourceUrl,

                    com.afitech
                        .data.model
                        .DownloadFormat
                        .MUSIC
                )
            )

            closePreviewIfNeeded()
        }

        binding.btnCover.setOnClickListener {

            onDownloadClick?.invoke(

                DownloadEvent.StartDownload(

                    sourceUrl,

                    com.afitech
                        .data.model
                        .DownloadFormat
                        .COVER
                )
            )

            closePreviewIfNeeded()
        }

        binding.btnSlides.setOnClickListener {

            onDownloadClick?.invoke(

                DownloadEvent.StartDownload(

                    sourceUrl,

                    com.afitech
                        .data.model
                        .DownloadFormat
                        .SLIDES
                )
            )

            closePreviewIfNeeded()
        }

        val thumbnailUrl =
            arguments?.getString(KEY_THUMBNAIL)
                ?: ""

        Glide.with(this)
            .load(thumbnailUrl)
            .into(binding.imgThumbnail)

        val isSlide =
            arguments?.getBoolean(
                KEY_IS_SLIDE,
                false
            ) ?: false
        binding.imgPlayOverlay.visibility =

            if (isSlide)
                View.GONE
            else
                View.VISIBLE

        binding.chipType.text =
            if (isSlide)
                "SLIDES"
            else
                "VIDEO"
        if (isSlide) {

            binding.btnVideo.visibility =
                View.GONE

            binding.btnCover.visibility =
                View.GONE

            binding.btnSlides.visibility =
                View.VISIBLE

        } else {

            binding.btnVideo.visibility =
                View.VISIBLE

            binding.btnCover.visibility =
                View.VISIBLE

            binding.btnSlides.visibility =
                View.GONE
        }
        if (
            !isSlide &&
            !videoUrl.isNullOrBlank()
        ) {

            binding.imgThumbnail.setOnClickListener {

                binding.imgPlayOverlay.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(80)
                    .withEndAction {

                        startActivity(

                            Intent(
                                requireContext(),
                                com.afitech.ui.player.VideoPreviewActivity::class.java
                            ).putExtra(
                                "video_url",
                                videoUrl
                            )
                        )

                        binding.imgPlayOverlay.scaleX = 1f
                        binding.imgPlayOverlay.scaleY = 1f
                    }
            }
        }
    }

    private fun closePreviewIfNeeded() {

        if (
            com.afitech.ui.settings.AutoAnalyzePreferences
                .isAutoClosePreviewEnabled(
                    requireContext()
                )
        ) {

            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}