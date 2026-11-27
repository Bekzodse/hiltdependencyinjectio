package com.example.hiltnotes.ui

import com.example.hiltnotes.data.GitHubRepository
import com.example.hiltnotes.data.GitHubUserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Question 27: Fast pure-JVM unit tests for GitHubUsersViewModel (success and error paths).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GitHubUsersViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeGitHubRepository(
        private var shouldFail: Boolean = false
    ) : GitHubRepository {
        private val users = MutableStateFlow<List<GitHubUserEntity>>(emptyList())

        override fun observeCachedUsers(): Flow<List<GitHubUserEntity>> = users

        override suspend fun refreshUsers() {
            if (shouldFail) throw IOException("Network offline")
            users.value = listOf(
                GitHubUserEntity(1L, "octocat", "https://avatars/1", "https://github.com/octocat")
            )
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun refresh_updatesUsersAndClearsError() = runTest(testDispatcher) {
        val viewModel = GitHubUsersViewModel(FakeGitHubRepository(shouldFail = false))
        val collectJob = launch { viewModel.users.collect {} }

        viewModel.refresh()
        advanceUntilIdle()

        assertNull(viewModel.error.value)
        assertEquals(1, viewModel.users.value.size)
        assertEquals("octocat", viewModel.users.value.first().login)

        collectJob.cancel()
    }

    @Test
    fun refresh_setsErrorMessageWhenRepositoryThrows() = runTest(testDispatcher) {
        val viewModel = GitHubUsersViewModel(FakeGitHubRepository(shouldFail = true))

        viewModel.refresh()
        advanceUntilIdle()

        assertEquals("Network offline", viewModel.error.value)
    }
}
