package com.afitech.ui.downloads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afitech.databinding.BottomSheetDeleteConfirmBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteConfirmBottomSheet(

    private val count: Int,

    private val onConfirm: () -> Unit

) : com.google.android.material.bottomsheet.BottomSheetDialogFragment() {

    private var _binding:
            BottomSheetDeleteConfirmBinding? = null

    private val binding
        get() = _binding!!

    override fun onCreateView(

        inflater: LayoutInflater,

        container: ViewGroup?,

        savedInstanceState: Bundle?

    ): View {

        _binding =

            BottomSheetDeleteConfirmBinding.inflate(

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

        if (count == 1) {

            binding.txtTitle.text =
                "Delete Download"

            binding.txtMessage.text =
                "This file will be removed from your device and download history."

        } else {

            binding.txtTitle.text =
                "Delete Files"

            binding.txtMessage.text =
                "Delete $count selected files?"
        }

        binding.txtMessage.text =
            "Delete $count selected files?"

        binding.btnDelete.setOnClickListener {

            onConfirm.invoke()

            dismiss()
        }

        binding.btnCancel.setOnClickListener {

            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}