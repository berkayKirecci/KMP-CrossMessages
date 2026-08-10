package io.github.berkaykirecci.crossmessages.toast

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** No OS-level toast on these targets, so [CrossToastStyle.Native] uses the Compose surface. */
@Composable
internal actual fun NativeToast(
    data: CrossToastData?,
    modifier: Modifier,
    contentWindowInsets: WindowInsets,
) {
    ComposeToast(data, modifier, contentWindowInsets)
}
