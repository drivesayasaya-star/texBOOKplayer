package com.talkbook.app.data

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.talkbook.app.extract.OcrHelper
import com.talkbook.app.extract.PdfTextExtractor
import com.talkbook.app.player.PlayerController
import com.talkbook.app.tts.TtsProgress
import com.talkbook.app.tts.TtsSynthesizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class ScreenState {
    data object Idle : ScreenState()
    data object ExtractingText : ScreenState()
    data class Synthesizing(val progress: TtsProgress) : ScreenState()
    data class Ready(val queue: List<String>) : ScreenState()
    data class Error(val message: String) : ScreenState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsStore = SettingsStore(application)
    val playerController = PlayerController(application)

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val state: StateFlow<ScreenState> = _state

    init {
        playerController.connect { }
    }

    /** Putar file audio/video biasa yang dipilih user secara langsung. */
    fun playMediaFile(uri: Uri, title: String) {
        playerController.playSingle(uri.toString(), title)
    }

    /** Alur: ambil PDF -> ekstrak teks -> TTS -> putar sebagai audiobook panjang. */
    fun readPdfAsAudio(uri: Uri) = processDocument { PdfTextExtractor.extractText(getApplication(), uri) }

    /** Alur: ambil JPG -> OCR -> TTS -> putar. */
    fun readImageAsAudio(uri: Uri) = processDocument { OcrHelper.extractText(getApplication(), uri) }

    private fun processDocument(extract: () -> String) {
        viewModelScope.launch {
            try {
                val apiKey = settingsStore.apiKey.first()
                if (apiKey.isBlank()) {
                    _state.value = ScreenState.Error("Isi dulu Google Cloud TTS API key di menu Settings.")
                    return@launch
                }

                _state.value = ScreenState.ExtractingText
                val text = withContext(Dispatchers.IO) { extract() }
                if (text.isBlank()) {
                    _state.value = ScreenState.Error("Tidak ada teks yang terbaca dari file ini.")
                    return@launch
                }

                val languageCode = settingsStore.languageCode.first()
                val voiceName = settingsStore.voiceName.first()
                val synthesizer = TtsSynthesizer(getApplication(), apiKey)

                val queue = withContext(Dispatchers.IO) {
                    synthesizer.synthesizeLongText(text, languageCode, voiceName) { progress ->
                        _state.value = ScreenState.Synthesizing(progress)
                    }
                }

                _state.value = ScreenState.Ready(queue)
                playerController.playQueue(queue)
            } catch (e: Exception) {
                _state.value = ScreenState.Error(e.message ?: "Terjadi kesalahan tak terduga.")
            }
        }
    }

    suspend fun saveApiKey(key: String) = settingsStore.saveApiKey(key)
    suspend fun currentApiKey() = settingsStore.apiKey.first()

    override fun onCleared() {
        playerController.release()
        super.onCleared()
    }
}
