package com.sazerat.languagelearning.core.domain.reading

import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.repository.ReadingLogEntry

/**
 * Share of the text the learner should understand, from 0 to 100 (spec §5.2): word occurrences
 * whose base form is known or being learnt. Occurrences, not distinct words, because a word seen
 * ten times weighs ten times in what you understand.
 */
public fun ReadingText.comprehension(statuses: Map<LemmaId, WordStatus>): Int {
    val words = wordLemmas
    if (words.isEmpty()) return 100
    val understood = words.count { statuses[it]?.isUnderstood == true }
    return understood * 100 / words.size
}

/** Above this, a text teaches almost nothing new: it is not suggested while others remain. */
public const val MAX_SUGGESTED_COMPREHENSION: Int = 98

/**
 * The text to read next (spec §5.2): among the unread ones, the easiest that still has something to
 * teach, i.e. the highest comprehension up to [MAX_SUGGESTED_COMPREHENSION]. If every unread text is
 * above it, the hardest of them. Ties go to the lower level, then the shorter text.
 * `null` when everything has been read.
 */
public fun suggestNextText(
    texts: List<ReadingText>,
    statuses: Map<LemmaId, WordStatus>,
    readTexts: Set<TextId>,
): ReadingText? {
    val unread = texts.filter { it.id !in readTexts }.map { it to it.comprehension(statuses) }
    val easierFirst = compareBy<Pair<ReadingText, Int>>({ -it.second }, { it.first.level }, { it.first.minutes })
    val worthReading = unread.filter { it.second <= MAX_SUGGESTED_COMPREHENSION }
    return if (worthReading.isNotEmpty()) {
        worthReading.minWith(easierFirst).first
    } else {
        unread.minByOrNull { it.second }?.first
    }
}

/**
 * A text to read again once every text has been read: the one read longest ago, since rereading
 * it brings back the most. `null` while some text is still unread, or with nothing read yet.
 */
public fun suggestReread(texts: List<ReadingText>, log: List<ReadingLogEntry>): ReadingText? {
    val lastRead = log.groupBy { it.textId }.mapValues { (_, entries) -> entries.maxOf { it.finishedAt } }
    if (texts.isEmpty() || texts.any { it.id !in lastRead }) return null
    return texts.minBy { lastRead.getValue(it.id) }
}
