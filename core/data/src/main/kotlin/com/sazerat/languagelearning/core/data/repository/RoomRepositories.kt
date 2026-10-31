package com.sazerat.languagelearning.core.data.repository

import com.sazerat.languagelearning.core.data.database.ReadingLogDao
import com.sazerat.languagelearning.core.data.database.ReadingLogEntity
import com.sazerat.languagelearning.core.data.database.ReviewCardEntity
import com.sazerat.languagelearning.core.data.database.ReviewDao
import com.sazerat.languagelearning.core.data.database.ReviewLogEntity
import com.sazerat.languagelearning.core.data.database.WordStatusDao
import com.sazerat.languagelearning.core.data.database.WordStatusEntity
import com.sazerat.languagelearning.core.domain.model.CardContext
import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LearnerWord
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.MemoryState
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import com.sazerat.languagelearning.core.domain.model.ReviewLogEntry
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.repository.ReadingLogEntry
import com.sazerat.languagelearning.core.domain.repository.ReadingLogRepository
import com.sazerat.languagelearning.core.domain.repository.ReviewRepository
import com.sazerat.languagelearning.core.domain.repository.WordStatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Repositories backed by Room. They only translate between domain models and table rows.

class RoomWordStatusRepository(
    private val dao: WordStatusDao,
    private val clock: Clock,
) : WordStatusRepository {
    override fun observeWords(languageCode: LanguageCode): Flow<List<LearnerWord>> =
        dao.observe(languageCode.value).map { rows ->
            rows.map { LearnerWord(LemmaId(it.lemmaId), it.status, it.updatedAt) }
        }

    override suspend fun setStatus(languageCode: LanguageCode, lemmaIds: Collection<LemmaId>, status: WordStatus) {
        val now = clock.now()
        dao.upsert(lemmaIds.map { WordStatusEntity(it.value, languageCode.value, status, now) })
    }
}

class RoomReviewRepository(private val dao: ReviewDao) : ReviewRepository {
    override fun observeCards(languageCode: LanguageCode): Flow<List<ReviewCard>> =
        dao.observeCards(languageCode.value).map { rows -> rows.map { it.toDomain() } }

    override suspend fun addCard(card: ReviewCard) {
        dao.insertCard(card.toEntity())
    }

    override suspend fun saveReview(card: ReviewCard, entry: ReviewLogEntry) {
        val log = ReviewLogEntity(
            lemmaId = entry.lemmaId.value,
            languageCode = card.languageCode.value,
            rating = entry.rating,
            reviewedAt = entry.reviewedAt,
        )
        dao.saveReview(card.toEntity(), log)
    }

    override fun observeLog(languageCode: LanguageCode): Flow<List<ReviewLogEntry>> =
        dao.observeLog(languageCode.value).map { rows ->
            rows.map { ReviewLogEntry(LemmaId(it.lemmaId), it.rating, it.reviewedAt) }
        }
}

class RoomReadingLogRepository(private val dao: ReadingLogDao) : ReadingLogRepository {
    override fun observeLog(languageCode: LanguageCode): Flow<List<ReadingLogEntry>> =
        dao.observe(languageCode.value).map { rows ->
            rows.map { ReadingLogEntry(TextId(it.textId), it.minutes, it.finishedAt) }
        }

    override suspend fun add(languageCode: LanguageCode, entry: ReadingLogEntry) {
        dao.insert(
            ReadingLogEntity(
                textId = entry.textId.value,
                languageCode = languageCode.value,
                minutes = entry.minutes,
                finishedAt = entry.finishedAt,
            ),
        )
    }
}

private fun ReviewCardEntity.toDomain() = ReviewCard(
    lemmaId = LemmaId(lemmaId),
    languageCode = LanguageCode(languageCode),
    context = CardContext(TextId(textId), sentenceIndex, tokenIndex),
    memory = if (stability != null && difficulty != null) MemoryState(stability, difficulty) else null,
    due = due,
    reps = reps,
    lapses = lapses,
    lastReview = lastReview,
    createdAt = createdAt,
)

private fun ReviewCard.toEntity() = ReviewCardEntity(
    lemmaId = lemmaId.value,
    languageCode = languageCode.value,
    textId = context.textId.value,
    sentenceIndex = context.sentenceIndex,
    tokenIndex = context.tokenIndex,
    stability = memory?.stability,
    difficulty = memory?.difficulty,
    due = due,
    reps = reps,
    lapses = lapses,
    lastReview = lastReview,
    createdAt = createdAt,
)
