package platform

import androidx.compose.runtime.Composable
import state.ToastState

@Composable
internal expect fun NativeToast(state: ToastState)