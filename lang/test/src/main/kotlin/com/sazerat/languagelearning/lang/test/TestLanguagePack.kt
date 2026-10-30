package com.sazerat.languagelearning.lang.test

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.lang.api.LanguagePack
import com.sazerat.languagelearning.lang.api.Phoneme
import com.sazerat.languagelearning.lang.api.TextNormalizer

/** Minimal fictitious language used to prove features work for any pack (spec §11.4). */
object TestLanguagePack : LanguagePack {
    override val code = LanguageCode("xx-TEST")
    override val displayName = "Langue de test"
    override val variants = listOf(code)
    override val phonemeInventory = listOf(Phoneme("a"))
    override val textNormalizer = TextNormalizer { it.lowercase() }
    override val specialKeys = listOf("ø")
}
