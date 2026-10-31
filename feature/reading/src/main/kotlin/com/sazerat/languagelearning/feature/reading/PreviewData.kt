package com.sazerat.languagelearning.feature.reading

import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.Gender
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.Sentence
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.TextKind
import com.sazerat.languagelearning.core.domain.model.Token
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.feature.reading.library.LibrarySection
import com.sazerat.languagelearning.feature.reading.library.LibraryUiState
import com.sazerat.languagelearning.feature.reading.library.TextItem
import com.sazerat.languagelearning.feature.reading.reader.ReaderUiState
import com.sazerat.languagelearning.speech.api.SpeechRate

// Data for @Preview and tests only.

internal val romanian = LanguageCode("ro-RO")

internal val previewLemmas = listOf(
    Lemma(LemmaId("ro:eu"), romanian, "eu", PartOfSpeech.PRONOUN, meaning = LocalizedText.of(en = "I"), frequencyRank = 1),
    Lemma(
        LemmaId("ro:merge"),
        romanian,
        "a merge",
        PartOfSpeech.VERB,
        meaning = LocalizedText.of(en = "to go"),
        frequencyRank = 2,
        ipa = "ˈmer.dʒe",
    ),
    Lemma(LemmaId("ro:la"), romanian, "la", PartOfSpeech.PREPOSITION, meaning = LocalizedText.of(en = "to, at"), frequencyRank = 3),
    Lemma(
        LemmaId("ro:piață"),
        romanian,
        "piață",
        PartOfSpeech.NOUN,
        Gender.FEMININE,
        meaning = LocalizedText.of(en = "market"),
        frequencyRank = 4,
    ),
    Lemma(LemmaId("ro:mult"), romanian, "mult", PartOfSpeech.ADVERB, meaning = LocalizedText.of(en = "a lot"), frequencyRank = 5),
)

private fun word(text: String, lemma: String, note: String? = null) =
    Token(text, LemmaId("ro:$lemma"), note?.let { LocalizedText.of(en = it) })

internal val previewText = ReadingText(
    id = TextId("preview-market"),
    languageCode = romanian,
    level = CefrLevel.A1,
    kind = TextKind.STORY,
    title = "La piață",
    minutes = 2,
    sentences = listOf(
        Sentence(
            tokens = listOf(word("Eu", "eu"), Token(" "), word("merg", "merge", "present, 1st person"), Token(" "), word("la", "la"), Token(" "), word("piață", "piață"), Token(".")),
            translation = LocalizedText.of(en = "I go to the market."),
        ),
        Sentence(
            tokens = listOf(word("Merg", "merge", "present, 1st person"), Token(" "), word("mult", "mult"), Token(".")),
            translation = LocalizedText.of(en = "I walk a lot."),
        ),
    ),
)

internal val previewReaderState = ReaderUiState.Success(
    text = previewText,
    statuses = mapOf(LemmaId("ro:eu") to WordStatus.KNOWN, LemmaId("ro:piață") to WordStatus.LEARNING),
    translatedSentences = setOf(0),
    currentSentence = 1,
    isPlaying = true,
    rate = SpeechRate.NORMAL,
)

internal val previewLibraryState = LibraryUiState.Success(
    languageCode = romanian,
    sections = listOf(LibrarySection.TEXTS, LibrarySection.LESSONS),
    section = LibrarySection.TEXTS,
    levels = listOf(CefrLevel.A1, CefrLevel.A2),
    level = null,
    hideRead = false,
    hasReadTexts = true,
    texts = listOf(
        TextItem(TextId("a"), "La piață", CefrLevel.A1, TextKind.STORY, 3, comprehension = 94, isRead = true),
        TextItem(TextId("b"), "La gară", CefrLevel.A1, TextKind.DIALOGUE, 3, comprehension = 82, isRead = false),
        TextItem(TextId("c"), "Vremea", CefrLevel.A2, TextKind.NEWS, 4, comprehension = 61, isRead = false),
    ),
    suggestedTextId = TextId("b"),
    lessons = emptyList(),
    exercises = emptyList(),
)
