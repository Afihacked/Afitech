package com.afitech.ui.history

import androidx.recyclerview.widget.DiffUtil
import com.afitech.data.local.room.DownloadHistoryEntity

object HistoryDiffCallback : DiffUtil.ItemCallback<DownloadHistoryEntity>() {

    override fun areItemsTheSame(
        oldItem: DownloadHistoryEntity,
        newItem: DownloadHistoryEntity
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: DownloadHistoryEntity,
        newItem: DownloadHistoryEntity
    ): Boolean {
        return oldItem == newItem
    }
}