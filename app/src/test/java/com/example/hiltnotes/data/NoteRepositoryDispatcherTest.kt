package com.example.hiltnotes.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Question 15: Pure JVM test passing StandardTestDispatcher to RoomNoteRepository
 * without any Android class or Hilt runner.
 */
class NoteRepositoryDispatcherTest {

    private class FakeNoteDao : NoteDao {
        private val notes = MutableStateFlow<List<NoteEntity>>(emptyList())
        private var nextId = 1L

        override fun observeAll(): Flow<List<NoteEntity>> = notes

        override suspend fun getById(id: Long): NoteEntity? =
            notes.value.firstOrNull { it.id == id }

        override suspend fun upsert(note: NoteEntity): Long {
            val id = if (note.id == 0L) nextId++ else note.id
            val saved = note.copy(id = id)
            notes.value = notes.value.filterNot { it.id == id } + saved
            return id
        }

        override suspend fun delete(note: NoteEntity) {
            notes.value = notes.value.filterNot { it.id == note.id }
        }
    }

    @Test
    fun addNote_runsOnInjectedStandardTestDispatcher() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val dao = FakeNoteDao()
        val repository = RoomNoteRepository(dao, testDispatcher)

        val id = repository.addNote("  Hilt DI  ", "  Inject dispatchers  ")
        val all = repository.observeNotes().first()

        assertEquals(1L, id)
        assertEquals(1, all.size)
        assertEquals("Hilt DI", all.first().title)
        assertEquals("Inject dispatchers", all.first().body)
    }
}
