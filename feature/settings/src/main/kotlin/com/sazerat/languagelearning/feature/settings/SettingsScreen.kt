package com.sazerat.languagelearning.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sazerat.languagelearning.core.domain.model.DailyGoal
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.ui.component.AppScaffold
import com.sazerat.languagelearning.core.ui.component.DailyGoalChips
import com.sazerat.languagelearning.core.ui.component.GlassCard
import com.sazerat.languagelearning.core.ui.component.LearningLanguageChips
import com.sazerat.languagelearning.core.ui.component.LoadingContent
import com.sazerat.languagelearning.core.ui.component.UiLanguageChips
import com.sazerat.languagelearning.core.ui.rememberAppLanguage
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme

/** [languages] are the languages that have content, in display order. */
@Composable
internal fun SettingsScreen(languages: List<LanguageCode>, onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appLanguage = rememberAppLanguage()
    // Android recreates the screen when the language changes; this state only bridges the moment before.
    var uiLanguage by remember { mutableStateOf(appLanguage.tag) }
    SettingsContent(
        uiState = uiState,
        languages = languages,
        uiLanguage = uiLanguage,
        onUiLanguageSelected = { tag ->
            uiLanguage = tag
            appLanguage.tag = tag
        },
        onLearningLanguageSelected = viewModel::onLearningLanguageSelected,
        onDailyGoalSelected = viewModel::onDailyGoalSelected,
        onPronunciationHelpChange = viewModel::onPronunciationHelpChange,
        onBack = onBack,
    )
}

@Composable
internal fun SettingsContent(
    uiState: SettingsUiState,
    languages: List<LanguageCode>,
    uiLanguage: String?,
    onUiLanguageSelected: (String?) -> Unit,
    onLearningLanguageSelected: (LanguageCode) -> Unit,
    onDailyGoalSelected: (DailyGoal) -> Unit,
    onPronunciationHelpChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    AppScaffold(title = stringResource(R.string.feature_settings_title), onBack = onBack) { padding ->
        when (uiState) {
            SettingsUiState.Loading -> LoadingContent(Modifier.padding(padding))
            is SettingsUiState.Success -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Section(stringResource(R.string.feature_settings_ui_language), stringResource(R.string.feature_settings_ui_language_description)) {
                    UiLanguageChips(uiLanguage, onUiLanguageSelected)
                }
                Section(stringResource(R.string.feature_settings_learning_language), null) {
                    LearningLanguageChips(languages, uiState.learningLanguage, onLearningLanguageSelected)
                }
                Section(stringResource(R.string.feature_settings_daily_goal), null) {
                    DailyGoalChips(uiState.dailyGoal, onDailyGoalSelected)
                }
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.feature_settings_pronunciation_help), style = MaterialTheme.typography.titleMedium)
                            Text(
                                stringResource(R.string.feature_settings_pronunciation_help_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(checked = uiState.pronunciationHelp, onCheckedChange = onPronunciationHelpChange)
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(title: String, description: String?, content: @Composable () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        if (description != null) {
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(modifier = Modifier.padding(top = 8.dp)) { content() }
    }
}

@PreviewLightDark
@Composable
private fun SettingsPreview() {
    LanguageLearningTheme {
        SettingsContent(
            uiState = SettingsUiState.Success(LanguageCode("ro-RO"), DailyGoal.TEN, pronunciationHelp = true),
            languages = listOf(LanguageCode("en-GB"), LanguageCode("ro-RO"), LanguageCode("fr-FR")),
            uiLanguage = null,
            onUiLanguageSelected = {},
            onLearningLanguageSelected = {},
            onDailyGoalSelected = {},
            onPronunciationHelpChange = {},
            onBack = {},
        )
    }
}
