package com.example.hiltnotes.di

import android.content.Context
import androidx.room.Room
import com.example.hiltnotes.data.AppDatabase
import com.example.hiltnotes.data.DefaultGitHubRepository
import com.example.hiltnotes.data.GitHubRepository
import com.example.hiltnotes.data.GitHubService
import com.example.hiltnotes.data.GitHubUserDao
import com.example.hiltnotes.data.NoteDao
import com.example.hiltnotes.data.NoteRepository
import com.example.hiltnotes.data.RoomNoteRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Manual DI container (Question 3) with creation counters to verify instance lifecycles.
 */
class AppContainer(appContext: Context) {

    val okHttpClient: OkHttpClient by lazy {
        val n = okHttpCount.incrementAndGet()
        println("created OkHttpClient (#$n)")
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    val retrofit: Retrofit by lazy {
        val n = retrofitCount.incrementAndGet()
        println("created Retrofit (#$n)")
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val gitHubService: GitHubService by lazy {
        val n = serviceCount.incrementAndGet()
        println("created GitHubService (#$n)")
        retrofit.create(GitHubService::class.java)
    }

    val database: AppDatabase by lazy {
        val n = dbCount.incrementAndGet()
        println("created AppDatabase (#$n)")
        Room.databaseBuilder(appContext, AppDatabase::class.java, "notes.db").build()
    }

    val noteDao: NoteDao by lazy {
        val n = daoCount.incrementAndGet()
        println("created NoteDao (#$n)")
        database.noteDao()
    }

    val gitHubUserDao: GitHubUserDao by lazy {
        database.gitHubUserDao()
    }

    val noteRepository: NoteRepository by lazy {
        RoomNoteRepository(noteDao)
    }

    val gitHubRepository: GitHubRepository by lazy {
        DefaultGitHubRepository(gitHubService, gitHubUserDao)
    }

    companion object {
        val okHttpCount = AtomicInteger(0)
        val retrofitCount = AtomicInteger(0)
        val serviceCount = AtomicInteger(0)
        val dbCount = AtomicInteger(0)
        val daoCount = AtomicInteger(0)
    }
}
