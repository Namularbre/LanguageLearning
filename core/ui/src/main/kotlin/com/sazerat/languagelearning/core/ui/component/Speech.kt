package com.sazerat.languagelearning.core.ui.component

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sazerat.languagelearning.core.ui.R
import com.sazerat.languagelearning.core.ui.icon.AppIcons
import com.sazerat.languagelearning.speech.api.SpeechError

/**
 * A word or phrase the learner can tap to hear. Shows a spinner while it is being spoken.
 * [supportingText] (translation, IPA…) is optional.
 */
@Composable
fun ListenCard(
    text: String,
    supportingText: String?,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                if (isPlaying) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = AppIcons.VolumeUp,
                        contentDescription = stringResource(R.string.core_ui_listen),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Column {
                Text(text, style = MaterialTheme.typography.titleMedium)
                if (supportingText != null) {
                    Text(
                        supportingText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun SlowSpeedChip(slowSpeed: Boolean, onSlowSpeedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    FilterChip(
        selected = slowSpeed,
        onClick = { onSlowSpeedChange(!slowSpeed) },
        label = { Text(stringResource(R.string.core_ui_slow_speed)) },
        modifier = modifier,
    )
}

/**
 * Shows [error] once in a snackbar, then calls [onShown]. A missing voice gets a shortcut to the
 * system text-to-speech settings, where the user can install it.
 */
@Composable
fun SpeechErrorSnackbar(error: SpeechError?, snackbarHostState: SnackbarHostState, onShown: () -> Unit) {
    if (error == null) return
    val context = LocalContext.current
    val message = stringResource(
        when (error) {
            SpeechError.UNSUPPORTED_LANGUAGE -> R.string.core_ui_voice_not_installed
            SpeechError.ENGINE_ERROR -> R.string.core_ui_playback_failed
        },
    )
    val settingsLabel = stringResource(R.string.core_ui_voice_settings)
    LaunchedEffect(error) {
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = settingsLabel.takeIf { error == SpeechError.UNSUPPORTED_LANGUAGE },
            // With an action, the default is "indefinite": the error would stay set and a new
            // failure of the same kind would show nothing. Long lets it go away on its own.
            duration = SnackbarDuration.Long,
        )
        if (result == SnackbarResult.ActionPerformed) {
            try {
                context.startActivity(Intent(TTS_SETTINGS_ACTION))
            } catch (_: ActivityNotFoundException) {
                // Some devices hide this screen; the message already says what is missing.
            }
        }
        onShown()
    }
}

// Public system action, but not exposed as a constant by the Android SDK.
private const val TTS_SETTINGS_ACTION = "com.android.settings.TTS_SETTINGS"
