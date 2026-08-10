package io.github.berkaykirecci.crossmessages.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.berkaykirecci.crossmessages.alert.CrossAlertAction
import io.github.berkaykirecci.crossmessages.alert.CrossAlertActionStyle
import io.github.berkaykirecci.crossmessages.alert.CrossAlertHost
import io.github.berkaykirecci.crossmessages.alert.CrossAlertResult
import io.github.berkaykirecci.crossmessages.alert.CrossAlertVisuals
import io.github.berkaykirecci.crossmessages.alert.rememberCrossAlertHostState
import io.github.berkaykirecci.crossmessages.snackbar.CrossSnackbarColors
import io.github.berkaykirecci.crossmessages.snackbar.CrossSnackbarDuration
import io.github.berkaykirecci.crossmessages.snackbar.CrossSnackbarHost
import io.github.berkaykirecci.crossmessages.snackbar.CrossSnackbarPosition
import io.github.berkaykirecci.crossmessages.snackbar.CrossSnackbarResult
import io.github.berkaykirecci.crossmessages.snackbar.CrossSnackbarStyle
import io.github.berkaykirecci.crossmessages.snackbar.CrossSnackbarStyleColors
import io.github.berkaykirecci.crossmessages.snackbar.CrossSnackbarVisuals
import io.github.berkaykirecci.crossmessages.snackbar.LocalCrossSnackbarColors
import io.github.berkaykirecci.crossmessages.snackbar.rememberCrossSnackbarHostState
import io.github.berkaykirecci.crossmessages.toast.CrossToastDuration
import io.github.berkaykirecci.crossmessages.toast.CrossToastHost
import io.github.berkaykirecci.crossmessages.toast.CrossToastStyle
import io.github.berkaykirecci.crossmessages.toast.rememberCrossToastHostState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

private val CustomSuccess = CrossSnackbarStyleColors(
    success = CrossSnackbarColors(
        containerColor = Color(0xFF0F5132),
        contentColor = Color(0xFFD1FADF),
        actionColor = Color(0xFF6EE7A0),
    )
)

