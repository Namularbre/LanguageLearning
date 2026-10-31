package com.sazerat.languagelearning.core.domain.repository

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LearnerWord
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.WordStatus
import kotlinx.coroutines.flow.Flow

/** The texts of the Library (read-only content, spec §9.2). */
public interface TextRepository {
    public fun observeTexts(languageCode: LanguageCode): Flow<List<ReadingText>>

    /** Emits `null` when no text has this id. */
    public fun observeText(id: TextId): Flow<ReadingText?>
}

/** Base forms of the words used by the texts (read-only content). */
public interface LexiconRepository {
    /** Every lemma of a language, most frequent first. */
    public fun observeLemmas(languageCode: LanguageCode): Flow<List<Lemma>>
}

/** Where the learner stands with each word (spec §5.2). Written by the learner's actions. */
public interface WordStatusRepository {
    /** Words that have a status; the others are new. */
    public fun observeWords(languageCode: LanguageCode): Flow<List<LearnerWord>>

    /** Sets the status of these words, e.g. every untouched word of a text that was just read. */
    public suspend fun setStatus(languageCode: LanguageCode, lemmaIds: Collection<LemmaId>, status: WordStatus)
}

/** A text the learner finished reading. Append-only log (spec §9.8). */
public data class ReadingLogEntry(
    val textId: TextId,
    val minutes: Int,
    /** Epoch milliseconds. */
    val finishedAt: Long,
)

public interface ReadingLogRepository {
    public fun observeLog(languageCode: LanguageCode): Flow<List<ReadingLogEntry>>

    public suspend fun add(languageCode: LanguageCode, entry: ReadingLogEntry)
}
