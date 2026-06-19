package com.afitech.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.afitech.ui.settings.AutoAnalyzePreferences

object DownloadHelper {

    fun enqueueDownload(

        context: Context,

        url: String,

        fileName: String,

        mimeType: String,

        folder: String

    ): Long {

        val request =

            DownloadManager.Request(
                Uri.parse(url)
            )

                .setMimeType(mimeType)

                .setTitle(fileName)

                .setDescription(
                    "Downloading..."
                )

                .setNotificationVisibility(

                    if (
                        com.afitech.ui.settings.AutoAnalyzePreferences
                            .isDownloadNotificationEnabled(
                                context
                            )
                    ) {

                        DownloadManager.Request
                            .VISIBILITY_VISIBLE_NOTIFY_COMPLETED

                    } else {

                        DownloadManager.Request
                            .VISIBILITY_VISIBLE
                    }
                )

                .setAllowedOverMetered(true)

                .setAllowedOverRoaming(true)

                .setDestinationInExternalPublicDir(

                    Environment.DIRECTORY_DOWNLOADS,

                    "AfitechTok/$folder/$fileName"
                )

        val downloadManager =

            context.getSystemService(
                Context.DOWNLOAD_SERVICE
            ) as DownloadManager

        return downloadManager.enqueue(
            request
        )
    }
}