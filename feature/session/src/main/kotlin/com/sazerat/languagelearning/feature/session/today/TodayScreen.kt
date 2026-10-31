package com.sazerat.languagelearning.feature.session.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.TextKind
import com.sazerat.languagelearning.core.ui.component.AppScaffold
import com.sazerat.languagelearning.core.ui.component.ComprehensionBadge
import com.sazerat.languagelearning.core.ui.component.GlassCard
import com.sazerat.languagelearning.core.ui.component.LoadingContent
import com.sazerat.languagelearning.core.ui.component.Overline
import com.sazerat.languagelearning.core.ui.component.textMeta
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme
import com.sazerat.languagelearning.feature.session.R

/** The app does the navigation: Review and the Reader belong to other features. */
@Composable
internal fun TodayScreen(
    onOpenReview: () -> Unit,
    onReadText: (TextId) -> Unit,
    languageSwitcher: @Composable () -> Unit,
    viewModel: TodayViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TodayContent(uiState, onOpenReview, onReadText, languageSwitcher)
}

@Composable
internal fun TodayContent(
    uiState: TodayUiState,
    onOpenReview: () -> Unit,
    onReadText: (TextId) -> Unit,
    languageSwitcher: @Composable () -> Unit = {},
) {
    AppScaffold(title = stringResource(R.string.feature_session_today_title), actions = { languageSwitcher() }) { padding ->
        when (uiState) {
            TodayUiState.Loading -> LoadingContent(Modifier.padding(padding))
            is TodayUiState.Success -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                GoalCard(uiState)
                ReviewCard(uiState, onOpenReview)
                uiState.nextText?.let { text -> NextTextCard(text, onClick = { onReadText(text.id) }) }
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Overline(stringResource(R.string.feature_session_new_words))
                    Text(
                        pluralStringResource(R.plurals.feature_session_new_words_count, uiState.newWordsThisWeek, uiState.newWordsThisWeek),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

/** Streak and the ring towards the daily goal. */
@Composable
private fun GoalCard(uiState: TodayUiState.Success) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            val goalDescription = stringResource(R.string.feature_session_goal_description, uiState.minutesToday, uiState.goalMinutes)
            Box(
                modifier = Modifier.size(88.dp).clearAndSetSemantics { contentDescription = goalDescription },
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    progress = { (uiState.minutesToday.toFloat() / uiState.goalMinutes).coerceAtMost(1f) },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 8.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Text(
                    stringResource(R.string.feature_session_goal_ring, uiState.minutesToday, uiState.goalMinutes),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Column {
                Text(
                    pluralStringResource(R.plurals.feature_session_streak, uiState.streak, uiState.streak),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    stringResource(
                        if (uiState.minutesToday >= uiState.goalMinutes) R.string.feature_session_goal_reached else R.string.feature_session_goal_pending,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ReviewCard(uiState: TodayUiState.Success, onOpenReview: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth(), onClick = onOpenReview) {
        Overline(stringResource(R.string.feature_session_to_review))
        if (uiState.dueCards == 0) {
            Text(stringResource(R.string.feature_session_nothing_to_review), style = MaterialTheme.typography.titleMedium)
        } else {
            Text(
                pluralStringResource(R.plurals.feature_session_due_cards, uiState.dueCards, uiState.dueCards),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                stringResource(R.string.feature_session_review_time, uiState.reviewMinutes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun NextTextCard(text: NextText, onClick: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Overline(
            stringResource(if (text.isReread) R.string.feature_session_read_again else R.string.feature_session_read_next),
            color = MaterialTheme.colorScheme.primary,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text.title, style = MaterialTheme.typography.titleLarge)
                Text(
                    textMeta(text.kind, text.level, text.minutes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            ComprehensionBadge(text.comprehension)
        }
    }
}

@PreviewLightDark
@Composable
private fun TodayPreview() {
    LanguageLearningTheme {
        TodayContent(
            uiState = TodayUiState.Success(
                streak = 4,
                minutesToday = 6,
                goalMinutes = 10,
                dueCards = 12,
                reviewMinutes = 3,
                nextText = NextText(TextId("t"), "La gară", CefrLevel.A1, TextKind.DIALOGUE, 3, 88),
                newWordsThisWeek = 17,
            ),
            onOpenReview = {},
            onReadText = {},
        )
    }
}
