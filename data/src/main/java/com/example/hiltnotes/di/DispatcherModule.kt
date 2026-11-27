package com.example.hiltnotes.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Question 15: Provides CoroutineDispatchers and configurable BaseUrl so tests can
 * substitute StandardTestDispatcher and MockWebServer URLs cleanly.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}

@Module
@InstallIn(SingletonComponent::class)
object BaseUrlModule {

    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = "https://api.github.com/"
}
