package com.afitech.ui.downloads

import androidx.recyclerview.widget.DiffUtil

object DownloadDiffCallback :
    DiffUtil.ItemCallback<DownloadItem>() {

    override fun areItemsTheSame(
        oldItem: DownloadItem,
        newItem: DownloadItem
    ): Boolean {

        return oldItem.downloadId ==
                newItem.downloadId
    }

    override fun areContentsTheSame(
        oldItem: DownloadItem,
        newItem: DownloadItem
    ): Boolean {

        return oldItem == newItem
    }
}