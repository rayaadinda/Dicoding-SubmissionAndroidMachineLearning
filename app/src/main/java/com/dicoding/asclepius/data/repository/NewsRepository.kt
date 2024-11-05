package com.dicoding.asclepius.data.repository

import com.dicoding.asclepius.data.remote.NewsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://newsapi.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val newsApiService = retrofit.create(NewsApiService::class.java)

    suspend fun getNews(apiKey: String, pageSize: Int = 20) = newsApiService.getNews(apiKey = apiKey, pageSize = pageSize)
}