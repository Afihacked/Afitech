package com.afitech.ui.status

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.afitech.data.model.WhatsappStatus
import com.afitech.databinding.ItemStatusBinding
import com.bumptech.glide.Glide

class StatusAdapter(
    private val onClick: (WhatsappStatus) -> Unit
) : ListAdapter<
        WhatsappStatus,
        StatusAdapter.ViewHolder
        >(
    StatusDiffCallback()
) {

    inner class ViewHolder(
        private val binding: ItemStatusBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            item: WhatsappStatus
        ) {

            Glide.with(binding.root)

                .load(item.uri)

                .centerCrop()

                .into(binding.imgThumbnail)

            binding.imgVideo.visibility =

                if (item.isVideo)
                    View.VISIBLE
                else
                    View.GONE

            binding.txtDuration.visibility =

                if (item.isVideo)
                    View.VISIBLE
                else
                    View.GONE

            binding.txtDuration.text =

                formatDuration(
                    item.duration
                )

            binding.root.setOnClickListener {

                onClick(item)
            }
        }
    }

    private fun formatDuration(
        durationMs: Long
    ): String {

        val totalSeconds =
            durationMs / 1000

        val minutes =
            totalSeconds / 60

        val seconds =
            totalSeconds % 60

        return String.format(
            "%02d:%02d",
            minutes,
            seconds
        )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(

            ItemStatusBinding.inflate(

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

        holder.bind(
            getItem(position)
        )
    }
}