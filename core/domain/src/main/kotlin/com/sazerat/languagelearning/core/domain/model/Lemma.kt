package com.sazerat.languagelearning.core.domain.model

/** Identifier of a base form, unique across languages, e.g. "ro:casă". */
@JvmInline
public value class LemmaId(public val value: String)

public enum class PartOfSpeech { NOUN, VERB, ADJECTIVE, ADVERB, PRONOUN, PREPOSITION, CONJUNCTION, ARTICLE, NUMERAL, INTERJECTION }

public enum class Gender { MASCULINE, FEMININE, NEUTER }

/**
 * A word as found in a dictionary: *casă* for *casa*, *casei* and *casele*. Statuses and review
 * cards are tracked per lemma, so every form of a word is the same word for the learner (spec §5.2).
 */
public data class Lemma(
    val id: LemmaId,
    val languageCode: LanguageCode,
    /** The base form, e.g. "casă" or "a cumpăra". */
    val text: String,
    val partOfSpeech: PartOfSpeech,
    val gender: Gender? = null,
    /** Main meaning in the UI languages other than the lemma's own language. */
    val meaning: LocalizedText,
    /** One or two other meanings, if any. */
    val otherMeanings: LocalizedText? = null,
    /** 1 for the most frequent word of the language: orders the level test (spec §4). */
    val frequencyRank: Int,
    /** Shown only when the pronunciation help is on (spec §7). */
    val ipa: String? = null,
    val pronunciationNote: LocalizedText? = null,
)
