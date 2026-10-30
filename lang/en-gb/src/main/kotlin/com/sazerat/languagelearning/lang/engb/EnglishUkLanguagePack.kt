package com.sazerat.languagelearning.lang.engb

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.lang.api.LanguagePack
import com.sazerat.languagelearning.lang.api.Phoneme
import com.sazerat.languagelearning.lang.api.TextNormalizer

/** British English, Standard Southern British English (SSBE) target accent (spec §5.1). */
object EnglishUkLanguagePack : LanguagePack {
    override val code = LanguageCode("en-GB")
    override val displayName = "Anglais (britannique)"
    override val variants = listOf(code)

    // Priority targets for a French speaker.
    override val phonemeInventory = listOf(
        "θ", "ð", "h", "ŋ", "r", "ɫ",
        "ɪ", "iː", "ʊ", "uː", "æ", "ʌ", "ɑː", "ɒ", "ɔː", "ɜː", "ə",
        "əʊ", "eɪ", "aɪ", "aʊ", "ɪə", "eə",
    ).map(::Phoneme)
    override val textNormalizer = TextNormalizer.Identity
    override val specialKeys = emptyList<String>()
}
