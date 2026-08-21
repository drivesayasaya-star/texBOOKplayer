package com.talkbook.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.talkbook.app.data.MainViewModel

@Composable
fun PlayerScreen(viewModel: MainViewModel) {
    var isPlaying by remember { mutableStateOf(false) }
    var positionMs by remember { mutableStateOf(0L) }
    var durationMs by remember { mutableStateOf(0L) }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        viewModel.playerController.addListener(listener)
        onDispose { }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sedang Diputar", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))

        IconButton(
            onClick = { viewModel.playerController.togglePlayPause() },
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Jeda" else "Putar",
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("Untuk file panjang (hasil TTS), bagian berikutnya otomatis lanjut tanpa jeda.")
    }
}
