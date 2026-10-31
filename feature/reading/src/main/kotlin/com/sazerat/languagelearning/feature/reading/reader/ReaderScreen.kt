package com.sazerat.languagelearning.feature.reading.reader

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.Sentence
import com.sazerat.languagelearning.core.domain.model.TextKind
import com.sazerat.languagelearning.core.domain.model.Token
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.ui.component.AppScaffold
import com.sazerat.languagelearning.core.ui.component.GlassCard
import com.sazerat.languagelearning.core.ui.component.LoadingContent
import com.sazerat.languagelearning.core.ui.component.MessageContent
import com.sazerat.languagelearning.core.ui.component.PrimaryButton
import com.sazerat.languagelearning.core.ui.component.SpeechErrorSnackbar
import com.sazerat.languagelearning.core.ui.component.textMeta
import com.sazerat.languagelearning.core.ui.icon.AppIcons
import com.sazerat.languagelearning.core.ui.localized
import com.sazerat.languagelearning.core.ui.theme.AppColors
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme
import com.sazerat.languagelearning.core.ui.theme.ReadingTextStyle
import com.sazerat.languagelearning.core.ui.theme.appColors
import com.sazerat.languagelearning.feature.reading.R
import com.sazerat.languagelearning.feature.reading.previewReaderState
import com.sazerat.languagelearning.feature.reading.previewText
import com.sazerat.languagelearning.speech.api.SpeechRate

@Composable
internal fun ReaderScreen(onBack: () -> Unit, viewModel: ReaderViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ReaderContent(
        uiState = uiState,
        actions = ReaderActions(
            onPlayPause = viewModel::onPlayPauseClick,
            onRepeatSentence = viewModel::onRepeatSentenceClick,
            onRateSelected = viewModel::onRateSelected,
            onSentenceLongPress = viewModel::onSentenceLongPress,
            onWordClick = viewModel::onWordClick,
            onWordSheetDismiss = viewModel::onWordSheetDismiss,
            onPlayWord = viewModel::onPlayWordClick,
            onAddToReview = viewModel::onAddToReviewClick,
            onKnown = viewModel::onKnownClick,
            onFinish = viewModel::onFinishClick,
            onSpeechErrorShown = viewModel::onSpeechErrorShown,
        ),
        onBack = onBack,
    )
}

/** Everything the learner can do on the Reader, grouped to keep the composables' signatures short. */
internal data class ReaderActions(
    val onPlayPause: () -> Unit,
    val onRepeatSentence: () -> Unit,
    val onRateSelected: (SpeechRate) -> Unit,
    val onSentenceLongPress: (Int) -> Unit,
    val onWordClick: (WordRef) -> Unit,
    val onWordSheetDismiss: () -> Unit,
    val onPlayWord: () -> Unit,
    val onAddToReview: () -> Unit,
    val onKnown: () -> Unit,
    val onFinish: () -> Unit,
    val onSpeechErrorShown: () -> Unit,
)

@Composable
internal fun ReaderContent(uiState: ReaderUiState, actions: ReaderActions, onBack: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val success = uiState as? ReaderUiState.Success
    AppScaffold(
        title = success?.text?.title.orEmpty(),
        onBack = onBack,
        snackbarHostState = snackbarHostState,
        bottomBar = {
            if (success != null && success.summary == null) PlayerBar(success, actions)
        },
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (uiState) {
            ReaderUiState.Loading -> LoadingContent(modifier)
            ReaderUiState.NotFound -> MessageContent(stringResource(R.string.feature_reading_not_found), modifier)
            is ReaderUiState.Success ->
                if (uiState.summary != null) {
                    SummaryContent(uiState.summary, onBack, modifier)
                } else {
                    TextContent(uiState, actions, padding)
                }
        }
    }
    if (success != null) {
        SpeechErrorSnackbar(success.speechError, snackbarHostState, actions.onSpeechErrorShown)
        success.wordSheet?.let { sheet ->
            WordSheet(
                sheet = sheet,
                isPlaying = success.isPlaying,
                onPlay = actions.onPlayWord,
                onAddToReview = actions.onAddToReview,
                onKnown = actions.onKnown,
                onDismiss = actions.onWordSheetDismiss,
            )
        }
    }
}

