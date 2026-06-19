package com.afitech.ui.home

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.afitech.R
import com.afitech.data.local.room.DownloadHistoryEntity
import com.afitech.databinding.ItemHistoryBinding
import com.afitech.ui.history.HistoryDiffCallback
import com.bumptech.glide.Glide
import com.google.android.material.color.MaterialColors
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecentDownloadsAdapter(

    private val onClick:
        (DownloadHistoryEntity) -> Unit

) : ListAdapter<
        DownloadHistoryEntity,
        RecentDownloadsAdapter.ViewHolder
        >(com.afitech.ui.history.HistoryDiffCallback) {

    inner class ViewHolder(

        private val binding:
        ItemHistoryBinding

    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(
            item: DownloadHistoryEntity
        ) {

            binding.txtFileName.text =
                item.fileName

            binding.txtType.text =
                item.fileType

            binding.txtTime.text =

                SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
                ).format(
                    Date(item.savedAt)
                )

            val iconRes = when (
                item.fileType.lowercase()
            ) {

                "videos" ->
                    R.drawable.ic_videos

                "music" ->
                    R.drawable.ic_note

                "slides" ->
                    R.drawable.ic_slides

                "covers" ->
                    R.drawable.ic_covers

                else ->
                    R.drawable.ic_file_save
            }

            val file =
                File(item.filePath)

            if (
                file.exists() &&
                (
                        item.fileType.equals("Videos", true)
                                ||
                                item.fileType.equals("Slides", true)
                                ||
                                item.fileType.equals("Covers", true)
                        )
            ) {

                binding.imgType.imageTintList =
                    null

                Glide.with(binding.root)
                    .load(file)
                    .centerCrop()
                    .into(binding.imgType)

            } else {

                binding.imgType.scaleType =
                    ImageView.ScaleType.FIT_CENTER

                binding.imgType.setPadding(
                    20,
                    20,
                    20,
                    20
                )

                binding.imgType.setImageResource(
                    iconRes
                )

                binding.imgType.imageTintList =
                    ColorStateList.valueOf(

                        MaterialColors.getColor(

                            binding.root,

                            com.google.android.material.R.attr.colorOnSurface

                        )
                    )
            }

            binding.imgArrow.visibility =
                ViewGroup.GONE

            binding.imgSelected.visibility =
                ViewGroup.GONE

            binding.root.setOnClickListener {

                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(

            ItemHistoryBinding.inflate(

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