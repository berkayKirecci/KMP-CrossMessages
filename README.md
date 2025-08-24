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
```
</details>

> 📦 Coming soon on MavenCentral. For now, use [local build](#🔧-development).

---

### 2. Setup `MessageHost` in Root Composable

```kotlin
@Composable
fun App() {
    MessageHost() // Must be at the root of your app
    MainScreen()
}
```

---

### 3. Trigger Messages

#### 📍 Show Snackbar
```kotlin
MessageManager.showSnackbar(
    message = "Settings saved",
    actionLabel = "Undo",
    onAction = { /* revert logic */ }
)
```

#### 📍 Show Native Alert
```kotlin
MessageManager.showNativeAlert(
    title = "Are you sure?",
    message = "This action cannot be undone.",
    onConfirm = { /* handle confirm */ },
    onCancel = { /* handle cancel */ }
)
```

---

## 🧹 Architecture

```
commonMain/
├── MessageManager.kt      # Public API
├── MessageQueue.kt        # Internal message state holder
├── MessageHost.kt         # UI Composable for snackbars
└── types/
    ├── Message.kt         # Message sealed class
    └── MessageType.kt     # Toast, Snackbar, Dialog, Alert

androidMain/
└── NativeAlertImpl.kt     # Uses AlertDialog

iosMain/
└── NativeAlertImpl.kt     # Uses UIAlertController (via UIKit)
```

---

## 🤪 Example Usage

See the [`example-app/`](example-app/) module to try it out with Compose Multiplatform setup.  
Supports both Android Emulator and iOS Simulator.

---

## 🔧 Development

Until published on MavenCentral:

```bash
# Clone the repo
git clone https://github.com/yourusername/compose-crossmessages.git
cd compose-crossmessages

# Run example on Android
./gradlew :example-app:androidRun

# Run example on iOS
open iosApp/iosApp.xcworkspace
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👋 Contributing

Pull requests and issues are welcome!  
Let’s build cross-platform UI utilities together.
