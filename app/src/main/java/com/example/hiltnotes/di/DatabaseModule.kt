package com.example.hiltnotes.di

import android.content.Context
import androidx.room.Room
import com.example.hiltnotes.data.AppDatabase
import com.example.hiltnotes.data.GitHubUserDao
import com.example.hiltnotes.data.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Question 13: Room DatabaseModule using Hilt's predefined @ApplicationContext qualifier.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        println("DatabaseModule: creating AppDatabase")
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "hilt_notes.db"
        ).build()
    }

    @Provides
    fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()

    @Provides
    fun provideGitHubUserDao(db: AppDatabase): GitHubUserDao = db.gitHubUserDao()
}
