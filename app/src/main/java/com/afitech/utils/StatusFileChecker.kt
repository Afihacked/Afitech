package com.afitech.utils

import java.io.File

object StatusFileChecker {

    private const val STATUS_FOLDER =

        "/storage/emulated/0/Download/AfitechTok/Status"

    fun isSaved(
        fileName: String
    ): Boolean {

        return File(
            STATUS_FOLDER,
            fileName
        ).exists()
    }
}