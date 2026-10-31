package com.sazerat.languagelearning.feature.onboarding

import androidx.lifecycle.SavedStateHandle
import com.sazerat.languagelearning.core.domain.model.DailyGoal
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.testing.FakeLexiconRepository
import com.sazerat.languagelearning.core.testing.FakeUserPreferencesRepository
import com.sazerat.languagelearning.core.testing.FakeWordStatusRepository
import com.sazerat.languagelearning.core.testing.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OnboardingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val romanian = LanguageCode("ro-RO")
    private val lemmas = (1..60).map { rank ->
        Lemma(LemmaId("ro:w$rank"), romanian, "w$rank", PartOfSpeech.NOUN, meaning = LocalizedText.of(en = "w"), frequencyRank = rank)
    }
    private val preferences = FakeUserPreferencesRepository()
    private val words = FakeWordStatusRepository()
    private val viewModel = OnboardingViewModel(SavedStateHandle(), preferences, words, FakeLexiconRepository(lemmas))

    @Test
    fun `the level test shows the most frequent words of the chosen language`() = runTest {
        viewModel.onLearningLanguageSelected(romanian)
        viewModel.onNextClick()

        val state = viewModel.uiState.first { it.step == OnboardingStep.LEVEL && it.testWords.isNotEmpty() }

        assertEquals(lemmas.take(OnboardingViewModel.TEST_SIZE), state.testWords)
    }

    @Test
    fun `nothing moves on before a language is chosen`() = runTest {
        viewModel.uiState.first()

        viewModel.onNextClick()

        assertEquals(OnboardingStep.LANGUAGES, viewModel.uiState.first().step)
    }

    @Test
    fun `finishing saves the language, the goal and the ticked words`() = runTest {
        viewModel.onLearningLanguageSelected(romanian)
        viewModel.onNextClick()
        viewModel.onWordToggled(LemmaId("ro:w1"))
        viewModel.onWordToggled(LemmaId("ro:w2"))
        viewModel.onWordToggled(LemmaId("ro:w2"))
        viewModel.onNextClick()
        viewModel.onDailyGoalSelected(DailyGoal.FIVE)
        viewModel.uiState.first { it.step == OnboardingStep.GOAL && it.dailyGoal == DailyGoal.FIVE }

        viewModel.onNextClick()

        assertTrue(viewModel.uiState.first { it.finished }.finished)
        val saved = preferences.preferences.value
        assertEquals(romanian, saved.learningLanguage)
        assertEquals(DailyGoal.FIVE, saved.dailyGoal)
        assertTrue(saved.onboardingDone)
        assertEquals(mapOf(LemmaId("ro:w1") to WordStatus.KNOWN), words.statuses.value)
    }

    @Test
    fun `a complete beginner skips the level test`() = runTest {
        viewModel.onLearningLanguageSelected(romanian)
        viewModel.onNextClick()

        viewModel.onWordToggled(LemmaId("ro:w1"))
        viewModel.onBeginnerClick()

        val state = viewModel.uiState.first { it.step == OnboardingStep.GOAL }
        assertTrue(state.knownWords.isEmpty())
    }
}
