package com.afitech.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardHelper {

    fun getClipboardText(
        context: Context
    ): String? {

        val clipboard =
            context.getSystemService(
                Context.CLIPBOARD_SERVICE
            ) as ClipboardManager

        if (!clipboard.hasPrimaryClip()) {
            return null
        }

        val clipData: ClipData =
            clipboard.primaryClip ?: return null

        if (clipData.itemCount == 0) {
            return null
        }

        return clipData
            .getItemAt(0)
            .text
            ?.toString()
    }
}