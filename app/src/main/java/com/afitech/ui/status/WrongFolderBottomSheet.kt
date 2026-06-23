package com.afitech.ui.status

import android.app.Dialog
import android.os.Bundle
import com.afitech.databinding.BottomsheetWrongFolderBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WrongFolderBottomSheet(

    private val title: String,
    private val message: String,
    private val onRetry: () -> Unit,
    private val onHelp: () -> Unit

) : BottomSheetDialogFragment() {

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {

        val binding =
            BottomsheetWrongFolderBinding.inflate(
                layoutInflater
            )

        binding.txtTitle.text =
            title

        binding.txtMessage.text =
            message

        binding.btnHelp.setOnClickListener {

            dismiss()

            onHelp()
        }

        binding.btnRetry.setOnClickListener {

            dismiss()

            onRetry()
        }

        return BottomSheetDialog(
            requireContext()
        ).apply {

            setContentView(
                binding.root
            )
        }
    }
}