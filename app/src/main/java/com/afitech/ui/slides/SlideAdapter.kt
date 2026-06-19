package com.afitech.ui.slides

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afitech.data.model.SlideItem
import com.afitech.databinding.ItemSlideBinding
import com.bumptech.glide.Glide

class SlideAdapter(

    private val items: List<SlideItem>,

    private val onSelectionChanged:
        () -> Unit,

    private val onImageClick:
        (Int) -> Unit

) : RecyclerView.Adapter<SlideAdapter.ViewHolder>() {

    inner class ViewHolder(

        private val binding: ItemSlideBinding

    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(item: SlideItem) {

            Glide.with(binding.root)
                .load(item.url)
                .into(binding.imgSlide)

            binding.checkSelect.setOnCheckedChangeListener(null)

            binding.checkSelect.isChecked =
                item.selected

            binding.checkSelect.setOnCheckedChangeListener {

                    _,

                    checked ->

                item.selected = checked

                onSelectionChanged()
            }

            binding.imgSlide.setOnClickListener {

                onImageClick(
                    bindingAdapterPosition
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(

            ItemSlideBinding.inflate(

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

        holder.bind(items[position])
    }

    override fun getItemCount() =
        items.size
}