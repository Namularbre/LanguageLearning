package com.sazerat.languagelearning.feature.grammar.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
import com.sazerat.languagelearning.core.domain.model.LessonExample
import com.sazerat.languagelearning.core.ui.component.AppScaffold
import com.sazerat.languagelearning.core.ui.component.ListenCard
import com.sazerat.languagelearning.core.ui.component.LoadingContent
import com.sazerat.languagelearning.core.ui.component.MessageContent
import com.sazerat.languagelearning.core.ui.component.SlowSpeedChip
import com.sazerat.languagelearning.core.ui.component.SpeechErrorSnackbar
import com.sazerat.languagelearning.core.ui.localized
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme
import com.sazerat.languagelearning.core.ui.uiLanguage
import com.sazerat.languagelearning.feature.grammar.R
import com.sazerat.languagelearning.feature.grammar.previewLessons

@Composable
internal fun LessonDetailScreen(
    onBack: () -> Unit,
    viewModel: LessonDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LessonDetailContent(
        uiState = uiState,
        onBack = onBack,
        onExampleClick = viewModel::onExampleClick,
        onSlowSpeedChange = viewModel::onSlowSpeedChange,
        onSpeechErrorShown = viewModel::onSpeechErrorShown,
    )
}

@Composable
internal fun LessonDetailContent(
    uiState: LessonDetailUiState,
    onBack: () -> Unit,
    onExampleClick: (LessonExample) -> Unit,
    onSlowSpeedChange: (Boolean) -> Unit,
    onSpeechErrorShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    SpeechErrorSnackbar(
        error = (uiState as? LessonDetailUiState.Success)?.speechError,
        snackbarHostState = snackbarHostState,
        onShown = onSpeechErrorShown,
    )

    val title = (uiState as? LessonDetailUiState.Success)?.lesson?.title?.localized()
        ?: stringResource(R.string.feature_grammar_list_title)
    AppScaffold(title = title, onBack = onBack, snackbarHostState = snackbarHostState) { padding ->
        when (uiState) {
            LessonDetailUiState.Loading -> LoadingContent(Modifier.padding(padding))
            LessonDetailUiState.NotFound ->
                MessageContent(stringResource(R.string.feature_grammar_not_found), Modifier.padding(padding))
            is LessonDetailUiState.Success -> LessonBody(uiState, padding, onExampleClick, onSlowSpeedChange)
        }
    }
}

@Composable
private fun LessonBody(
    state: LessonDetailUiState.Success,
    padding: PaddingValues,
    onExampleClick: (LessonExample) -> Unit,
    onSlowSpeedChange: (Boolean) -> Unit,
) {
    val lesson = state.lesson
    // A translation into the language being learnt adds nothing: hide it.
    val showTranslations = uiLanguage() != lesson.languageCode.language
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = padding.calculateTopPadding() + 8.dp,
            bottom = padding.calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Text(lesson.summary.localized(), style = MaterialTheme.typography.bodyLarge)
        }
        if (lesson.sections.any { it.examples.isNotEmpty() }) {
            item { SlowSpeedChip(state.slowSpeed, onSlowSpeedChange) }
        }
        items(lesson.sections) { section ->
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(section.heading.localized(), style = MaterialTheme.typography.titleMedium)
                Text(section.body.localized(), style = MaterialTheme.typography.bodyLarge)
                section.examples.forEach { example ->
                    ListenCard(
                        text = example.text,
                        supportingText = if (showTranslations) example.translation.localized() else null,
                        isPlaying = example.text == state.playingText,
                        onClick = { onExampleClick(example) },
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LessonDetailPreview() {
    LanguageLearningTheme(dynamicColor = false) {
        LessonDetailContent(
            uiState = LessonDetailUiState.Success(previewLessons.first(), playingText = "o fată"),
            onBack = {},
            onExampleClick = {},
            onSlowSpeedChange = {},
            onSpeechErrorShown = {},
        )
    }
}
