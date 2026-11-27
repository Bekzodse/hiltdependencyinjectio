package com.example.hiltnotes.di

import com.example.hiltnotes.data.GitHubService
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Question 30 (Capstone): Replaces NetworkModule in instrumented tests with a
 * MockWebServer-backed Retrofit & GitHubService so network tests are 100% deterministic
 * and offline.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
object TestNetworkModule {

    @Provides
    @Singleton
    fun provideMockWebServer(): MockWebServer {
        val server = MockWebServer()
        // Start on a background thread to avoid NetworkOnMainThreadException on Android
        val executor = Executors.newSingleThreadExecutor()
        try {
            executor.submit { server.start() }.get()
        } finally {
            executor.shutdown()
        }
        return server
    }

    @Provides
    @Singleton
    @ApiClient
    fun provideTestApiOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @DownloadClient
    fun provideTestDownloadOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideTestRetrofit(
        @ApiClient okHttpClient: OkHttpClient,
        mockWebServer: MockWebServer
    ): Retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideTestGitHubService(retrofit: Retrofit): GitHubService =
        retrofit.create(GitHubService::class.java)
}
