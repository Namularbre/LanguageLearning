package com.sazerat.languagelearning.feature.grammar.list

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
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.ui.component.AppScaffold
import com.sazerat.languagelearning.core.ui.component.LoadingContent
import com.sazerat.languagelearning.core.ui.component.MessageContent
import com.sazerat.languagelearning.core.ui.displayName
import com.sazerat.languagelearning.core.ui.localized
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme
import com.sazerat.languagelearning.feature.grammar.R
import com.sazerat.languagelearning.feature.grammar.previewLessons

@Composable
internal fun LessonListScreen(
    onLessonClick: (LessonId) -> Unit,
    onBack: () -> Unit,
    viewModel: LessonListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LessonListContent(
        languageCode = viewModel.languageCode,
        uiState = uiState,
        onLessonClick = onLessonClick,
        onBack = onBack,
    )
}

@Composable
internal fun LessonListContent(
    languageCode: LanguageCode,
    uiState: LessonListUiState,
    onLessonClick: (LessonId) -> Unit,
    onBack: () -> Unit,
) {
    AppScaffold(
        title = stringResource(R.string.feature_grammar_list_title, languageCode.displayName()),
        onBack = onBack,
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (uiState) {
            LessonListUiState.Loading -> LoadingContent(modifier)
            is LessonListUiState.Success ->
                if (uiState.lessons.isEmpty()) {
                    MessageContent(stringResource(R.string.feature_grammar_list_empty), modifier)
                } else {
                    LessonList(uiState.lessons, onLessonClick, modifier)
                }
        }
    }
}

@Composable
private fun LessonList(
    lessons: List<Lesson>,
    onLessonClick: (LessonId) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(lessons, key = { it.id.value }) { lesson ->
            ListItem(
                overlineContent = { Text(stringResource(R.string.feature_grammar_lesson_number, lesson.order)) },
                headlineContent = { Text(lesson.title.localized()) },
                supportingContent = { Text(lesson.summary.localized()) },
                modifier = Modifier.clickable { onLessonClick(lesson.id) },
            )
            HorizontalDivider()
        }
    }
}

@PreviewLightDark
@Composable
private fun LessonListPreview() {
    LanguageLearningTheme {
        LessonListContent(
            languageCode = LanguageCode("ro-RO"),
            uiState = LessonListUiState.Success(previewLessons),
            onLessonClick = {},
            onBack = {},
        )
    }
}
