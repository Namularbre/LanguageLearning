package com.sazerat.languagelearning.feature.onboarding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.model.DailyGoal
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.repository.LexiconRepository
import com.sazerat.languagelearning.core.domain.repository.UserPreferencesRepository
import com.sazerat.languagelearning.core.domain.repository.WordStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** The three steps of the first launch (spec §4). */
enum class OnboardingStep { LANGUAGES, LEVEL, GOAL }

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.LANGUAGES,
    /** `null` until chosen. */
    val learningLanguage: LanguageCode? = null,
    /** Most frequent words first: the learner ticks those they know. */
    val testWords: List<Lemma> = emptyList(),
    val knownWords: Set<LemmaId> = emptySet(),
    val dailyGoal: DailyGoal = DailyGoal.Default,
    /** Set once everything is saved: the screen then leaves the onboarding. */
    val finished: Boolean = false,
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val preferencesRepository: UserPreferencesRepository,
    private val wordStatusRepository: WordStatusRepository,
    lexiconRepository: LexiconRepository,
) : ViewModel() {
    // Every answer is saved, so process death resumes on the same step.
    private val step = savedStateHandle.getStateFlow(STEP_KEY, OnboardingStep.LANGUAGES.name)
    private val language = savedStateHandle.getStateFlow<String?>(LANGUAGE_KEY, null)
    private val known = savedStateHandle.getStateFlow(KNOWN_KEY, arrayListOf<String>())
    private val goal = savedStateHandle.getStateFlow(GOAL_KEY, DailyGoal.Default.name)
    private val finished = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testWords = language.flatMapLatest { code ->
        if (code == null) flowOf(emptyList()) else lexiconRepository.observeLemmas(LanguageCode(code)).map { it.take(TEST_SIZE) }
    }

    val uiState: StateFlow<OnboardingUiState> =
        combine(combine(step, language, known, goal, ::Answers), testWords, finished) { answers, words, finished ->
            OnboardingUiState(
                step = OnboardingStep.valueOf(answers.step),
                learningLanguage = answers.language?.let(::LanguageCode),
                testWords = words,
                knownWords = answers.known.map(::LemmaId).toSet(),
                dailyGoal = DailyGoal.valueOf(answers.goal),
                finished = finished,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = OnboardingUiState(),
        )

    fun onLearningLanguageSelected(languageCode: LanguageCode) {
        if (languageCode.value != language.value) savedStateHandle[KNOWN_KEY] = arrayListOf<String>()
        savedStateHandle[LANGUAGE_KEY] = languageCode.value
    }

    fun onWordToggled(lemmaId: LemmaId) {
        val current = ArrayList(known.value)
        if (!current.remove(lemmaId.value)) current.add(lemmaId.value)
        savedStateHandle[KNOWN_KEY] = current
    }

    /** "I'm a complete beginner": no word known, straight to the goal. */
    fun onBeginnerClick() {
        savedStateHandle[KNOWN_KEY] = arrayListOf<String>()
        savedStateHandle[STEP_KEY] = OnboardingStep.GOAL.name
    }

    fun onDailyGoalSelected(goal: DailyGoal) {
        savedStateHandle[GOAL_KEY] = goal.name
    }

    fun onNextClick() {
        // Read from the saved answers, not uiState: they are current even before the screen subscribes.
        when (OnboardingStep.valueOf(step.value)) {
            OnboardingStep.LANGUAGES -> if (language.value != null) savedStateHandle[STEP_KEY] = OnboardingStep.LEVEL.name
            OnboardingStep.LEVEL -> savedStateHandle[STEP_KEY] = OnboardingStep.GOAL.name
            OnboardingStep.GOAL -> finish()
        }
    }

    fun onBackClick() {
        val previous = OnboardingStep.entries.getOrNull(OnboardingStep.valueOf(step.value).ordinal - 1) ?: return
        savedStateHandle[STEP_KEY] = previous.name
    }

    private fun finish() {
        val language = language.value?.let(::LanguageCode) ?: return
        viewModelScope.launch {
            preferencesRepository.setLearningLanguage(language)
            preferencesRepository.setDailyGoal(DailyGoal.valueOf(goal.value))
            // Ticked words count as read: they raise the comprehension of the first texts.
            wordStatusRepository.setStatus(language, known.value.map(::LemmaId), WordStatus.KNOWN)
            preferencesRepository.setOnboardingDone()
            finished.value = true
        }
    }

    private data class Answers(val step: String, val language: String?, val known: List<String>, val goal: String)

    companion object {
        /** Number of words in the level test (spec §4: 30 to 50). */
        const val TEST_SIZE = 40

        private const val STEP_KEY = "step"
        private const val LANGUAGE_KEY = "language"
        private const val KNOWN_KEY = "known"
        private const val GOAL_KEY = "goal"
    }
}
