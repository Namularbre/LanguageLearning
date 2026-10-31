package com.sazerat.languagelearning.feature.settings.switcher

import com.sazerat.languagelearning.core.domain.model.CardContext
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.UserPreferences
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.progress.StudiedLanguages
import com.sazerat.languagelearning.core.testing.FakeClock
import com.sazerat.languagelearning.core.testing.FakeReadingLogRepository
import com.sazerat.languagelearning.core.testing.FakeReviewRepository
import com.sazerat.languagelearning.core.testing.FakeUserPreferencesRepository
import com.sazerat.languagelearning.core.testing.FakeWordStatusRepository
import com.sazerat.languagelearning.core.testing.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageSwitcherViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val romanian = LanguageCode("ro-RO")
    private val english = LanguageCode("en-GB")
    private val preferences = FakeUserPreferencesRepository(UserPreferences(learningLanguage = romanian))
    private val words = FakeWordStatusRepository()
    private val reviews = FakeReviewRepository()
    private val clock = FakeClock(now = 10_000)

    private fun viewModel() = LanguageSwitcherViewModel(preferences, words, reviews, FakeReadingLogRepository(), clock)

    private suspend fun LanguageSwitcherViewModel.success(current: LanguageCode = romanian) =
        uiState.first { it is LanguageSwitcherUiState.Success && it.current == current } as LanguageSwitcherUiState.Success

    @Test
    fun `every studied language is listed with its own words and due reviews`() = runTest {
        words.setStatus(romanian, listOf(LemmaId("ro:a"), LemmaId("ro:b")), WordStatus.KNOWN)
        reviews.addCard(ReviewCard(LemmaId("en:c"), english, CardContext(TextId("t"), 0, 0), memory = null, due = 0, createdAt = 0))

        val state = viewModel().success()

        assertEquals(StudiedLanguages, state.languages.map { it.languageCode })
        assertEquals(2, state.languages.single { it.languageCode == romanian }.knownWords)
        assertEquals(1, state.languages.single { it.languageCode == english }.dueCards)
        assertEquals(0, state.languages.single { it.languageCode == romanian }.dueCards)
    }

    @Test
    fun `choosing a language makes it the one being learnt`() = runTest {
        val viewModel = viewModel()
        viewModel.success()

        viewModel.onLanguageSelected(english)

        assertEquals(english, viewModel.success(current = english).current)
        assertEquals(english, preferences.preferences.value.learningLanguage)
    }
}
