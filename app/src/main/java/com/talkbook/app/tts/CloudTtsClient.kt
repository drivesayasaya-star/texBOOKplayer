package com.talkbook.app.tts

import android.util.Base64
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Klien untuk Google Cloud Text-to-Speech REST API.
 * https://texttospeech.googleapis.com/v1/text:synthesize
 *
 * Butuh API key dari Google Cloud Console (aktifkan "Cloud Text-to-Speech API").
 */
class CloudTtsClient(private val apiKey: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val endpoint = "https://texttospeech.googleapis.com/v1/text:synthesize?key=$apiKey"
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Sintesis satu potongan teks (maks ~4500 karakter, lihat TtsChunker) menjadi audio MP3.
     * Mengembalikan byte MP3 mentah (masih ter-encode Base64 dari API, sudah didecode di sini).
     */
    @Throws(IOException::class)
    fun synthesize(
        text: String,
        languageCode: String = "id-ID",
        voiceName: String = "id-ID-Wavenet-A",
        speakingRate: Double = 1.0
    ): ByteArray {
        val body = JSONObject().apply {
            put("input", JSONObject().put("text", text))
            put("voice", JSONObject().apply {
                put("languageCode", languageCode)
                put("name", voiceName)
            })
            put("audioConfig", JSONObject().apply {
                put("audioEncoding", "MP3")
                put("speakingRate", speakingRate)
            })
        }.toString()

        val request = Request.Builder()
            .url(endpoint)
            .post(body.toRequestBody(jsonMediaType))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("TTS API gagal (${response.code}): ${response.body?.string()}")
            }
            val json = JSONObject(response.body?.string() ?: "{}")
            val audioContentB64 = json.getString("audioContent")
            return Base64.decode(audioContentB64, Base64.DEFAULT)
        }
    }
}
