package platform

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun NativeAlert(message: String?, title: String?, actions: List<Action>?) {
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
}