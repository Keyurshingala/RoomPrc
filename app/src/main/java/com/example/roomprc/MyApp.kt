package com.example.roomprc

import androidx.multidex.MultiDexApplication
import androidx.room.Room
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.roomprc.database.AppDatabase

class MyApp : MultiDexApplication() {

    companion object {
        lateinit var db: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        val migration1to2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table need to write property of table in same order
                database.execSQL("CREATE TABLE IF NOT EXISTS `Favicon` (`imgSt64` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
//                database.execSQL("CREATE TABLE IF NOT EXISTS Favicon (imgSt64 TEXT, id INTEGER PRIMARY KEY AUTOINCREMENT)")

                // Add any other migration steps here
            }
        }

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, getString(st.app_name))
                .addMigrations(migration1to2)
                .build()

        //also can add .allowMainThreadQueries()
    }
}
