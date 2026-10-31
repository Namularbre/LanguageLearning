package com.sazerat.languagelearning.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.feature.onboarding.OnboardingScreen
import kotlinx.serialization.Serializable

/** First launch only: the app starts here until the onboarding is done. */
@Serializable
data object Onboarding

fun NavGraphBuilder.onboardingScreen(languages: List<LanguageCode>, onFinished: () -> Unit) {
    composable<Onboarding> {
        OnboardingScreen(languages = languages, onFinished = onFinished)
    }
}
