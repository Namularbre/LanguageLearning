package com.sazerat.languagelearning.feature.pronunciation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sazerat.languagelearning.core.domain.model.PronunciationItem
import com.sazerat.languagelearning.core.ui.component.AppScaffold
import com.sazerat.languagelearning.core.ui.component.ListenCard
import com.sazerat.languagelearning.core.ui.component.LoadingContent
import com.sazerat.languagelearning.core.ui.component.MessageContent
import com.sazerat.languagelearning.core.ui.component.SlowSpeedChip
import com.sazerat.languagelearning.core.ui.component.SpeechErrorSnackbar
import com.sazerat.languagelearning.core.ui.localized
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme
import com.sazerat.languagelearning.feature.pronunciation.R
import com.sazerat.languagelearning.feature.pronunciation.label
import com.sazerat.languagelearning.feature.pronunciation.previewExercises

@Composable
internal fun PronunciationDetailScreen(
    onBack: () -> Unit,
    viewModel: PronunciationDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PronunciationDetailContent(
        uiState = uiState,
        onBack = onBack,
        onItemClick = viewModel::onItemClick,
        onSlowSpeedChange = viewModel::onSlowSpeedChange,
        onSpeechErrorShown = viewModel::onSpeechErrorShown,
    )
}

@Composable
internal fun PronunciationDetailContent(
    uiState: PronunciationDetailUiState,
    onBack: () -> Unit,
    onItemClick: (PronunciationItem) -> Unit,
    onSlowSpeedChange: (Boolean) -> Unit,
    onSpeechErrorShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    SpeechErrorSnackbar(
        error = (uiState as? PronunciationDetailUiState.Success)?.speechError,
        snackbarHostState = snackbarHostState,
        onShown = onSpeechErrorShown,
    )

    val title = (uiState as? PronunciationDetailUiState.Success)?.exercise?.title?.localized()
        ?: stringResource(R.string.feature_pronunciation_list_title)
    AppScaffold(title = title, onBack = onBack, snackbarHostState = snackbarHostState) { padding ->
        when (uiState) {
            PronunciationDetailUiState.Loading -> LoadingContent(Modifier.padding(padding))
            PronunciationDetailUiState.NotFound ->
                MessageContent(stringResource(R.string.feature_pronunciation_not_found), Modifier.padding(padding))
            is PronunciationDetailUiState.Success -> ExerciseDetail(uiState, padding, onItemClick, onSlowSpeedChange)
        }
    }
}

@Composable
private fun ExerciseDetail(
    state: PronunciationDetailUiState.Success,
    padding: PaddingValues,
    onItemClick: (PronunciationItem) -> Unit,
    onSlowSpeedChange: (Boolean) -> Unit,
) {
    val exercise = state.exercise
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = padding.calculateTopPadding() + 8.dp,
            bottom = padding.calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(exercise.type.label()) })
                AssistChip(onClick = {}, label = { Text(exercise.focus.localized()) })
            }
        }
        item {
            Text(stringResource(R.string.feature_pronunciation_instructions), style = MaterialTheme.typography.titleMedium)
            Text(exercise.instructions.localized(), style = MaterialTheme.typography.bodyLarge)
        }
        item {
            Text(stringResource(R.string.feature_pronunciation_items), style = MaterialTheme.typography.titleMedium)
        }
        item { SlowSpeedChip(state.slowSpeed, onSlowSpeedChange) }
        items(exercise.items, key = { it.text }) { item ->
            ListenCard(
                text = item.text,
                supportingText = item.ipa,
                isPlaying = item.text == state.playingText,
                onClick = { onItemClick(item) },
            )
        }
        item {
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.feature_pronunciation_recording_soon),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PronunciationDetailPreview() {
    LanguageLearningTheme(dynamicColor = false) {
        PronunciationDetailContent(
            uiState = PronunciationDetailUiState.Success(previewExercises.first(), playingText = "sheep"),
            onBack = {},
            onItemClick = {},
            onSlowSpeedChange = {},
            onSpeechErrorShown = {},
        )
    }
}
