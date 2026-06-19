package com.afitech.data.repository

import com.afitech.data.model.DownloadInfo
import com.afitech.data.model.TikTokPreview

interface TikTokRepository {

    suspend fun analyzeUrl(
        url: String
    ): Result<TikTokPreview>

    suspend fun getDownloadInfo(
        url: String,
        format: String
    ): Result<DownloadInfo>

    suspend fun getSlideImages(
        url: String
    ): Result<List<String>>
}