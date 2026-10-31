package com.sazerat.languagelearning.feature.srs

import com.sazerat.languagelearning.core.domain.model.Gender
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import com.sazerat.languagelearning.core.domain.model.MILLIS_PER_DAY
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech
import com.sazerat.languagelearning.core.domain.model.Rating
import com.sazerat.languagelearning.core.domain.model.Sentence
import com.sazerat.languagelearning.core.domain.model.Token
import com.sazerat.languagelearning.core.domain.review.ExerciseKind
import com.sazerat.languagelearning.feature.srs.review.ExerciseUiState
import com.sazerat.languagelearning.feature.srs.review.ReviewCardUiState
import com.sazerat.languagelearning.feature.srs.review.ReviewUiState

// Data for @Preview only.

internal val previewReviewing = ReviewUiState.Reviewing(
    card = ReviewCardUiState(
        lemma = Lemma(
            LemmaId("ro:piață"),
            LanguageCode("ro-RO"),
            "piață",
            PartOfSpeech.NOUN,
            Gender.FEMININE,
            meaning = LocalizedText.of(en = "market"),
            frequencyRank = 4,
        ),
        sentence = Sentence(
            tokens = listOf(Token("Merg"), Token(" "), Token("la"), Token(" "), Token("piață"), Token(".")),
            translation = LocalizedText.of(en = "I go to the market."),
        ),
        tokenIndex = 4,
        revealed = false,
        intervals = mapOf(
            Rating.AGAIN to 600_000L,
            Rating.HARD to MILLIS_PER_DAY,
            Rating.GOOD to 4 * MILLIS_PER_DAY,
            Rating.EASY to 14 * MILLIS_PER_DAY,
        ),
        showPronunciation = false,
    ),
    position = 2,
    total = 8,
)

private fun previewNoun(word: String, meaning: String, rank: Int) = Lemma(
    LemmaId("ro:$word"),
    LanguageCode("ro-RO"),
    word,
    PartOfSpeech.NOUN,
    Gender.FEMININE,
    meaning = LocalizedText.of(en = meaning),
    frequencyRank = rank,
)

internal val previewChoices = previewReviewing.copy(
    card = previewReviewing.card.copy(
        exercise = ExerciseUiState(
            kind = ExerciseKind.FILL_BLANK,
            choices = listOf(
                previewNoun("casă", "house", 10),
                previewReviewing.card.lemma,
                previewNoun("gară", "station", 30),
                previewNoun("școală", "school", 20),
            ),
            selected = null,
        ),
    ),
)
