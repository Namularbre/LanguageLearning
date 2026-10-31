package com.sazerat.languagelearning.feature.reading.library

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.ui.component.AppScaffold
import com.sazerat.languagelearning.core.ui.component.ComprehensionBadge
import com.sazerat.languagelearning.core.ui.component.GlassCard
import com.sazerat.languagelearning.core.ui.component.LevelTile
import com.sazerat.languagelearning.core.ui.component.LoadingContent
import com.sazerat.languagelearning.core.ui.component.Overline
import com.sazerat.languagelearning.core.ui.component.textMeta
import com.sazerat.languagelearning.core.ui.localized
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme
import com.sazerat.languagelearning.core.ui.theme.appColors
import com.sazerat.languagelearning.feature.reading.R
import com.sazerat.languagelearning.feature.reading.previewLibraryState

@Composable
internal fun LibraryScreen(
    onTextClick: (TextId) -> Unit,
    onLessonClick: (LessonId) -> Unit,
    onExerciseClick: (ExerciseId) -> Unit,
    onBack: (() -> Unit)?,
    languageSwitcher: @Composable () -> Unit,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LibraryContent(
        uiState = uiState,
        onSectionSelected = viewModel::onSectionSelected,
        onLevelSelected = viewModel::onLevelSelected,
        onTextClick = onTextClick,
        onLessonClick = onLessonClick,
        onExerciseClick = onExerciseClick,
        onBack = onBack,
        languageSwitcher = languageSwitcher,
    )
}

@Composable
internal fun LibraryContent(
    uiState: LibraryUiState,
    onSectionSelected: (LibrarySection) -> Unit,
    onLevelSelected: (CefrLevel?) -> Unit,
    onTextClick: (TextId) -> Unit,
    onLessonClick: (LessonId) -> Unit,
    onExerciseClick: (ExerciseId) -> Unit,
    onBack: (() -> Unit)?,
    languageSwitcher: @Composable () -> Unit = {},
) {
    // The language is named by the switcher next to the title.
    AppScaffold(title = stringResource(R.string.feature_reading_library_title), onBack = onBack, actions = { languageSwitcher() }) { padding ->
        when (uiState) {
            LibraryUiState.Loading -> LoadingContent(Modifier.padding(padding))
            is LibraryUiState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding() + 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "filters") { Filters(uiState, onSectionSelected, onLevelSelected) }
                when (uiState.section) {
                    LibrarySection.TEXTS -> texts(uiState, onTextClick)
                    LibrarySection.LESSONS -> items(uiState.lessons, key = { it.id.value }) { lesson ->
                        SimpleRow(
                            overline = stringResource(R.string.feature_reading_lesson_number, lesson.order),
                            title = lesson.title.localized(),
                            supporting = lesson.summary.localized(),
                            onClick = { onLessonClick(lesson.id) },
                        )
                    }
                    LibrarySection.PRONUNCIATION -> items(uiState.exercises, key = { it.id.value }) { exercise ->
                        SimpleRow(
                            overline = null,
                            title = exercise.title.localized(),
                            supporting = exercise.focus.localized(),
                            onClick = { onExerciseClick(exercise.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Filters(
    uiState: LibraryUiState.Success,
    onSectionSelected: (LibrarySection) -> Unit,
    onLevelSelected: (CefrLevel?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            uiState.sections.forEach { section ->
                FilterChip(
                    selected = section == uiState.section,
                    onClick = { onSectionSelected(section) },
                    label = { Text(section.label()) },
                )
            }
        }
        if (uiState.section == LibrarySection.TEXTS && uiState.levels.size > 1) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = uiState.level == null,
                    onClick = { onLevelSelected(null) },
                    label = { Text(stringResource(R.string.feature_reading_all_levels)) },
                )
                uiState.levels.forEach { level ->
                    FilterChip(
                        selected = level == uiState.level,
                        onClick = { onLevelSelected(level) },
                        label = { Text(level.name) },
                    )
                }
            }
        }
    }
}

private fun LazyListScope.texts(uiState: LibraryUiState.Success, onTextClick: (TextId) -> Unit) {
    items(uiState.texts, key = { it.id.value }) { text ->
        TextRow(text, isSuggested = text.id == uiState.suggestedTextId, onClick = { onTextClick(text.id) })
    }
}

@Composable
private fun TextRow(text: TextItem, isSuggested: Boolean, onClick: () -> Unit) {
    val blobs = MaterialTheme.appColors.blobs
    GlassCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LevelTile(text.level, blobs[text.level.ordinal % blobs.size].copy(alpha = 0.35f))
            Column(modifier = Modifier.weight(1f)) {
                when {
                    isSuggested -> Overline(
                        stringResource(R.string.feature_reading_suggested),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    text.isRead -> Overline(stringResource(R.string.feature_reading_read))
                }
                Text(text.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    textMeta(text.kind, text.level, text.minutes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            ComprehensionBadge(text.comprehension)
        }
    }
}

@Composable
private fun SimpleRow(overline: String?, title: String, supporting: String, onClick: () -> Unit) {
    GlassCard(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        if (overline != null) Overline(overline)
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(supporting, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LibrarySection.label(): String = stringResource(
    when (this) {
        LibrarySection.TEXTS -> R.string.feature_reading_section_texts
        LibrarySection.LESSONS -> R.string.feature_reading_section_lessons
        LibrarySection.PRONUNCIATION -> R.string.feature_reading_section_pronunciation
    },
)

@PreviewLightDark
@Composable
private fun LibraryPreview() {
    LanguageLearningTheme {
        LibraryContent(
            uiState = previewLibraryState,
            onSectionSelected = {},
            onLevelSelected = {},
            onTextClick = {},
            onLessonClick = {},
            onExerciseClick = {},
            onBack = null,
        )
    }
}
