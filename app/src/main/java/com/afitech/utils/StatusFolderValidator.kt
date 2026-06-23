package com.afitech.utils

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

object StatusFolderValidator {

    enum class Result {
        VALID,
        NOT_WHATSAPP,
        NOT_STATUSES,
        EMPTY_STATUS_FOLDER
    }

    fun validate(
        context: Context,
        uri: Uri
    ): Result {

        val uriText =
            uri.toString().lowercase()

        val root =
            DocumentFile.fromTreeUri(
                context,
                uri
            ) ?: return Result.NOT_WHATSAPP

        val files =
            root.listFiles()

        val isWhatsappFolder =

            uriText.contains(
                "whatsapp"
            )

        if (!isWhatsappFolder) {

            return Result.NOT_WHATSAPP
        }

        val isStatusesFolder =

            uriText.contains(
                ".statuses"
            )

        if (!isStatusesFolder) {

            return Result.NOT_STATUSES
        }

        val hasStatusFiles =

            files.any {

                val name =
                    it.name
                        ?.lowercase()
                        ?: return@any false

                name.endsWith(".jpg")
                        ||
                        name.endsWith(".jpeg")
                        ||
                        name.endsWith(".png")
                        ||
                        name.endsWith(".mp4")
            }

        return if (hasStatusFiles) {

            Result.VALID

        } else {

            Result.EMPTY_STATUS_FOLDER
        }
    }
}