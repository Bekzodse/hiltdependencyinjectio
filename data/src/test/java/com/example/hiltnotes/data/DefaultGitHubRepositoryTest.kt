package com.example.hiltnotes.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Question 27: Fast pure-JVM unit tests for DefaultGitHubRepository using hand-made fakes
 * (no Hilt, no Android, no emulator).
 */
class DefaultGitHubRepositoryTest {

    private class FakeGitHubService(
        private val usersToReturn: List<GitHubUserDto>
    ) : GitHubService {
        var callCount = 0
            private set

        override suspend fun getUsers(perPage: Int): List<GitHubUserDto> {
            callCount++
            return usersToReturn
        }
    }

    private class FakeGitHubUserDao : GitHubUserDao {
        private val users = MutableStateFlow<List<GitHubUserEntity>>(emptyList())

        override fun observeUsers(): Flow<List<GitHubUserEntity>> = users

        override suspend fun insertAll(users: List<GitHubUserEntity>) {
            this.users.value = users
        }

        override suspend fun clearAll() {
            users.value = emptyList()
        }
    }

    @Test
    fun refreshUsers_fetchesRemoteAndCachesInDao() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val service = FakeGitHubService(
            listOf(
                GitHubUserDto(1L, "octocat", "https://avatars/1", "https://github.com/octocat"),
                GitHubUserDto(2L, "torvalds", "https://avatars/2", "https://github.com/torvalds")
            )
        )
        val dao = FakeGitHubUserDao()
        val repository = DefaultGitHubRepository(service, dao, dispatcher)

        repository.refreshUsers()
        val cached = repository.observeCachedUsers().first()

        assertEquals(1, service.callCount)
        assertEquals(2, cached.size)
        assertEquals("octocat", cached[0].login)
        assertEquals("torvalds", cached[1].login)
    }

    @Test
    fun observeCachedUsers_returnsEmptyBeforeFirstRefresh() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = DefaultGitHubRepository(FakeGitHubService(emptyList()), FakeGitHubUserDao(), dispatcher)

        val cached = repository.observeCachedUsers().first()
        assertEquals(0, cached.size)
    }
}
