package com.sazerat.languagelearning.feature.pronunciation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import com.sazerat.languagelearning.core.domain.model.PronunciationExercise
import com.sazerat.languagelearning.core.domain.model.PronunciationExerciseType
import com.sazerat.languagelearning.core.domain.model.PronunciationItem

@Composable
internal fun PronunciationExerciseType.label(): String = stringResource(
    when (this) {
        PronunciationExerciseType.MINIMAL_PAIR_LISTENING -> R.string.feature_pronunciation_type_listening
        PronunciationExerciseType.MINIMAL_PAIR_SPEAKING -> R.string.feature_pronunciation_type_speaking
        PronunciationExerciseType.TARGETED_SENTENCE -> R.string.feature_pronunciation_type_sentence
        PronunciationExerciseType.SHADOWING -> R.string.feature_pronunciation_type_shadowing
        PronunciationExerciseType.WORD_STRESS -> R.string.feature_pronunciation_type_word_stress
    },
)

/** Data for @Preview only. */
internal val previewExercises = listOf(
    PronunciationExercise(
        id = ExerciseId("preview-ship-sheep"),
        languageCode = LanguageCode("en-GB"),
        type = PronunciationExerciseType.MINIMAL_PAIR_LISTENING,
        title = LocalizedText.of(en = "ship / sheep"),
        focus = LocalizedText.of(en = "/ɪ/ – /iː/"),
        instructions = LocalizedText.of(en = "Listen and pick the word you hear."),
        items = listOf(PronunciationItem("ship", "/ʃɪp/"), PronunciationItem("sheep", "/ʃiːp/")),
    ),
    PronunciationExercise(
        id = ExerciseId("preview-think-sink"),
        languageCode = LanguageCode("en-GB"),
        type = PronunciationExerciseType.MINIMAL_PAIR_SPEAKING,
        title = LocalizedText.of(en = "think / sink"),
        focus = LocalizedText.of(en = "/θ/ – /s/"),
        instructions = LocalizedText.of(en = "Tongue between the teeth for /θ/."),
        items = listOf(PronunciationItem("think", "/θɪŋk/"), PronunciationItem("sink", "/sɪŋk/")),
    ),
)
