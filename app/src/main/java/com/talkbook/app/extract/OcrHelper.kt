package com.talkbook.app.extract

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

object OcrHelper {

    /**
     * Membaca teks dari gambar (JPG/PNG) memakai ML Kit on-device OCR.
     * Latin recognizer mendukung huruf Latin -> cocok untuk teks Indonesia maupun Inggris
     * dalam satu gambar yang sama.
     *
     * Catatan: fungsi ini blocking (pakai Tasks.await), jalankan di background thread.
     */
    fun extractText(context: Context, imageUri: Uri): String {
        val image = InputImage.fromFilePath(context, imageUri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val result = Tasks.await(recognizer.process(image))
        return result.text
    }
}
