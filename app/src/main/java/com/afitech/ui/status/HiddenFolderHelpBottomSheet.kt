package com.afitech.ui.status

import android.app.Dialog
import android.os.Bundle
import com.afitech.databinding.BottomsheetHiddenFolderBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HiddenFolderHelpBottomSheet :
    BottomSheetDialogFragment() {

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {

        val binding =
            BottomsheetHiddenFolderBinding.inflate(
                layoutInflater
            )

        return BottomSheetDialog(
            requireContext()
        ).apply {

            setContentView(
                binding.root
            )
        }
    }
}