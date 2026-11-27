package com.example.hiltnotes.di

import android.content.Context
import androidx.room.Room
import com.example.hiltnotes.data.AppDatabase
import com.example.hiltnotes.data.GitHubUserDao
import com.example.hiltnotes.data.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Question 29: Replaces DatabaseModule across all instrumented tests with an
 * isolated, in-memory Room database so UI tests never touch user disk data and
 * start completely empty on every test run.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideInMemoryDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()

    @Provides
    fun provideGitHubUserDao(db: AppDatabase): GitHubUserDao = db.gitHubUserDao()
}
