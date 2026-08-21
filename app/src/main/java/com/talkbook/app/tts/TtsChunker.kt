package com.talkbook.app.tts

object TtsChunker {

    /**
     * Google Cloud TTS membatasi request ~5000 byte teks.
     * Kita pakai batas aman 4000 karakter, dipecah di akhir kalimat (. ! ?)
     * supaya intonasi hasil audio tetap wajar dan potongan bisa disambung mulus.
     */
    fun chunk(text: String, maxChars: Int = 4000): List<String> {
        val cleaned = text.replace(Regex("\\s+"), " ").trim()
        if (cleaned.isEmpty()) return emptyList()
        if (cleaned.length <= maxChars) return listOf(cleaned)

        val sentences = cleaned.split(Regex("(?<=[.!?])\\s+"))
        val chunks = mutableListOf<String>()
        val current = StringBuilder()

        for (sentence in sentences) {
            if (current.length + sentence.length + 1 > maxChars) {
                if (current.isNotEmpty()) {
                    chunks.add(current.toString().trim())
                    current.clear()
                }
                // Kalimat tunggal yang lebih panjang dari maxChars -> potong paksa per kata.
                if (sentence.length > maxChars) {
                    sentence.chunked(maxChars).forEach { chunks.add(it) }
                    continue
                }
            }
            current.append(sentence).append(' ')
        }
        if (current.isNotEmpty()) chunks.add(current.toString().trim())
        return chunks
    }
}
