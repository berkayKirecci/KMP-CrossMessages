package io.github.berkaykirecci.crossmessages.alert

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun NativeAlert(data: CrossAlertData?, modifier: Modifier) {
    ComposeAlert(data, modifier)
}
