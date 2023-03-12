package com.example.roomprc.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TabDao {
    @Query("SELECT * FROM BrowserTab")
    fun getAll(): LiveData<List<BrowserTab>>

    @Insert
    suspend fun insert(tab: BrowserTab)

    @Update
    suspend fun update(tab: BrowserTab)

    @Delete
    suspend fun delete(tab: BrowserTab)

    @Query("SELECT * FROM BrowserTab WHERE isSelected = 1")
    suspend fun getSelected(): BrowserTab

    @Query("UPDATE BrowserTab SET isSelected = 0")
    suspend fun updateMyColumnToFalse()
}