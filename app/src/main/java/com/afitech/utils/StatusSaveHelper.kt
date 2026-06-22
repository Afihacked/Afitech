package com.afitech.utils

import android.content.Context
import android.net.Uri
import java.io.File

object StatusSaveHelper {

    fun saveStatus(

        context: Context,

        sourceUri: Uri,

        fileName: String

    ): String {

        val rootFolder = File(

            "/storage/emulated/0/Download/AfitechTok/Status"
        )

        if (!rootFolder.exists()) {

            rootFolder.mkdirs()
        }

        val targetFile =

            File(
                rootFolder,
                fileName
            )

        context.contentResolver
            .openInputStream(sourceUri)
            ?.use { input ->

                targetFile.outputStream()
                    .use { output ->

                        input.copyTo(output)
                    }
            }

        return targetFile.absolutePath
    }
}