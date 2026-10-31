package com.sazerat.languagelearning.feature.reading.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.PronunciationExercise
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.TextKind
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.model.toStatusMap
import com.sazerat.languagelearning.core.domain.reading.comprehension
import com.sazerat.languagelearning.core.domain.reading.suggestNextText
import com.sazerat.languagelearning.core.domain.repository.LessonRepository
import com.sazerat.languagelearning.core.domain.repository.PronunciationRepository
import com.sazerat.languagelearning.core.domain.repository.ReadingLogRepository
import com.sazerat.languagelearning.core.domain.repository.TextRepository
import com.sazerat.languagelearning.core.domain.repository.UserPreferencesRepository
import com.sazerat.languagelearning.core.domain.repository.WordStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** What the Library lists (spec §4 and §6): texts to read, spelling lessons, pronunciation exercises. */
enum class LibrarySection { TEXTS, LESSONS, PRONUNCIATION }

/** A text row: what the learner needs to choose what to read. */
data class TextItem(
    val id: TextId,
    val title: String,
    val level: CefrLevel,
    val kind: TextKind,
    val minutes: Int,
    val comprehension: Int,
    val isRead: Boolean,
)

sealed interface LibraryUiState {
    data object Loading : LibraryUiState

    data class Success(
        val languageCode: LanguageCode,
        /** Pronunciation is only offered when the pronunciation help is on (spec §7). */
        val sections: List<LibrarySection>,
        val section: LibrarySection,
        /** Levels that have at least one text, for the filter chips. */
        val levels: List<CefrLevel>,
        /** `null` shows every level. */
        val level: CefrLevel?,
        /** Texts already read are left out of [texts]. */
        val hideRead: Boolean,
        /** Whether some text of the chosen level has been read: the "hide read texts" box is useless otherwise. */
        val hasReadTexts: Boolean,
        val texts: List<TextItem>,
        val suggestedTextId: TextId?,
        val lessons: List<Lesson>,
        val exercises: List<PronunciationExercise>,
    ) : LibraryUiState
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    preferencesRepository: UserPreferencesRepository,
    private val textRepository: TextRepository,
    private val wordStatusRepository: WordStatusRepository,
    private val readingLogRepository: ReadingLogRepository,
    private val lessonRepository: LessonRepository,
    private val pronunciationRepository: PronunciationRepository,
) : ViewModel() {
    // Filters are screen state: saved so they survive process death.
    private val section = savedStateHandle.getStateFlow(SECTION_KEY, LibrarySection.TEXTS.name)
    private val level = savedStateHandle.getStateFlow<String?>(LEVEL_KEY, null)
    private val hideRead = savedStateHandle.getStateFlow(HIDE_READ_KEY, false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val content: Flow<LibraryContent> = preferencesRepository.preferences.flatMapLatest { preferences ->
        val language = preferences.learningLanguageOrDefault
        combine(
            textRepository.observeTexts(language),
            wordStatusRepository.observeWords(language),
            readingLogRepository.observeLog(language),
            lessonRepository.observeLessons(language),
            if (preferences.pronunciationHelp) pronunciationRepository.observeExercises(language) else flowOf(null),
        ) { texts, words, log, lessons, exercises ->
            LibraryContent(language, texts, words.toStatusMap(), log.map { it.textId }.toSet(), lessons, exercises)
        }
    }

    val uiState: StateFlow<LibraryUiState> = combine(content, section, level, hideRead) { content, sectionName, levelName, hideRead ->
        val sections = buildList {
            add(LibrarySection.TEXTS)
            add(LibrarySection.LESSONS)
            if (content.exercises != null) add(LibrarySection.PRONUNCIATION)
        }
        val level = CefrLevel.entries.find { it.name == levelName }
        val textItems = content.texts.map { text ->
            TextItem(
                id = text.id,
                title = text.title,
                level = text.level,
                kind = text.kind,
                minutes = text.minutes,
                comprehension = text.comprehension(content.statuses),
                isRead = text.id in content.readTexts,
            )
        }
        val inLevel = textItems.filter { level == null || it.level == level }.sortedWith(compareBy({ it.level }, { it.minutes }))
        LibraryUiState.Success(
            languageCode = content.languageCode,
            sections = sections,
            // The help may have been turned off since the section was chosen.
            section = sections.find { it.name == sectionName } ?: LibrarySection.TEXTS,
            levels = content.texts.map { it.level }.distinct().sorted(),
            level = level,
            hideRead = hideRead,
            hasReadTexts = inLevel.any { it.isRead },
            texts = inLevel.filter { !hideRead || !it.isRead },
            suggestedTextId = suggestNextText(content.texts, content.statuses, content.readTexts)?.id,
            lessons = content.lessons,
            exercises = content.exercises.orEmpty(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LibraryUiState.Loading,
    )

    fun onSectionSelected(section: LibrarySection) {
        savedStateHandle[SECTION_KEY] = section.name
    }

    /** `null` shows every level. */
    fun onLevelSelected(level: CefrLevel?) {
        savedStateHandle[LEVEL_KEY] = level?.name
    }

    fun onHideReadChange(hide: Boolean) {
        savedStateHandle[HIDE_READ_KEY] = hide
    }

    private data class LibraryContent(
        val languageCode: LanguageCode,
        val texts: List<ReadingText>,
        val statuses: Map<LemmaId, WordStatus>,
        val readTexts: Set<TextId>,
        val lessons: List<Lesson>,
        /** `null` when the pronunciation help is off. */
        val exercises: List<PronunciationExercise>?,
    )

    private companion object {
        const val SECTION_KEY = "section"
        const val LEVEL_KEY = "level"
        const val HIDE_READ_KEY = "hideRead"
    }
}
