package com.example.hiltnotes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hiltnotes.data.NoteEntity
import com.example.hiltnotes.data.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Question 22: @HiltViewModel receiving SavedStateHandle automatically populated with
 * navigation route argument "noteId" from "note/{noteId}".
 */
@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository
) : ViewModel() {

    val noteId: Long = checkNotNull(savedStateHandle.get<Long>("noteId")) {
        "Missing required navigation argument: noteId"
    }

    private val _note = MutableStateFlow<NoteEntity?>(null)
    val note: StateFlow<NoteEntity?> = _note.asStateFlow()

    init {
        viewModelScope.launch {
            _note.value = repository.getNote(noteId)
        }
    }
}
