package com.sazerat.languagelearning.feature.srs.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.Rating
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.review.ExerciseKind
import com.sazerat.languagelearning.core.ui.component.AppScaffold
import com.sazerat.languagelearning.core.ui.component.GlassCard
import com.sazerat.languagelearning.core.ui.component.LoadingContent
import com.sazerat.languagelearning.core.ui.component.Overline
import com.sazerat.languagelearning.core.ui.component.PrimaryButton
import com.sazerat.languagelearning.core.ui.component.SpeechErrorSnackbar
import com.sazerat.languagelearning.core.ui.component.durationLabel
import com.sazerat.languagelearning.core.ui.component.label
import com.sazerat.languagelearning.core.ui.icon.AppIcons
import com.sazerat.languagelearning.core.ui.localized
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme
import com.sazerat.languagelearning.core.ui.theme.ReadingTextStyle
import com.sazerat.languagelearning.core.ui.theme.appColors
import com.sazerat.languagelearning.feature.srs.previewChoices
import com.sazerat.languagelearning.feature.srs.R
import com.sazerat.languagelearning.feature.srs.previewReviewing
import com.sazerat.languagelearning.core.ui.R as CoreUiR

@Composable
internal fun ReviewScreen(
    onReadText: (TextId) -> Unit,
    onBack: (() -> Unit)?,
    viewModel: ReviewViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ReviewContent(
        uiState = uiState,
        onReveal = viewModel::onRevealClick,
        onPlay = viewModel::onPlayClick,
        onRating = viewModel::onRatingClick,
        onChoice = viewModel::onChoiceClick,
        onContinue = viewModel::onContinueClick,
        onSpeechErrorShown = viewModel::onSpeechErrorShown,
        onReadText = onReadText,
        onBack = onBack,
    )
}

@Composable
internal fun ReviewContent(
    uiState: ReviewUiState,
    onReveal: () -> Unit,
    onPlay: () -> Unit,
    onRating: (Rating) -> Unit,
    onSpeechErrorShown: () -> Unit,
    onReadText: (TextId) -> Unit,
    onBack: (() -> Unit)?,
    onChoice: (LemmaId) -> Unit = {},
    onContinue: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    AppScaffold(
        title = stringResource(R.string.feature_srs_title),
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (uiState) {
            ReviewUiState.Loading -> LoadingContent(modifier)
            is ReviewUiState.NothingDue -> NothingDueContent(uiState, onReadText, modifier)
            is ReviewUiState.Reviewing -> ReviewingContent(uiState, onReveal, onPlay, onRating, onChoice, onContinue, modifier)
            is ReviewUiState.Finished -> FinishedContent(uiState, onReadText, modifier)
        }
    }
    if (uiState is ReviewUiState.Reviewing) {
        SpeechErrorSnackbar(uiState.speechError, snackbarHostState, onSpeechErrorShown)
    }
}

@Composable
private fun ReviewingContent(
    uiState: ReviewUiState.Reviewing,
    onReveal: () -> Unit,
    onPlay: () -> Unit,
    onRating: (Rating) -> Unit,
    onChoice: (LemmaId) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val card = uiState.card
    val exercise = card.exercise
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                stringResource(R.string.feature_srs_progress, uiState.position + 1, uiState.total),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { uiState.position.toFloat() / uiState.total },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CardFront(card)
            if (exercise != null) {
                ChoiceButtons(card.lemma.id, exercise, onChoice)
                if (exercise.answered) AnswerFeedback(correct = exercise.selected == card.lemma.id)
            }
            if (card.revealed) CardBack(card, uiState.isPlaying, onPlay)
        }
        when {
            exercise != null -> if (exercise.answered) PrimaryButton(stringResource(R.string.feature_srs_continue), onContinue)
            card.revealed -> RatingButtons(card.intervals, onRating)
            else -> PrimaryButton(stringResource(R.string.feature_srs_show_answer), onReveal)
        }
    }
}

