package com.dicoding.asclepius.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.model.Article
import com.dicoding.asclepius.data.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val _news = MutableLiveData<List<Article>>()
    val news: LiveData<List<Article>> = _news

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val apiKey = BuildConfig.API_KEY
    private val newsRepository = NewsRepository()

    init {
        fetchNews()
    }

    private fun fetchNews() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = newsRepository.getNews(apiKey, pageSize = 10)
                _news.value = response.articles
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                _news.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}