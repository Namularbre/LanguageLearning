package com.sazerat.languagelearning.core.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocalizedTextTest {
    private val text = LocalizedText.of(en = "house", fr = "maison", ro = "casă")

    @Test
    fun `resolves the requested language`() {
        assertEquals("casă", text.resolve("ro"))
    }

    @Test
    fun `falls back to English for a missing language`() {
        assertEquals("house", text.resolve("de"))
    }

    @Test
    fun `falls back to any translation without English`() {
        assertEquals("maison", LocalizedText.of(fr = "maison").resolve("ro"))
    }

    @Test
    fun `an empty text is rejected`() {
        assertFailsWith<IllegalArgumentException> { LocalizedText.of() }
    }

    @Test
    fun `language code exposes its language part`() {
        assertEquals("ro", LanguageCode("ro-RO").language)
    }
}
