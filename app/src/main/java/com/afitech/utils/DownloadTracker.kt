package com.afitech.utils

import android.app.DownloadManager
import android.content.Context
import kotlinx.coroutines.*

object DownloadTracker {

    fun track(
        scope: CoroutineScope,
        context: Context,
        downloadId: Long,
        onProgress: (
            Int,
            Boolean
        ) -> Unit
    ) {

        scope.launch(
            Dispatchers.IO
        ) {

            val manager =

                context.getSystemService(
                    Context.DOWNLOAD_SERVICE
                ) as DownloadManager

            while (isActive) {

                val query =
                    DownloadManager.Query()

                query.setFilterById(
                    downloadId
                )

                val cursor =
                    manager.query(query)

                if (cursor.moveToFirst()) {

                    val downloaded =

                        cursor.getLong(

                            cursor.getColumnIndexOrThrow(

                                DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR
                            )
                        )

                    val total =

                        cursor.getLong(

                            cursor.getColumnIndexOrThrow(

                                DownloadManager.COLUMN_TOTAL_SIZE_BYTES
                            )
                        )

                    val status =

                        cursor.getInt(

                            cursor.getColumnIndexOrThrow(

                                DownloadManager.COLUMN_STATUS
                            )
                        )

                    val progress =

                        if (total > 0)

                            ((downloaded * 100) / total).toInt()

                        else 0

                    withContext(
                        Dispatchers.Main
                    ) {

                        onProgress(

                            progress,

                            status ==
                                    DownloadManager.STATUS_SUCCESSFUL
                        )

                    }

                    if (
                        status ==
                        DownloadManager.STATUS_SUCCESSFUL
                    ) {

                        break
                    }

                    if (
                        status ==
                        DownloadManager.STATUS_FAILED
                    ) {

                        break
                    }
                }

                cursor.close()

                delay(1000)
            }
        }
    }
}