/**
 * The answers to pick from: words of the language to fill the blank, or meanings of the word shown.
 * Once one is picked, the right answer turns green and a wrong pick red.
 */
@Composable
private fun ChoiceButtons(answer: LemmaId, exercise: ExerciseUiState, onChoice: (LemmaId) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        exercise.choices.forEach { choice ->
            val isAnswer = choice.id == answer
            val (container, content) = when {
                !exercise.answered -> MaterialTheme.appColors.glass to MaterialTheme.colorScheme.onSurface
                isAnswer -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
                choice.id == exercise.selected -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
                else -> MaterialTheme.appColors.glass to MaterialTheme.colorScheme.onSurfaceVariant
            }
            OutlinedButton(
                onClick = { onChoice(choice.id) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(containerColor = container, contentColor = content),
            ) {
                val label = when (exercise.kind) {
                    ExerciseKind.MEANING -> choice.meaning.localized()
                    ExerciseKind.FILL_BLANK -> choice.text
                }
                // The words of the language are set like the text they fill.
                val style = if (exercise.kind == ExerciseKind.FILL_BLANK) ReadingTextStyle else MaterialTheme.typography.titleMedium
                Text(label, style = style, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun AnswerFeedback(correct: Boolean) {
    Text(
        stringResource(if (correct) R.string.feature_srs_right_answer else R.string.feature_srs_wrong_answer),
        style = MaterialTheme.typography.titleMedium,
        color = if (correct) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
}

/**
 * The sentence the word was met in. The word is hidden until the answer is shown, except when the
 * question asks for its meaning: the word is then highlighted, and the sentence's translation,
 * which would give the meaning away, waits for the answer.
 */
@Composable
private fun CardFront(card: ReviewCardUiState) {
    val asksMeaning = card.exercise?.kind == ExerciseKind.MEANING
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Overline(stringResource(if (asksMeaning) R.string.feature_srs_meaning_hint else R.string.feature_srs_front_hint))
        val sentence = buildAnnotatedString {
            card.sentence.tokens.forEachIndexed { index, token ->
                when {
                    index != card.tokenIndex -> append(token.text)
                    card.revealed || asksMeaning -> withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                        append(token.text)
                    }
                    else -> withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(BLANK)
                    }
                }
            }
        }
        Text(sentence, style = ReadingTextStyle, modifier = Modifier.padding(vertical = 8.dp))
        if (!asksMeaning || card.revealed) {
            Text(
                card.sentence.translation.localized(),
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CardBack(card: ReviewCardUiState, isPlaying: Boolean, onPlay: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(card.lemma.text, style = MaterialTheme.typography.headlineSmall)
                val grammar = listOfNotNull(card.lemma.partOfSpeech.label(), card.lemma.gender?.label()).joinToString(" · ")
                Text(grammar, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onPlay, enabled = !isPlaying) {
                Icon(
                    AppIcons.VolumeUp,
                    contentDescription = stringResource(CoreUiR.string.core_ui_listen),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(Modifier.padding(4.dp))
        Text(card.lemma.meaning.localized(), style = MaterialTheme.typography.titleMedium)
        if (card.showPronunciation && card.lemma.ipa != null) {
            Text("/${card.lemma.ipa}/", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** The four FSRS answers, each with the time until the card comes back. */
@Composable
private fun RatingButtons(intervals: Map<Rating, Long>, onRating: (Rating) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Rating.entries.forEach { rating ->
            val colors = if (rating == Rating.GOOD) {
                ButtonDefaults.buttonColors()
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(
                onClick = { onRating(rating) },
                modifier = Modifier.weight(1f).heightIn(min = 64.dp),
                shape = MaterialTheme.shapes.medium,
                colors = colors,
                contentPadding = ButtonDefaults.TextButtonContentPadding,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(rating.label(), style = MaterialTheme.typography.labelLarge, maxLines = 1)
                    intervals[rating]?.let { Text(durationLabel(it), style = MaterialTheme.typography.labelSmall) }
                }
            }
        }
    }
}

@Composable
private fun Rating.label(): String = stringResource(
    when (this) {
        Rating.AGAIN -> R.string.feature_srs_again
        Rating.HARD -> R.string.feature_srs_hard
        Rating.GOOD -> R.string.feature_srs_good
        Rating.EASY -> R.string.feature_srs_easy
    },
)

@Composable
private fun NothingDueContent(uiState: ReviewUiState.NothingDue, onReadText: (TextId) -> Unit, modifier: Modifier = Modifier) {
    CenteredColumn(modifier) {
        if (uiState.nextReviewIn == null) {
            CenteredText(stringResource(R.string.feature_srs_no_cards), MaterialTheme.typography.titleMedium)
        } else {
            CenteredText(stringResource(R.string.feature_srs_nothing_due))
            CenteredText(stringResource(R.string.feature_srs_next_review, durationLabel(uiState.nextReviewIn)))
        }
        uiState.suggestedText?.let { ReadSuggested(it, onReadText) }
    }
}

@Composable
private fun FinishedContent(uiState: ReviewUiState.Finished, onReadText: (TextId) -> Unit, modifier: Modifier = Modifier) {
    CenteredColumn(modifier) {
        Text(stringResource(R.string.feature_srs_finished_title), style = MaterialTheme.typography.headlineMedium)
        CenteredText(pluralStringResource(R.plurals.feature_srs_reviewed, uiState.reviewed, uiState.reviewed))
        CenteredText(stringResource(R.string.feature_srs_success_rate, uiState.successRate))
        uiState.nextReviewIn?.let { CenteredText(stringResource(R.string.feature_srs_next_review, durationLabel(it))) }
        uiState.suggestedText?.let { ReadSuggested(it, onReadText) }
    }
}

@Composable
private fun ReadSuggested(text: SuggestedText, onReadText: (TextId) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.feature_srs_read_now), style = MaterialTheme.typography.titleMedium)
        OutlinedButton(onClick = { onReadText(text.id) }, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
            Text(stringResource(R.string.feature_srs_read_suggested, text.title))
        }
    }
}

@Composable
private fun CenteredColumn(modifier: Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

@Composable
private fun CenteredText(text: String, style: TextStyle = MaterialTheme.typography.bodyLarge) {
    Text(text, style = style, textAlign = TextAlign.Center)
}

/** Shown in place of the hidden word on the front. */
private const val BLANK = "_____"

@PreviewLightDark
@Composable
private fun ReviewFrontPreview() {
    LanguageLearningTheme {
        ReviewContent(previewReviewing, {}, {}, {}, {}, {}, onBack = null)
    }
}

@PreviewLightDark
@Composable
private fun ReviewBackPreview() {
    LanguageLearningTheme {
        val revealed = previewReviewing.copy(card = previewReviewing.card.copy(revealed = true))
        ReviewContent(revealed, {}, {}, {}, {}, {}, onBack = null)
    }
}

@PreviewLightDark
@Composable
private fun ReviewChoicesPreview() {
    LanguageLearningTheme {
        ReviewContent(previewChoices, {}, {}, {}, {}, {}, onBack = null)
    }
}

@PreviewLightDark
@Composable
private fun ReviewChoiceAnsweredPreview() {
    LanguageLearningTheme {
        val card = previewChoices.card
        val answered = card.copy(revealed = true, exercise = card.exercise?.copy(selected = card.exercise.choices.first().id))
        ReviewContent(previewChoices.copy(card = answered), {}, {}, {}, {}, {}, onBack = null)
    }
}

@PreviewLightDark
@Composable
private fun ReviewFinishedPreview() {
    LanguageLearningTheme {
        ReviewContent(
            ReviewUiState.Finished(reviewed = 12, successRate = 83, nextReviewIn = 4 * 3_600_000L, suggestedText = SuggestedText(TextId("t"), "La gară")),
            {}, {}, {}, {}, {}, onBack = null,
        )
    }
}
