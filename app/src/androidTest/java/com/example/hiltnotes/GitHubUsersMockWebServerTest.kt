package com.example.hiltnotes

import com.example.hiltnotes.data.GitHubRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/**
 * Question 30 (Capstone): End-to-end instrumented integration test verifying
 * TestNetworkModule (MockWebServer) -> DefaultGitHubRepository -> TestDatabaseModule (in-memory Room).
 */
@HiltAndroidTest
class GitHubUsersMockWebServerTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var mockWebServer: MockWebServer

    @Inject
    lateinit var gitHubRepository: GitHubRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun refreshUsers_readsFromMockWebServerAndCachesInInMemoryRoom() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    [
                      {
                        "id": 101,
                        "login": "octocat",
                        "avatar_url": "https://avatars.githubusercontent.com/u/101",
                        "html_url": "https://github.com/octocat"
                      },
                      {
                        "id": 202,
                        "login": "android-dev",
                        "avatar_url": "https://avatars.githubusercontent.com/u/202",
                        "html_url": "https://github.com/android-dev"
                      }
                    ]
                    """.trimIndent()
                )
        )

        gitHubRepository.refreshUsers()
        val cached = gitHubRepository.observeCachedUsers().first()

        assertEquals(2, cached.size)
        assertEquals("octocat", cached[0].login)
        assertEquals("android-dev", cached[1].login)
        assertEquals("/users?per_page=20", mockWebServer.takeRequest().path)
    }
}
