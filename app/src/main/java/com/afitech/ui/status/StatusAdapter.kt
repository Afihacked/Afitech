package com.afitech.ui.status

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afitech.R
import com.afitech.data.model.WhatsappStatus
import com.afitech.databinding.ItemStatusBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class StatusAdapter(
    private val onClick: (WhatsappStatus) -> Unit,
    private val onDownload: (WhatsappStatus) -> Unit
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
                .thumbnail(0.25f)
                .centerCrop()
                .transition(
                    DrawableTransitionOptions.withCrossFade(180)
                )
                .into(binding.imgThumbnail)

            if (item.isVideo) {

                binding.imgVideo.visibility =
                    View.VISIBLE

                binding.txtDuration.visibility =
                    View.VISIBLE

                binding.txtDuration.text =
                    formatDuration(
                        item.duration
                    )

            } else {

                binding.imgVideo.visibility =
                    View.GONE

                binding.txtDuration.visibility =
                    View.GONE
            }

            binding.root.setOnClickListener {

                onClick(item)
            }

            if (item.isSaved) {

                binding.imgDownload.setImageResource(
                    R.drawable.ic_check_circle
                )

                binding.imgDownload.isEnabled = false

                binding.imgDownload.alpha = 1f

            } else {

                binding.imgDownload.setImageResource(
                    R.drawable.ic_download
                )

                binding.imgDownload.isEnabled = true

                binding.imgDownload.alpha = 1f

                binding.imgDownload.setOnClickListener {

                    onDownload(item)

                    item.isSaved = true

                    notifyItemChanged(
                        bindingAdapterPosition
                    )
                }
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