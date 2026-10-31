package com.sazerat.languagelearning.core.data.sample

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
import com.sazerat.languagelearning.core.domain.reading.Tokenizer
import java.util.Locale

/**
 * The words of one language and the forms they take, used to turn plain sample sentences into
 * tokens linked to their base form (spec §5.1). This is what the content API will deliver from M2:
 * the app itself never guesses a base form.
 */
internal class Lexicon(private val languageCode: LanguageCode) {
    private val lemmas = mutableListOf<Lemma>()

    // Written form (lower case) → its lemma and, for an inflected form, a grammar note.
    private val forms = mutableMapOf<String, Pair<LemmaId, LocalizedText?>>()

    // Proper names: shown as written, cannot be tapped and do not count in comprehension.
    private val names = mutableSetOf<String>()

    val all: List<Lemma> get() = lemmas.sortedBy { it.frequencyRank }

    /**
     * Declares a lemma. Its base form is a form of it too, unless it has several words
     * ("a cumpăra"). [forms] maps each other written form to a grammar note, or `null` when none
     * is worth showing.
     */
    fun word(
        text: String,
        partOfSpeech: PartOfSpeech,
        rank: Int,
        meaning: LocalizedText,
        gender: Gender? = null,
        other: LocalizedText? = null,
        ipa: String? = null,
        note: LocalizedText? = null,
        forms: Map<String, LocalizedText?> = emptyMap(),
    ) {
        val id = LemmaId("${languageCode.language}:$text")
        lemmas += Lemma(id, languageCode, text, partOfSpeech, gender, meaning, other, rank, ipa, note)
        if (' ' !in text) addForm(text, id, null)
        forms.forEach { (form, formNote) -> addForm(form, id, formNote) }
    }

    fun names(vararg names: String) {
        this.names += names
    }

    private fun addForm(form: String, id: LemmaId, note: LocalizedText?) {
        val key = form.lowercase(Locale.ROOT)
        // One form, one lemma: sample sentences avoid ambiguous forms instead of marking them.
        check(key !in forms || forms[key]?.first == id) { "'$form' is already a form of ${forms[key]?.first?.value}" }
        forms[key] = id to note
    }

    /** A text whose sentences are cut into words, each linked to its lemma. Unknown words fail. */
    fun readingText(
        id: String,
        level: CefrLevel,
        kind: TextKind,
        title: String,
        minutes: Int,
        vararg sentences: Pair<String, LocalizedText>,
    ): ReadingText = ReadingText(
        id = TextId(id),
        languageCode = languageCode,
        level = level,
        kind = kind,
        title = title,
        minutes = minutes,
        sentences = sentences.map { (sentence, translation) -> Sentence(tokens(sentence, id), translation) },
    )

    private fun tokens(sentence: String, textId: String): List<Token> = Tokenizer.split(sentence).map { piece ->
        when {
            !piece.isWord || piece.text in names -> Token(piece.text)
            else -> {
                val (lemmaId, note) = checkNotNull(forms[piece.text.lowercase(Locale.ROOT)]) {
                    "Unknown word '${piece.text}' in $textId"
                }
                Token(piece.text, lemmaId, note)
            }
        }
    }
}

/** A sentence and its translation into the UI languages other than the text's language. */
internal infix fun String.means(translation: LocalizedText): Pair<String, LocalizedText> = this to translation

/** A meaning in the UI languages other than the word's own language: `null` ones are left out. */
internal fun meaning(en: String? = null, fr: String? = null, ro: String? = null): LocalizedText =
    LocalizedText.of(en = en, fr = fr, ro = ro)
