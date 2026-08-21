package com.talkbook.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "talkbook_settings")

class SettingsStore(private val context: Context) {

    private val apiKeyPref = stringPreferencesKey("gcloud_tts_api_key")
    private val voicePref = stringPreferencesKey("tts_voice_name")
    private val langPref = stringPreferencesKey("tts_language_code")

    val apiKey: Flow<String> = context.dataStore.data.map { it[apiKeyPref] ?: "" }
    val voiceName: Flow<String> = context.dataStore.data.map { it[voicePref] ?: "id-ID-Wavenet-A" }
    val languageCode: Flow<String> = context.dataStore.data.map { it[langPref] ?: "id-ID" }

    suspend fun saveApiKey(key: String) {
        context.dataStore.edit { it[apiKeyPref] = key }
    }

    suspend fun saveVoice(languageCode: String, voiceName: String) {
        context.dataStore.edit {
            it[langPref] = languageCode
            it[voicePref] = voiceName
        }
    }
}
