package com.dicoding.asclepius.data.remote

import com.dicoding.asclepius.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/everything")
    suspend fun getNews(
        @Query("q") query: String = "cancer",
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int = 20
    ): NewsResponse
}