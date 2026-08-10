# 📦 Compose CrossMessages

**Compose CrossMessages** shows snackbars, toasts and alert dialogs from a single Compose
Multiplatform codebase, using each platform's own presentation where it matters.

| | Snackbar | Toast | Alert |
|---|---|---|---|
| **Android** | Material 3 Compose | `android.widget.Toast` | Material 3 `AlertDialog` |
| **iOS** | Material 3 Compose | UIKit overlay | `UIAlertController` |
| **Desktop (JVM)** | Material 3 Compose | Compose surface | Material 3 `AlertDialog` |
| **Web (wasmJs / js)** | Material 3 Compose | Compose surface | Material 3 `AlertDialog` |

📖 [Library guide](docs/LIBRARY.md) · 📝 [What changed in 2.0.0](docs/CHANGES-2.0.0.md) ·
🚀 [Publishing](docs/PUBLISHING.md) — all available in English and Türkçe.

---

## ✨ Features

- **One suspending API.** `showSnackbar(...)` returns a `CrossSnackbarResult`, so you learn whether
  the user tapped your action or let it time out — no callback plumbing.
- **Real queueing.** Messages are serialized by a `Mutex`, so repeating the same text queues
  correctly and nothing is dropped or dequeued out of order.
- **Material 3 by default.** Shape, typography, elevation and colors all come from the ambient
  `MaterialTheme` and follow its light/dark switch. Nothing is hard-coded to white or black.
- **Accessible.** Messages are announced as polite live regions, the dismiss button is a real
  48dp-target `IconButton`, and durations stretch to the platform's recommended accessibility
  timeout.
- **Gesture-friendly.** Swipe a snackbar away, or press and hold to pause its timer while you read.
- **Insets-aware.** Snackbars stay clear of status bars, gesture bars, cutouts and the keyboard.

<p align="center">
  <img src="./media/android.gif" alt="Android Snackbar Demo"/>
  &nbsp;
  <img src="./media/ios.gif" alt="iOS Snackbar Demo"/>
</p>

---

## 🚀 Getting started

```kotlin
dependencies {
    implementation("io.github.berkaykirecci:crossmessages:2.0.0")
}
```

Published targets: `android`, `iosArm64`, `iosSimulatorArm64`, `jvm`, `wasmJs`, `js`.

> **iOS:** `iosX64` is not published. Compose Multiplatform dropped it in 1.11.0, so the iOS
> simulator on Intel Macs is unsupported — device builds and Apple Silicon simulators are unaffected.
>
> **Kotlin/JS:** the Compose JS canvas target is still experimental upstream, so add
> `org.jetbrains.compose.experimental.jscanvas.enabled=true` to your `gradle.properties`.
> `wasmJs` needs no flag.

Place the hosts as the last children of a full-size container so they overlay your content:

```kotlin
@Composable
fun App() {
    val snackbarHostState = rememberCrossSnackbarHostState()
    val toastHostState = rememberCrossToastHostState()
    val alertHostState = rememberCrossAlertHostState()

    Box(Modifier.fillMaxSize()) {
        MyScreen(snackbarHostState)

        CrossSnackbarHost(snackbarHostState)
        CrossToastHost(toastHostState)
        CrossAlertHost(alertHostState)
    }
}
```

---

## 📍 Snackbar

### Semantic shorthands

Each one picks a palette derived from your `MaterialTheme` and a matching leading icon.

```kotlin
snackbarHostState.success("Saved to your library")
snackbarHostState.warning("Storage almost full")
snackbarHostState.error("Upload failed")
snackbarHostState.info("Syncing in the background")
```

### Actions, and knowing what the user did

The suspending overload returns the outcome, which is the point of having an action at all:

```kotlin
scope.launch {
    val result = snackbarHostState.showSnackbar(
        message = "Message archived",
        actionLabel = "Undo",
        duration = CrossSnackbarDuration.Long,
    )
    if (result == CrossSnackbarResult.ActionPerformed) {
        unarchive()
    }
}
```

### Full control

```kotlin
snackbarHostState.show(
    CrossSnackbarVisuals(
        message = "Custom message",
        actionLabel = "Retry",
        withDismissAction = true,
        duration = CrossSnackbarDuration.Indefinite,
        position = CrossSnackbarPosition.Top,
        style = CrossSnackbarStyle.Warning,
        icon = CrossSnackbarIcon.Resource(Res.drawable.my_icon),
        colors = CrossSnackbarColors(
            containerColor = Color(0xFF0F5132),
            contentColor = Color(0xFFD1FADF),
            actionColor = Color(0xFF6EE7A0),
        ),
        textAlign = TextAlign.Center,
        dismissActionContentDescription = "Close notification",
    )
)
```

`CrossSnackbarDuration` is `Short` (~4s), `Long` (~10s) or `Indefinite`. All three are widened to
the platform's recommended accessibility timeout when the user has asked for more time.

### Theming every snackbar at once

```kotlin
CompositionLocalProvider(
    LocalCrossSnackbarColors provides CrossSnackbarStyleColors(
        success = CrossSnackbarColors(Color(0xFF0F5132), Color(0xFFD1FADF)),
    )
) {
    CrossSnackbarHost(snackbarHostState)
}
```

Entries left `null` keep the theme-derived default. `CrossSnackbarStyle.Error` maps onto
`colorScheme.errorContainer`, and the neutral style onto `inverseSurface`, so a custom color scheme
carries through without any configuration.

### Custom surface

```kotlin
CrossSnackbarHost(snackbarHostState) { data ->
    MyOwnSnackbar(data.visuals.message, onDismiss = data::dismiss)
}
```

---

## 📍 Toast

```kotlin
toastHostState.show("Toast message")
toastHostState.show("Longer toast", CrossToastDuration.Long)
```

