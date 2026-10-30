package com.sazerat.languagelearning.lang.api

import com.sazerat.languagelearning.core.domain.model.LanguageCode

/**
 * Code-level rules of one language (spec §11.2). Content (lessons, exercises) comes from the API;
 * only what needs code lives here, so adding a language never touches `:feature:*`.
 */
public interface LanguagePack {
    public val code: LanguageCode
    public val displayName: String
    public val variants: List<LanguageCode>
    public val phonemeInventory: List<Phoneme>
    public val textNormalizer: TextNormalizer
    /** Characters shown on the built-in keyboard, e.g. Romanian diacritics. */
    public val specialKeys: List<String>
}

/** A sound written in IPA, e.g. "θ" or "iː". */
@JvmInline
public value class Phoneme(public val ipa: String)

/** Brings user input and content to one canonical spelling before comparing them. */
public fun interface TextNormalizer {
    public fun normalize(text: String): String

    public companion object {
        public val Identity: TextNormalizer = TextNormalizer { it }
    }
}
