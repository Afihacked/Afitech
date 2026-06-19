package com.afitech.data.model

data class TikTokPreview(

    val title: String,

    val author: String,

    val duration: String,

    val thumbnail: String,

    val sourceUrl: String,

    val isSlide: Boolean,

    val previewVideoUrl: String?
)