`CrossToastHost` defaults to `CrossToastStyle.Native` — `android.widget.Toast` on Android, a UIKit
overlay on iOS, and the shared Compose surface on desktop and web. Pass
`CrossToastStyle.Compose` to get the identical Compose surface on every platform:

```kotlin
CrossToastHost(toastHostState, style = CrossToastStyle.Compose)
```

---

## 📍 Alert

```kotlin
scope.launch {
    val result = alertHostState.showAlert(
        CrossAlertVisuals(
            title = "Delete draft?",
            message = "This cannot be undone.",
            actions = persistentListOf(
                CrossAlertAction("Cancel", CrossAlertActionStyle.Cancel),
                CrossAlertAction("Delete", CrossAlertActionStyle.Destructive),
            ),
        )
    )
    if (result is CrossAlertResult.ActionPerformed && result.index == 1) {
        deleteDraft()
    }
}
```

Any number of actions is supported. `CrossAlertActionStyle.Destructive` renders in
`colorScheme.error` on Compose platforms and maps to `UIAlertActionStyleDestructive` on iOS.
Passing no actions at all yields a single confirm button labelled `defaultConfirmLabel`.

`CrossAlertVisuals.actions` is an `ImmutableList` so the Compose compiler can *infer* its stability
rather than take an `@Immutable` promise. If you'd rather pass a plain `List`, use the convenience
overload, which converts for you:

```kotlin
alertHostState.show(
    title = "Delete draft?",
    message = "This cannot be undone.",
    actions = listOf(
        CrossAlertAction("Cancel", CrossAlertActionStyle.Cancel),
        CrossAlertAction("Delete", CrossAlertActionStyle.Destructive),
    ),
)
```

---

## 🧪 Sample app

The sample exercises every API — all snackbar styles, positions, queueing, the suspending result
flow, both toast styles, and alerts with one, two and three actions.

It is split in two: `:sample` holds the shared Compose UI and the desktop/iOS/web entry points,
while `:sample-android` is a thin Android launcher. They are separate because AGP 9 no longer allows
`com.android.application` in a Kotlin Multiplatform module.

```bash
./gradlew :sample-android:installDebug          # Android
./gradlew :sample:run                           # Desktop
./gradlew :sample:wasmJsBrowserDevelopmentRun   # Web
open iosApp/iosApp.xcodeproj                    # iOS — then hit Run
```

The iOS app lives in `iosApp/`. A build phase invokes
`:sample:embedAndSignAppleFrameworkForXcode`, which links the `SampleKit` framework and copies the
Compose resources into the app bundle, so Xcode is the only thing you need. From the command line:

```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 17,OS=latest' build
```

Building for a physical device additionally needs your Apple Developer Team ID in
`iosApp/Configuration/Config.xcconfig`; the simulator needs no signing.

---

## 🔀 Migrating from 1.x

2.0 is a clean break. Everything now lives under `io.github.berkaykirecci.crossmessages.*` instead
of the top-level `model` / `state` / `ui` / `platform` packages — the old `platform` package shadowed
Kotlin/Native's own `platform.*` namespace, and `model.SnackbarDefaults` collided with
`androidx.compose.material3.SnackbarDefaults`.

| 1.x | 2.0 |
|---|---|
| `MultiPlatformSnackbar(state = s)` | `CrossSnackbarHost(hostState = s)` |
| `Toast(state = s)` | `CrossToastHost(hostState = s)` |
| `Alert(state = s)` | `CrossAlertHost(hostState = s)` |
| `rememberSnackbarState()` | `rememberCrossSnackbarHostState()` |
| `rememberToastState()` | `rememberCrossToastHostState()` |
| `rememberAlertState()` | `rememberCrossAlertHostState()` |
| `SnackbarModel` | `CrossSnackbarVisuals` |
| `LeadingIconModel` / `TextModel` / `ActionButtonModel` | folded into `CrossSnackbarVisuals` + `CrossSnackbarColors` |
| `duration = 3000L` | `duration = CrossSnackbarDuration.Long` |
| `alignment = Alignment.TopCenter` | `position = CrossSnackbarPosition.Top` |
| `backgroundColor` + `textModel.textColor` | `colors = CrossSnackbarColors(container, content)` |
| `showActionButton` + `actionButtonModel` | `withDismissAction` + `actionLabel` |
| `LocalSnackbarColors` / `DefaultSnackbarColors` | `LocalCrossSnackbarColors` / `CrossSnackbarStyleColors` |
| `AlertModel(message, title, actions)` | `CrossAlertVisuals(title, message, actions)` |
| `DialogAction(actionTitle, style, callbak)` | `CrossAlertAction(label, style, onClick)` |
| `ActionStyle.DEFAULT` / `.CANCEL` / `.DESTRUCTIVE` | `CrossAlertActionStyle.Default` / `.Cancel` / `.Destructive` |
| `state.clear()` | `state.dismissCurrent()` |
| `platform.getViewController()` | removed — internal |

Behavioural changes worth knowing about:

- **Timing moved into the hosts.** v1 ran up to three competing timers per toast; there is now one.
- **Native presentation happens in effects.** v1 called `Toast.makeText().show()` and
  `presentViewController` from the composable body, so recomposition duplicated them.
- **Snackbars are no longer edge-to-edge.** They are inset-padded, elevated, rounded, and capped at
  600dp wide, per the Material 3 spec.
- **Alerts render every action.** v1 kept only the first `DEFAULT` and first `CANCEL`, and always
  drew a dismiss button — blank when there was no cancel action.
- **Desktop toasts work.** v1's JVM implementation was an empty function.

---

## 📄 License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 👋 Contributing

Pull requests and issues are welcome!
