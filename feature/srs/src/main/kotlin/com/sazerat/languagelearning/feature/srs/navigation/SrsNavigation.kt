package com.sazerat.languagelearning.feature.srs.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.feature.srs.review.ReviewScreen
import kotlinx.serialization.Serializable

/** The Review tab: a review session for the language being learnt. */
@Serializable
data object Review

/** [onReadText] opens a text in the Reader, which belongs to another feature (spec §11.2). */
fun NavGraphBuilder.reviewScreen(onReadText: (TextId) -> Unit) {
    composable<Review> {
        ReviewScreen(onReadText = onReadText, onBack = null)
    }
}
