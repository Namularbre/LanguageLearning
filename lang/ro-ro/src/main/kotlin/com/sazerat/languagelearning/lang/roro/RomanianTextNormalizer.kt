package com.sazerat.languagelearning.lang.roro

import com.sazerat.languagelearning.lang.api.TextNormalizer

/**
 * Romanian ș and ț take a comma below (U+0219, U+021B). Many keyboards and old texts use the
 * cedilla forms ş and ţ (U+015F, U+0163) instead; they look alike but compare as different strings.
 */
object RomanianTextNormalizer : TextNormalizer {
    private val replacements = mapOf(
        'ş' to 'ș', // ş -> ș
        'Ş' to 'Ș', // Ş -> Ș
        'ţ' to 'ț', // ţ -> ț
        'Ţ' to 'Ț', // Ţ -> Ț
    )

    override fun normalize(text: String): String =
        buildString(text.length) {
            for (char in text) append(replacements[char] ?: char)
        }
}
