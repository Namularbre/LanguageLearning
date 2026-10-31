package com.sazerat.languagelearning.core.domain.review

import com.sazerat.languagelearning.core.domain.model.CardContext
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.MILLIS_PER_DAY
import com.sazerat.languagelearning.core.domain.model.Rating
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import com.sazerat.languagelearning.core.domain.model.TextId
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FsrsSchedulerTest {
    private val scheduler = FsrsScheduler()
    private val start = 1_000_000L
    private val newCard = ReviewCard(
        lemmaId = LemmaId("ro:casă"),
        languageCode = LanguageCode("ro-RO"),
        context = CardContext(TextId("t"), 0, 0),
        memory = null,
        due = start,
        createdAt = start,
    )

    @Test
    fun `a new card gets the default initial stability of its first answer`() {
        val good = scheduler.review(newCard, Rating.GOOD, start)

        assertEquals(3.7145, good.memory!!.stability, 1e-9)
        assertEquals(5.1618, good.memory.difficulty, 1e-9)
        assertEquals(start + 4 * MILLIS_PER_DAY, good.due)
        assertEquals(1, good.reps)
        assertEquals(start, good.lastReview)
    }

    @Test
    fun `again brings the card back after ten minutes`() {
        val again = scheduler.review(newCard, Rating.AGAIN, start)

        assertEquals(start + FsrsScheduler.AGAIN_DELAY, again.due)
        // A card never learnt is not a lapse.
        assertEquals(0, again.lapses)
    }

    @Test
    fun `intervals grow from hard to easy`() {
        listOf(newCard, scheduler.review(newCard, Rating.GOOD, start)).forEach { card ->
            val now = card.due
            val intervals = scheduler.nextIntervals(card, now)

            assertEquals(FsrsScheduler.AGAIN_DELAY, intervals.getValue(Rating.AGAIN))
            assertTrue(intervals.getValue(Rating.HARD) >= MILLIS_PER_DAY)
            assertTrue(intervals.getValue(Rating.HARD) < intervals.getValue(Rating.GOOD))
            assertTrue(intervals.getValue(Rating.GOOD) < intervals.getValue(Rating.EASY))
        }
    }

    @Test
    fun `remembering on time makes the memory more stable`() {
        val first = scheduler.review(newCard, Rating.GOOD, start)

        val second = scheduler.review(first, Rating.GOOD, first.due)

        assertTrue(second.memory!!.stability > first.memory!!.stability * 2)
        assertTrue(second.due - first.due > first.due - start)
    }

    @Test
    fun `forgetting lowers the stability and counts a lapse`() {
        val learnt = scheduler.review(scheduler.review(newCard, Rating.GOOD, start), Rating.GOOD, start + 4 * MILLIS_PER_DAY)

        val forgotten = scheduler.review(learnt, Rating.AGAIN, learnt.due)

        assertTrue(forgotten.memory!!.stability < learnt.memory!!.stability)
        assertTrue(forgotten.memory.difficulty > learnt.memory.difficulty)
        assertEquals(1, forgotten.lapses)
    }

    @Test
    fun `difficulty stays between 1 and 10`() {
        var easy = newCard
        var hard = newCard
        repeat(30) {
            easy = scheduler.review(easy, Rating.EASY, easy.due)
            hard = scheduler.review(hard, Rating.AGAIN, hard.due)
        }

        assertTrue(easy.memory!!.difficulty >= 1.0)
        assertTrue(hard.memory!!.difficulty <= 10.0)
    }

    @Test
    fun `the interval at 90 percent retention equals the stability`() {
        val retrievability = FsrsScheduler.retrievability(elapsedDays = 10.0, stability = 10.0)

        assertTrue(abs(retrievability - FsrsScheduler.DESIRED_RETENTION) < 1e-9)
    }
}
