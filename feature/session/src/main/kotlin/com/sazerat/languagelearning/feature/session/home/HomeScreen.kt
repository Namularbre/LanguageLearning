package com.sazerat.languagelearning.feature.session.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.SessionDuration
import com.sazerat.languagelearning.core.ui.component.AppScaffold
import com.sazerat.languagelearning.core.ui.displayName
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme
import com.sazerat.languagelearning.feature.session.R

/**
 * Home screen. It does not know the other features: the app passes navigation callbacks,
 * so `:feature:session` never depends on `:feature:pronunciation` or `:feature:grammar`.
 */
@Composable
internal fun HomeScreen(
    languages: List<LanguageCode>,
    onOpenPronunciation: (LanguageCode) -> Unit,
    onOpenLessons: (LanguageCode) -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenReview: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val selectedDuration by viewModel.selectedDuration.collectAsStateWithLifecycle()
    val selectedLanguageCode by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val selectedLanguage = selectedLanguageCode?.let(::LanguageCode) ?: languages.first()
    HomeContent(
        selectedDuration = selectedDuration,
        onDurationSelected = viewModel::onDurationSelected,
        languages = languages,
        selectedLanguage = selectedLanguage,
        onLanguageSelected = viewModel::onLanguageSelected,
        onOpenPronunciation = { onOpenPronunciation(selectedLanguage) },
        onOpenLessons = { onOpenLessons(selectedLanguage) },
        onOpenLibrary = onOpenLibrary,
        onOpenReview = onOpenReview,
    )
}

@Composable
internal fun HomeContent(
    selectedDuration: SessionDuration,
    onDurationSelected: (SessionDuration) -> Unit,
    languages: List<LanguageCode>,
    selectedLanguage: LanguageCode,
    onLanguageSelected: (LanguageCode) -> Unit,
    onOpenPronunciation: () -> Unit,
    onOpenLessons: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenReview: () -> Unit,
) {
    AppScaffold(title = stringResource(R.string.feature_session_home_title)) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Temporary entry to the reading loop until Today replaces this screen.
            ModuleCard(
                title = stringResource(R.string.feature_session_module_library),
                description = stringResource(R.string.feature_session_module_library_description),
                onClick = onOpenLibrary,
            )
            ModuleCard(
                title = stringResource(R.string.feature_session_module_review),
                description = stringResource(R.string.feature_session_module_review_description),
                onClick = onOpenReview,
            )
            DailySessionCard(selectedDuration, onDurationSelected)
            Text(stringResource(R.string.feature_session_a_la_carte), style = MaterialTheme.typography.titleMedium)
            LanguageChips(languages, selectedLanguage, onLanguageSelected)
            ModuleCard(
                title = stringResource(R.string.feature_session_module_pronunciation),
                description = stringResource(R.string.feature_session_module_pronunciation_description),
                onClick = onOpenPronunciation,
            )
            ModuleCard(
                title = stringResource(R.string.feature_session_module_lessons),
                description = stringResource(R.string.feature_session_module_lessons_description),
                onClick = onOpenLessons,
            )
        }
    }
}

@Composable
private fun DailySessionCard(
    selectedDuration: SessionDuration,
    onDurationSelected: (SessionDuration) -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.feature_session_daily_title), style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SessionDuration.entries.forEach { duration ->
                    FilterChip(
                        selected = duration == selectedDuration,
                        onClick = { onDurationSelected(duration) },
                        label = { Text(stringResource(R.string.feature_session_minutes, duration.minutes)) },
                    )
                }
            }
            // The daily session itself is an M3 deliverable.
            Button(onClick = {}, enabled = false) {
                Text(stringResource(R.string.feature_session_start))
            }
            Text(
                stringResource(R.string.feature_session_coming_soon),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun LanguageChips(
    languages: List<LanguageCode>,
    selectedLanguage: LanguageCode,
    onLanguageSelected: (LanguageCode) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        languages.forEach { language ->
            FilterChip(
                selected = language == selectedLanguage,
                onClick = { onLanguageSelected(language) },
                label = { Text(language.displayName()) },
            )
        }
    }
}

@Composable
private fun ModuleCard(title: String, description: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@PreviewLightDark
@Composable
private fun HomePreview() {
    LanguageLearningTheme {
        HomeContent(
            selectedDuration = SessionDuration.TEN,
            onDurationSelected = {},
            languages = previewLanguages,
            selectedLanguage = previewLanguages.first(),
            onLanguageSelected = {},
            onOpenPronunciation = {},
            onOpenLessons = {},
            onOpenLibrary = {},
            onOpenReview = {},
        )
    }
}

private val previewLanguages = listOf(LanguageCode("en-GB"), LanguageCode("ro-RO"), LanguageCode("fr-FR"))
