package com.example.hiltnotes.ui

import com.example.hiltnotes.data.NoteEntity
import com.example.hiltnotes.data.NoteRepository
import com.example.hiltnotes.logging.Logger
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Question 27 & Capstone: Pure-JVM unit test for DraftViewModel using hand-made fakes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DraftViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class RecordingLogger : Logger {
        val messages = mutableListOf<String>()
        override fun log(tag: String, message: String) {
            messages += "[$tag] $message"
        }
    }

    private class StubNoteRepository : NoteRepository {
        val added = mutableListOf<Pair<String, String>>()
        override fun observeNotes(): Flow<List<NoteEntity>> = MutableStateFlow(emptyList())
        override suspend fun getNote(id: Long): NoteEntity? = null
        override suspend fun addNote(title: String, body: String): Long {
            added += title to body
            return added.size.toLong()
        }
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
    fun saveDraft_persistsTitleAndBodyAndInvokesCallback() = runTest(testDispatcher) {
        val repo = StubNoteRepository()
        val logger = RecordingLogger()
        val vm = DraftViewModel(repo, logger)
        var savedCalled = false

        vm.updateTitle("  Sprint Plan  ")
        vm.updateBody("  Ship Hilt capstone  ")
        vm.saveDraft { savedCalled = true }
        advanceUntilIdle()

        assertTrue(savedCalled)
        assertEquals(listOf("Sprint Plan" to "Ship Hilt capstone"), repo.added)
        assertTrue(logger.messages.any { it.contains("Created instance=") })
    }
}
