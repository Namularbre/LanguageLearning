package com.sazerat.languagelearning.core.domain.review

import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.Rating
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import kotlin.random.Random

/** What a multiple-choice review asks for. */
public enum class ExerciseKind {
    /** The word is shown in its sentence; the learner picks its meaning. Easier: for words still fragile. */
    MEANING,

    /** The word is hidden in its sentence; the learner picks it among words of the same language. */
    FILL_BLANK,
}

/**
 * Multiple-choice reviews: the learner picks an answer instead of rating themselves, and the
 * FSRS rating is deduced from whether the answer was right and how long it took.
 */
public object MultipleChoice {
    /** Number of answers offered, the right one included. */
    public const val CHOICE_COUNT: Int = 4

    /** A card is asked for its meaning until its memory holds this long (days). */
    public const val MEANING_STABILITY_DAYS: Double = 7.0

    /** A right answer given this fast (milliseconds) counts as [Rating.EASY]. */
    public const val EASY_WITHIN: Long = 4_000L

    /** A right answer that took longer than this (milliseconds) counts as [Rating.HARD]. */
    public const val HARD_AFTER: Long = 15_000L

    /** New cards and cards just forgotten are asked for their meaning, the others for the word itself. */
    public fun kindFor(card: ReviewCard): ExerciseKind {
        val stability = card.memory?.stability ?: return ExerciseKind.MEANING
        return if (stability < MEANING_STABILITY_DAYS) ExerciseKind.MEANING else ExerciseKind.FILL_BLANK
    }

    /**
     * The answers to offer for [answer], in the order to show them, or `null` when [candidates] do not
     * hold enough distinct wrong answers. Wrong answers favour the same part of speech, then words
     * the learner has already met ([met]); none shares the answer's base form or meaning.
     */
    public fun choices(answer: Lemma, candidates: List<Lemma>, met: Set<LemmaId>, random: Random): List<LemmaId>? {
        val wrong = candidates
            .filter { it.id != answer.id && it.languageCode == answer.languageCode }
            .shuffled(random)
            .sortedBy { (if (it.partOfSpeech == answer.partOfSpeech) 0 else 2) + (if (it.id in met) 0 else 1) }
            .fold(mutableListOf<Lemma>()) { picked, lemma ->
                val clashes = (picked + answer).any { it.text.equals(lemma.text, ignoreCase = true) || it.meaning == lemma.meaning }
                if (picked.size < CHOICE_COUNT - 1 && !clashes) picked += lemma
                picked
            }
        if (wrong.size < CHOICE_COUNT - 1) return null
        return (wrong + answer).map { it.id }.shuffled(random)
    }

    /** The FSRS rating for an answer: wrong is "Again", right is "Good", made easier or harder by the time taken. */
    public fun rating(correct: Boolean, elapsedMillis: Long): Rating = when {
        !correct -> Rating.AGAIN
        elapsedMillis <= EASY_WITHIN -> Rating.EASY
        elapsedMillis > HARD_AFTER -> Rating.HARD
        else -> Rating.GOOD
    }
}
