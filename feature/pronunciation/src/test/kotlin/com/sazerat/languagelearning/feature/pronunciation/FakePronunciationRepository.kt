package com.sazerat.languagelearning.feature.pronunciation

import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.PronunciationExercise
import com.sazerat.languagelearning.core.domain.repository.PronunciationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** Test double: the test controls exactly what the repository emits. */
class FakePronunciationRepository(exercises: List<PronunciationExercise> = emptyList()) : PronunciationRepository {
    val exercises = MutableStateFlow(exercises)

    override fun observeExercises(languageCode: LanguageCode): Flow<List<PronunciationExercise>> =
        exercises.map { all -> all.filter { it.languageCode == languageCode } }

    override fun observeExercise(id: ExerciseId): Flow<PronunciationExercise?> =
        exercises.map { all -> all.find { it.id == id } }
}
