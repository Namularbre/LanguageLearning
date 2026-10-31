package com.sazerat.languagelearning.core.domain.repository

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import com.sazerat.languagelearning.core.domain.model.ReviewLogEntry
import kotlinx.coroutines.flow.Flow

public interface ReviewRepository {
    public fun observeCards(languageCode: LanguageCode): Flow<List<ReviewCard>>

    /** Adds a card, unless the lemma already has one: its schedule is kept. */
    public suspend fun addCard(card: ReviewCard)

    /** Saves the card's new schedule and logs the answer, both or neither. */
    public suspend fun saveReview(card: ReviewCard, entry: ReviewLogEntry)

    public fun observeLog(languageCode: LanguageCode): Flow<List<ReviewLogEntry>>
}
