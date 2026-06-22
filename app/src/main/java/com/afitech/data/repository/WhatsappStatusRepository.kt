package com.afitech.data.repository

import android.content.Context
import android.os.Build
import com.afitech.data.model.WhatsappStatus
import com.afitech.utils.StatusScanner

class WhatsappStatusRepository(

    private val context: Context

) {

    suspend fun getStatuses():
            List<WhatsappStatus> {

        return if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.R
        ) {

            val safResult =

                StatusScanner.scanSaf(
                    context
                )

            if (safResult.isNotEmpty()) {

                safResult

            } else {

                StatusScanner.scan(
                    context
                )
            }

        } else {

            StatusScanner.scan(
                context
            )
        }
    }
}