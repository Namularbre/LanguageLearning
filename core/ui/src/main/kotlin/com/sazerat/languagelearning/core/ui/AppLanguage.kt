package com.sazerat.languagelearning.core.ui

import android.app.LocaleManager
import android.content.Context
import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * The app's own UI language, through Android's per-app language API (spec §4: the UI language is
 * the native language and can be changed). It is the same setting as in the system settings, so
 * the two always agree. Android recreates the activity when it changes.
 */
class AppLanguage(context: Context) {
    private val localeManager = context.getSystemService(LocaleManager::class.java)

    /** Language tag of the chosen language, e.g. "fr", or `null` to follow the phone. */
    var tag: String?
        get() = localeManager.applicationLocales.takeUnless { it.isEmpty }?.get(0)?.language
        set(value) {
            localeManager.applicationLocales = if (value == null) LocaleList.getEmptyLocaleList() else LocaleList.forLanguageTags(value)
        }
}

@Composable
fun rememberAppLanguage(): AppLanguage {
    val context = LocalContext.current
    return remember(context) { AppLanguage(context) }
}

/** Name of a UI language in that language, e.g. "Français": what its speakers look for. */
fun autonym(tag: String): String {
    val locale = Locale.forLanguageTag(tag)
    return locale.getDisplayLanguage(locale).replaceFirstChar { it.titlecase(locale) }
}
