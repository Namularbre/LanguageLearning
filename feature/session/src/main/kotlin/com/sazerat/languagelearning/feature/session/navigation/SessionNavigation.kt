package com.sazerat.languagelearning.feature.session.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.feature.session.today.TodayScreen
import kotlinx.serialization.Serializable

/** The Today tab, the app's home. */
@Serializable
data object Today

/** [languageSwitcher] is shown in the top bar: the app provides it, this feature does not know it. */
fun NavGraphBuilder.todayScreen(
    onOpenReview: () -> Unit,
    onReadText: (TextId) -> Unit,
    languageSwitcher: @Composable () -> Unit,
) {
    composable<Today> {
        TodayScreen(onOpenReview = onOpenReview, onReadText = onReadText, languageSwitcher = languageSwitcher)
    }
}
