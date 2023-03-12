package com.example.roomprc.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "History")
data class History(
        var title: String,
        var link: String,
        @PrimaryKey val id: Long = System.currentTimeMillis()
)

@Entity(tableName = "BrowserTab")
data class BrowserTab(
        var favicon: String = "",/*file of bitmap as string*/
        var title: String = "",
        var imgFile: String = "",
        var url: String = "",
        var isSelected: Boolean = false,
        @PrimaryKey val id: Long = System.currentTimeMillis()
)

@Entity(tableName = "Favicon")
data class Favicon(
        var imgSt64: String="",
        @PrimaryKey(autoGenerate = true) val id: Int = 0
)

/*@Entity(tableName = "users")
data class User(
        @PrimaryKey(autoGenerate = true) val id: Int,
        val name: String,
        val email: String
)*/
