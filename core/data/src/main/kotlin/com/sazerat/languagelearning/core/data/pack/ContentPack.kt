package com.sazerat.languagelearning.core.data.pack

import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.Gender
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.LessonExample
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.model.LessonSection
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.Sentence
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.TextKind
import com.sazerat.languagelearning.core.domain.model.Token
import kotlinx.serialization.Serializable

/**
 * A language pack (spec §9.2–9.3): all the reviewed content of one language, as published by the
 * content pipeline (a separate project) and, from M2, served by `GET /packs/{languageCode}`.
 *
 * These classes mirror the JSON file one to one; the `to…()` functions turn them into domain
 * models. A text in several UI languages (`LocalizedText`) is a JSON object such as
 * `{"en": "house", "fr": "maison"}`.
 */
@Serializable
data class ContentPack(
    /** Version of the file format. The app refuses a format it does not know. */
    val format: Int,
    /** BCP-47 tag of the language taught by the pack, e.g. "ro-RO". */
    val languageCode: String,
    /** Version of the content, raised at each publication: lets the app ask only for what changed. */
    val version: Int,
    val lemmas: List<PackLemma> = emptyList(),
    val texts: List<PackText> = emptyList(),
    val lessons: List<PackLesson> = emptyList(),
) {
    private val language: LanguageCode get() = LanguageCode(languageCode)

    fun toLemmas(): List<Lemma> = lemmas.map { it.toDomain(language) }

    fun toTexts(): List<ReadingText> = texts.map { it.toDomain(language) }

    fun toLessons(): List<Lesson> = lessons.map { it.toDomain(language) }

    companion object {
        /** The only format this version of the app reads. */
        const val FORMAT: Int = 1
    }
}

@Serializable
data class PackLemma(
    /** Unique across languages, e.g. "ro:casă". */
    val id: String,
    /** The base form, e.g. "casă" or "a cumpăra". */
    val text: String,
    val partOfSpeech: PartOfSpeech,
    val gender: Gender? = null,
    val meaning: Map<String, String>,
    val otherMeanings: Map<String, String>? = null,
    val frequencyRank: Int,
    val ipa: String? = null,
    val pronunciationNote: Map<String, String>? = null,
) {
    fun toDomain(language: LanguageCode): Lemma = Lemma(
        id = LemmaId(id),
        languageCode = language,
        text = text,
        partOfSpeech = partOfSpeech,
        gender = gender,
        meaning = LocalizedText(meaning),
        otherMeanings = otherMeanings?.let(::LocalizedText),
        frequencyRank = frequencyRank,
        ipa = ipa,
        pronunciationNote = pronunciationNote?.let(::LocalizedText),
    )
}

@Serializable
data class PackText(
    val id: String,
    val level: CefrLevel,
    val kind: TextKind,
    val title: String,
    val minutes: Int,
    val sentences: List<PackSentence>,
) {
    fun toDomain(language: LanguageCode): ReadingText = ReadingText(
        id = TextId(id),
        languageCode = language,
        level = level,
        kind = kind,
        title = title,
        minutes = minutes,
        sentences = sentences.map { it.toDomain() },
    )
}

@Serializable
data class PackSentence(
    /** The sentence cut into pieces: joining their `text` gives the sentence back. */
    val tokens: List<PackToken>,
    val translation: Map<String, String>,
) {
    fun toDomain(): Sentence = Sentence(tokens.map { it.toDomain() }, LocalizedText(translation))
}

/**
 * A piece of a sentence. A word has the id of its lemma; spaces, punctuation and names have none.
 * The pipeline sets the lemma (spec §5.1): the app never guesses it.
 */
@Serializable
data class PackToken(
    val text: String,
    val lemma: String? = null,
    val formNote: Map<String, String>? = null,
) {
    fun toDomain(): Token = Token(text, lemma?.let(::LemmaId), formNote?.let(::LocalizedText))
}

@Serializable
data class PackLesson(
    val id: String,
    val order: Int,
    val title: Map<String, String>,
    val summary: Map<String, String>,
    val sections: List<PackLessonSection>,
) {
    fun toDomain(language: LanguageCode): Lesson = Lesson(
        id = LessonId(id),
        languageCode = language,
        order = order,
        title = LocalizedText(title),
        summary = LocalizedText(summary),
        sections = sections.map { it.toDomain() },
    )
}

@Serializable
data class PackLessonSection(
    val heading: Map<String, String>,
    val body: Map<String, String>,
    val examples: List<PackExample> = emptyList(),
) {
    fun toDomain(): LessonSection = LessonSection(
        heading = LocalizedText(heading),
        body = LocalizedText(body),
        examples = examples.map { LessonExample(it.text, LocalizedText(it.translation)) },
    )
}

@Serializable
data class PackExample(
    val text: String,
    val translation: Map<String, String>,
)
