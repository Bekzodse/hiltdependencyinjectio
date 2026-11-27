package com.example.hiltnotes.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class GitHubUserDto(
    val id: Long,
    val login: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("html_url") val htmlUrl: String
)

interface GitHubService {
    @GET("users")
    suspend fun getUsers(@Query("per_page") perPage: Int = 20): List<GitHubUserDto>
}
