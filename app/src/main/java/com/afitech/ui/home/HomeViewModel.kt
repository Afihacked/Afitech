package com.afitech.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afitech.data.model.HomeUiState
import com.afitech.data.repository.TikTokRepositoryImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    private var analyzing = false
    val uiState: StateFlow<HomeUiState> = _uiState

    fun analyzeUrl(url: String) {

        if (analyzing) return

        analyzing = true

        viewModelScope.launch {

            try {

                if (url.isBlank()) {

                    _event.emit(
                        HomeEvent.ShowError(
                            "URL tidak boleh kosong"
                        )
                    )

                    return@launch
                }

                if (
                    !url.contains("tiktok.com") &&
                    !url.contains("vt.tiktok.com")
                ) {

                    _event.emit(
                        HomeEvent.ShowError(
                            "Link TikTok tidak valid"
                        )
                    )

                    return@launch
                }

                _uiState.value =
                    HomeUiState.Loading

                val result =
                    repository.analyzeUrl(url)

                result.onSuccess { preview ->

                    _uiState.value =
                        HomeUiState.Success(preview)

                    _event.emit(
                        HomeEvent.OpenPreview(
                            preview
                        )
                    )

                    _uiState.value =
                        HomeUiState.Idle
                }

                result.onFailure {

                    _uiState.value =
                        HomeUiState.Idle

                    _event.emit(
                        HomeEvent.ClearUrlField
                    )

                    _event.emit(
                        HomeEvent.ShowError(
                            "Link TikTok tidak ditemukan atau sudah tidak valid"
                        )
                    )
                }

            } catch (_: Exception) {

                _uiState.value =
                    HomeUiState.Idle

                _event.emit(
                    HomeEvent.ClearUrlField
                )

                _event.emit(
                    HomeEvent.ShowError(
                        "Gagal menganalisis link"
                    )
                )

            } finally {

                analyzing = false
            }
        }
    }

    fun startDownload(
        sourceUrl: String,
        format: com.afitech.data.model.DownloadFormat
    ) {
        viewModelScope.launch {

            val result =

                repository.getDownloadInfo(
                    sourceUrl,
                    format.name
                )

            result.onSuccess {

                _downloadEvent.emit(

                    HomeDownloadEvent
                        .StartDownload(it)
                )
            }

            result.onFailure {

                _downloadEvent.emit(

                    HomeDownloadEvent.ShowError(
                        "Failed get download url"
                    )
                )
            }
        }
    }
    private val _event =
        MutableSharedFlow<HomeEvent>(
            extraBufferCapacity = 1
        )
    val event: SharedFlow<HomeEvent> = _event

    private val _downloadEvent =
        MutableSharedFlow<HomeDownloadEvent>(
            extraBufferCapacity = 1
        )
    val downloadEvent:
            SharedFlow<HomeDownloadEvent>
            = _downloadEvent
    private val repository =
        TikTokRepositoryImpl()

    fun testSlides(
        sourceUrl: String
    ) {

        viewModelScope.launch {

            val result =

                repository.getSlideImages(
                    sourceUrl
                )

            result.onSuccess { imageUrls ->

                if (imageUrls.isEmpty()) {

                    _downloadEvent.emit(

                        HomeDownloadEvent.ShowError(
                            "Slides not found"
                        )
                    )

                    return@onSuccess
                }

                _downloadEvent.emit(

                    HomeDownloadEvent.ShowSlides(
                        imageUrls
                    )
                )
            }

            result.onFailure {

                _downloadEvent.emit(

                    HomeDownloadEvent.ShowError(
                        "Slides not found"
                    )
                )
            }
        }
    }
    fun downloadSlides(
        images: List<String>
    ) {

        viewModelScope.launch {

            _downloadEvent.emit(

                HomeDownloadEvent
                    .StartSlidesDownload(
                        images
                    )
            )
        }
    }
}