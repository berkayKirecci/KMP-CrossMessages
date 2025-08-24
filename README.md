# 📦 Compose CrossMessages

**Compose CrossMessages** is a simple and lightweight library for displaying messages across Android and iOS using Jetpack Compose Multiplatform (KMP).  
It currently supports:

- ✅ Custom **Snackbar** UI for Compose-based apps
- ✅ Native **Alert Dialogs** using platform-specific APIs:
    - `AlertDialog` on Android
    - `UIAlertController` on iOS

---

## ✨ Features

- Cross-platform message handling with shared API
- Easily trigger messages from any layer (UI, ViewModel, etc.)
- Snackbar supports:
    - Custom actions
    - Duration control
    - Queued messages
- Native alerts on both platforms
- Designed for **KMP-first** projects (uses `expect/actual`)

---

## 📸 Screenshots

| Android Snackbar | iOS Native Alert |
|------------------|------------------|
| ![android-snackbar](./screenshots/snackbar_android.png) | ![ios-alert](./screenshots/alert_ios.png) |

---

## 🚀 Getting Started

### 1. Add Dependency

<details>
<summary><b>Gradle (Kotlin DSL)</b></summary>

```kotlin
dependencies {
    implementation("com.yourorg:compose-crossmessages:<version>")
}
