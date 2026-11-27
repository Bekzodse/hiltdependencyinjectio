package com.example.hiltnotes

import com.example.hiltnotes.data.NoteRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/**
 * Question 28: First @HiltAndroidTest verifying HiltTestRunner, HiltAndroidRule,
 * and hiltRule.inject() populate @Inject lateinit var repository: NoteRepository.
 */
@HiltAndroidTest
class HiltInjectionSmokeTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: NoteRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun repositoryIsInjectedAndPersistsNote() = runTest {
        assertNotNull(repository)
        repository.addNote("Smoke Test Note", "Injected by HiltAndroidRule")
        val notes = repository.observeNotes().first()
        assertEquals(1, notes.size)
        assertEquals("Smoke Test Note", notes.first().title)
    }
}
