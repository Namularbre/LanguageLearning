package com.sazerat.languagelearning.feature.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.progress.GlobalProgress
import com.sazerat.languagelearning.core.domain.progress.LanguageSummary
import com.sazerat.languagelearning.core.domain.progress.LevelComprehension
import com.sazerat.languagelearning.core.ui.component.AppScaffold
import com.sazerat.languagelearning.core.ui.component.GlassCard
import com.sazerat.languagelearning.core.ui.component.LoadingContent
import com.sazerat.languagelearning.core.ui.component.Overline
import com.sazerat.languagelearning.core.ui.displayName
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme

@Composable
internal fun ProgressScreen(
    onOpenSettings: () -> Unit,
    languageSwitcher: @Composable () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ProgressContent(uiState, onOpenSettings, viewModel::onLanguageSelected, languageSwitcher)
}

@Composable
internal fun ProgressContent(
    uiState: ProgressUiState,
    onOpenSettings: () -> Unit,
    onLanguageSelected: (LanguageCode) -> Unit = {},
    languageSwitcher: @Composable () -> Unit = {},
) {
    AppScaffold(
        title = stringResource(R.string.feature_dashboard_title),
        actions = {
            languageSwitcher()
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.feature_dashboard_settings))
            }
        },
    ) { padding ->
        when (uiState) {
            ProgressUiState.Loading -> LoadingContent(Modifier.padding(padding))
            is ProgressUiState.Success -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                GlobalProgressCard(uiState.global, uiState.languageCode, onLanguageSelected)
                Text(
                    uiState.languageCode.displayName(),
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(stringResource(R.string.feature_dashboard_known_words), uiState.knownWords, Modifier.weight(1f))
                    StatCard(stringResource(R.string.feature_dashboard_in_review), uiState.learningWords, Modifier.weight(1f))
                    StatCard(stringResource(R.string.feature_dashboard_streak), uiState.streak, Modifier.weight(1f))
                }
                uiState.levelComprehension?.let { comprehension ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            stringResource(R.string.feature_dashboard_comprehension, comprehension.percent, comprehension.level.name),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Overline(stringResource(R.string.feature_dashboard_known_words_chart))
                    KnownWordsChart(uiState.knownWordsByDay)
                }
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Overline(stringResource(R.string.feature_dashboard_calendar))
                    ActivityCalendar(uiState.activeDays)
                }
            }
        }
    }
}

/**
 * Every language together: total words and the streak, which counts a day practised in any
 * language. Each started language is listed; tapping one shows its details below.
 */
@Composable
private fun GlobalProgressCard(global: GlobalProgress, current: LanguageCode, onLanguageSelected: (LanguageCode) -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Overline(stringResource(R.string.feature_dashboard_all_languages))
        Text(
            pluralStringResource(R.plurals.feature_dashboard_all_known_words, global.knownWords, global.knownWords),
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            pluralStringResource(R.plurals.feature_dashboard_all_streak, global.streak, global.streak),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val started = global.languages.filter { it.isStarted || it.languageCode == current }
        Column(modifier = Modifier.padding(top = 8.dp)) {
            started.forEach { summary ->
                LanguageProgressRow(summary, isCurrent = summary.languageCode == current, onClick = { onLanguageSelected(summary.languageCode) })
            }
        }
    }
}

@Composable
private fun LanguageProgressRow(summary: LanguageSummary, isCurrent: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .selectable(selected = isCurrent, onClick = onClick, role = Role.RadioButton)
            .heightIn(min = 48.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            summary.languageCode.displayName(),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
        Text(
            pluralStringResource(R.plurals.feature_dashboard_language_known_words, summary.knownWords, summary.knownWords),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatCard(label: String, value: Int, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier, shape = MaterialTheme.shapes.large) {
        Text(value.toString(), style = MaterialTheme.typography.headlineMedium)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** A line of the known words over the last days. Read out as its last value. */
@Composable
private fun KnownWordsChart(values: List<Int>) {
    val color = MaterialTheme.colorScheme.primary
    val description = pluralStringResource(R.plurals.feature_dashboard_known_words_today, values.lastOrNull() ?: 0, values.lastOrNull() ?: 0)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(top = 12.dp)
            .clearAndSetSemantics { contentDescription = description },
    ) {
        if (values.size < 2) return@Canvas
        val max = values.max().coerceAtLeast(1)
        val path = Path()
        values.forEachIndexed { index, value ->
            val point = Offset(size.width * index / (values.size - 1), size.height * (1 - value.toFloat() / max))
            if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
        }
        drawPath(path, color, style = Stroke(width = 3.dp.toPx()))
    }
}

/** One square per day, a week per row, filled when something was done. */
@Composable
private fun ActivityCalendar(activeDays: List<Boolean>) {
    val active = MaterialTheme.colorScheme.secondary
    val inactive = MaterialTheme.colorScheme.surfaceVariant
    val description = stringResource(R.string.feature_dashboard_calendar_description, activeDays.count { it }, activeDays.size)
    Column(
        modifier = Modifier.padding(top = 12.dp).clearAndSetSemantics { contentDescription = description },
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        activeDays.chunked(DAYS_PER_WEEK).forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                week.forEach { isActive ->
                    Box(
                        modifier = Modifier
                            .size(CALENDAR_DAY_SIZE)
                            .background(if (isActive) active else inactive, MaterialTheme.shapes.extraSmall),
                    )
                }
            }
        }
    }
}

private const val DAYS_PER_WEEK = 7
private val CALENDAR_DAY_SIZE = 28.dp

@PreviewLightDark
@Composable
private fun ProgressPreview() {
    LanguageLearningTheme {
        ProgressContent(
            uiState = ProgressUiState.Success(
                languageCode = LanguageCode("ro-RO"),
                knownWords = 182,
                learningWords = 24,
                knownWordsByDay = List(30) { 60 + it * 4 },
                activeDays = List(35) { it % 3 != 0 || it > 30 },
                streak = 5,
                levelComprehension = LevelComprehension(CefrLevel.A1, 86),
                global = GlobalProgress(
                    knownWords = 222,
                    learningWords = 30,
                    streak = 6,
                    languages = listOf(
                        LanguageSummary(LanguageCode("en-GB"), 0, 0, 0, 0, emptySet()),
                        LanguageSummary(LanguageCode("ro-RO"), 182, 24, 12, 3, setOf(1)),
                        LanguageSummary(LanguageCode("fr-FR"), 40, 6, 0, 1, setOf(1)),
                    ),
                ),
            ),
            onOpenSettings = {},
        )
    }
}
