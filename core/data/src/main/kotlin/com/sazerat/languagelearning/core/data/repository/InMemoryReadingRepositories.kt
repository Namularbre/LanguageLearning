package com.sazerat.languagelearning.core.data.repository

import com.sazerat.languagelearning.core.data.sample.SampleEnglishReading
import com.sazerat.languagelearning.core.data.sample.SampleFrenchReading
import com.sazerat.languagelearning.core.data.sample.SampleRomanianReading
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.repository.LexiconRepository
import com.sazerat.languagelearning.core.domain.repository.TextRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** Sample texts shipped in the APK. M2 replaces them with language packs stored in Room (spec §9.2). */
class InMemoryTextRepository(
    texts: List<ReadingText> = SampleRomanianReading.texts + SampleEnglishReading.texts + SampleFrenchReading.texts,
) : TextRepository {
    private val texts = MutableStateFlow(texts)

    override fun observeTexts(languageCode: LanguageCode): Flow<List<ReadingText>> =
        texts.map { all -> all.filter { it.languageCode == languageCode } }

    override fun observeText(id: TextId): Flow<ReadingText?> = texts.map { all -> all.find { it.id == id } }
}

class InMemoryLexiconRepository(
    lemmas: List<Lemma> = SampleRomanianReading.lemmas + SampleEnglishReading.lemmas + SampleFrenchReading.lemmas,
) : LexiconRepository {
    private val lemmas = MutableStateFlow(lemmas)

    override fun observeLemmas(languageCode: LanguageCode): Flow<List<Lemma>> =
        lemmas.map { all -> all.filter { it.languageCode == languageCode }.sortedBy { it.frequencyRank } }
}
