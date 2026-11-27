package com.example.hiltnotes.data

import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Question 30 (Capstone): Pure-JVM MockWebServer test validating Retrofit + Gson
 * deserialization of GitHubService without an emulator.
 */
class GitHubServiceMockWebServerTest {

    private lateinit var server: MockWebServer
    private lateinit var service: GitHubService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun getUsers_parsesJsonPayloadCorrectly() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    [
                      {"id": 1, "login": "mojombo", "avatar_url": "https://img/1", "html_url": "https://github.com/mojombo"}
                    ]
                    """.trimIndent()
                )
        )

        val users = service.getUsers(perPage = 10)
        val request = server.takeRequest()

        assertEquals("/users?per_page=10", request.path)
        assertEquals(1, users.size)
        assertEquals("mojombo", users.first().login)
        assertEquals("https://img/1", users.first().avatarUrl)
    }
}
