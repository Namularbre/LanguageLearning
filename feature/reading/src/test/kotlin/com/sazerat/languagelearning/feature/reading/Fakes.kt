package com.sazerat.languagelearning.feature.reading

import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.DailyGoal
import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LearnerWord
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.model.PronunciationExercise
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import com.sazerat.languagelearning.core.domain.model.ReviewLogEntry
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.UserPreferences
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.repository.LessonRepository
import com.sazerat.languagelearning.core.domain.repository.LexiconRepository
import com.sazerat.languagelearning.core.domain.repository.PronunciationRepository
import com.sazerat.languagelearning.core.domain.repository.ReadingLogEntry
import com.sazerat.languagelearning.core.domain.repository.ReadingLogRepository
import com.sazerat.languagelearning.core.domain.repository.ReviewRepository
import com.sazerat.languagelearning.core.domain.repository.TextRepository
import com.sazerat.languagelearning.core.domain.repository.UserPreferencesRepository
import com.sazerat.languagelearning.core.domain.repository.WordStatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

// In-memory repositories for the ViewModel tests. Only what the tests need is stored.

internal class FakeClock(var now: Long = 1_000) : Clock {
    override fun now(): Long = now

    override fun dayOf(millis: Long): Long = millis / 86_400_000
}

internal class FakeTextRepository(private val texts: List<ReadingText>) : TextRepository {
    override fun observeTexts(languageCode: LanguageCode): Flow<List<ReadingText>> =
        flowOf(texts.filter { it.languageCode == languageCode })

    override fun observeText(id: TextId): Flow<ReadingText?> = flowOf(texts.find { it.id == id })
}

internal class FakeLexiconRepository(private val lemmas: List<Lemma>) : LexiconRepository {
    override fun observeLemmas(languageCode: LanguageCode): Flow<List<Lemma>> =
        flowOf(lemmas.filter { it.languageCode == languageCode })
}

internal class FakeWordStatusRepository : WordStatusRepository {
    val statuses = MutableStateFlow<Map<LemmaId, WordStatus>>(emptyMap())

    override fun observeWords(languageCode: LanguageCode): Flow<List<LearnerWord>> =
        statuses.map { all -> all.map { (id, status) -> LearnerWord(id, status, updatedAt = 0) } }

    override suspend fun setStatus(languageCode: LanguageCode, lemmaIds: Collection<LemmaId>, status: WordStatus) {
        statuses.update { all -> all + lemmaIds.associateWith { status } }
    }
}

internal class FakeReviewRepository : ReviewRepository {
    val cards = MutableStateFlow<List<ReviewCard>>(emptyList())
    val log = MutableStateFlow<List<ReviewLogEntry>>(emptyList())

    override fun observeCards(languageCode: LanguageCode): Flow<List<ReviewCard>> =
        cards.map { all -> all.filter { it.languageCode == languageCode } }

    override suspend fun addCard(card: ReviewCard) {
        cards.update { all -> if (all.any { it.lemmaId == card.lemmaId }) all else all + card }
    }

    override suspend fun saveReview(card: ReviewCard, entry: ReviewLogEntry) {
        cards.update { all -> all.map { if (it.lemmaId == card.lemmaId) card else it } }
        log.update { it + entry }
    }

    override fun observeLog(languageCode: LanguageCode): Flow<List<ReviewLogEntry>> = log
}

internal class FakeReadingLogRepository : ReadingLogRepository {
    val entries = MutableStateFlow<List<ReadingLogEntry>>(emptyList())

    override fun observeLog(languageCode: LanguageCode): Flow<List<ReadingLogEntry>> = entries

    override suspend fun add(languageCode: LanguageCode, entry: ReadingLogEntry) {
        entries.update { it + entry }
    }
}

internal class FakeUserPreferencesRepository(initial: UserPreferences = UserPreferences()) : UserPreferencesRepository {
    override val preferences = MutableStateFlow(initial)

    override suspend fun setLearningLanguage(languageCode: LanguageCode) {
        preferences.update { it.copy(learningLanguage = languageCode) }
    }

    override suspend fun setDailyGoal(goal: DailyGoal) {
        preferences.update { it.copy(dailyGoal = goal) }
    }

    override suspend fun setPronunciationHelp(enabled: Boolean) {
        preferences.update { it.copy(pronunciationHelp = enabled) }
    }

    override suspend fun setOnboardingDone() {
        preferences.update { it.copy(onboardingDone = true) }
    }
}

internal class FakeLessonRepository(private val lessons: List<Lesson> = emptyList()) : LessonRepository {
    override fun observeLessons(languageCode: LanguageCode): Flow<List<Lesson>> =
        flowOf(lessons.filter { it.languageCode == languageCode })

    override fun observeLesson(id: LessonId): Flow<Lesson?> = flowOf(lessons.find { it.id == id })
}

internal class FakePronunciationRepository(private val exercises: List<PronunciationExercise> = emptyList()) :
    PronunciationRepository {
    override fun observeExercises(languageCode: LanguageCode): Flow<List<PronunciationExercise>> =
        flowOf(exercises.filter { it.languageCode == languageCode })

    override fun observeExercise(id: ExerciseId): Flow<PronunciationExercise?> = flowOf(exercises.find { it.id == id })
}
