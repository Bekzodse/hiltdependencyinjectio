package com.example.hiltnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hiltnotes.di.AppContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as NotesApp).container
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ManualNavApp(container)
                }
            }
        }
    }
}

@Composable
private fun ManualNavApp(container: AppContainer) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "notes") {
        composable("notes") {
            val notes by container.noteRepository.observeNotes().collectAsState(initial = emptyList())
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Notes (${notes.size})", style = MaterialTheme.typography.headlineSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { navController.navigate("detail/1") }) { Text("Detail") }
                    Button(onClick = { navController.navigate("github") }) { Text("GitHub Users") }
                }
            }
        }
        composable("detail/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toLongOrNull() ?: -1L
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Note Detail #$noteId", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = { navController.popBackStack() }) { Text("Back") }
            }
        }
        composable("github") {
            val users by container.gitHubRepository.observeCachedUsers().collectAsState(initial = emptyList())
            LaunchedEffect(Unit) {
                runCatching { container.gitHubRepository.refreshUsers() }
            }
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("GitHub Users (${users.size})", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = { navController.popBackStack() }) { Text("Back") }
            }
        }
    }
}
