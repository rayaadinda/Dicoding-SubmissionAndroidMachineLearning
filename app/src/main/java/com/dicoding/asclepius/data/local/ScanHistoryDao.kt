package com.dicoding.asclepius.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dicoding.asclepius.data.model.ScanHistory

@Dao
interface ScanHistoryDao {
    @Insert
    suspend fun insert(history: ScanHistory)

    @Query("SELECT * FROM scan_history ORDER BY date DESC")
    suspend fun getAllHistory(): List<ScanHistory>
}