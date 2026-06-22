package com.afitech.utils

import android.content.Context
import android.net.Uri

object StatusPreferences {

    private const val PREF =
        "status_pref"

    private const val KEY_URI =
        "status_folder_uri"

    fun saveUri(
        context: Context,
        uri: Uri
    ) {
        context.getSharedPreferences(
            PREF,
            Context.MODE_PRIVATE
        ).edit()
            .putString(
                KEY_URI,
                uri.toString()
            )
            .apply()
    }

    fun getUri(
        context: Context
    ): Uri? {

        val value =
            context.getSharedPreferences(
                PREF,
                Context.MODE_PRIVATE
            )
                .getString(
                    KEY_URI,
                    null
                )

        return value?.let {
            Uri.parse(it)
        }
    }
}