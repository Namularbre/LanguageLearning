package com.sazerat.languagelearning.core.data.repository

import com.sazerat.languagelearning.core.data.sample.SamplePronunciationExercises
import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.PronunciationExercise
import com.sazerat.languagelearning.core.domain.repository.PronunciationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** M1 stand-in serving sample data. M2 replaces it with a Room-backed repository. */
class InMemoryPronunciationRepository(
    exercises: List<PronunciationExercise> = SamplePronunciationExercises.all,
) : PronunciationRepository {
    private val exercises = MutableStateFlow(exercises)

    override fun observeExercises(languageCode: LanguageCode): Flow<List<PronunciationExercise>> =
        exercises.map { all -> all.filter { it.languageCode == languageCode } }

    override fun observeExercise(id: ExerciseId): Flow<PronunciationExercise?> =
        exercises.map { all -> all.find { it.id == id } }
}
