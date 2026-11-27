package com.example.hiltnotes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Question 29: Compose UI test backed by TestDatabaseModule (in-memory Room DB).
 * Running addNote_firstRun and addNote_secondRun_startsWithEmptyDatabase back-to-back
 * proves each test gets a brand-new empty in-memory Room instance.
 */
@HiltAndroidTest
class NotesUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun addNote_firstRun_displaysNewNoteInList() {
        composeRule.onNodeWithText("First Run Note").assertDoesNotExist()

        composeRule.onNodeWithText("Note title").performTextInput("First Run Note")
        composeRule.onNodeWithText("Note body").performTextInput("Stored in memory only")
        composeRule.onNodeWithText("Add Note").performClick()

        composeRule.onNodeWithText("First Run Note").assertIsDisplayed()
        composeRule.onNodeWithText("Stored in memory only").assertIsDisplayed()
    }

    @Test
    fun addNote_secondRun_startsWithEmptyDatabase() {
        // Proves data from the previous test did NOT persist to disk
        composeRule.onNodeWithText("First Run Note").assertDoesNotExist()

        composeRule.onNodeWithText("Note title").performTextInput("Second Run Note")
        composeRule.onNodeWithText("Add Note").performClick()

        composeRule.onNodeWithText("Second Run Note").assertIsDisplayed()
    }
}
