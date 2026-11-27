package com.example.hiltnotes.di

import com.example.hiltnotes.data.DefaultGitHubRepository
import com.example.hiltnotes.data.GitHubRepository
import com.example.hiltnotes.data.NoteRepository
import com.example.hiltnotes.data.RoomNoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Question 21: @Binds bindings for NoteRepository and GitHubRepository.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: RoomNoteRepository): NoteRepository

    @Binds
    @Singleton
    abstract fun bindGitHubRepository(impl: DefaultGitHubRepository): GitHubRepository
}
