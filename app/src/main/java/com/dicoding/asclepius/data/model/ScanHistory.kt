package com.dicoding.asclepius.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imageUri: String,
    val date: String,
    val result: String,
    val recommendation: String
)