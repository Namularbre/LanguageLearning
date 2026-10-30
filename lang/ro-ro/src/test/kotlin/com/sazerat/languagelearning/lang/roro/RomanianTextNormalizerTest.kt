package com.sazerat.languagelearning.lang.roro

import kotlin.test.Test
import kotlin.test.assertEquals

class RomanianTextNormalizerTest {
    @Test
    fun `cedilla forms are replaced by comma-below forms`() {
        assertEquals("școală, țară, Ștefan, Țepeș", RomanianTextNormalizer.normalize("şcoală, ţară, Ştefan, Ţepeş"))
    }

    @Test
    fun `text already correct is unchanged`() {
        val text = "În România, mâine e sărbătoare."
        assertEquals(text, RomanianTextNormalizer.normalize(text))
    }

    @Test
    fun `special keys are already in canonical form`() {
        RomanianLanguagePack.specialKeys.forEach { key ->
            assertEquals(key, RomanianTextNormalizer.normalize(key))
        }
    }
}
