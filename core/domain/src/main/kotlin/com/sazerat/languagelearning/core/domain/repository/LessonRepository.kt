package com.sazerat.languagelearning.core.domain.repository

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.LessonId
import kotlinx.coroutines.flow.Flow

public interface LessonRepository {
    /** Lessons of one language, sorted by [Lesson.order]. */
    public fun observeLessons(languageCode: LanguageCode): Flow<List<Lesson>>

    /** Emits `null` when no lesson has this id. */
    public fun observeLesson(id: LessonId): Flow<Lesson?>
}
