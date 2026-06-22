# Afitech

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" width="140" alt="Afitech Logo"/>
</p>

<h3 align="center">
Afitech
</h3>

<p align="center">
TikTok Downloader • WhatsApp Status Saver • Modern Android Utility
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-8%2B-brightgreen"/>
  <img src="https://img.shields.io/badge/Kotlin-100%25-purple"/>
  <img src="https://img.shields.io/badge/Material%203-Modern-blue"/>
  <img src="https://img.shields.io/badge/MVVM-Architecture-orange"/>
</p>

---

## 📱 About

Afitech is a modern Android application that combines a powerful TikTok Downloader and WhatsApp Status Saver in a single lightweight experience.

The app is designed with modern Android development practices using Kotlin, MVVM Architecture, Material Design 3, Room Database, Media3 ExoPlayer, and Android Storage APIs.

---

## ✨ Features

### 🎵 TikTok Downloader

* Download TikTok videos without watermark
* Download original music/audio
* Download cover images
* Download slide/photo posts
* Smart URL analysis
* Automatic short-link resolution
* Built-in media preview

---

### 💬 WhatsApp Status Saver

* Browse WhatsApp Status directly from `.Statuses`
* Supports Android 8 to latest Android versions
* Android 11+ SAF (Storage Access Framework) support
* Image Status Preview
* Video Status Preview
* Save Status to device
* Share Status directly
* Saved status detection
* Video duration display
* Image and Video filters

---

### 📋 Smart Clipboard

* Auto Paste TikTok URL
* Auto Analyze Clipboard
* Clipboard monitoring
* Link detection status

---

### 📥 Download Manager

* Download progress tracking
* Multiple simultaneous downloads
* Download history
* File sharing
* File preview
* Search downloads
* Multi-selection actions
* Swipe-to-delete support
* Download statistics

---

### 🎬 Media Preview

#### Video Preview

* Built-in Media3 ExoPlayer
* Fullscreen playback
* Direct playback from local files
* TikTok and WhatsApp video support

#### Image Preview

* Fullscreen image viewer
* Save image
* Share image
* Saved state detection

---

### 🎨 Modern UI

* Material Design 3
* Dynamic Theme Support
* Light & Dark Mode
* Smooth animations
* Modern Bottom Sheets
* Responsive layouts
* Optimized RecyclerView performance

---

## 📂 Download Structure

Files are saved automatically into:

```text
Download/
└── Afitech/
    ├── Videos/
    ├── Music/
    ├── Covers/
    ├── Slides/
    ├── Status Images/
    └── Status Videos/
```

---

## 🛠 Built With

* Kotlin
* Android SDK
* Material Design 3
* MVVM Architecture
* Room Database
* Kotlin Coroutines
* Kotlin Flow
* Glide
* Media3 ExoPlayer
* Android DownloadManager
* Storage Access Framework (SAF)

---

## 🏗 Architecture

```text
UI Layer
│
├── Fragments
├── Activities
├── Bottom Sheets
│
ViewModel Layer
│
├── HomeViewModel
├── DownloadsViewModel
├── StatusViewModel
│
Repository Layer
│
├── TikTokRepository
├── HistoryRepository
├── WhatsappStatusRepository
│
Data Layer
│
├── Room Database
├── TikWM API
├── Storage Access Framework
└── Preferences
```

---

## 🚀 Main Screens

### Home

* Smart clipboard detection
* URL analysis
* Download options
* Recent downloads

### WhatsApp Status

* WhatsApp folder connection
* Browse available statuses
* Video and image filters
* Preview status
* Save status
* Share status

### Downloads

* Download history
* Search downloads
* Filters
* Statistics
* Share files
* Delete files
* Multi-selection actions

### Settings

* Auto Paste Clipboard
* Auto Analyze Clipboard
* Download Notifications
* Auto Close Download Options

---

## 📸 Screenshots

<p align="center">
  <img src="assets/screenshots/home.png" width="220"/>
  <img src="assets/screenshots/downloads.png" width="220"/>
  <img src="assets/screenshots/settings.png" width="220"/>
</p>

---

## 📦 Installation

Clone repository:

```bash
git clone https://github.com/Afihacked/Afitech.git
```

Open project in Android Studio.

Build and run:

```bash
./gradlew assembleDebug
```

---

## 📋 Requirements

* Android 8 (API 27) or higher
* Internet connection (TikTok Downloader)
* Storage permission (Android 8–10)
* SAF Folder Access (Android 11+ WhatsApp Status)

---

## ⚠ Disclaimer

This application is intended for personal use only.

Users are responsible for complying with TikTok's Terms of Service, WhatsApp policies, and applicable copyright laws.

---

## 👨‍💻 Developer

**Afi Tech**

GitHub:

https://github.com/Afihacked

---

## ⭐ Support

If you like this project:

* Star this repository
* Report bugs
* Suggest new features
* Share with friends

---

<p align="center">
Made with ❤️ by Afi Tech
</p>
