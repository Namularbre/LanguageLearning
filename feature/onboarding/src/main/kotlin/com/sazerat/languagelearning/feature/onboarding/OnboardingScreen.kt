package com.sazerat.languagelearning.feature.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sazerat.languagelearning.core.domain.model.DailyGoal
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.ui.component.AppScaffold
import com.sazerat.languagelearning.core.ui.component.DailyGoalChips
import com.sazerat.languagelearning.core.ui.component.GlassCard
import com.sazerat.languagelearning.core.ui.component.LearningLanguageChips
import com.sazerat.languagelearning.core.ui.component.PrimaryButton
import com.sazerat.languagelearning.core.ui.component.UiLanguageChips
import com.sazerat.languagelearning.core.ui.rememberAppLanguage
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme
import com.sazerat.languagelearning.core.ui.uiLanguage

/** [languages] are the languages that have content; [onFinished] leaves the onboarding for good. */
@Composable
internal fun OnboardingScreen(
    languages: List<LanguageCode>,
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appLanguage = rememberAppLanguage()
    var uiLanguageTag by remember { mutableStateOf(appLanguage.tag) }
    LaunchedEffect(uiState.finished) {
        if (uiState.finished) onFinished()
    }
    BackHandler(enabled = uiState.step != OnboardingStep.LANGUAGES, onBack = viewModel::onBackClick)
    OnboardingContent(
        uiState = uiState,
        // The language being spoken is not offered as a language to learn.
        languages = languages.filter { !it.value.startsWith(uiLanguage()) },
        uiLanguageTag = uiLanguageTag,
        onUiLanguageSelected = { tag ->
            uiLanguageTag = tag
            appLanguage.tag = tag
        },
        actions = OnboardingActions(
            onLearningLanguageSelected = viewModel::onLearningLanguageSelected,
            onWordToggled = viewModel::onWordToggled,
            onBeginner = viewModel::onBeginnerClick,
            onDailyGoalSelected = viewModel::onDailyGoalSelected,
            onNext = viewModel::onNextClick,
        ),
    )
}

internal data class OnboardingActions(
    val onLearningLanguageSelected: (LanguageCode) -> Unit,
    val onWordToggled: (LemmaId) -> Unit,
    val onBeginner: () -> Unit,
    val onDailyGoalSelected: (DailyGoal) -> Unit,
    val onNext: () -> Unit,
)

@Composable
internal fun OnboardingContent(
    uiState: OnboardingUiState,
    languages: List<LanguageCode>,
    uiLanguageTag: String?,
    onUiLanguageSelected: (String?) -> Unit,
    actions: OnboardingActions,
) {
    AppScaffold(title = stringResource(R.string.feature_onboarding_title)) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LinearProgressIndicator(
                progress = { (uiState.step.ordinal + 1f) / OnboardingStep.entries.size },
                modifier = Modifier.fillMaxWidth(),
            )
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                when (uiState.step) {
                    OnboardingStep.LANGUAGES -> LanguagesStep(uiState, languages, uiLanguageTag, onUiLanguageSelected, actions)
                    OnboardingStep.LEVEL -> LevelStep(uiState, actions)
                    OnboardingStep.GOAL -> GoalStep(uiState, actions)
                }
            }
            if (uiState.step == OnboardingStep.LEVEL) {
                OutlinedButton(onClick = actions.onBeginner, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                    Text(stringResource(R.string.feature_onboarding_beginner))
                }
            }
            PrimaryButton(
                text = stringResource(
                    if (uiState.step == OnboardingStep.GOAL) R.string.feature_onboarding_start else R.string.feature_onboarding_next,
                ),
                onClick = actions.onNext,
                enabled = uiState.learningLanguage != null,
            )
        }
    }
}

@Composable
private fun LanguagesStep(
    uiState: OnboardingUiState,
    languages: List<LanguageCode>,
    uiLanguageTag: String?,
    onUiLanguageSelected: (String?) -> Unit,
    actions: OnboardingActions,
) {
    StepTitle(stringResource(R.string.feature_onboarding_languages_title))
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.feature_onboarding_i_speak), style = MaterialTheme.typography.titleMedium)
        UiLanguageChips(uiLanguageTag, onUiLanguageSelected, Modifier.padding(top = 8.dp))
    }
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.feature_onboarding_i_learn), style = MaterialTheme.typography.titleMedium)
        LearningLanguageChips(languages, uiState.learningLanguage, actions.onLearningLanguageSelected, Modifier.padding(top = 8.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LevelStep(uiState: OnboardingUiState, actions: OnboardingActions) {
    StepTitle(stringResource(R.string.feature_onboarding_level_title))
    Text(
        pluralStringResource(R.plurals.feature_onboarding_known_count, uiState.knownWords.size, uiState.knownWords.size),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        uiState.testWords.forEach { lemma ->
            FilterChip(
                selected = lemma.id in uiState.knownWords,
                onClick = { actions.onWordToggled(lemma.id) },
                label = { Text(lemma.text) },
            )
        }
    }
}

@Composable
private fun GoalStep(uiState: OnboardingUiState, actions: OnboardingActions) {
    StepTitle(stringResource(R.string.feature_onboarding_goal_title))
    Text(
        stringResource(R.string.feature_onboarding_goal_description),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    DailyGoalChips(uiState.dailyGoal, actions.onDailyGoalSelected)
}

@Composable
private fun StepTitle(text: String) {
    Text(text, style = MaterialTheme.typography.headlineSmall)
}

@PreviewLightDark
@Composable
private fun OnboardingPreview() {
    LanguageLearningTheme {
        OnboardingContent(
            uiState = OnboardingUiState(learningLanguage = LanguageCode("ro-RO")),
            languages = listOf(LanguageCode("en-GB"), LanguageCode("ro-RO")),
            uiLanguageTag = null,
            onUiLanguageSelected = {},
            actions = OnboardingActions({}, {}, {}, {}, {}),
        )
    }
}
