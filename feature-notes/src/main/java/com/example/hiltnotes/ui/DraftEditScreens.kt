package com.example.hiltnotes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Question 25: Two-step draft wizard screens (EditTitle -> EditBody) sharing one DraftViewModel
 * scoped to the nested "edit" navigation graph.
 */
@Composable
fun EditTitleScreen(
    title: String,
    onTitleChange: (String) -> Unit,
    onNext: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Draft Wizard — Step 1: Title", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Draft title") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onCancel) { Text("Cancel") }
            Button(onClick = onNext, enabled = title.isNotBlank()) { Text("Next: Body") }
        }
    }
}

@Composable
fun EditBodyScreen(
    title: String,
    body: String,
    onBodyChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Draft Wizard — Step 2: Body", style = MaterialTheme.typography.headlineSmall)
        Text("Title from step 1: $title", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = body,
            onValueChange = onBodyChange,
            label = { Text("Draft body") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onBack) { Text("Back") }
            Button(onClick = onSave) { Text("Save Draft") }
        }
    }
}
