package com.sazerat.languagelearning.feature.reading.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.Sentence
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
 * One sentence: words are links styled by status (spec §5.2), a long press shows the translation.
 * The words' own taps are handled by the links, so the long press does not hide them.
 */
@Composable
private fun SentenceBlock(
    sentence: Sentence,
    statuses: Map<LemmaId, WordStatus>,
    isCurrent: Boolean,
    showTranslation: Boolean,
    onWordClick: (tokenIndex: Int) -> Unit,
    onLongPress: () -> Unit,
) {
    val colors = MaterialTheme.appColors
    val translateLabel = stringResource(R.string.feature_reading_translate_action)
    val background = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, MaterialTheme.shapes.medium)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .pointerInput(onLongPress) { detectTapGestures(onLongPress = { onLongPress() }) }
            .semantics {
                onLongClick(label = translateLabel) {
                    onLongPress()
                    true
                }
            },
    ) {
        Text(
            text = sentenceText(sentence, statuses, colors, onWordClick),
            style = ReadingTextStyle,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (showTranslation) {
            Text(
                sentence.translation.localized(),
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun sentenceText(
    sentence: Sentence,
    statuses: Map<LemmaId, WordStatus>,
    colors: AppColors,
    onWordClick: (tokenIndex: Int) -> Unit,
): AnnotatedString = buildAnnotatedString {
    sentence.tokens.forEachIndexed { index, token ->
        val lemmaId = token.lemmaId
        if (lemmaId == null) {
            append(token.text)
        } else {
            val style = when (statuses[lemmaId] ?: WordStatus.NEW) {
                WordStatus.NEW -> SpanStyle(color = colors.newWord, textDecoration = TextDecoration.Underline)
                WordStatus.LEARNING -> SpanStyle(color = colors.onLearningWord, background = colors.learningWord)
                WordStatus.KNOWN -> SpanStyle()
            }
            val link = LinkAnnotation.Clickable(
                tag = index.toString(),
                styles = TextLinkStyles(style = style),
                linkInteractionListener = { onWordClick(index) },
            )
            withLink(link) { append(token.text) }
        }
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

private val previewActions = ReaderActions({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})
