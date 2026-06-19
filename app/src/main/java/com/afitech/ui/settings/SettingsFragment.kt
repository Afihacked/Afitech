package com.afitech.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.afitech.R
import com.afitech.databinding.FragmentSettingsBinding
import com.afitech.BuildConfig
class SettingsFragment :
    androidx.fragment.app.Fragment(R.layout.fragment_settings) {

    private var _binding:
            FragmentSettingsBinding? = null

    private val binding
        get() = _binding!!

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )
        _binding =
            FragmentSettingsBinding.bind(view)

        _binding =
            FragmentSettingsBinding.bind(
                view
            )

        val autoPasteEnabled =

            AutoAnalyzePreferences
                .isAutoPasteEnabled(
                    requireContext()
                )

        binding.switchAutoPaste.isChecked =
            autoPasteEnabled

        binding.switchAutoAnalyze.isChecked =

            AutoAnalyzePreferences
                .isEnabled(
                    requireContext()
                )
        binding.switchAutoClosePreview.isChecked =

            AutoAnalyzePreferences
                .isAutoClosePreviewEnabled(
                    requireContext()
                )

        binding.switchDownloadNotification.isChecked =

            AutoAnalyzePreferences
                .isDownloadNotificationEnabled(
                    requireContext()
                )

        binding.switchAutoAnalyze.isEnabled =
            autoPasteEnabled
        binding.switchAutoAnalyze.alpha =
            if (autoPasteEnabled) 1f else 0.5f
        binding.txtAutoAnalyzeDesc.text =

            if (autoPasteEnabled) {

                "Automatically analyze TikTok links from clipboard when opening the app or returning to Home."

            } else {

                "Enable Auto Paste first."
            }

        binding.txtAutoAnalyzeDesc.alpha =
            if (autoPasteEnabled) 1f else 0.5f
        binding.switchAutoPaste
            .setOnCheckedChangeListener {

                    _,

                    isChecked ->

                AutoAnalyzePreferences
                    .setAutoPasteEnabled(

                        requireContext(),

                        isChecked
                    )

                binding.switchAutoAnalyze.isEnabled =
                    isChecked
                binding.switchAutoAnalyze.alpha =
                    if (isChecked) 1f else 0.5f
                binding.txtAutoAnalyzeDesc.text =

                    if (autoPasteEnabled) {

                        "Automatically analyze TikTok links from clipboard when opening the app or returning to Home."

                    } else {

                        "Enable Auto Paste first."
                    }

                binding.txtAutoAnalyzeDesc.alpha =
                    if (isChecked) 1f else 0.5f
                if (!isChecked) {

                    binding.switchAutoAnalyze.isChecked =
                        false

                    AutoAnalyzePreferences
                        .setEnabled(

                            requireContext(),

                            false
                        )
                }
            }

        binding.switchAutoAnalyze
            .setOnCheckedChangeListener {

                    _,

                    isChecked ->

                AutoAnalyzePreferences
                    .setEnabled(

                        requireContext(),

                        isChecked
                    )
            }
        binding.switchAutoClosePreview
            .setOnCheckedChangeListener {

                    _,

                    isChecked ->

                AutoAnalyzePreferences
                    .setAutoClosePreviewEnabled(

                        requireContext(),

                        isChecked
                    )
            }

        binding.switchDownloadNotification
            .setOnCheckedChangeListener {

                    _,

                    isChecked ->

                AutoAnalyzePreferences
                    .setDownloadNotificationEnabled(

                        requireContext(),

                        isChecked
                    )
            }

        binding.txtVersion.text =
            BuildConfig.VERSION_NAME

        binding.txtBuildNumber.text =
            BuildConfig.VERSION_CODE.toString()
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}