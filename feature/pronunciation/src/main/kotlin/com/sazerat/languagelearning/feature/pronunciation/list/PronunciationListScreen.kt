package com.sazerat.languagelearning.feature.pronunciation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.PronunciationExercise
import com.sazerat.languagelearning.core.ui.component.AppScaffold
import com.sazerat.languagelearning.core.ui.component.LoadingContent
import com.sazerat.languagelearning.core.ui.component.MessageContent
import com.sazerat.languagelearning.core.ui.displayName
import com.sazerat.languagelearning.core.ui.localized
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme
import com.sazerat.languagelearning.feature.pronunciation.R
import com.sazerat.languagelearning.feature.pronunciation.label
import com.sazerat.languagelearning.feature.pronunciation.previewExercises

/** Stateful entry point: gets its ViewModel from Hilt and forwards state to [PronunciationListContent]. */
@Composable
internal fun PronunciationListScreen(
    onExerciseClick: (ExerciseId) -> Unit,
    onBack: () -> Unit,
    viewModel: PronunciationListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PronunciationListContent(
        languageCode = viewModel.languageCode,
        uiState = uiState,
        onExerciseClick = onExerciseClick,
        onBack = onBack,
    )
}

@Composable
internal fun PronunciationListContent(
    languageCode: LanguageCode,
    uiState: PronunciationListUiState,
    onExerciseClick: (ExerciseId) -> Unit,
    onBack: () -> Unit,
) {
    AppScaffold(
        title = stringResource(R.string.feature_pronunciation_list_title, languageCode.displayName()),
        onBack = onBack,
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (uiState) {
            PronunciationListUiState.Loading -> LoadingContent(modifier)
            is PronunciationListUiState.Success ->
                if (uiState.exercises.isEmpty()) {
                    MessageContent(stringResource(R.string.feature_pronunciation_list_empty), modifier)
                } else {
                    ExerciseList(uiState.exercises, onExerciseClick, modifier)
                }
        }
    }
}

@Composable
private fun ExerciseList(
    exercises: List<PronunciationExercise>,
    onExerciseClick: (ExerciseId) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(exercises, key = { it.id.value }) { exercise ->
            ListItem(
                overlineContent = { Text(exercise.type.label()) },
                headlineContent = { Text(exercise.title.localized()) },
                supportingContent = { Text(exercise.focus.localized()) },
                modifier = Modifier.clickable { onExerciseClick(exercise.id) },
            )
            HorizontalDivider()
        }
    }
}

@PreviewLightDark
@Composable
private fun PronunciationListPreview() {
    LanguageLearningTheme(dynamicColor = false) {
        PronunciationListContent(
            languageCode = LanguageCode("en-GB"),
            uiState = PronunciationListUiState.Success(previewExercises),
            onExerciseClick = {},
            onBack = {},
        )
    }
}
