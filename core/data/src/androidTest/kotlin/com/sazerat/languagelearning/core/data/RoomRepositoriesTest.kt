package com.sazerat.languagelearning.core.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sazerat.languagelearning.core.data.database.LanguageLearningDatabase
import com.sazerat.languagelearning.core.data.repository.RoomReviewRepository
import com.sazerat.languagelearning.core.data.repository.RoomWordStatusRepository
import com.sazerat.languagelearning.core.domain.model.CardContext
import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.MemoryState
import com.sazerat.languagelearning.core.domain.model.Rating
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import com.sazerat.languagelearning.core.domain.model.ReviewLogEntry
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.WordStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

/** Real Room DAOs on the device (spec §11.4), in an in-memory database thrown away after each test. */
@RunWith(AndroidJUnit4::class)
class RoomRepositoriesTest {
    private val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        LanguageLearningDatabase::class.java,
    ).build()

    private val clock = object : Clock {
        override fun now() = 1_000L

        override fun dayOf(millis: Long) = 0L
    }

    private val romanian = LanguageCode("ro-RO")
    private val casa = LemmaId("ro:casă")

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun wordStatusIsSavedPerLanguageAndCanChange() = runTest {
        val repository = RoomWordStatusRepository(database.wordStatusDao(), clock)

        repository.setStatus(romanian, listOf(casa), WordStatus.LEARNING)
        repository.setStatus(romanian, listOf(casa), WordStatus.KNOWN)
        repository.setStatus(LanguageCode("fr-FR"), listOf(LemmaId("fr:maison")), WordStatus.KNOWN)

        val words = repository.observeWords(romanian).first()
        assertEquals(listOf(casa), words.map { it.lemmaId })
        assertEquals(WordStatus.KNOWN, words.single().status)
    }

    @Test
    fun addingACardTwiceKeepsTheFirstSchedule() = runTest {
        val repository = RoomReviewRepository(database.reviewDao())
        val card = ReviewCard(casa, romanian, CardContext(TextId("ro-market"), 2, 4), memory = null, due = 10, createdAt = 10)

        repository.addCard(card)
        repository.addCard(card.copy(due = 99))

        assertEquals(listOf(card), repository.observeCards(romanian).first())
    }

    @Test
    fun aReviewUpdatesTheCardAndIsLogged() = runTest {
        val repository = RoomReviewRepository(database.reviewDao())
        val card = ReviewCard(casa, romanian, CardContext(TextId("ro-market"), 2, 4), memory = null, due = 10, createdAt = 10)
        repository.addCard(card)

        val reviewed = card.copy(memory = MemoryState(3.0, 5.0), due = 500, reps = 1, lastReview = 20)
        repository.saveReview(reviewed, ReviewLogEntry(casa, Rating.GOOD, reviewedAt = 20))

        assertEquals(listOf(reviewed), repository.observeCards(romanian).first())
        assertEquals(listOf(ReviewLogEntry(casa, Rating.GOOD, 20)), repository.observeLog(romanian).first())
    }
}
