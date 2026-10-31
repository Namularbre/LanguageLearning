package com.sazerat.languagelearning.core.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WordStatusEntity::class, ReviewCardEntity::class, ReviewLogEntity::class, ReadingLogEntity::class],
    version = 1,
)
abstract class LanguageLearningDatabase : RoomDatabase() {
    abstract fun wordStatusDao(): WordStatusDao

    abstract fun reviewDao(): ReviewDao

    abstract fun readingLogDao(): ReadingLogDao

    companion object {
        fun create(context: Context): LanguageLearningDatabase =
            Room.databaseBuilder(context, LanguageLearningDatabase::class.java, "language-learning.db").build()
    }
}
