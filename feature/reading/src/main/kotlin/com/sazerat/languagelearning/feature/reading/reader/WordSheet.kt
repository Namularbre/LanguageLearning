package com.sazerat.languagelearning.feature.reading.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.ui.component.GlassCard
import com.sazerat.languagelearning.core.ui.component.Overline
import com.sazerat.languagelearning.core.ui.component.PrimaryButton
import com.sazerat.languagelearning.core.ui.component.label
import com.sazerat.languagelearning.core.ui.icon.AppIcons
import com.sazerat.languagelearning.core.ui.localized
import com.sazerat.languagelearning.feature.reading.R
import com.sazerat.languagelearning.core.ui.R as CoreUiR

/**
 * What a tapped word means (spec §4): its base form, grammar, meaning, the sentence it was met in,
 * and what the learner wants to do with it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WordSheet(
    sheet: WordSheetUiState,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onAddToReview: () -> Unit,
    onKnown: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            WordHeader(sheet, isPlaying, onPlay)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(sheet.lemma.meaning.localized(), style = MaterialTheme.typography.titleMedium)
                sheet.lemma.otherMeanings?.let { other ->
                    Text(
                        stringResource(R.string.feature_reading_other_meanings, other.localized()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (sheet.showPronunciation && sheet.lemma.ipa != null) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Overline(stringResource(R.string.feature_reading_pronunciation))
                    Text("/${sheet.lemma.ipa}/", style = MaterialTheme.typography.bodyLarge)
                    sheet.lemma.pronunciationNote?.let { note ->
                        Text(
                            note.localized(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            SentenceCard(sheet)
            WordActions(sheet.status, onAddToReview, onKnown)
        }
    }
}

@Composable
private fun WordHeader(sheet: WordSheetUiState, isPlaying: Boolean, onPlay: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            // "casei → casă" when the form differs from the base form.
            val headline = if (sheet.form.lowercase() == sheet.lemma.text.lowercase()) {
                sheet.lemma.text
            } else {
                "${sheet.form} → ${sheet.lemma.text}"
            }
            Text(headline, style = MaterialTheme.typography.headlineSmall)
            val grammar = listOfNotNull(sheet.lemma.partOfSpeech.label(), sheet.lemma.gender?.label()).joinToString(" · ")
            Text(grammar, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            sheet.formNote?.let { note ->
                Text(note.localized(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        IconButton(onClick = onPlay) {
            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                if (isPlaying) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        AppIcons.VolumeUp,
                        contentDescription = stringResource(CoreUiR.string.core_ui_listen),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

/** The sentence the word was met in, with the word in bold, and its translation. */
@Composable
private fun SentenceCard(sheet: WordSheetUiState) {
    GlassCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        val sentence = buildAnnotatedString {
            sheet.sentence.tokens.forEachIndexed { index, token ->
                if (index == sheet.word.tokenIndex) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(token.text) }
                } else {
                    append(token.text)
                }
            }
        }
        Text(sentence, style = MaterialTheme.typography.bodyLarge)
        Text(
            sheet.sentence.translation.localized(),
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun WordActions(status: WordStatus, onAddToReview: () -> Unit, onKnown: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (status) {
            WordStatus.NEW -> {
                PrimaryButton(stringResource(R.string.feature_reading_add_to_review), onAddToReview)
                SecondaryButton(stringResource(R.string.feature_reading_already_known), onKnown)
            }
            WordStatus.LEARNING -> {
                StatusLine(stringResource(R.string.feature_reading_in_reviews))
                SecondaryButton(stringResource(R.string.feature_reading_known_now), onKnown)
            }
            WordStatus.KNOWN -> {
                StatusLine(stringResource(R.string.feature_reading_you_know_it))
                SecondaryButton(stringResource(R.string.feature_reading_review_anyway), onAddToReview)
            }
        }
    }
}

@Composable
private fun StatusLine(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
}

@Composable
private fun SecondaryButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}
