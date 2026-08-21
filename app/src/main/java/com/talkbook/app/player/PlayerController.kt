package com.talkbook.app.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors

/**
 * Wrapper sederhana di atas MediaController agar layar Compose
 * tinggal panggil fungsi play/pause/seek tanpa urus koneksi service.
 */
class PlayerController(private val context: Context) {

    var controller: MediaController? = null
        private set

    fun connect(onReady: (MediaController) -> Unit) {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, sessionToken).buildAsync()
        future.addListener({
            controller = future.get()
            controller?.let(onReady)
        }, MoreExecutors.directExecutor())
    }

    /** Putar satu file audio/video tunggal (mode "Player" biasa). */
    fun playSingle(uri: String, title: String) {
        val item = MediaItem.Builder().setUri(uri).setMediaId(uri).build()
        controller?.apply {
            setMediaItem(item)
            prepare()
            playWhenReady = true
        }
    }

    /** Putar rangkaian file audio hasil TTS secara berurutan (durasi panjang / non-stop). */
    fun playQueue(uris: List<String>, startIndex: Int = 0) {
        val items = uris.mapIndexed { idx, uri ->
            MediaItem.Builder().setUri(uri).setMediaId("chunk_$idx").build()
        }
        controller?.apply {
            setMediaItems(items, startIndex, 0L)
            prepare()
            playWhenReady = true
        }
    }

    fun togglePlayPause() {
        controller?.apply {
            if (isPlaying) pause() else play()
        }
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
    }

    fun addListener(listener: Player.Listener) {
        controller?.addListener(listener)
    }

    fun release() {
        controller?.release()
        controller = null
    }
}
