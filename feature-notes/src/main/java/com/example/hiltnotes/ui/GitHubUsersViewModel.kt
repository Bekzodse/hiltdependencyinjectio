package com.example.hiltnotes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hiltnotes.data.GitHubRepository
import com.example.hiltnotes.data.GitHubUserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GitHubUsersViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {

    val users: StateFlow<List<GitHubUserEntity>> = repository.observeCachedUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _error.value = null
            runCatching { repository.refreshUsers() }
                .onFailure { _error.value = it.message ?: "Failed to load GitHub users" }
        }
    }
}
