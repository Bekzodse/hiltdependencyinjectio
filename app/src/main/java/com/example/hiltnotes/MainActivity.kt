package com.example.hiltnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hiltnotes.experiments.A
import com.example.hiltnotes.experiments.ActivityRetainedCounter
import com.example.hiltnotes.experiments.SingletonCounter
import com.example.hiltnotes.experiments.UnscopedCounter
import com.example.hiltnotes.logging.Logger
import com.example.hiltnotes.ui.GitHubUsersScreen
import com.example.hiltnotes.ui.GitHubUsersViewModel
import com.example.hiltnotes.ui.NoteDetailScreen
import com.example.hiltnotes.ui.NoteDetailViewModel
import com.example.hiltnotes.ui.NotesListScreen
import com.example.hiltnotes.ui.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var logger: Logger
    @Inject lateinit var firstA: A
    @Inject lateinit var secondA: A
    @Inject lateinit var unscopedCounter: UnscopedCounter
    @Inject lateinit var singletonCounter: SingletonCounter
    @Inject lateinit var activityRetainedCounter: ActivityRetainedCounter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.log(
            "MainActivity",
            "Q9 same A? ${firstA === secondA} | Q16 unscoped=#${unscopedCounter.instanceId}, singleton=#${singletonCounter.instanceId}, retained=#${activityRetainedCounter.instanceId}"
        )
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HiltNotesNavHost()
                }
            }
        }
    }
}

@Composable
fun HiltNotesNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "notes") {
        composable("notes") {
            val vm: NotesViewModel = hiltViewModel()
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
        ) {
            val vm: NoteDetailViewModel = hiltViewModel()
            val note by vm.note.collectAsState()
            NoteDetailScreen(
                note = note,
                onBack = { navController.popBackStack() }
            )
        }
        composable("github") {
            val vm: GitHubUsersViewModel = hiltViewModel()
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
