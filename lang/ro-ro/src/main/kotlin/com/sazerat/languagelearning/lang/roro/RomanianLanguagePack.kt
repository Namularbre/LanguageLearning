package com.sazerat.languagelearning.lang.roro

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.lang.api.LanguagePack
import com.sazerat.languagelearning.lang.api.Phoneme
import com.sazerat.languagelearning.lang.api.TextNormalizer

object RomanianLanguagePack : LanguagePack {
    override val code = LanguageCode("ro-RO")
    override val displayName = "Roumain"
    override val variants = listOf(code)

    // Sounds a French speaker has to learn: ă, â/î, ș, ț, and the ce/ci, ge/gi affricates.
    override val phonemeInventory = listOf("ə", "ɨ", "ʃ", "ts", "tʃ", "dʒ", "e̯a", "o̯a").map(::Phoneme)
    override val textNormalizer: TextNormalizer = RomanianTextNormalizer
    override val specialKeys = listOf("ă", "â", "î", "ș", "ț")
}
