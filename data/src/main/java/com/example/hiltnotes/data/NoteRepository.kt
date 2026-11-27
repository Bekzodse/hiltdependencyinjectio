package com.example.hiltnotes.data

import com.example.hiltnotes.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

interface NoteRepository {
    fun observeNotes(): Flow<List<NoteEntity>>
    suspend fun getNote(id: Long): NoteEntity?
    suspend fun addNote(title: String, body: String): Long
    suspend fun deleteNote(note: NoteEntity)
}

@Singleton
class RoomNoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NoteRepository {

    init {
        val count = creationCount.incrementAndGet()
        println("created RoomNoteRepository (#$count)")
    }

    override fun observeNotes(): Flow<List<NoteEntity>> = noteDao.observeAll()

    override suspend fun getNote(id: Long): NoteEntity? = withContext(ioDispatcher) {
        noteDao.getById(id)
    }

    override suspend fun addNote(title: String, body: String): Long = withContext(ioDispatcher) {
        noteDao.upsert(NoteEntity(title = title.trim(), body = body.trim()))
    }

    override suspend fun deleteNote(note: NoteEntity) = withContext(ioDispatcher) {
        noteDao.delete(note)
    }

    companion object {
        val creationCount = AtomicInteger(0)
    }
}

interface GitHubRepository {
    fun observeCachedUsers(): Flow<List<GitHubUserEntity>>
    suspend fun refreshUsers()
}

@Singleton
class DefaultGitHubRepository @Inject constructor(
    private val service: GitHubService,
    private val userDao: GitHubUserDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GitHubRepository {

    init {
        val count = creationCount.incrementAndGet()
        println("created DefaultGitHubRepository (#$count)")
    }

    override fun observeCachedUsers(): Flow<List<GitHubUserEntity>> = userDao.observeUsers()

    override suspend fun refreshUsers() = withContext(ioDispatcher) {
        val remote = service.getUsers().map {
            GitHubUserEntity(
                id = it.id,
                login = it.login,
                avatarUrl = it.avatarUrl,
                htmlUrl = it.htmlUrl
            )
        }
        userDao.insertAll(remote)
    }

    companion object {
        val creationCount = AtomicInteger(0)
    }
}
