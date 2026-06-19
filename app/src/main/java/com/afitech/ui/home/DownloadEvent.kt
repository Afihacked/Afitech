package com.afitech.ui.home

import com.afitech.data.model.DownloadFormat

sealed interface DownloadEvent {

    data class StartDownload(

        val url: String,

        val format: DownloadFormat

    ) : DownloadEvent
}