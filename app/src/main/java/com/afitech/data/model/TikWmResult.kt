package com.afitech.data.model

data class TikWmResult(

    val title: String,

    val author: String,

    val duration: String,

    val thumbnail: String,

    val videoUrl: String?,

    val musicUrl: String?,

    val coverUrl: String?,

    val imageUrls: List<String>,

    val isSlide: Boolean
)