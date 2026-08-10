package io.github.berkaykirecci.crossmessages.snackbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import io.github.berkaykirecci.crossmessages.resources.Res
import io.github.berkaykirecci.crossmessages.resources.ic_close

/** The Material 3 snackbar surface used by [CrossSnackbarHost]. */
@Composable
fun CrossSnackbar(
    data: CrossSnackbarData,
    modifier: Modifier = Modifier,
    shape: Shape = CrossSnackbarDefaults.shape,
    colors: CrossSnackbarColors = CrossSnackbarDefaults.colors(data.visuals),
    elevation: Dp = CrossSnackbarDefaults.Elevation,
) {
    val visuals = data.visuals
    val icon = CrossSnackbarDefaults.icon(visuals)

    Surface(
        modifier = modifier
            .padding(CrossSnackbarDefaults.Margin)
            .widthIn(max = CrossSnackbarDefaults.MaxWidth),
        shape = shape,
        color = colors.containerColor,
        contentColor = colors.contentColor,
        shadowElevation = elevation,
    ) {
        Row(
            modifier = Modifier.padding(CrossSnackbarDefaults.ContentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = colors.iconColor,
                    modifier = Modifier.size(CrossSnackbarDefaults.IconSize),
                )
                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = visuals.message,
                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = visuals.textAlign,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            if (visuals.actionLabel != null) {
                TextButton(
                    onClick = data::performAction,
                    colors = ButtonDefaults.textButtonColors(contentColor = colors.actionColor),
                ) {
                    Text(
                        text = visuals.actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            if (visuals.withDismissAction) {
                IconButton(
                    onClick = data::dismiss,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = colors.actionColor),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_close),
                        contentDescription = visuals.dismissActionContentDescription,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
