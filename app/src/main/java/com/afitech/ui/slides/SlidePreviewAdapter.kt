package com.afitech.ui.slides

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afitech.databinding.ItemPreviewSlideBinding
import com.bumptech.glide.Glide

class SlidePreviewAdapter(

    private val images: List<String>

) : RecyclerView.Adapter<SlidePreviewAdapter.ViewHolder>() {

    inner class ViewHolder(

        private val binding:
        ItemPreviewSlideBinding

    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(url: String) {

            Glide.with(binding.root)
                .load(url)
                .into(binding.imgPreview)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(

            ItemPreviewSlideBinding.inflate(

                LayoutInflater.from(
                    parent.context
                ),

                parent,

                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.bind(images[position])
    }

    override fun getItemCount() =
        images.size
}