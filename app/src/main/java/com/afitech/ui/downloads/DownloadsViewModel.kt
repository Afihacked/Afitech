package com.afitech.ui.downloads

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.afitech.data.local.room.DownloadHistoryEntity
import com.afitech.data.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DownloadsViewModel(

    private val repository: HistoryRepository

) : ViewModel() {

    private var allHistory =
        emptyList<DownloadHistoryEntity>()

    private var currentQuery = ""

    private var currentFilter = "All"

    private val _history =

        MutableStateFlow<List<DownloadHistoryEntity>>(
            emptyList()
        )

    val history:
            StateFlow<List<DownloadHistoryEntity>>
            = _history

    fun loadHistory(
        syncFiles: Boolean = true
    ) {

        viewModelScope.launch {

            if (syncFiles) {

                val removedCount =

                    repository.syncMissingFiles()

                if (
                    removedCount > 0
                ) {

                    _removedFilesEvent.emit(
                        removedCount
                    )
                }
            }

            allHistory =
                repository.getAll()

            applyFilters()
        }
    }

    fun searchHistory(
        query: String
    ) {

        currentQuery = query

        applyFilters()
    }

    fun filterByType(
        type: String
    ) {

        currentFilter = type

        applyFilters()
    }

    private fun applyFilters() {

        var result = allHistory

        if (currentFilter != "All") {

            result = result.filter {

                it.fileType.equals(

                    currentFilter,

                    ignoreCase = true
                )
            }
        }

        if (currentQuery.isNotBlank()) {

            result = result.filter {

                it.fileName.contains(

                    currentQuery,

                    ignoreCase = true

                ) ||

                        it.fileType.contains(

                            currentQuery,

                            ignoreCase = true
                        )
            }
        }
        _history.value = result
    }
    fun deleteHistory(
        id: Long
    ) {

        viewModelScope.launch {

            repository.deleteHistory(
                id
            )

            loadHistory()
        }
    }
    fun deleteManyHistory(
        ids: List<Long>
    ) {

        viewModelScope.launch {

            repository.deleteManyHistory(
                ids
            )

            loadHistory()
        }
    }

    private val _removedFilesEvent =
        MutableSharedFlow<Int>()

    val removedFilesEvent:
            SharedFlow<Int>
            = _removedFilesEvent
}


class DownloadsViewModelFactory(

    private val context: Context

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(

        modelClass: Class<T>

    ): T {

        return DownloadsViewModel(

            HistoryRepository(
                context
            )

        ) as T
    }
}