@Composable
private fun TextContent(uiState: ReaderUiState.Success, actions: ReaderActions, padding: PaddingValues) {
    val text = uiState.text
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "header") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    textMeta(text.kind, text.level, text.minutes),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    stringResource(R.string.feature_reading_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        itemsIndexed(text.sentences) { index, sentence ->
            SentenceBlock(
                sentence = sentence,
                // A dialogue reads like a text message thread: each line is a turn, alternating
                // between the two speakers.
                bubbleSide = if (text.kind == TextKind.DIALOGUE) BubbleSide.of(index) else null,
                statuses = uiState.statuses,
                isCurrent = uiState.isPlaying && index == uiState.currentSentence,
                showTranslation = index in uiState.translatedSentences,
                onWordClick = { tokenIndex -> actions.onWordClick(WordRef(index, tokenIndex)) },
                onLongPress = { actions.onSentenceLongPress(index) },
            )
        }
        item(key = "finish") {
            PrimaryButton(
                text = stringResource(R.string.feature_reading_finish),
                onClick = actions.onFinish,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

/**
 * One sentence: words are styled by status (spec §5.2). A tap on a word opens its sheet, a long
 * press anywhere on the sentence shows its translation. Both go through one gesture detector:
 * with clickable links, a long press on a word would open the word instead.
 */
@Composable
private fun SentenceBlock(
    sentence: Sentence,
    bubbleSide: BubbleSide?,
    statuses: Map<LemmaId, WordStatus>,
    isCurrent: Boolean,
    showTranslation: Boolean,
    onWordClick: (tokenIndex: Int) -> Unit,
    onLongPress: () -> Unit,
) {
    val colors = MaterialTheme.appColors
    val translateLabel = stringResource(R.string.feature_reading_translate_action)
    val lookUpLabels = sentence.tokens.map { stringResource(R.string.feature_reading_look_up, it.text) }
    // In a bubble the dash that opens each line of a dialogue is not shown: the bubble marks the turn.
    val firstToken = if (bubbleSide != null) firstSpokenToken(sentence) else 0
    val tokenAt = remember(sentence, firstToken) { tokenIndexByChar(sentence, firstToken) }
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val currentOnWordClick by rememberUpdatedState(onWordClick)
    val currentOnLongPress by rememberUpdatedState(onLongPress)
    val frame = if (bubbleSide == null) {
        val background = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent
        Modifier
            .fillMaxWidth()
            .background(background, MaterialTheme.shapes.medium)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    } else {
        val shape = bubbleSide.shape()
        val border = if (isCurrent) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, colors.glassBorder)
        }
        Modifier
            .clip(shape)
            .background(bubbleSide.color(), shape)
            .border(border, shape)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (bubbleSide == BubbleSide.END) Arrangement.End else Arrangement.Start,
    ) {
        if (bubbleSide == BubbleSide.END) Spacer(Modifier.width(BUBBLE_GUTTER))
        Column(
            // A bubble is as wide as its text, up to the width left by a gutter on the other side.
            modifier = Modifier
                .weight(1f, fill = bubbleSide == null)
                .then(frame)
                .pointerInput(sentence, firstToken) {
                    detectTapGestures(
                        onTap = { position ->
                            layout?.wordAt(position, tokenAt, sentence)?.let(currentOnWordClick)
                        },
                        onLongPress = { currentOnLongPress() },
                    )
                }
                // Screen readers get the same actions: translate, and look up each word.
                .semantics {
                    onLongClick(label = translateLabel) {
                        currentOnLongPress()
                        true
                    }
                    customActions = sentence.tokens.indices.filter { sentence.tokens[it].isWord }.map { index ->
                        CustomAccessibilityAction(lookUpLabels[index]) {
                            currentOnWordClick(index)
                            true
                        }
                    }
                },
        ) {
            Text(
                text = sentenceText(sentence, statuses, colors, firstToken),
                style = ReadingTextStyle,
                color = MaterialTheme.colorScheme.onSurface,
                onTextLayout = { layout = it },
            )
            if (showTranslation) {
                Text(
                    sentence.translation.localized().let { if (bubbleSide != null) it.withoutDialogueDash() else it },
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (bubbleSide == BubbleSide.START) Spacer(Modifier.width(BUBBLE_GUTTER))
    }
}

/** Which speaker of a dialogue says a line: the first speaker on the left, the second on the right. */
private enum class BubbleSide {
    START,
    END,
    ;

    companion object {
        fun of(lineIndex: Int): BubbleSide = if (lineIndex % 2 == 0) START else END
    }
}

/** Rounded bubble with a sharp corner at the bottom, on the speaker's side. */
private fun BubbleSide.shape(): Shape = when (this) {
    BubbleSide.START -> RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 4.dp)
    BubbleSide.END -> RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 4.dp, bottomStart = 20.dp)
}

@Composable
private fun BubbleSide.color(): Color = when (this) {
    BubbleSide.START -> MaterialTheme.appColors.glass
    BubbleSide.END -> MaterialTheme.colorScheme.secondaryContainer
}

private val BUBBLE_GUTTER = 48.dp

/** Index of the first token after the dash that opens a line of dialogue, or 0 when there is none. */
private fun firstSpokenToken(sentence: Sentence): Int {
    val first = sentence.tokens.firstOrNull() ?: return 0
    return if (!first.isWord && first.text.trim() in DIALOGUE_DASHES) 1 else 0
}

private val DIALOGUE_DASHES = setOf("—", "–", "-")

private fun String.withoutDialogueDash(): String {
    val trimmed = trimStart()
    return if (trimmed.take(1) in DIALOGUE_DASHES) trimmed.drop(1).trimStart() else this
}

/** For each character shown, from token [firstToken] on, the index of the token it belongs to. */
private fun tokenIndexByChar(sentence: Sentence, firstToken: Int): IntArray =
    sentence.tokens.withIndex().drop(firstToken)
        .flatMap { (index, token) -> List(token.text.length) { index } }
        .toIntArray()

/** The word under [position], or `null` for punctuation, a name or the blank after a line. */
private fun TextLayoutResult.wordAt(position: Offset, tokenAt: IntArray, sentence: Sentence): Int? {
    val line = getLineForVerticalPosition(position.y)
    if (position.x < getLineLeft(line) || position.x > getLineRight(line)) return null
    val token = tokenAt.getOrNull(getOffsetForPosition(position)) ?: return null
    return token.takeIf { sentence.tokens[it].isWord }
}

private fun sentenceText(
    sentence: Sentence,
    statuses: Map<LemmaId, WordStatus>,
    colors: AppColors,
    firstToken: Int,
): AnnotatedString =
    buildAnnotatedString {
        sentence.tokens.drop(firstToken).forEach { token ->
            val lemmaId = token.lemmaId
            val style = when (lemmaId?.let { statuses[it] ?: WordStatus.NEW }) {
                WordStatus.NEW -> SpanStyle(color = colors.newWord, textDecoration = TextDecoration.Underline)
                WordStatus.LEARNING -> SpanStyle(color = colors.onLearningWord, background = colors.learningWord)
                WordStatus.KNOWN, null -> null
            }
            if (style == null) append(token.text) else withStyle(style) { append(token.text) }
        }
    }

@Composable
private fun PlayerBar(uiState: ReaderUiState.Success, actions: ReaderActions) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = actions.onRepeatSentence) {
                Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.feature_reading_repeat))
            }
            FilledIconButton(onClick = actions.onPlayPause, modifier = Modifier.size(56.dp)) {
                if (uiState.isPlaying) {
                    Icon(AppIcons.Pause, contentDescription = stringResource(R.string.feature_reading_pause))
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.feature_reading_play))
                }
            }
            val nextRate = SPEEDS[(SPEEDS.indexOf(uiState.rate) + 1) % SPEEDS.size]
            val speedLabel = uiState.rate.label()
            TextButton(onClick = { actions.onRateSelected(nextRate) }) {
                Text(stringResource(R.string.feature_reading_speed, speedLabel))
            }
        }
    }
}

