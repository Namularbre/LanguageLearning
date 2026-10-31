package com.sazerat.languagelearning.core.domain.model

@JvmInline
public value class TextId(public val value: String)

/** Common European Framework levels offered in the Library (spec §4). */
public enum class CefrLevel { A1, A2, B1, B2, C1 }

public enum class TextKind { STORY, DIALOGUE, NEWS }

/**
 * A short text to read and listen to (spec §5.1). It is written in [languageCode] and arrives
 * already split into words, each linked to its base form: the phone never has to lemmatise.
 */
public data class ReadingText(
    val id: TextId,
    val languageCode: LanguageCode,
    val level: CefrLevel,
    val kind: TextKind,
    /** Title in the text's own language. */
    val title: String,
    /** Estimated reading time, also counted towards the daily goal once the text is finished. */
    val minutes: Int,
    val sentences: List<Sentence>,
) {
    /** Lemma of every word occurrence, in reading order. */
    public val wordLemmas: List<LemmaId> get() = sentences.flatMap { sentence -> sentence.tokens.mapNotNull { it.lemmaId } }
}

public data class Sentence(
    val tokens: List<Token>,
    /** Meaning of the whole sentence in the UI languages other than the text's language. */
    val translation: LocalizedText,
) {
    /** The sentence as written, e.g. to be spoken aloud. */
    public val text: String get() = tokens.joinToString(separator = "") { it.text }
}

/**
 * A piece of a sentence as written. A word has a [lemmaId]; spaces, punctuation and names do not,
 * and cannot be tapped.
 */
public data class Token(
    val text: String,
    val lemmaId: LemmaId? = null,
    /** Grammar of this form when it differs from the base form, e.g. "genitive, definite". */
    val formNote: LocalizedText? = null,
) {
    public val isWord: Boolean get() = lemmaId != null
}
