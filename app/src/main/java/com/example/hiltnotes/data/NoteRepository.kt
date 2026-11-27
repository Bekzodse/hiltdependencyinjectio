package com.example.hiltnotes.data

import kotlinx.coroutines.flow.Flow
import java.util.concurrent.atomic.AtomicInteger

interface NoteRepository {
    fun observeNotes(): Flow<List<NoteEntity>>
    suspend fun getNote(id: Long): NoteEntity?
    suspend fun addNote(title: String, body: String): Long
    suspend fun deleteNote(note: NoteEntity)
}

class RoomNoteRepository(
    private val noteDao: NoteDao
) : NoteRepository {

    init {
        val count = creationCount.incrementAndGet()
        println("created RoomNoteRepository (#$count)")
    }

    override fun observeNotes(): Flow<List<NoteEntity>> = noteDao.observeAll()

    override suspend fun getNote(id: Long): NoteEntity? = noteDao.getById(id)

    override suspend fun addNote(title: String, body: String): Long {
        return noteDao.upsert(NoteEntity(title = title.trim(), body = body.trim()))
    }

    override suspend fun deleteNote(note: NoteEntity) = noteDao.delete(note)

    companion object {
        val creationCount = AtomicInteger(0)
    }
}

interface GitHubRepository {
    fun observeCachedUsers(): Flow<List<GitHubUserEntity>>
    suspend fun refreshUsers()
}

class DefaultGitHubRepository(
    private val service: GitHubService,
    private val userDao: GitHubUserDao
) : GitHubRepository {

    init {
        val count = creationCount.incrementAndGet()
        println("created DefaultGitHubRepository (#$count)")
    }

    override fun observeCachedUsers(): Flow<List<GitHubUserEntity>> = userDao.observeUsers()

    override suspend fun refreshUsers() {
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
