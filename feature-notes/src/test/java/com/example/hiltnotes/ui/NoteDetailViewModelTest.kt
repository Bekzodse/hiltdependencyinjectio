package com.example.hiltnotes.ui

import androidx.lifecycle.SavedStateHandle
import com.example.hiltnotes.data.NoteEntity
import com.example.hiltnotes.data.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Question 22: JVM unit test passing SavedStateHandle(mapOf("noteId" to 7L)) directly
 * to NoteDetailViewModel without Hilt or Android.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NoteDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeNoteRepository(initial: List<NoteEntity>) : NoteRepository {
        private val flow = MutableStateFlow(initial)
        override fun observeNotes(): Flow<List<NoteEntity>> = flow
        override suspend fun getNote(id: Long): NoteEntity? = flow.value.firstOrNull { it.id == id }
        override suspend fun addNote(title: String, body: String): Long = 0L
        override suspend fun deleteNote(note: NoteEntity) {}
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
    fun loadsNoteFromSavedStateHandleNoteId() = runTest(testDispatcher) {
        val target = NoteEntity(id = 7L, title = "SavedState Note", body = "Survives process death")
        val repository = FakeNoteRepository(listOf(target))
        val savedStateHandle = SavedStateHandle(mapOf("noteId" to 7L))

        val viewModel = NoteDetailViewModel(savedStateHandle, repository)
        advanceUntilIdle()

        assertEquals(7L, viewModel.noteId)
        assertNotNull(viewModel.note.value)
        assertEquals("SavedState Note", viewModel.note.value?.title)
    }
}
