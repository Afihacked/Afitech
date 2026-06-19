package com.afitech.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(

    entities = [
        DownloadHistoryEntity::class
    ],

    version = 2,

    exportSchema = false
)
abstract class AppDatabase :
    RoomDatabase() {

    abstract fun historyDao():
            DownloadHistoryDao
}