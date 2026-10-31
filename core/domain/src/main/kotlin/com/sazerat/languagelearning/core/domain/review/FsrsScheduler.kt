package com.sazerat.languagelearning.core.domain.review

import com.sazerat.languagelearning.core.domain.model.MILLIS_PER_DAY
import com.sazerat.languagelearning.core.domain.model.MILLIS_PER_MINUTE
import com.sazerat.languagelearning.core.domain.model.MemoryState
import com.sazerat.languagelearning.core.domain.model.Rating
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Spaced repetition with FSRS 4.5 (spec §8.1): default weights, 90 % desired retention, so the next
 * review falls when the chance of remembering drops to 90 %. Simplified as the spec says: no
 * learning steps within the day, except that [Rating.AGAIN] brings the card back after 10 minutes.
 *
 * Formulas: https://github.com/open-spaced-repetition/fsrs4anki/wiki/The-Algorithm (version 4.5).
 */
public class FsrsScheduler(private val w: List<Double> = DEFAULT_WEIGHTS) {
    init {
        require(w.size == DEFAULT_WEIGHTS.size) { "FSRS 4.5 needs ${DEFAULT_WEIGHTS.size} weights" }
    }

    /** The card after the learner answered [rating] at [now] (epoch milliseconds). */
    public fun review(card: ReviewCard, rating: Rating, now: Long): ReviewCard {
        val memory = nextMemory(card, rating, now)
        return card.copy(
            memory = memory,
            due = now + intervalMillis(card, rating, memory, now),
            reps = card.reps + 1,
            lapses = card.lapses + if (rating == Rating.AGAIN && card.memory != null) 1 else 0,
            lastReview = now,
        )
    }

    /** Time until the next review for each answer, shown under the rating buttons (spec §4). */
    public fun nextIntervals(card: ReviewCard, now: Long): Map<Rating, Long> =
        Rating.entries.associateWith { rating -> intervalMillis(card, rating, nextMemory(card, rating, now), now) }

    private fun intervalMillis(card: ReviewCard, rating: Rating, memory: MemoryState, now: Long): Long {
        if (rating == Rating.AGAIN) return AGAIN_DELAY
        // Hard <= Good <= Easy, at least a day apart, as the learner expects from the buttons.
        val days = Rating.entries.filter { it != Rating.AGAIN }.runningFold(0L) { previous, each ->
            val stability = if (each == rating) memory.stability else nextMemory(card, each, now).stability
            maxOf(intervalDays(stability), previous + 1)
        }
        return days[rating.ordinal] * MILLIS_PER_DAY
    }

    private fun nextMemory(card: ReviewCard, rating: Rating, now: Long): MemoryState {
        val memory = card.memory ?: return MemoryState(initialStability(rating), initialDifficulty(rating))
        val elapsedDays = ((now - (card.lastReview ?: now)).coerceAtLeast(0)).toDouble() / MILLIS_PER_DAY
        val retrievability = retrievability(elapsedDays, memory.stability)
        val stability = if (rating == Rating.AGAIN) {
            forgetStability(memory, retrievability)
        } else {
            recallStability(memory, retrievability, rating)
        }
        return MemoryState(stability, nextDifficulty(memory.difficulty, rating))
    }

    /** 1 (Again) to 4 (Easy), as in the paper. */
    private val Rating.grade: Int get() = ordinal + 1

    private fun initialStability(rating: Rating): Double = w[rating.grade - 1].coerceAtLeast(MIN_STABILITY)

    private fun initialDifficulty(rating: Rating): Double = (w[4] - (rating.grade - 3) * w[5]).coerceIn(1.0, 10.0)

    private fun nextDifficulty(difficulty: Double, rating: Rating): Double {
        val next = difficulty - w[6] * (rating.grade - 3)
        // Mean reversion towards the difficulty of a first "Good".
        return (w[7] * initialDifficulty(Rating.GOOD) + (1 - w[7]) * next).coerceIn(1.0, 10.0)
    }

    private fun recallStability(memory: MemoryState, retrievability: Double, rating: Rating): Double {
        val hardPenalty = if (rating == Rating.HARD) w[15] else 1.0
        val easyBonus = if (rating == Rating.EASY) w[16] else 1.0
        val growth = exp(w[8]) * (11 - memory.difficulty) * memory.stability.pow(-w[9]) *
            (exp(w[10] * (1 - retrievability)) - 1) * hardPenalty * easyBonus
        return memory.stability * (growth + 1)
    }

    private fun forgetStability(memory: MemoryState, retrievability: Double): Double {
        val stability = w[11] * memory.difficulty.pow(-w[12]) * ((memory.stability + 1).pow(w[13]) - 1) *
            exp(w[14] * (1 - retrievability))
        // Forgetting never makes the memory stronger.
        return stability.coerceIn(MIN_STABILITY, memory.stability)
    }

    public companion object {
        /** FSRS 4.5 default weights, fitted by its authors on millions of reviews. */
        public val DEFAULT_WEIGHTS: List<Double> = listOf(
            0.4872, 1.4003, 3.7145, 13.8206, 5.1618, 1.2298, 0.8975, 0.031, 1.6474,
            0.1367, 1.0461, 2.1072, 0.0793, 0.3246, 1.587, 0.2272, 2.8755,
        )

        /** Chance of remembering the learner aims for. */
        public const val DESIRED_RETENTION: Double = 0.9

        /** "Again" brings the card back this soon, instead of FSRS learning steps. */
        public const val AGAIN_DELAY: Long = 10 * MILLIS_PER_MINUTE

        private const val DECAY = -0.5
        private const val FACTOR = 19.0 / 81.0
        private const val MIN_STABILITY = 0.1
        private const val MAX_INTERVAL_DAYS = 36_500L

        /** Chance of remembering after [elapsedDays] for a memory of this [stability]. */
        public fun retrievability(elapsedDays: Double, stability: Double): Double =
            (1 + FACTOR * elapsedDays / stability).pow(DECAY)

        /** Days until the chance of remembering falls to [DESIRED_RETENTION] (= the stability at 90 %). */
        private fun intervalDays(stability: Double): Long {
            val days = stability / FACTOR * (DESIRED_RETENTION.pow(1 / DECAY) - 1)
            return days.roundToLong().coerceIn(1, MAX_INTERVAL_DAYS)
        }
    }
}
