package com.example.hiltnotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "github_users")
data class GitHubUserEntity(
    @PrimaryKey val id: Long,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String
)
