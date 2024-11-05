package com.dicoding.asclepius.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.database.AppDatabase
import com.dicoding.asclepius.data.model.ScanHistory
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val _history = MutableLiveData<List<ScanHistory>>()
    val history: LiveData<List<ScanHistory>> = _history

    fun fetchHistory() {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(getApplication())
            _history.value = db.scanHistoryDao().getAllHistory()
        }
    }

    fun saveToHistory(history: ScanHistory) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(getApplication())
            db.scanHistoryDao().insert(history)
        }
    }
}