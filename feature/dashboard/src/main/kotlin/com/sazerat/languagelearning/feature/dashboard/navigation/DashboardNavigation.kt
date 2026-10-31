package com.sazerat.languagelearning.feature.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sazerat.languagelearning.feature.dashboard.ProgressScreen
import kotlinx.serialization.Serializable

/** The Progress tab. */
@Serializable
data object Progress

fun NavGraphBuilder.progressScreen(onOpenSettings: () -> Unit) {
    composable<Progress> {
        ProgressScreen(onOpenSettings = onOpenSettings)
    }
}
