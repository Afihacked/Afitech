package com.afitech.ui.downloads

data class DownloadItem(

    val downloadId: Long,

    val fileName: String,

    val progress: Int = 0,

    val completed: Boolean = false
)