package com.sazerat.languagelearning.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import java.util.Locale

/** Languages the UI and the content are written in. Must match the `values-*` resource folders. */
private val uiLanguages = setOf("en", "fr", "ro")

/**
 * Language of the UI, e.g. "ro": the phone's language (or the one chosen for this app in the
 * system settings) if the app supports it, English otherwise. Same rule as Android's string
 * resources, so the content always matches the buttons around it.
 */
@Composable
@ReadOnlyComposable
fun uiLanguage(): String =
    LocalConfiguration.current.locales[0].language.takeIf { it in uiLanguages } ?: LocalizedText.FALLBACK_LANGUAGE

/** This text in the UI language. */
@Composable
@ReadOnlyComposable
fun LocalizedText.localized(): String = resolve(uiLanguage())

/**
 * Name of the language in the UI language, e.g. "Romanian", "Roumain" or "Română" for ro-RO.
 * Comes from the platform, so a new language needs no new string.
 */
@Composable
@ReadOnlyComposable
fun LanguageCode.displayName(): String {
    val locale = Locale.forLanguageTag(uiLanguage())
    return Locale.forLanguageTag(value).getDisplayLanguage(locale).replaceFirstChar { it.titlecase(locale) }
}
