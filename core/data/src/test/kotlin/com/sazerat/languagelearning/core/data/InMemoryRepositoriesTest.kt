package com.sazerat.languagelearning.core.data

import com.sazerat.languagelearning.core.data.repository.InMemoryLessonRepository
import com.sazerat.languagelearning.core.data.repository.InMemoryPronunciationRepository
import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LessonId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InMemoryRepositoriesTest {
    private val pronunciation = InMemoryPronunciationRepository()
    private val lessons = InMemoryLessonRepository()

    @Test
    fun `known exercise is found by id`() = runTest {
        val exercise = pronunciation.observeExercise(ExerciseId("en-ship-sheep")).first()
        assertEquals("ship / sheep", exercise?.title?.resolve("en"))
    }

    @Test
    fun `exercises are filtered by language`() = runTest {
        listOf("en-GB", "ro-RO", "fr-FR").map(::LanguageCode).forEach { language ->
            val exercises = pronunciation.observeExercises(language).first()
            assertTrue(exercises.isNotEmpty(), "No exercise for ${language.value}")
            assertTrue(exercises.all { it.languageCode == language })
        }
    }

    @Test
    fun `unknown exercise emits null`() = runTest {
        assertNull(pronunciation.observeExercise(ExerciseId("missing")).first())
    }

    @Test
    fun `lessons are filtered by language and sorted`() = runTest {
        assertEquals((1..6).toList(), lessons.observeLessons(LanguageCode("ro-RO")).first().map { it.order })
        assertEquals((1..6).toList(), lessons.observeLessons(LanguageCode("en-GB")).first().map { it.order })
        assertEquals((1..6).toList(), lessons.observeLessons(LanguageCode("fr-FR")).first().map { it.order })
        assertTrue(lessons.observeLessons(LanguageCode("xx-TEST")).first().isEmpty())
    }

    @Test
    fun `unknown lesson emits null`() = runTest {
        assertNull(lessons.observeLesson(LessonId("missing")).first())
    }
}
