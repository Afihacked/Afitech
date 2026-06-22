package com.afitech.ui.status

sealed class StatusEvent {

    data class Saved(
        val fileName: String
    ) : StatusEvent()

    data class Error(
        val message: String
    ) : StatusEvent()
}