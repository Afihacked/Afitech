package com.afitech.ui.home

import com.afitech.data.model.DownloadInfo

sealed interface HomeDownloadEvent {

    data class StartDownload(
        val downloadInfo: DownloadInfo
    ) : HomeDownloadEvent

    data class ShowSlides(

        val images: List<String>

    ) : HomeDownloadEvent

    data class StartSlidesDownload(

        val images: List<String>

    ) : HomeDownloadEvent

    data class ShowError(
        val message: String
    ) : HomeDownloadEvent
}