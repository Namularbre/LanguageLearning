package com.sazerat.languagelearning.core.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sazerat.languagelearning.core.domain.model.Rating
import com.sazerat.languagelearning.core.domain.model.WordStatus

// Tables of the learner's progress (spec §9.8). Content (texts, lemmas) is not stored here yet:
// it ships in the APK until the M2 content packs. Enums are stored by name (Room does it).

/** Status of a word for the learner. Lemma ids already contain the language ("ro:casă"). */
@Entity(tableName = "word_status", indices = [Index("languageCode")])
data class WordStatusEntity(
    @PrimaryKey val lemmaId: String,
    val languageCode: String,
    val status: WordStatus,
    val updatedAt: Long,
)

/** A review card and its FSRS schedule: one per lemma. */
@Entity(tableName = "review_card", indices = [Index("languageCode")])
data class ReviewCardEntity(
    @PrimaryKey val lemmaId: String,
    val languageCode: String,
    val textId: String,
    val sentenceIndex: Int,
    val tokenIndex: Int,
    /** Both `null` until the first review. */
    val stability: Double?,
    val difficulty: Double?,
    val due: Long,
    val reps: Int,
    val lapses: Int,
    val lastReview: Long?,
    val createdAt: Long,
)

/** Append-only: one row per answer. */
@Entity(tableName = "review_log", indices = [Index("languageCode")])
data class ReviewLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lemmaId: String,
    val languageCode: String,
    val rating: Rating,
    val reviewedAt: Long,
)

/** Append-only: one row per text finished. */
@Entity(tableName = "reading_log", indices = [Index("languageCode")])
data class ReadingLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val textId: String,
    val languageCode: String,
    val minutes: Int,
    val finishedAt: Long,
)
