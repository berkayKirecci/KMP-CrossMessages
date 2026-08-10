package io.github.berkaykirecci.crossmessages.alert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/** Renders whatever [hostState] is showing: `UIAlertController` on iOS, a Material 3 dialog elsewhere. */
@Composable
fun CrossAlertHost(
    hostState: CrossAlertHostState,
    modifier: Modifier = Modifier,
) {
    NativeAlert(hostState.currentAlertData, modifier)
}

/** Presents [data] with platform chrome. */
@Composable
internal expect fun NativeAlert(data: CrossAlertData?, modifier: Modifier)

/** The Material 3 dialog shared by every target except iOS. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ComposeAlert(data: CrossAlertData?, modifier: Modifier) {
    if (data == null) return

    val visuals = data.visuals
    val actions = data.actions
    val properties = DialogProperties(
        dismissOnBackPress = visuals.dismissible,
        dismissOnClickOutside = visuals.dismissible,
    )
    val onDismissRequest: () -> Unit = { if (visuals.dismissible) data.dismiss() }

    if (actions.size <= 2) {
        val cancelIndex = actions.indexOfFirst { it.style == CrossAlertActionStyle.Cancel }
        val confirmIndex = actions.indices.lastOrNull { it != cancelIndex } ?: cancelIndex
        val dismissIndex = actions.indices.firstOrNull { it != confirmIndex }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            title = { Text(visuals.title) },
            text = visuals.message?.let { message -> { Text(message) } },
            confirmButton = {
                if (confirmIndex >= 0) {
                    AlertActionButton(actions[confirmIndex]) { data.performAction(confirmIndex) }
                }
            },
            dismissButton = dismissIndex?.let { index ->
                { AlertActionButton(actions[index]) { data.performAction(index) } }
            },
            properties = properties,
        )
    } else {
        BasicAlertDialog(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            properties = properties,
        ) {
            Surface(
                shape = AlertDialogDefaults.shape,
                color = AlertDialogDefaults.containerColor,
                tonalElevation = AlertDialogDefaults.TonalElevation,
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = visuals.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = AlertDialogDefaults.titleContentColor,
                    )
                    if (visuals.message != null) {
                        Text(
                            text = visuals.message,
                            modifier = Modifier.padding(top = 16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = AlertDialogDefaults.textContentColor,
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        actions.forEachIndexed { index, action ->
                            AlertActionButton(action) { data.performAction(index) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertActionButton(action: CrossAlertAction, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        colors = when (action.style) {
            CrossAlertActionStyle.Destructive ->
                ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            else -> ButtonDefaults.textButtonColors()
        },
    ) {
        Text(action.label)
    }
}
