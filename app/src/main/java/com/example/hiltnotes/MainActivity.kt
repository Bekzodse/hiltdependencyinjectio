package com.example.hiltnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hiltnotes.di.AppContainer
import com.example.hiltnotes.ui.GitHubUsersScreen
import com.example.hiltnotes.ui.GitHubUsersViewModel
import com.example.hiltnotes.ui.NoteDetailScreen
import com.example.hiltnotes.ui.NoteDetailViewModel
import com.example.hiltnotes.ui.NotesListScreen
import com.example.hiltnotes.ui.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as NotesApp).container
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ManualNotesNavHost(container)
                }
            }
        }
    }
}

@Composable
private fun ManualNotesNavHost(container: AppContainer) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "notes") {
        composable("notes") {
            val vm: NotesViewModel = viewModel(factory = container.notesViewModelFactory())
            val notes by vm.notes.collectAsState()
            NotesListScreen(
                notes = notes,
                onAddNote = vm::addNote,
                onDeleteNote = vm::deleteNote,
                onNoteClick = { id -> navController.navigate("note/$id") },
                onOpenGitHub = { navController.navigate("github") }
            )
        }
        composable(
            route = "note/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { entry ->
            val noteId = entry.arguments?.getLong("noteId") ?: -1L
            val vm: NoteDetailViewModel = viewModel(
                factory = container.noteDetailViewModelFactory(noteId)
            )
            val note by vm.note.collectAsState()
            NoteDetailScreen(
                note = note,
                onBack = { navController.popBackStack() }
            )
        }
        composable("github") {
            val vm: GitHubUsersViewModel = viewModel(factory = container.gitHubUsersViewModelFactory())
            val users by vm.users.collectAsState()
            val error by vm.error.collectAsState()
            GitHubUsersScreen(
                users = users,
                error = error,
                onRefresh = vm::refresh,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
