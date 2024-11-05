package com.dicoding.asclepius.data.model

data class Article(
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String
)

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: String) : Result<Nothing>()
    data class Loading(val isLoading: Boolean) : Result<Nothing>()
}