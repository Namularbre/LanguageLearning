package com.sazerat.languagelearning.core.domain.reading

import kotlin.test.Test
import kotlin.test.assertEquals

class TokenizerTest {
    private fun words(sentence: String) = Tokenizer.split(sentence).filter { it.isWord }.map { it.text }

    @Test
    fun `joining the pieces gives the sentence back`() {
        val sentence = "„Bună dimineața, Ana! Ce mai faci?”"

        assertEquals(sentence, Tokenizer.split(sentence).joinToString("") { it.text })
    }

    @Test
    fun `Romanian diacritics stay inside words`() {
        assertEquals(listOf("Ea", "cumpără", "roșii", "și", "brânză"), words("Ea cumpără roșii și brânză."))
    }

    @Test
    fun `a French elision gives two words`() {
        assertEquals(listOf("l'", "homme", "qu'", "il", "aime"), words("l'homme qu'il aime"))
    }

    @Test
    fun `a hyphen separates words`() {
        assertEquals(listOf("într", "o", "zi"), words("într-o zi"))
    }

    @Test
    fun `a sentence without letters has no word`() {
        assertEquals(listOf(Tokenizer.Piece("…", isWord = false)), Tokenizer.split("…"))
    }
}
