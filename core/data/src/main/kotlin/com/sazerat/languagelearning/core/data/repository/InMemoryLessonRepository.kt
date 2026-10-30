package com.sazerat.languagelearning.core.data.repository

import com.sazerat.languagelearning.core.data.sample.SampleEnglishLessons
import com.sazerat.languagelearning.core.data.sample.SampleFrenchLessons
import com.sazerat.languagelearning.core.data.sample.SampleRomanianLessons
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** M1 stand-in serving sample data. M2 replaces it with a Room-backed repository. */
class InMemoryLessonRepository(
    lessons: List<Lesson> = SampleEnglishLessons.all + SampleRomanianLessons.all + SampleFrenchLessons.all,
) : LessonRepository {
    private val lessons = MutableStateFlow(lessons)

    override fun observeLessons(languageCode: LanguageCode): Flow<List<Lesson>> =
        lessons.map { all -> all.filter { it.languageCode == languageCode }.sortedBy { it.order } }

    override fun observeLesson(id: LessonId): Flow<Lesson?> =
        lessons.map { all -> all.find { it.id == id } }
}
