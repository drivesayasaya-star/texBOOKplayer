package com.talkbook.app.extract

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

object PdfTextExtractor {

    private var initialized = false

    private fun ensureInit(context: Context) {
        if (!initialized) {
            PDFBoxResourceLoader.init(context.applicationContext)
            initialized = true
        }
    }

    /**
     * Mengekstrak seluruh teks dari file PDF yang dipilih user.
     * Dijalankan di background thread oleh pemanggil (mis. di dalam viewModelScope).
     */
    fun extractText(context: Context, uri: Uri): String {
        ensureInit(context)
        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Tidak bisa membuka file PDF" }
            PDDocument.load(input).use { document ->
                val stripper = PDFTextStripper()
                return stripper.getText(document)
            }
        }
    }
}
