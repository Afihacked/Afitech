package com.afitech.data.repository

import android.content.Context
import com.afitech.data.local.room.DatabaseProvider
import com.afitech.data.local.room.DownloadHistoryEntity
import java.io.File

class HistoryRepository(

    context: Context
) {

    private val dao =

        DatabaseProvider
            .getDatabase(context)
            .historyDao()

    suspend fun saveHistory(

        fileName: String,

        fileType: String,

        filePath: String

    ) {

        dao.insert(

            DownloadHistoryEntity(

                fileName = fileName,

                fileType = fileType,

                filePath = filePath,

                savedAt =
                    System.currentTimeMillis()
            )
        )
    }

    suspend fun getAll() =

        dao.getAll()

    suspend fun syncMissingFiles(): Int {

        val allItems =
            dao.getAll()

        val missingIds =
            allItems.filter {

                !File(it.filePath).exists()

            }.map {
                it.id
            }

        if (
            missingIds.isNotEmpty()
        ) {

            dao.deleteMany(
                missingIds
            )
        }

        return missingIds.size
    }
    suspend fun deleteHistory(
        id: Long
    ) {

        dao.deleteById(id)
    }
    suspend fun deleteManyHistory(
        ids: List<Long>
    ) {

        dao.deleteMany(ids)
    }

    suspend fun getRecent() =

        dao.getRecent()

    fun observeRecent() =

        dao.observeRecent()
}