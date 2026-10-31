package com.sazerat.languagelearning.feature.settings.switcher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.progress.LanguageSummary
import com.sazerat.languagelearning.core.ui.component.GlassCard
import com.sazerat.languagelearning.core.ui.component.Overline
import com.sazerat.languagelearning.core.ui.displayName
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme
import com.sazerat.languagelearning.feature.settings.R

/**
 * A chip naming the language being learnt, for the top bar of each tab. Tapping it lists every
 * language with where the learner stands in it; one more tap switches.
 */
@Composable
fun LanguageSwitcher(modifier: Modifier = Modifier, viewModel: LanguageSwitcherViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val state = uiState as? LanguageSwitcherUiState.Success ?: return
    LanguageSwitcherContent(state, viewModel::onLanguageSelected, modifier)
}

@Composable
internal fun LanguageSwitcherContent(
    uiState: LanguageSwitcherUiState.Success,
    onLanguageSelected: (LanguageCode) -> Unit,
    modifier: Modifier = Modifier,
) {
    var open by rememberSaveable { mutableStateOf(false) }
    val switchLabel = stringResource(R.string.feature_settings_switch_language)
    AssistChip(
        onClick = { open = true },
        label = { Text(uiState.current.displayName()) },
        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = switchLabel, modifier = Modifier.size(18.dp)) },
        modifier = modifier.padding(end = 4.dp),
    )
    if (open) {
        LanguageSheet(
            uiState = uiState,
            onLanguageSelected = { language ->
                open = false
                if (language != uiState.current) onLanguageSelected(language)
            },
            onDismiss = { open = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSheet(
    uiState: LanguageSwitcherUiState.Success,
    onLanguageSelected: (LanguageCode) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        LanguageList(uiState, onLanguageSelected)
    }
}

@Composable
private fun LanguageList(uiState: LanguageSwitcherUiState.Success, onLanguageSelected: (LanguageCode) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Overline(stringResource(R.string.feature_settings_learning_language))
        uiState.languages.forEach { summary ->
            LanguageRow(summary, isCurrent = summary.languageCode == uiState.current, onClick = { onLanguageSelected(summary.languageCode) })
        }
    }
}

@Composable
private fun LanguageRow(summary: LanguageSummary, isCurrent: Boolean, onClick: () -> Unit) {
    GlassCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().semantics { role = Role.RadioButton },
        shape = MaterialTheme.shapes.large,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(summary.languageCode.displayName(), style = MaterialTheme.typography.titleMedium)
                Text(
                    summaryLine(summary),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (isCurrent) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = stringResource(R.string.feature_settings_current_language),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun summaryLine(summary: LanguageSummary): String = if (!summary.isStarted) {
    stringResource(R.string.feature_settings_language_not_started)
} else {
    listOf(
        pluralStringResource(R.plurals.feature_settings_language_known_words, summary.knownWords, summary.knownWords),
        pluralStringResource(R.plurals.feature_settings_language_due_cards, summary.dueCards, summary.dueCards),
    ).joinToString(separator = " · ")
}

@PreviewLightDark
@Composable
private fun LanguageListPreview() {
    LanguageLearningTheme {
        LanguageList(
            uiState = LanguageSwitcherUiState.Success(
                current = LanguageCode("ro-RO"),
                languages = listOf(
                    LanguageSummary(LanguageCode("en-GB"), 0, 0, 0, 0, emptySet()),
                    LanguageSummary(LanguageCode("ro-RO"), 182, 24, 12, 3, setOf(1)),
                    LanguageSummary(LanguageCode("fr-FR"), 40, 6, 0, 1, setOf(1)),
                ),
            ),
            onLanguageSelected = {},
        )
    }
}
