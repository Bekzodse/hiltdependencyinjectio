package com.example.hiltnotes.ui

import com.example.hiltnotes.data.NoteEntity
import com.example.hiltnotes.data.NoteRepository
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
import org.junit.Before
import org.junit.Test

/**
 * Question 27: Fast pure-JVM unit tests for NotesViewModel with a hand-made FakeNoteRepository.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class InMemoryNoteRepository : NoteRepository {
        private val notes = MutableStateFlow<List<NoteEntity>>(emptyList())
        private var nextId = 1L

        override fun observeNotes(): Flow<List<NoteEntity>> = notes

        override suspend fun getNote(id: Long): NoteEntity? =
            notes.value.firstOrNull { it.id == id }

        override suspend fun addNote(title: String, body: String): Long {
            val id = nextId++
            notes.value = notes.value + NoteEntity(id = id, title = title, body = body)
            return id
        }

        override suspend fun deleteNote(note: NoteEntity) {
            notes.value = notes.value.filterNot { it.id == note.id }
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
    fun addNote_ignoresBlankTitleAndPersistsValidNote() = runTest(testDispatcher) {
        val repository = InMemoryNoteRepository()
        val viewModel = NotesViewModel(repository)

        val collectJob = launch { viewModel.notes.collect {} }

        viewModel.addNote("   ", "ignored")
        viewModel.addNote("Buy Milk", "2 liters")
        advanceUntilIdle()

        assertEquals(1, viewModel.notes.value.size)
        assertEquals("Buy Milk", viewModel.notes.value.first().title)

        collectJob.cancel()
    }

    @Test
    fun deleteNote_removesNoteFromStateFlow() = runTest(testDispatcher) {
        val repository = InMemoryNoteRepository()
        val viewModel = NotesViewModel(repository)
        val collectJob = launch { viewModel.notes.collect {} }

        viewModel.addNote("Temp", "Remove me")
        advanceUntilIdle()
        val added = viewModel.notes.value.single()

        viewModel.deleteNote(added)
        advanceUntilIdle()

        assertEquals(0, viewModel.notes.value.size)
        collectJob.cancel()
    }
}
