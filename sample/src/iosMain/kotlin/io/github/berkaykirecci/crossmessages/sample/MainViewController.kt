package io.github.berkaykirecci.crossmessages.sample

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/** Entry point for the Xcode project in `iosApp/`, which embeds the `SampleKit` framework. */
@Suppress("FunctionName", "unused")
fun MainViewController(): UIViewController = ComposeUIViewController { SampleApp() }
