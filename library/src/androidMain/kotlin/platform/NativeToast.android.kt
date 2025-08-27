package platform

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import state.ToastState

@Composable
actual fun NativeToast(state: ToastState) {
    Toast.makeText(LocalContext.current, state.toastMessage, Toast.LENGTH_SHORT).show()
}