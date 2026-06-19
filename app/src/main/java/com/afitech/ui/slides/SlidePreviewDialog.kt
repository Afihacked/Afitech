package com.afitech.ui.slides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.afitech.databinding.DialogSlidePreviewBinding
import androidx.viewpager2.widget.ViewPager2
class SlidePreviewDialog(

    private val images: List<String>,

    private val startPosition: Int

) : androidx.fragment.app.DialogFragment() {

    private var _binding:
            DialogSlidePreviewBinding? = null

    private val binding
        get() = _binding!!
    var onDownloadThis:

            ((String) -> Unit)?

            = null
    override fun onCreateView(

        inflater: LayoutInflater,

        container: ViewGroup?,

        savedInstanceState: Bundle?

    ): View {

        _binding =

            DialogSlidePreviewBinding.inflate(

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
        val adapter =

            SlidePreviewAdapter(
                images
            )

        binding.viewPager.adapter =
            adapter

        binding.viewPager.setCurrentItem(
            startPosition,
            false
        )

        binding.txtCounter.text =

            "${startPosition + 1}/${images.size}"

        binding.viewPager.registerOnPageChangeCallback(

            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(
                    position: Int
                ) {

                    binding.txtCounter.text =

                        "${position + 1}/${images.size}"
                }
            }
        )

        binding.btnClose.setOnClickListener {

            dismiss()
        }

        binding.btnDownloadThis.setOnClickListener {

            val currentPosition =

                binding.viewPager.currentItem

            onDownloadThis?.invoke(

                images[currentPosition]
            )
        }
    }
    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(

            ViewGroup.LayoutParams.MATCH_PARENT,

            ViewGroup.LayoutParams.MATCH_PARENT
        )

        dialog?.window?.setBackgroundDrawableResource(
            android.R.color.transparent
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}