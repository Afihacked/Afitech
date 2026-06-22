package com.afitech.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.afitech.data.model.WhatsappStatus
import java.io.File

object StatusScanner {

    private const val TAG = "StatusScanner"

    fun scan(
        context: Context
    ): List<WhatsappStatus> {

        val result = mutableListOf<WhatsappStatus>()

        val possiblePaths = listOf(
            "/storage/emulated/0/WhatsApp/Media/.Statuses",
            "/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
        )

        possiblePaths.forEach { path ->

            val folder = File(path)

            if (!folder.exists()) return@forEach

            folder.listFiles()
                ?.filter { file ->

                    file.isFile &&
                            !file.name.endsWith(".nomedia")

                }
                ?.forEach { file ->

                    val isVideo =
                        file.extension.equals(
                            "mp4",
                            true
                        )

                    result.add(

                        WhatsappStatus(

                            name = file.name,

                            uri = Uri.fromFile(file),

                            isVideo = isVideo,

                            duration =

                                if (isVideo)

                                    getVideoDuration(
                                        context,
                                        Uri.fromFile(file)
                                    )

                                else

                                    0L,

                            dateModified =
                                file.lastModified(),

                            isSaved =
                                StatusFileChecker.isSaved(
                                    file.name
                                )
                        )
                    )
                }
        }

        return result.sortedByDescending {
            it.dateModified
        }

    }
    private fun getVideoDuration(
        context: Context,
        uri: Uri
    ): Long {

        return try {

            val retriever =
                MediaMetadataRetriever()

            retriever.setDataSource(
                context,
                uri
            )

            val duration =

                retriever.extractMetadata(

                    MediaMetadataRetriever.METADATA_KEY_DURATION
                )?.toLongOrNull()

            retriever.release()

            duration ?: 0L

        } catch (_: Exception) {

            0L
        }
    }
    fun scanSaf(
        context: Context
    ): List<WhatsappStatus> {

        val result =
            mutableListOf<WhatsappStatus>()

        try {

            val uri =
                StatusPreferences.getUri(
                    context
                ) ?: return emptyList()

            val root =
                DocumentFile.fromTreeUri(
                    context,
                    uri
                ) ?: return emptyList()

            Log.d(
                TAG,
                "Root=${root.uri}"
            )

            val files =
                root.listFiles()

            files.forEach { file ->

                val name =
                    file.name
                        ?: return@forEach

                if (
                    !file.isFile ||
                    name.endsWith(".nomedia")
                ) {
                    return@forEach
                }

                val lower =
                    name.lowercase()

                val isSupported =

                    lower.endsWith(".jpg") ||
                            lower.endsWith(".jpeg") ||
                            lower.endsWith(".png") ||
                            lower.endsWith(".mp4")

                if (!isSupported) {
                    return@forEach
                }

                result.add(

                    WhatsappStatus(

                        name = name,

                        uri = file.uri,

                        isVideo =
                            lower.endsWith(".mp4"),

                        duration =

                            if (lower.endsWith(".mp4"))

                                getVideoDuration(
                                    context,
                                    file.uri
                                )

                            else

                                0L,

                        dateModified =
                            file.lastModified(),

                        isSaved =
                            StatusFileChecker.isSaved(
                                name
                            )
                    )
                )
            }

        } catch (e: Exception) {

            Log.e(
                TAG,
                "scanSaf failed",
                e
            )
        }

        return result.sortedByDescending {
            it.dateModified
        }
    }
}