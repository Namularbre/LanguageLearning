package com.sazerat.languagelearning.core.domain.progress

import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.LearnerWord
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.ReviewLogEntry
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.reading.comprehension
import com.sazerat.languagelearning.core.domain.repository.ReadingLogEntry

/** Estimated time of one review (spec §8.2): enough to read the sentence and answer. */
public const val SECONDS_PER_REVIEW: Int = 15

/** What the learner did, as logged: the source of every statistic (spec §8.2). */
public data class Activity(
    val reviews: List<ReviewLogEntry>,
    val readings: List<ReadingLogEntry>,
)

/** Days (epoch days) with at least one review or one text read. */
public fun Activity.activeDays(clock: Clock): Set<Long> =
    (reviews.map { clock.dayOf(it.reviewedAt) } + readings.map { clock.dayOf(it.finishedAt) }).toSet()

/** Estimated minutes of practice on [day]: 15 s per review plus the reading time of each text finished. */
public fun Activity.minutesOn(day: Long, clock: Clock): Int {
    val reviewSeconds = reviews.count { clock.dayOf(it.reviewedAt) == day } * SECONDS_PER_REVIEW
    val readingMinutes = readings.filter { clock.dayOf(it.finishedAt) == day }.sumOf { it.minutes }
    return reviewSeconds / 60 + readingMinutes
}

/**
 * Consecutive active days up to [today]. A day with nothing done yet does not break the streak
 * before it ends: until then, the streak counts up to yesterday.
 */
public fun streak(activeDays: Set<Long>, today: Long): Int {
    var day = if (today in activeDays) today else today - 1
    var count = 0
    while (day in activeDays) {
        count++
        day--
    }
    return count
}

/**
 * Number of known words at the end of each of [days]. Only the current status and the time it
 * was last set are kept, so a word known today counts from the day it became known.
 */
public fun knownWordsByDay(words: List<LearnerWord>, days: LongRange, clock: Clock): List<Int> {
    val knownSince = words.filter { it.status == WordStatus.KNOWN }.map { clock.dayOf(it.updatedAt) }
    return days.map { day -> knownSince.count { it <= day } }
}

/** Words added to the reviews in the 7 days ending [today]. */
public fun newWordsThisWeek(words: List<LearnerWord>, today: Long, clock: Clock): Int =
    words.count { it.status == WordStatus.LEARNING && clock.dayOf(it.updatedAt) > today - 7 }

/** "You understand about [percent] % of an [level] text" (spec §4). */
public data class LevelComprehension(val level: CefrLevel, val percent: Int)

/**
 * Average comprehension of the texts of the highest level the learner reads comfortably (75 % or
 * more), or of the easiest level if none is. `null` without texts.
 */
public fun levelComprehension(texts: List<ReadingText>, statuses: Map<LemmaId, WordStatus>): LevelComprehension? {
    val byLevel = texts.groupBy { it.level }.toSortedMap().mapValues { (_, inLevel) ->
        inLevel.sumOf { it.comprehension(statuses) } / inLevel.size
    }
    if (byLevel.isEmpty()) return null
    val (level, percent) = byLevel.entries.lastOrNull { it.value >= COMFORTABLE_COMPREHENSION } ?: byLevel.entries.first()
    return LevelComprehension(level, percent)
}

private const val COMFORTABLE_COMPREHENSION = 75
