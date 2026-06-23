package com.afitech.ui.status

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.afitech.data.model.WhatsappStatus
import com.afitech.data.repository.HistoryRepository
import com.afitech.data.repository.WhatsappStatusRepository
import com.afitech.utils.StatusSaveHelper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StatusViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        WhatsappStatusRepository(
            application
        )

    private val historyRepository =
        HistoryRepository(
            application
        )

    private val _statuses =
        MutableStateFlow<List<WhatsappStatus>>(
            emptyList()
        )

    val statuses:
            StateFlow<List<WhatsappStatus>>
            = _statuses

    private val eventChannel =
        Channel<StatusEvent>()

    val event =
        eventChannel.receiveAsFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading:
            StateFlow<Boolean>
            = _isLoading

    private var cachedStatuses:
            List<WhatsappStatus>? = null

    private var loadingJobActive =
        false

    fun loadStatuses(
        forceRefresh: Boolean = false
    ) {

        if (
            !forceRefresh &&
            !cachedStatuses.isNullOrEmpty()
        ) {

            _statuses.value =
                cachedStatuses!!

            return
        }

        if (loadingJobActive) {
            return
        }

        viewModelScope.launch {

            loadingJobActive = true

            _isLoading.value = true

            try {

                val result =
                    repository.getStatuses()

                cachedStatuses =
                    result

                _statuses.value =
                    result

            } catch (e: Exception) {

                e.printStackTrace()

                eventChannel.send(

                    StatusEvent.Error(

                        e.message
                            ?: "Failed to load statuses"
                    )
                )

            } finally {

                loadingJobActive =
                    false

                _isLoading.value =
                    false
            }
        }
    }

    fun clearCache() {

        cachedStatuses = null
    }

    fun saveStatus(
        status: WhatsappStatus
    ) {

        viewModelScope.launch {

            try {

                val path =

                    StatusSaveHelper.saveStatus(

                        getApplication(),

                        status.uri,

                        status.name
                    )

                historyRepository.saveHistory(

                    fileName = status.name,

                    fileType =

                        if (status.isVideo)

                            "Status Video"

                        else

                            "Status Image",

                    filePath = path
                )

                status.isSaved = true

                cachedStatuses = null

                eventChannel.send(

                    StatusEvent.Saved(
                        status.name
                    )
                )

            } catch (e: Exception) {

                eventChannel.send(

                    StatusEvent.Error(

                        e.message
                            ?: "Save failed"
                    )
                )
            }
        }
    }
}