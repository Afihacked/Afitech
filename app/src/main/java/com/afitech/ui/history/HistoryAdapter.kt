package com.afitech.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.afitech.R
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afitech.data.local.room.DownloadHistoryEntity
import com.afitech.databinding.ItemHistoryBinding
import com.bumptech.glide.Glide
import com.google.android.material.color.MaterialColors
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(

    private val onItemClick:
        (DownloadHistoryEntity) -> Unit,

    private val onItemLongClick:
        (DownloadHistoryEntity) -> Unit,

    private val onSelectionChanged:
        (Int) -> Unit

) : ListAdapter<
        DownloadHistoryEntity,
        HistoryAdapter.ViewHolder
        >(HistoryDiffCallback) {

    private val selectedItems =
        mutableSetOf<Long>()

    private var restoredItemId:
            Long? = null
    private var selectionMode =
        false

    inner class ViewHolder(

        private val binding: ItemHistoryBinding

    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(item: DownloadHistoryEntity) {

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

            val file = File(item.filePath)

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

                binding.imgType.setPadding(
                    0,
                    0,
                    0,
                    0
                )

                binding.imgType.imageTintList = null

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
                    android.content.res.ColorStateList.valueOf(

                        MaterialColors.getColor(

                            binding.root,

                            com.google.android.material.R.attr.colorOnSurface

                        )
                    )
            }

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

                Glide.with(binding.root)
                    .load(file)
                    .centerCrop()
                    .into(binding.imgType)

            } else {

                binding.imgType.setImageResource(
                    iconRes
                )
            }

            val selected =
                selectedItems.contains(
                    item.id
                )

            binding.imgSelected.visibility =
                if (selected)
                    View.VISIBLE
                else
                    View.GONE

            binding.imgArrow.visibility =
                if (selected)
                    View.GONE
                else
                    View.VISIBLE

            if (selected) {

                binding.root.strokeWidth = 2

                binding.root.strokeColor =

                    MaterialColors.getColor(

                        binding.root,

                        com.google.android.material.R.attr.colorPrimaryContainer
                    )

                binding.root.cardElevation = 6f

                binding.imgSelected.visibility =
                    View.VISIBLE

            } else {

                binding.root.strokeWidth = 0

                binding.root.cardElevation = 2f

                binding.imgSelected.visibility =
                    View.GONE
            }

            binding.root.setOnClickListener {

                if (selectionMode) {

                    toggleSelection(item)

                } else {

                    onItemClick(item)
                }
            }

            binding.root.setOnLongClickListener {

                onItemLongClick(item)

                true
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

        val item =
            getItem(position)

        holder.bind(item)

        if (
            restoredItemId == item.id
        ) {

            holder.itemView.translationX =
                300f

            holder.itemView.animate()

                .translationX(0f)

                .setDuration(250)

                .start()

            restoredItemId = null
        }
    }

    fun updateData(
        newItems: List<DownloadHistoryEntity>
    ) {

        submitList(newItems)
    }

    fun toggleSelection(
        item: DownloadHistoryEntity
    ) {

        selectionMode = true

        if (
            selectedItems.contains(
                item.id
            )
        ) {

            selectedItems.remove(
                item.id
            )

        } else {

            selectedItems.add(
                item.id
            )
        }

        if (
            selectedItems.isEmpty()
        ) {

            selectionMode = false
        }

        onSelectionChanged(
            selectedItems.size
        )

        notifyDataSetChanged()
    }

    fun clearSelection() {

        selectedItems.clear()

        selectionMode = false

        onSelectionChanged(0)

        notifyDataSetChanged()
    }

    fun getSelectedItems():
            List<DownloadHistoryEntity> {

        return currentList.filter {

            selectedItems.contains(
                it.id
            )
        }
    }
    fun selectAll() {

        selectedItems.clear()

        currentList.forEach {

            selectedItems.add(
                it.id
            )
        }

        selectionMode = true

        onSelectionChanged(
            selectedItems.size
        )

        notifyDataSetChanged()
    }
    fun isAllSelected(): Boolean {

        return currentList.isNotEmpty() &&
                selectedItems.size ==
                currentList.size
    }

    fun toggleSelectAll() {

        if (isAllSelected()) {

            clearSelection()

        } else {

            selectAll()
        }
    }

    fun removeItem(
        item: DownloadHistoryEntity
    ) {

        val newList =
            currentList.toMutableList()

        val position =
            newList.indexOfFirst {
                it.id == item.id
            }

        if (position == -1) return

        newList.removeAt(position)

        submitList(newList)
    }

    fun restoreItem(
        item: DownloadHistoryEntity,
        position: Int
    ) {

        val newList =
            currentList.toMutableList()

        val targetPosition =

            position.coerceIn(
                0,
                newList.size
            )

        newList.add(
            targetPosition,
            item
        )

        submitList(newList)
    }
    fun getItemAt(
        position: Int
    ): DownloadHistoryEntity {

        return currentList[position]
    }
}