package com.example.hiltnotes.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Question 23: In-memory fake NoteRepository pre-populated with sample data
 * for offline demos, previews, and debug variant switching.
 */
@Singleton
class FakeNoteRepository @Inject constructor() : NoteRepository {

    private val nextId = AtomicLong(4L)
    private val notes = MutableStateFlow(
        listOf(
            NoteEntity(
                id = 1L,
                title = "Welcome to Hilt DI (Fake Mode)",
                body = "This note comes from FakeNoteRepository with zero Room database calls."
            ),
            NoteEntity(
                id = 2L,
                title = "Constructor Injection",
                body = "Prefer @Inject constructor everywhere except Android framework entry points."
            ),
            NoteEntity(
                id = 3L,
                title = "Fast JVM Tests",
                body = "Interfaces like NoteRepository let ViewModel tests run in milliseconds."
            )
        )
    )

    override fun observeNotes(): Flow<List<NoteEntity>> = notes

    override suspend fun getNote(id: Long): NoteEntity? =
        notes.value.firstOrNull { it.id == id }

    override suspend fun addNote(title: String, body: String): Long {
        val id = nextId.getAndIncrement()
        val entity = NoteEntity(id = id, title = title.trim(), body = body.trim())
        notes.value = listOf(entity) + notes.value
        return id
    }

    override suspend fun deleteNote(note: NoteEntity) {
        notes.value = notes.value.filterNot { it.id == note.id }
    }
}
