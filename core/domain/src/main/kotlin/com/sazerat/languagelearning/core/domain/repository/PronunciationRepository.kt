package com.sazerat.languagelearning.core.domain.repository

import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.PronunciationExercise
import kotlinx.coroutines.flow.Flow

public interface PronunciationRepository {
    /** Exercises of one language. */
    public fun observeExercises(languageCode: LanguageCode): Flow<List<PronunciationExercise>>

    /** Emits `null` when no exercise has this id. */
    public fun observeExercise(id: ExerciseId): Flow<PronunciationExercise?>
}
