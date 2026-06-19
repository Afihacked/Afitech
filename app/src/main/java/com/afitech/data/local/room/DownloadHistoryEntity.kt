package com.afitech.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "download_history"
)
data class DownloadHistoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val fileName: String,

    val fileType: String,

    val filePath: String,

    val savedAt: Long
)