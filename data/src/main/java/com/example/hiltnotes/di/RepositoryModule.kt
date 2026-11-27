package com.example.hiltnotes.di

import com.example.hiltnotes.data.BuildConfig
import com.example.hiltnotes.data.DefaultGitHubRepository
import com.example.hiltnotes.data.FakeNoteRepository
import com.example.hiltnotes.data.GitHubRepository
import com.example.hiltnotes.data.NoteRepository
import com.example.hiltnotes.data.RoomNoteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Questions 21 & 23:
 * - Binds GitHubRepository to DefaultGitHubRepository via @Binds.
 * - Provides NoteRepository as RoomNoteRepository by default, or FakeNoteRepository
 *   when built with debug switch `-PuseFakeRepo=true` (BuildConfig.USE_FAKE_REPO).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGitHubRepository(impl: DefaultGitHubRepository): GitHubRepository

    companion object {
        @Provides
        @Singleton
        fun provideNoteRepository(
            real: Provider<RoomNoteRepository>,
            fake: Provider<FakeNoteRepository>
        ): NoteRepository = if (BuildConfig.USE_FAKE_REPO) fake.get() else real.get()
    }
}
