package com.example.hiltnotes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hiltnotes.data.NoteRepository
import com.example.hiltnotes.logging.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Question 25: Shared ViewModel scoped to the nested "edit" navigation graph
 * across EditTitle and EditBody destinations.
 */
@HiltViewModel
class DraftViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val logger: Logger
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _body = MutableStateFlow("")
    val body: StateFlow<String> = _body.asStateFlow()

    init {
        logger.log("DraftViewModel", "Created instance=${System.identityHashCode(this)}")
    }

    fun updateTitle(value: String) {
        _title.value = value
    }

    fun updateBody(value: String) {
        _body.value = value
    }

    fun saveDraft(onSaved: () -> Unit) {
        val currentTitle = _title.value.trim()
        if (currentTitle.isEmpty()) return
        viewModelScope.launch {
            repository.addNote(currentTitle, _body.value.trim())
            onSaved()
        }
    }

    override fun onCleared() {
        super.onCleared()
        logger.log("DraftViewModel", "onCleared() called for instance=${System.identityHashCode(this)}")
    }
}
