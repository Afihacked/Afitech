package com.afitech.ui.slides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.afitech.data.model.SlideItem
import com.afitech.databinding.BottomsheetSlidePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class SlidePickerBottomSheet(

    private val images: List<String>

) : com.google.android.material.bottomsheet.BottomSheetDialogFragment() {

    private lateinit var items:
            MutableList<SlideItem>

    private lateinit var adapter:
            SlideAdapter

    var onDownloadSelected:
            ((List<String>) -> Unit)?
            = null

    private var _binding:
            BottomsheetSlidePickerBinding? = null

    private val binding
        get() = _binding!!

    override fun onCreateView(

        inflater: LayoutInflater,

        container: ViewGroup?,

        savedInstanceState: Bundle?

    ): View {

        _binding =

            BottomsheetSlidePickerBinding.inflate(

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

        binding.txtTitle.text =
            "${images.size} Photos"

        binding.rvSlides.layoutManager =

            GridLayoutManager(
                requireContext(),
                2
            )

        items =

            images.map {

                SlideItem(it)

            }.toMutableList()

        adapter =

            SlideAdapter(

                items,

                {
                    updateDownloadButton()
                },

                { position ->

                    val dialog =

                        SlidePreviewDialog(

                            images,

                            position
                        )

                    dialog.onDownloadThis = {

                        onDownloadSelected?.invoke(

                            listOf(it)
                        )
                    }

                    dialog.show(

                        parentFragmentManager,

                        "preview_image"
                    )
                }
            )

        binding.rvSlides.adapter =
            adapter

        binding.btnSelectAll.setOnClickListener {

            items.forEach {

                it.selected = true
            }

            adapter.notifyDataSetChanged()

            updateDownloadButton()
        }

        binding.btnClearAll.setOnClickListener {

            items.forEach {

                it.selected = false
            }

            adapter.notifyDataSetChanged()

            updateDownloadButton()
        }

        binding.btnDownloadSelected.setOnClickListener {

            val selectedUrls =

                items.filter {

                    it.selected

                }.map {

                    it.url
                }

            if (selectedUrls.isEmpty()) {

                Snackbar.make(

                    binding.root,

                    "Pilih minimal 1 foto",

                    Snackbar.LENGTH_SHORT

                ).show()

                return@setOnClickListener
            }

            onDownloadSelected?.invoke(
                selectedUrls
            )

            dismiss()
        }

        updateDownloadButton()
    }

    private fun updateDownloadButton() {

        val selectedCount =

            items.count {

                it.selected
            }

        binding.btnDownloadSelected.text =

            "Download Selected ($selectedCount)"

        binding.btnDownloadSelected.isEnabled =

            selectedCount > 0

        binding.btnDownloadSelected.alpha =

            if (selectedCount > 0)
                1f
            else
                0.5f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}