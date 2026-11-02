package com.sazerat.languagelearning.core.data

import com.sazerat.languagelearning.core.data.pack.ContentPackParser
import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LemmaId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ContentPackParserTest {
    private val pack = """
        {
          "format": 1, "languageCode": "ro-RO", "version": 3,
          "lemmas": [
            {"id": "ro:casă", "text": "casă", "partOfSpeech": "NOUN", "gender": "FEMININE",
             "meaning": {"en": "house", "fr": "maison"}, "frequencyRank": 12}
          ],
          "texts": [
            {"id": "ro-home", "level": "A1", "kind": "STORY", "title": "Acasă", "minutes": 1, "sentences": [
              {"tokens": [{"text": "Casa", "lemma": "ro:casă", "formNote": {"en": "definite article"}}, {"text": "."}],
               "translation": {"en": "The house.", "fr": "La maison."}}
            ]}
          ],
          "someFutureField": true
        }
    """.trimIndent()

    @Test
    fun `a pack becomes domain models of its language`() {
        val parsed = ContentPackParser.parse(pack)
        val text = parsed.toTexts().single()
        assertEquals(LanguageCode("ro-RO"), text.languageCode)
        assertEquals(CefrLevel.A1, text.level)
        assertEquals("Casa.", text.sentences.single().text)
        assertEquals(listOf(LemmaId("ro:casă")), text.wordLemmas)
        assertEquals("definite article", text.sentences.single().tokens.first().formNote?.resolve("fr"))
        assertEquals("maison", parsed.toLemmas().single().meaning.resolve("fr"))
        assertEquals(emptyList(), parsed.toLessons())
    }

    @Test
    fun `punctuation has no lemma`() {
        val token = ContentPackParser.parse(pack).toTexts().single().sentences.single().tokens.last()
        assertNull(token.lemmaId)
    }

    @Test
    fun `a pack in another format is refused`() {
        assertFailsWith<IllegalArgumentException> {
            ContentPackParser.parse(pack.replace("\"format\": 1", "\"format\": 2"))
        }
    }

    @Test
    fun `a malformed pack is refused`() {
        assertFailsWith<IllegalArgumentException> { ContentPackParser.parse("""{"format": 1}""") }
    }
}
