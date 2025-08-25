package model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource

data class LeadingIconModel(
    val iconRes: DrawableResource,
    val iconTint: Color = Color.Black,
    val iconSize: Dp = 24.dp
)