package com.sazerat.languagelearning.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sazerat.languagelearning.core.domain.model.DailyGoal
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.ui.R
import com.sazerat.languagelearning.core.ui.autonym
import com.sazerat.languagelearning.core.ui.displayName
import com.sazerat.languagelearning.core.ui.supportedUiLanguages

// Choices shared by the onboarding and the settings.

/** UI language: the phone's ([selected] `null`) or one of the supported languages, named in itself. */
@Composable
fun UiLanguageChips(selected: String?, onSelected: (String?) -> Unit, modifier: Modifier = Modifier) {
    ChipRow(modifier) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelected(null) },
            label = { Text(stringResource(R.string.core_ui_phone_language)) },
        )
        supportedUiLanguages.forEach { tag ->
            FilterChip(selected = tag == selected, onClick = { onSelected(tag) }, label = { Text(autonym(tag)) })
        }
    }
}

@Composable
fun LearningLanguageChips(
    languages: List<LanguageCode>,
    selected: LanguageCode?,
    onSelected: (LanguageCode) -> Unit,
    modifier: Modifier = Modifier,
) {
    ChipRow(modifier) {
        languages.forEach { language ->
            FilterChip(selected = language == selected, onClick = { onSelected(language) }, label = { Text(language.displayName()) })
        }
    }
}

@Composable
fun DailyGoalChips(selected: DailyGoal, onSelected: (DailyGoal) -> Unit, modifier: Modifier = Modifier) {
    ChipRow(modifier) {
        DailyGoal.entries.forEach { goal ->
            FilterChip(
                selected = goal == selected,
                onClick = { onSelected(goal) },
                label = { Text(stringResource(R.string.core_ui_duration_minutes, goal.minutes)) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipRow(modifier: Modifier, content: @Composable () -> Unit) {
    FlowRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) { content() }
}
