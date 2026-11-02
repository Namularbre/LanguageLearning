package com.sazerat.languagelearning.core.data

import com.sazerat.languagelearning.core.data.repository.PackLessonRepository
import com.sazerat.languagelearning.core.data.repository.PackLexiconRepository
import com.sazerat.languagelearning.core.data.repository.PackTextRepository
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.model.TextId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PackRepositoriesTest {
    private val packs = flowOf(TestPacks.all)
    private val lessons = PackLessonRepository(packs)
    private val texts = PackTextRepository(packs)
    private val lexicon = PackLexiconRepository(packs)
    private val languages = listOf("ro-RO", "en-GB", "fr-FR").map(::LanguageCode)

    @Test
    fun `lessons are filtered by language and sorted`() = runTest {
        languages.forEach { language ->
            assertEquals((1..6).toList(), lessons.observeLessons(language).first().map { it.order }, language.value)
        }
        assertTrue(lessons.observeLessons(LanguageCode("xx-TEST")).first().isEmpty())
    }

    @Test
    fun `unknown lesson emits null`() = runTest {
        assertNull(lessons.observeLesson(LessonId("missing")).first())
    }

    @Test
    fun `texts are filtered by language and found by id`() = runTest {
        languages.forEach { language ->
            val all = texts.observeTexts(language).first()
            assertTrue(all.isNotEmpty(), "No text for ${language.value}")
            assertTrue(all.all { it.languageCode == language })
            assertEquals(all.first(), texts.observeText(all.first().id).first())
        }
        assertNull(texts.observeText(TextId("missing")).first())
    }

    @Test
    fun `lemmas are filtered by language, most frequent first`() = runTest {
        languages.forEach { language ->
            val ranks = lexicon.observeLemmas(language).first().map { it.frequencyRank }
            assertTrue(ranks.isNotEmpty(), "No lemma for ${language.value}")
            assertEquals(ranks.sorted(), ranks)
        }
    }
}
