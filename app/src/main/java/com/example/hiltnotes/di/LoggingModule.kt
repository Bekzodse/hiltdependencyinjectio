package com.example.hiltnotes.di

import com.example.hiltnotes.logging.FileLogger
import com.example.hiltnotes.logging.LogcatLogger
import com.example.hiltnotes.logging.Logger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Question 11: @Binds module for Logger.
 * Switching from LogcatLogger to FileLogger requires changing only the parameter type
 * on bindLogger — zero consumer files need edits.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LoggingModule {

    @Binds
    @Singleton
    abstract fun bindLogger(impl: LogcatLogger): Logger
    // To switch every consumer in the app to FileLogger, replace the single line above with:
    // abstract fun bindLogger(impl: FileLogger): Logger
}
