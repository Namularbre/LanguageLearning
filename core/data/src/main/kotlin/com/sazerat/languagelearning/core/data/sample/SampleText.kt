package com.sazerat.languagelearning.core.data.sample

import com.sazerat.languagelearning.core.domain.model.LocalizedText

// Short helpers so the sample pronunciation exercises read like a list. Texts, lexicon and lessons
// are language packs instead (`assets/packs/`, see `pack/`).

/** A text written in the three UI languages. */
internal fun text(en: String, fr: String, ro: String): LocalizedText = LocalizedText.of(en = en, fr = fr, ro = ro)

/** The same text for every UI language, e.g. a minimal pair or an IPA symbol. */
internal fun same(text: String): LocalizedText = LocalizedText.of(en = text)
