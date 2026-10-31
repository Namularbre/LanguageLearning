package com.sazerat.languagelearning.core.domain.model

/** Where the learner stands with a word (spec §5.2). A word with no status yet is [NEW]. */
public enum class WordStatus {
    /** Never met, or met but not touched. */
    NEW,

    /** Added to the reviews. */
    LEARNING,

    /** Known: said so, or read without being tapped. */
    KNOWN,
    ;

    /** Counts as understood when reading: comprehension and the Library badges. */
    public val isUnderstood: Boolean get() = this != NEW
}

/** The status of one word for the learner, with the time it was last changed (epoch milliseconds). */
public data class LearnerWord(
    val lemmaId: LemmaId,
    val status: WordStatus,
    val updatedAt: Long,
)

/** Status of each word the learner has a status for; missing words are [WordStatus.NEW]. */
public fun List<LearnerWord>.toStatusMap(): Map<LemmaId, WordStatus> = associate { it.lemmaId to it.status }
