package com.example.roomprc.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
        entities = [History::class, BrowserTab::class,Favicon::class],
        version = 2,
        exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun tabDao(): TabDao
    abstract fun faviconDao(): faviconDao
}
