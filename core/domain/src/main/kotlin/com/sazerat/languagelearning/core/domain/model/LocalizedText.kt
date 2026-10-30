package com.sazerat.languagelearning.core.domain.model

/**
 * A piece of content written in several UI languages, keyed by ISO 639 language code
 * ("en", "fr", "ro"). The app shows the one matching the phone's language.
 *
 * A text with a single English entry is the same for everyone, e.g. "ship / sheep".
 */
public data class LocalizedText(val translations: Map<String, String>) {
    init {
        require(translations.isNotEmpty()) { "A localized text needs at least one translation" }
    }

    /** The text in [language], else in English, else in any available language. */
    public fun resolve(language: String): String =
        translations[language] ?: translations[FALLBACK_LANGUAGE] ?: translations.values.first()

    public companion object {
        public const val FALLBACK_LANGUAGE: String = "en"

        /** Builds a text from the translations that are given; `null` ones are left out. */
        public fun of(en: String? = null, fr: String? = null, ro: String? = null): LocalizedText =
            LocalizedText(listOfNotNull(en?.let { "en" to it }, fr?.let { "fr" to it }, ro?.let { "ro" to it }).toMap())
    }
}
