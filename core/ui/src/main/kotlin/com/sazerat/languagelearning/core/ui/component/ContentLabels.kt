package com.sazerat.languagelearning.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.Gender
import com.sazerat.languagelearning.core.domain.model.MILLIS_PER_DAY
import com.sazerat.languagelearning.core.domain.model.MILLIS_PER_MINUTE
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech
import com.sazerat.languagelearning.core.domain.model.TextKind
import com.sazerat.languagelearning.core.ui.R

// Labels and small badges for content, shared by the Library, the Reader, Today and the reviews.

@Composable
fun TextKind.label(): String = stringResource(
    when (this) {
        TextKind.STORY -> R.string.core_ui_kind_story
        TextKind.DIALOGUE -> R.string.core_ui_kind_dialogue
        TextKind.NEWS -> R.string.core_ui_kind_news
    },
)

@Composable
fun PartOfSpeech.label(): String = stringResource(
    when (this) {
        PartOfSpeech.NOUN -> R.string.core_ui_pos_noun
        PartOfSpeech.VERB -> R.string.core_ui_pos_verb
        PartOfSpeech.ADJECTIVE -> R.string.core_ui_pos_adjective
        PartOfSpeech.ADVERB -> R.string.core_ui_pos_adverb
        PartOfSpeech.PRONOUN -> R.string.core_ui_pos_pronoun
        PartOfSpeech.PREPOSITION -> R.string.core_ui_pos_preposition
        PartOfSpeech.CONJUNCTION -> R.string.core_ui_pos_conjunction
        PartOfSpeech.ARTICLE -> R.string.core_ui_pos_article
        PartOfSpeech.NUMERAL -> R.string.core_ui_pos_numeral
        PartOfSpeech.INTERJECTION -> R.string.core_ui_pos_interjection
    },
)

@Composable
fun Gender.label(): String = stringResource(
    when (this) {
        Gender.MASCULINE -> R.string.core_ui_gender_masculine
        Gender.FEMININE -> R.string.core_ui_gender_feminine
        Gender.NEUTER -> R.string.core_ui_gender_neuter
    },
)

/** "Story · A1 · 3 min". */
@Composable
fun textMeta(kind: TextKind, level: CefrLevel, minutes: Int): String =
    stringResource(R.string.core_ui_text_meta, kind.label(), level.name, minutes)

/** A short delay, e.g. "10 min", "4 d", "3 mo": review intervals and the next review. */
@Composable
fun durationLabel(millis: Long): String {
    // Rounded to the nearest unit: a due date a few seconds away from "4 d" must not read "3 d".
    val minutes = ((millis + MILLIS_PER_MINUTE / 2) / MILLIS_PER_MINUTE).coerceAtLeast(1)
    val hours = (minutes + 30) / 60
    val days = (millis + MILLIS_PER_DAY / 2) / MILLIS_PER_DAY
    return when {
        minutes < 60 -> stringResource(R.string.core_ui_duration_minutes, minutes.toInt())
        days < 1 -> stringResource(R.string.core_ui_duration_hours, hours.toInt())
        days < 60 -> stringResource(R.string.core_ui_duration_days, days.toInt())
        else -> stringResource(R.string.core_ui_duration_months, (days / 30).toInt())
    }
}

/**
 * Estimated comprehension of a text, coloured as in spec §4: green from 90 %, orange from 75 %,
 * grey below. The colour is never the only cue: the number is always written.
 */
@Composable
fun ComprehensionBadge(percent: Int, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val (background, content) = when {
        percent >= 90 -> colors.secondaryContainer to colors.onSecondaryContainer
        percent >= 75 -> colors.tertiaryContainer to colors.onTertiaryContainer
        else -> colors.surfaceVariant to colors.onSurfaceVariant
    }
    val description = stringResource(R.string.core_ui_comprehension_description, percent)
    Text(
        text = stringResource(R.string.core_ui_percent, percent),
        modifier = modifier
            .semantics { contentDescription = description }
            .background(background, MaterialTheme.shapes.small)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelLarge,
        color = content,
    )
}

/** Square tile with the CEFR level, at the start of a text row. */
@Composable
fun LevelTile(level: CefrLevel, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(52.dp).background(color, MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center,
    ) {
        Text(level.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
    }
}
