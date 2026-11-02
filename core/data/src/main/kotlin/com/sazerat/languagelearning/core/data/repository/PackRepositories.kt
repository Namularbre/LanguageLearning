package com.sazerat.languagelearning.core.data.repository

import com.sazerat.languagelearning.core.data.pack.ContentPack
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.repository.LessonRepository
import com.sazerat.languagelearning.core.domain.repository.LexiconRepository
import com.sazerat.languagelearning.core.domain.repository.TextRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Repositories serving the content of language packs. They take a Flow of packs, so the source can
// change without touching them: bundled packs today (`BundledPacks`), packs stored in Room in M2,
// a hand-made list in tests.

/** Texts of the Library, from the packs. */
class PackTextRepository(private val packs: Flow<List<ContentPack>>) : TextRepository {
    private val texts: Flow<List<ReadingText>> = packs.map { all -> all.flatMap { it.toTexts() } }

    override fun observeTexts(languageCode: LanguageCode): Flow<List<ReadingText>> =
        texts.map { all -> all.filter { it.languageCode == languageCode } }

    override fun observeText(id: TextId): Flow<ReadingText?> = texts.map { all -> all.find { it.id == id } }
}

/** Base forms of the words used by the texts, from the packs. */
class PackLexiconRepository(private val packs: Flow<List<ContentPack>>) : LexiconRepository {
    override fun observeLemmas(languageCode: LanguageCode): Flow<List<Lemma>> = packs.map { all ->
        all.filter { it.languageCode == languageCode.value }.flatMap { it.toLemmas() }.sortedBy { it.frequencyRank }
    }
}

/** "How the language works" lessons, from the packs. */
class PackLessonRepository(private val packs: Flow<List<ContentPack>>) : LessonRepository {
    private val lessons: Flow<List<Lesson>> = packs.map { all -> all.flatMap { it.toLessons() } }

    override fun observeLessons(languageCode: LanguageCode): Flow<List<Lesson>> =
        lessons.map { all -> all.filter { it.languageCode == languageCode }.sortedBy { it.order } }

    override fun observeLesson(id: LessonId): Flow<Lesson?> = lessons.map { all -> all.find { it.id == id } }
}
