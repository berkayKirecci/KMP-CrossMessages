package io.github.berkaykirecci.crossmessages.toast

import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun NativeToast(
    data: CrossToastData?,
    modifier: Modifier,
    contentWindowInsets: WindowInsets,
) {
    val context = LocalContext.current.applicationContext

    DisposableEffect(data) {
        val toast = data?.let {
            Toast.makeText(context, it.visuals.message, it.visuals.duration.toAndroidLength())
                .apply { show() }
        }
        onDispose { toast?.cancel() }
    }
}

private fun CrossToastDuration.toAndroidLength(): Int = when (this) {
    CrossToastDuration.Short -> Toast.LENGTH_SHORT
    CrossToastDuration.Long -> Toast.LENGTH_LONG
}
