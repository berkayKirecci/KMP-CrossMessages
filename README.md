# 📦 Compose CrossMessages

**Compose CrossMessages** is a simple and lightweight library for displaying messages across Android
and iOS using Jetpack Compose Multiplatform (KMP).  
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

| Android Snackbar                                        | iOS Native Alert                          |
|---------------------------------------------------------|-------------------------------------------|
| ![android-snackbar](./screenshots/snackbar_android.png) | ![ios-alert](./screenshots/alert_ios.png) |

---

## 🚀 Getting Started

### 1. Add Dependency

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.berkaykirecci:snackbar:$version")
}
```

---

### 2. Example Usages

#### 📍 Show Snackbar Messages

```kotlin
@Composable
fun App() {
    val state = rememberSnackbarState()
    state.show(SnackbarDefaults.success("This is a success Message."))
    state.show(SnackbarDefaults.warning("This is a warning Message."))
    state.show(SnackbarDefaults.error("This is a error Message."))
    state.show(SnackbarDefaults.info("This is a info Message."))
    state.show(
      model = SnackbarModel(
        message = "Custom Mesaj",
        backgroundColor = Color.LightGray,
        duration = 3000L,
        leadingIcon = Res.drawable.leadingIcon,
        showActionButton = false,
        alignment = Alignment.BottomCenter
      )
    )
}
```

---

#### 📍 Show Native Alert

```kotlin
@Composable
fun App() {
    NativeAlert(
        message = "Warning",
        title = "This is a warning message!",
        actions = listOf(
            Action("Ok", ActionStyle.DEFAULT),
            Action("Cancel", ActionStyle.CANCEL)
        )
    )
}
```

---

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
