package io.github.berkaykirecci.crossmessages.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "CrossMessages Sample") {
        SampleApp()
    }
}
