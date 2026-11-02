package com.sazerat.languagelearning.core.data

import com.sazerat.languagelearning.core.data.repository.InMemoryPronunciationRepository
import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InMemoryRepositoriesTest {
    private val pronunciation = InMemoryPronunciationRepository()

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
}
