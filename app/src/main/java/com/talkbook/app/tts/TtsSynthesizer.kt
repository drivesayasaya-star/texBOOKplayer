package com.talkbook.app.tts

import android.content.Context
import java.io.File

data class TtsProgress(val done: Int, val total: Int)

class TtsSynthesizer(private val context: Context, apiKey: String) {

    private val client = CloudTtsClient(apiKey)
    private val outputDir: File by lazy {
        File(context.cacheDir, "tts_audio").apply { mkdirs() }
    }

    /**
     * Sintesis seluruh teks (bisa sangat panjang -> hasil ebook/OCR) menjadi
     * daftar path file MP3 lokal, siap dijadikan playlist ExoPlayer untuk
     * playback berdurasi panjang tanpa jeda antar potongan.
     *
     * onProgress dipanggil setiap satu chunk selesai, berguna untuk progress bar di UI.
     */
    suspend fun synthesizeLongText(
        text: String,
        languageCode: String,
        voiceName: String,
        onProgress: (TtsProgress) -> Unit = {}
    ): List<String> {
        val chunks = TtsChunker.chunk(text)
        val outputPaths = mutableListOf<String>()

        chunks.forEachIndexed { index, chunkText ->
            val audioBytes = client.synthesize(chunkText, languageCode, voiceName)
            val file = File(outputDir, "chunk_${System.currentTimeMillis()}_$index.mp3")
            file.writeBytes(audioBytes)
            outputPaths.add(file.absolutePath)
            onProgress(TtsProgress(index + 1, chunks.size))
        }
        return outputPaths
    }

    fun clearCache() {
        outputDir.listFiles()?.forEach { it.delete() }
    }
}
