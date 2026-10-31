package com.sazerat.languagelearning.core.domain.review

import com.sazerat.languagelearning.core.domain.model.CardContext
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import com.sazerat.languagelearning.core.domain.model.MemoryState
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech
import com.sazerat.languagelearning.core.domain.model.Rating
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import com.sazerat.languagelearning.core.domain.model.TextId
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MultipleChoiceTest {
    private val romanian = LanguageCode("ro-RO")

    private fun lemma(text: String, partOfSpeech: PartOfSpeech = PartOfSpeech.NOUN, meaning: String = text, language: LanguageCode = romanian) =
        Lemma(LemmaId("${language.language}:$text"), language, text, partOfSpeech, meaning = LocalizedText.of(en = meaning), frequencyRank = 1)

    private val answer = lemma("casă", meaning = "house")

    private fun card(memory: MemoryState?) =
        ReviewCard(answer.id, romanian, CardContext(TextId("t"), 0, 0), memory = memory, due = 0, createdAt = 0)

    @Test
    fun `new and fragile cards ask for the meaning, solid ones for the word`() {
        assertEquals(ExerciseKind.MEANING, MultipleChoice.kindFor(card(null)))
        assertEquals(ExerciseKind.MEANING, MultipleChoice.kindFor(card(MemoryState(stability = 3.7, difficulty = 5.0))))
        assertEquals(ExerciseKind.FILL_BLANK, MultipleChoice.kindFor(card(MemoryState(stability = 13.8, difficulty = 5.0))))
    }

    @Test
    fun `four choices hold the answer once, in a random place`() {
        val candidates = listOf(answer, lemma("gară"), lemma("piață"), lemma("școală"), lemma("masă"))

        val positions = (0 until 50).map { seed ->
            val choices = assertNotNull(MultipleChoice.choices(answer, candidates, emptySet(), Random(seed)))
            assertEquals(MultipleChoice.CHOICE_COUNT, choices.size)
            assertEquals(MultipleChoice.CHOICE_COUNT, choices.toSet().size)
            assertEquals(1, choices.count { it == answer.id })
            choices.indexOf(answer.id)
        }

        assertEquals(setOf(0, 1, 2, 3), positions.toSet())
    }

    @Test
    fun `wrong answers favour the same part of speech, then words already met`() {
        val metNoun = lemma("gară")
        val nouns = listOf(metNoun, lemma("piață"), lemma("școală"))
        val verbs = listOf(lemma("merge", PartOfSpeech.VERB), lemma("vedea", PartOfSpeech.VERB))
        val unmetNoun = lemma("masă")

        repeat(20) { seed ->
            val choices = assertNotNull(MultipleChoice.choices(answer, verbs + nouns + unmetNoun + answer, setOf(metNoun.id), Random(seed)))
            assertTrue(metNoun.id in choices)
            assertTrue(verbs.none { it.id in choices })
        }
    }

    @Test
    fun `no wrong answer shares the base form or the meaning of the answer, nor the language changes`() {
        val sameMeaning = lemma("locuință", meaning = "house")
        val sameText = lemma("Casă", PartOfSpeech.VERB)
        val english = lemma("market", language = LanguageCode("en-GB"))
        val others = listOf(lemma("gară"), lemma("piață"), lemma("școală"))

        val choices = assertNotNull(MultipleChoice.choices(answer, listOf(sameMeaning, sameText, english) + others, emptySet(), Random(1)))

        assertEquals((others + answer).map { it.id }.toSet(), choices.toSet())
    }

    @Test
    fun `too small a lexicon gives no choices`() {
        assertNull(MultipleChoice.choices(answer, listOf(answer, lemma("gară"), lemma("piață")), emptySet(), Random(1)))
    }

    @Test
    fun `the rating follows from the answer and the time taken`() {
        assertEquals(Rating.AGAIN, MultipleChoice.rating(correct = false, elapsedMillis = 1_000))
        assertEquals(Rating.EASY, MultipleChoice.rating(correct = true, elapsedMillis = 3_000))
        assertEquals(Rating.GOOD, MultipleChoice.rating(correct = true, elapsedMillis = 8_000))
        assertEquals(Rating.HARD, MultipleChoice.rating(correct = true, elapsedMillis = 20_000))
    }
}
