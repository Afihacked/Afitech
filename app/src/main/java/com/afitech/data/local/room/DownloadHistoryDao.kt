package com.afitech.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DownloadHistoryDao {

    @Insert
    suspend fun insert(
        item: DownloadHistoryEntity
    )

    @Query(
        "SELECT * FROM download_history ORDER BY savedAt DESC"
    )
    suspend fun getAll():
            List<DownloadHistoryEntity>

    @Query(
        "DELETE FROM download_history"
    )
    suspend fun clear()

    @Query(
        "DELETE FROM download_history WHERE id = :id"
    )
    suspend fun deleteById(
        id: Long
    )
    @Query(
        "DELETE FROM download_history WHERE id IN (:ids)"
    )
    suspend fun deleteMany(
        ids: List<Long>
    )

    @Query(
        "SELECT * FROM download_history ORDER BY savedAt DESC LIMIT 3"
    )
    suspend fun getRecent():
            List<DownloadHistoryEntity>
    @Query(
        "SELECT * FROM download_history ORDER BY savedAt DESC LIMIT 3"
    )
    fun observeRecent():
            kotlinx.coroutines.flow.Flow<
                    List<DownloadHistoryEntity>>
}