package com.afitech.ui.home

import com.afitech.data.model.TikTokPreview

sealed class HomeEvent {

    data class ShowError(
        val message: String
    ) : HomeEvent()

    data class OpenPreview(
        val preview: TikTokPreview
    ) : HomeEvent()
    object ClearUrlField : HomeEvent()
}