package com.sazerat.languagelearning.core.domain.model

/** The four answers of a review (spec §4), as in FSRS. */
public enum class Rating { AGAIN, HARD, GOOD, EASY }

/** Where the word was met: the card shows this sentence with the word hidden. */
public data class CardContext(
    val textId: TextId,
    val sentenceIndex: Int,
    /** Index of the word in [Sentence.tokens]. */
    val tokenIndex: Int,
)

/** What FSRS knows about the learner's memory of a card (spec §8.1). */
public data class MemoryState(
    /** Days after which the chance of remembering falls to 90 %. */
    val stability: Double,
    /** From 1 (easy) to 10 (hard). */
    val difficulty: Double,
)

/** A review card: one per lemma, created from the word sheet. */
public data class ReviewCard(
    val lemmaId: LemmaId,
    val languageCode: LanguageCode,
    val context: CardContext,
    /** `null` until the first review. */
    val memory: MemoryState?,
    /** Epoch milliseconds: the card is due from then on. */
    val due: Long,
    val reps: Int = 0,
    val lapses: Int = 0,
    val lastReview: Long? = null,
    val createdAt: Long,
) {
    public fun isDue(now: Long): Boolean = due <= now
}

/** One answer given during a review. Append-only log, used by the statistics. */
public data class ReviewLogEntry(
    val lemmaId: LemmaId,
    val rating: Rating,
    /** Epoch milliseconds. */
    val reviewedAt: Long,
)
