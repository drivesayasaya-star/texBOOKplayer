# TalkBook Player (MVP)

Aplikasi Android prototipe: pemutar audio/video, pembaca PDF & JPG yang
diubah jadi audio (Text-to-Speech) dengan playback berdurasi panjang.

## Fitur MVP
- Putar file audio & video lokal (Media3 ExoPlayer, jalan di background).
- Buka PDF → teks diekstrak (PdfBox-Android) → dibacakan via TTS.
- Buka JPG → OCR (ML Kit, mendukung huruf Latin: Indonesia & Inggris) → dibacakan via TTS.
- Teks panjang otomatis dipecah jadi beberapa bagian dan diputar berurutan
  tanpa jeda, jadi cocok untuk buku/dokumen panjang.
- TTS pakai **Google Cloud Text-to-Speech** (perlu API key sendiri).

## Cara build
1. Install **Android Studio** (versi terbaru, sudah termasuk JDK 17 & Android SDK).
2. Buka folder project ini (`File > Open`), tunggu Gradle sync selesai
   (butuh koneksi internet untuk download dependency pertama kali).
3. Sambungkan HP Android (aktifkan USB debugging) atau pakai emulator, lalu klik **Run**.
4. Untuk file APK yang bisa dibagikan: `Build > Generate Signed Bundle / APK`.

## Setup API Key Google Cloud TTS
1. Buka [Google Cloud Console](https://console.cloud.google.com/), buat project (gratis).
2. Aktifkan **Cloud Text-to-Speech API**.
3. Buat API key di **APIs & Services > Credentials**.
4. Di aplikasi, buka menu **Settings**, tempel API key, tekan **Simpan**.
   (Google Cloud TTS punya kuota gratis bulanan; setelah itu berbayar — cek harga di console.)

## Struktur project
```
app/src/main/java/com/talkbook/app/
  MainActivity.kt          -> entry point + navigasi Compose
  player/
    PlaybackService.kt     -> Media3 foreground service (audio/video/tts)
    PlayerController.kt    -> jembatan UI <-> service
  extract/
    PdfTextExtractor.kt    -> ekstrak teks dari PDF
    OcrHelper.kt           -> OCR dari JPG
  tts/
    CloudTtsClient.kt      -> panggil Google Cloud TTS REST API
    TtsChunker.kt          -> pecah teks panjang jadi potongan aman
    TtsSynthesizer.kt      -> orkestrasi sintesis + simpan file mp3
  data/
    SettingsStore.kt       -> simpan API key (DataStore, lokal di HP)
    MainViewModel.kt       -> state & alur aplikasi
  ui/
    HomeScreen.kt, PlayerScreen.kt, SettingsScreen.kt
```

## Yang belum ada (untuk pengembangan lanjut)
- Progress bar posisi/durasi real-time & tombol seek/skip di PlayerScreen
  (kerangka listener sudah ada, tinggal dilengkapi).
- Library/riwayat file yang pernah dibuka.
- Opsi TTS offline (TTS bawaan Android) sebagai alternatif tanpa API key —
  bisa ditambahkan sebagai implementasi lain dari `CloudTtsClient`.
- Ganti file mentah sebagai cache TTS dengan penyimpanan permanen supaya
  tidak perlu sintesis ulang saat file dibuka lagi.
- Icon aplikasi masih placeholder sederhana — ganti sesuai selera di
  `res/drawable/ic_launcher_*.xml` atau pakai Image Asset Studio.
