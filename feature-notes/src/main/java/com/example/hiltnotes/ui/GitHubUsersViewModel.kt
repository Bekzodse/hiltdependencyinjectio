package com.example.hiltnotes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hiltnotes.data.GitHubRepository
import com.example.hiltnotes.data.GitHubUserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GitHubUsersViewModel(
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

class GitHubUsersViewModelFactory(
    private val repository: GitHubRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GitHubUsersViewModel::class.java)) {
            return GitHubUsersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