/** Speeds offered, from slowest to fastest: the speed button cycles through them. */
private val SPEEDS = listOf(SpeechRate.SLOW, SpeechRate.NORMAL, SpeechRate.FAST)

@Composable
private fun SpeechRate.label(): String = stringResource(
    when (this) {
        SpeechRate.SLOW -> R.string.feature_reading_speed_slow
        SpeechRate.NORMAL -> R.string.feature_reading_speed_normal
        SpeechRate.FAST -> R.string.feature_reading_speed_fast
    },
)

@Composable
private fun SummaryContent(summary: ReadingSummary, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.feature_reading_summary_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            pluralStringResource(R.plurals.feature_reading_summary_known, summary.newlyKnown, summary.newlyKnown),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            pluralStringResource(R.plurals.feature_reading_summary_learning, summary.learning, summary.learning),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        PrimaryButton(text = stringResource(R.string.feature_reading_back_to_library), onClick = onBack)
    }
}

@PreviewLightDark
@Composable
private fun ReaderPreview() {
    LanguageLearningTheme {
        ReaderContent(uiState = previewReaderState, actions = previewActions, onBack = {})
    }
}

@PreviewLightDark
@Composable
private fun ReaderSummaryPreview() {
    LanguageLearningTheme {
        ReaderContent(
            uiState = previewReaderState.copy(summary = ReadingSummary(newlyKnown = 12, learning = 3)),
            actions = previewActions,
            onBack = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun ReaderDialoguePreview() {
    val dialogue = previewText.copy(
        kind = TextKind.DIALOGUE,
        sentences = previewText.sentences.map { it.copy(tokens = listOf(Token("— ")) + it.tokens) },
    )
    LanguageLearningTheme {
        ReaderContent(uiState = previewReaderState.copy(text = dialogue), actions = previewActions, onBack = {})
    }
}

private val previewActions = ReaderActions({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})
