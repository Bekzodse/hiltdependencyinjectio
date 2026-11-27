package com.example.hiltnotes.di

import javax.inject.Qualifier

/**
 * Question 14: Custom compile-time safe qualifiers replacing @Named("api") / @Named("download").
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DownloadClient

/**
 * Question 15: Qualifiers for configurable base URL and CoroutineDispatchers.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher
