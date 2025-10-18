package model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.berkaykirecci.library.generated.resources.Res
import io.github.berkaykirecci.library.generated.resources.ic_close
import org.jetbrains.compose.resources.DrawableResource

data class ActionButtonModel(
    val iconRes: DrawableResource = Res.drawable.ic_close,
    val iconTint: Color = Color.White,
    val iconSize: Dp = 24.dp,
    val onActionClick: (() -> Unit)? = null
)