package ui

import androidx.compose.runtime.Composable
import platform.NativeAlert
import state.AlertState

@Composable
fun Alert(state: AlertState) {
    if (state.alertModel != null) {
        NativeAlert(state)
    }
}