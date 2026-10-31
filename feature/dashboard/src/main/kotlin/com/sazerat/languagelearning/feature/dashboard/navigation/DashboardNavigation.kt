package com.sazerat.languagelearning.feature.dashboard.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sazerat.languagelearning.feature.dashboard.ProgressScreen
import kotlinx.serialization.Serializable

/** The Progress tab. */
@Serializable
data object Progress

/** [languageSwitcher] is shown in the top bar: the app provides it, this feature does not know it. */
fun NavGraphBuilder.progressScreen(onOpenSettings: () -> Unit, languageSwitcher: @Composable () -> Unit) {
    composable<Progress> {
        ProgressScreen(onOpenSettings = onOpenSettings, languageSwitcher = languageSwitcher)
    }
}
