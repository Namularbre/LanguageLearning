package com.sazerat.languagelearning.core.domain.model

/** The five pronunciation exercise kinds (spec §5.2). */
public enum class PronunciationExerciseType {
    MINIMAL_PAIR_LISTENING,
    MINIMAL_PAIR_SPEAKING,
    TARGETED_SENTENCE,
    SHADOWING,
    WORD_STRESS,
}

/** A word or sentence to work on, with its expected transcription (British SSBE for English). */
public data class PronunciationItem(
    val text: String,
    val ipa: String,
)

public data class PronunciationExercise(
    val id: ExerciseId,
    val languageCode: LanguageCode,
    val type: PronunciationExerciseType,
    val title: LocalizedText,
    /** The sound or phenomenon targeted, e.g. "/ɪ/ – /iː/". */
    val focus: LocalizedText,
    val instructions: LocalizedText,
    val items: List<PronunciationItem>,
)
