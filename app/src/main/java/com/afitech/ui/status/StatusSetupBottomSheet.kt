package com.afitech.ui.status

import android.app.Dialog
import android.os.Bundle
import com.afitech.databinding.BottomsheetStatusSetupBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StatusSetupBottomSheet(

    private val onConnect: () -> Unit

) : BottomSheetDialogFragment() {

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {

        val binding =
            BottomsheetStatusSetupBinding.inflate(
                layoutInflater
            )

        binding.btnConnect.setOnClickListener {

            dismiss()

            onConnect()
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