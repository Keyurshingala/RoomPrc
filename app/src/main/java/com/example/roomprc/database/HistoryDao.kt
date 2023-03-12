package com.example.roomprc.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HistoryDao {
    @Query("SELECT * FROM History")
    fun getAll(): List<History>

    @Query("SELECT * FROM History")
    fun getAllLive(): LiveData<List<History>>

    @Query("SELECT * FROM History WHERE id LIKE :id")
    suspend fun findById(id: Long): History

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg history: History)

    @Update
    suspend fun update(history: History)

    @Delete
    suspend fun delete(history: History)
}
