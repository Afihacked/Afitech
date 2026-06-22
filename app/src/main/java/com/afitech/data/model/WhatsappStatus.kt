package com.afitech.data.model

import android.net.Uri

data class WhatsappStatus(

    val name: String,

    val uri: Uri,

    val isVideo: Boolean,

    val duration: Long = 0,

    val dateModified: Long = 0,

    var isSaved: Boolean = false
)