@Composable
fun SampleApp() {
    var darkTheme by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()) {
        var position by remember { mutableStateOf(CrossSnackbarPosition.Bottom) }
        var toastStyle by remember { mutableStateOf(CrossToastStyle.Native) }
        var customColors by remember { mutableStateOf(false) }
        var lastOutcome by remember { mutableStateOf("—") }

        var callbackLabel by remember { mutableStateOf<String?>(null) }

        val snackbarHostState = rememberCrossSnackbarHostState()
        val toastHostState = rememberCrossToastHostState()
        val alertHostState = rememberCrossAlertHostState()
        val scope = rememberCoroutineScope()

        fun runAlert(visuals: CrossAlertVisuals) {
            callbackLabel = null
            scope.launch {
                val returned = when (val result = alertHostState.showAlert(visuals)) {
                    is CrossAlertResult.ActionPerformed -> "${result.action.label} #${result.index}"
                    CrossAlertResult.Dismissed -> "Dismissed"
                }
                val summary = "result $returned · onClick ${callbackLabel ?: "—"}"
                lastOutcome = summary
                toastHostState.show(summary)
            }
        }

        Box(Modifier.fillMaxSize()) {
            Surface(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("CrossMessages 2.0", style = MaterialTheme.typography.headlineMedium)

                    Card(Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ToggleRow("Dark theme", darkTheme) { darkTheme = it }
                            ToggleRow("Custom success palette", customColors) { customColors = it }
                            Row2 {
                                CrossSnackbarPosition.entries.forEach { entry ->
                                    FilterChip(
                                        selected = position == entry,
                                        onClick = { position = entry },
                                        label = { Text("Snackbar ${entry.name}") },
                                    )
                                }
                            }
                            Row2 {
                                CrossToastStyle.entries.forEach { entry ->
                                    FilterChip(
                                        selected = toastStyle == entry,
                                        onClick = { toastStyle = entry },
                                        label = { Text("Toast ${entry.name}") },
                                    )
                                }
                            }
                            HorizontalDivider()
                            Text(
                                "Last outcome: $lastOutcome",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }

                    SectionTitle("Snackbar styles")
                    Row2 {
                        Button(onClick = {
                            snackbarHostState.success("Saved to your library", position = position)
                        }) { Text("Success") }
                        Button(onClick = {
                            snackbarHostState.warning("Storage almost full", position = position)
                        }) { Text("Warning") }
                        Button(onClick = {
                            snackbarHostState.error("Upload failed", position = position)
                        }) { Text("Error") }
                        Button(onClick = {
                            snackbarHostState.info("Syncing in the background", position = position)
                        }) { Text("Info") }
                        Button(onClick = {
                            snackbarHostState.show(
                                CrossSnackbarVisuals(
                                    message = "Neutral Material 3 snackbar",
                                    position = position,
                                )
                            )
                        }) { Text("Neutral") }
                    }

                    SectionTitle("Snackbar behaviour")
                    Row2 {
                        Button(onClick = {
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Message archived",
                                    actionLabel = "Undo",
                                    duration = CrossSnackbarDuration.Long,
                                    position = position,
                                    style = CrossSnackbarStyle.Info,
                                )
                                lastOutcome = when (result) {
                                    CrossSnackbarResult.ActionPerformed -> "Snackbar: Undo tapped"
                                    CrossSnackbarResult.Dismissed -> "Snackbar: dismissed"
                                }
                            }
                        }) { Text("With action") }

                        Button(onClick = {
                            snackbarHostState.show(
                                CrossSnackbarVisuals(
                                    message = "Stays until you dismiss it — swipe me away",
                                    duration = CrossSnackbarDuration.Indefinite,
                                    position = position,
                                    style = CrossSnackbarStyle.Warning,
                                )
                            )
                        }) { Text("Indefinite") }

                        Button(onClick = {
                            repeat(3) { snackbarHostState.info("Queued message", position = position) }
                        }) { Text("Queue x3") }

                        OutlinedButton(onClick = snackbarHostState::dismissCurrent) {
                            Text("Dismiss")
                        }
                    }

                    SectionTitle("Toast")
                    Row2 {
                        Button(onClick = { toastHostState.show("Short toast") }) { Text("Short") }
                        Button(onClick = {
                            toastHostState.show("Long toast", CrossToastDuration.Long)
                        }) { Text("Long") }
                        Button(onClick = {
                            repeat(3) { toastHostState.show("Queued toast") }
                        }) { Text("Queue x3") }
                    }

                    SectionTitle("Alert")
                    Text(
                        "Every action reports twice: through its own onClick callback, and through " +
                            "the value the suspending showAlert() returns. Both land in the toast.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Row2 {
                        Button(onClick = {
                            runAlert(
                                CrossAlertVisuals(
                                    title = "Single action",
                                    message = "v1 drew a second, blank button here.",
                                    actions = persistentListOf(
                                        CrossAlertAction("OK", onClick = { callbackLabel = "OK" }),
                                    ),
                                )
                            )
                        }) { Text("1 action") }

                        Button(onClick = {
                            runAlert(
                                CrossAlertVisuals(
                                    title = "Delete draft?",
                                    message = "This cannot be undone.",
                                    actions = persistentListOf(
                                        CrossAlertAction(
                                            label = "Cancel",
                                            style = CrossAlertActionStyle.Cancel,
                                            onClick = { callbackLabel = "Cancel" },
                                        ),
                                        CrossAlertAction(
                                            label = "Delete",
                                            style = CrossAlertActionStyle.Destructive,
                                            onClick = { callbackLabel = "Delete" },
                                        ),
                                    ),
                                )
                            )
                        }) { Text("2 actions") }

                        Button(onClick = {
                            runAlert(
                                CrossAlertVisuals(
                                    title = "Export format",
                                    message = "v1 silently dropped every action past the first two.",
                                    actions = persistentListOf(
                                        CrossAlertAction("PDF", onClick = { callbackLabel = "PDF" }),
                                        CrossAlertAction("CSV", onClick = { callbackLabel = "CSV" }),
                                        CrossAlertAction(
                                            label = "Cancel",
                                            style = CrossAlertActionStyle.Cancel,
                                            onClick = { callbackLabel = "Cancel" },
                                        ),
                                    ),
                                )
                            )
                        }) { Text("3 actions") }
                    }
                }
            }

            CompositionLocalProvider(
                LocalCrossSnackbarColors provides if (customColors) CustomSuccess else null
            ) {
                CrossSnackbarHost(snackbarHostState)
            }
            CrossToastHost(toastHostState, style = toastStyle)
            CrossAlertHost(alertHostState)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun Row2(content: @Composable () -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) { content() }
}
