package com.example.hiltnotes.data

import com.example.hiltnotes.di.DownloadClient
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Question 14: Separate consumer injected with @DownloadClient (60s timeout)
 * while Retrofit uses @ApiClient (10s timeout).
 */
@Singleton
class FileDownloader @Inject constructor(
    @DownloadClient private val downloadClient: OkHttpClient
) {
    fun configuredReadTimeoutMillis(): Int = downloadClient.readTimeoutMillis

    fun buildDownloadRequest(url: String): Request = Request.Builder().url(url).build()
}
