package com.sazerat.languagelearning.core.domain.progress

import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LearnerWord
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import com.sazerat.languagelearning.core.domain.model.ReviewLogEntry
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.repository.ReadingLogEntry
import com.sazerat.languagelearning.core.domain.repository.ReadingLogRepository
import com.sazerat.languagelearning.core.domain.repository.ReviewRepository
import com.sazerat.languagelearning.core.domain.repository.WordStatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

/**
 * Languages that have content, in display order. M2 will derive them from the downloaded content
 * packs.
 */
public val StudiedLanguages: List<LanguageCode> = listOf(LanguageCode("en-GB"), LanguageCode("ro-RO"), LanguageCode("fr-FR"))

/** Where the learner stands in one language: what the language switcher and the global progress show. */
public data class LanguageSummary(
    val languageCode: LanguageCode,
    val knownWords: Int,
    val learningWords: Int,
    val dueCards: Int,
    val textsRead: Int,
    /** Days (epoch days) with at least one review or one text read in this language. */
    val activeDays: Set<Long>,
) {
    /** Nothing done yet in this language. */
    public val isStarted: Boolean get() = knownWords + learningWords > 0 || activeDays.isNotEmpty()
}

public fun languageSummary(
    languageCode: LanguageCode,
    words: List<LearnerWord>,
    cards: List<ReviewCard>,
    reviews: List<ReviewLogEntry>,
    readings: List<ReadingLogEntry>,
    clock: Clock,
): LanguageSummary {
    val now = clock.now()
    return LanguageSummary(
        languageCode = languageCode,
        knownWords = words.count { it.status == WordStatus.KNOWN },
        learningWords = words.count { it.status == WordStatus.LEARNING },
        dueCards = cards.count { it.isDue(now) },
        textsRead = readings.map { it.textId }.distinct().size,
        activeDays = Activity(reviews, readings).activeDays(clock),
    )
}

/** Progress across every language: a day counts for the streak whatever the language practised. */
public data class GlobalProgress(
    val knownWords: Int,
    val learningWords: Int,
    val streak: Int,
    val languages: List<LanguageSummary>,
)

public fun globalProgress(summaries: List<LanguageSummary>, today: Long): GlobalProgress = GlobalProgress(
    knownWords = summaries.sumOf { it.knownWords },
    learningWords = summaries.sumOf { it.learningWords },
    streak = streak(summaries.flatMapTo(mutableSetOf()) { it.activeDays }, today),
    languages = summaries,
)

/** The summary of each of [languages], kept up to date. */
public fun observeLanguageSummaries(
    languages: List<LanguageCode>,
    wordStatusRepository: WordStatusRepository,
    reviewRepository: ReviewRepository,
    readingLogRepository: ReadingLogRepository,
    clock: Clock,
): Flow<List<LanguageSummary>> {
    if (languages.isEmpty()) return flowOf(emptyList())
    val perLanguage = languages.map { language ->
        combine(
            wordStatusRepository.observeWords(language),
            reviewRepository.observeCards(language),
            reviewRepository.observeLog(language),
            readingLogRepository.observeLog(language),
        ) { words, cards, reviews, readings ->
            languageSummary(language, words, cards, reviews, readings, clock)
        }
    }
    return combine(perLanguage) { it.toList() }
}
