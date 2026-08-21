@file:OptIn(ExperimentalMaterial3Api::class)
package com.talkbook.app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.talkbook.app.data.MainViewModel
import com.talkbook.app.data.ScreenState

@Composable
fun HomeScreen(viewModel: MainViewModel, onOpenSettings: () -> Unit, onOpenPlayer: () -> Unit) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is ScreenState.Ready) onOpenPlayer()
    }

    val mediaPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            viewModel.playMediaFile(it, title = "Media")
            onOpenPlayer()
        }
    }
    val pdfPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { viewModel.readPdfAsAudio(it) }
    }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { viewModel.readImageAsAudio(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TalkBook Player") },
                actions = {
                    TextButton(onClick = onOpenSettings) { Text("Settings") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Pilih file untuk diputar atau dibacakan:", style = MaterialTheme.typography.titleMedium)

            Button(
                onClick = { mediaPicker.launch(arrayOf("audio/*", "video/*")) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Putar Audio / Video") }

            Button(
                onClick = { pdfPicker.launch(arrayOf("application/pdf")) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Bacakan PDF (Text-to-Speech)") }

            Button(
                onClick = { imagePicker.launch(arrayOf("image/jpeg", "image/jpg")) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Bacakan Gambar JPG (OCR + TTS)") }

            Divider()

            StatusSection(state)
        }
    }
}

@Composable
private fun StatusSection(state: ScreenState) {
    when (state) {
        is ScreenState.Idle -> Text("Siap. Pilih salah satu file di atas.")
        is ScreenState.ExtractingText -> Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Mengekstrak teks dari file...")
        }
        is ScreenState.Synthesizing -> Column {
            Text("Menyintesis audio: ${state.progress.done}/${state.progress.total} bagian")
            LinearProgressIndicator(
                progress = { if (state.progress.total == 0) 0f else state.progress.done / state.progress.total.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )
        }
        is ScreenState.Ready -> Text("Siap diputar — ${state.queue.size} bagian audio dalam antrean.")
        is ScreenState.Error -> Text(
            "Error: ${state.message}",
            color = MaterialTheme.colorScheme.error
        )
    }
}
