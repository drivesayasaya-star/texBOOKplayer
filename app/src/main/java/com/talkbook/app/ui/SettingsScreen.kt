@file:OptIn(ExperimentalMaterial3Api::class)
package com.talkbook.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.talkbook.app.data.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var apiKey by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        apiKey = viewModel.currentApiKey()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Kembali") } }
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
            Text("Google Cloud Text-to-Speech API Key", style = MaterialTheme.typography.titleMedium)
            Text(
                "Buat API key di Google Cloud Console, aktifkan \"Cloud Text-to-Speech API\", " +
                    "lalu tempel key-nya di sini. Key hanya disimpan lokal di HP ini.",
                style = MaterialTheme.typography.bodySmall
            )

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it; saved = false },
                label = { Text("API Key") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        viewModel.saveApiKey(apiKey)
                        saved = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Simpan") }

            if (saved) Text("Tersimpan.", color = MaterialTheme.colorScheme.primary)
        }
    }
}
