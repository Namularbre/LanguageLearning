package com.sazerat.languagelearning.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

// Reads return Flows: Room emits again whenever a table they read changes, so the screens update
// on their own (spec §9.2: the UI observes Room).

@Dao
interface WordStatusDao {
    @Query("SELECT * FROM word_status WHERE languageCode = :languageCode")
    fun observe(languageCode: String): Flow<List<WordStatusEntity>>

    @Upsert
    suspend fun upsert(words: List<WordStatusEntity>)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM review_card WHERE languageCode = :languageCode ORDER BY due")
    fun observeCards(languageCode: String): Flow<List<ReviewCardEntity>>

    /** IGNORE: a lemma that already has a card keeps its schedule. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCard(card: ReviewCardEntity)

    @Upsert
    suspend fun upsertCard(card: ReviewCardEntity)

    @Insert
    suspend fun insertLog(entry: ReviewLogEntity)

    /** One transaction: the new schedule is never saved without its log entry, or the reverse. */
    @Transaction
    suspend fun saveReview(card: ReviewCardEntity, entry: ReviewLogEntity) {
        upsertCard(card)
        insertLog(entry)
    }

    @Query("SELECT * FROM review_log WHERE languageCode = :languageCode ORDER BY reviewedAt")
    fun observeLog(languageCode: String): Flow<List<ReviewLogEntity>>
}

@Dao
interface ReadingLogDao {
    @Query("SELECT * FROM reading_log WHERE languageCode = :languageCode ORDER BY finishedAt")
    fun observe(languageCode: String): Flow<List<ReadingLogEntity>>

    @Insert
    suspend fun insert(entry: ReadingLogEntity)
}
