package com.afitech.ui.settings

import android.content.Context

object AutoAnalyzePreferences {

    private const val PREF_NAME =
        "afitech_settings"

    private const val KEY_AUTO_ANALYZE =
        "auto_analyze"

    private const val KEY_AUTO_PASTE =
        "auto_paste"

    private const val KEY_AUTO_CLOSE_PREVIEW =
        "auto_close_preview"

    private const val KEY_DOWNLOAD_NOTIFICATION =
        "download_notification"

    fun isEnabled(
        context: Context
    ): Boolean {

        return context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .getBoolean(
                KEY_AUTO_ANALYZE,
                false
            )
    }

    fun setEnabled(
        context: Context,
        enabled: Boolean
    ) {

        context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putBoolean(
                KEY_AUTO_ANALYZE,
                enabled
            )
            .apply()
    }

    fun isAutoPasteEnabled(
        context: Context
    ): Boolean {

        return context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .getBoolean(
                KEY_AUTO_PASTE,
                true
            )
    }

    fun setAutoPasteEnabled(
        context: Context,
        enabled: Boolean
    ) {

        context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putBoolean(
                KEY_AUTO_PASTE,
                enabled
            )
            .apply()
    }

    fun isAutoClosePreviewEnabled(
        context: Context
    ): Boolean {

        return context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .getBoolean(
                KEY_AUTO_CLOSE_PREVIEW,
                true
            )
    }

    fun setAutoClosePreviewEnabled(
        context: Context,
        enabled: Boolean
    ) {

        context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putBoolean(
                KEY_AUTO_CLOSE_PREVIEW,
                enabled
            )
            .apply()
    }

    fun isDownloadNotificationEnabled(
        context: Context
    ): Boolean {

        return context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .getBoolean(
                KEY_DOWNLOAD_NOTIFICATION,
                true
            )
    }

    fun setDownloadNotificationEnabled(
        context: Context,
        enabled: Boolean
    ) {

        context
            .getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putBoolean(
                KEY_DOWNLOAD_NOTIFICATION,
                enabled
            )
            .apply()
    }
}