package com.afitech.data.model

sealed class HomeUiState {

    data object Idle : HomeUiState()

    data object Loading : HomeUiState()

    data class Success(
        val preview: TikTokPreview
    ) : HomeUiState()

    data class Error(
        val message: String
    ) : HomeUiState()
}