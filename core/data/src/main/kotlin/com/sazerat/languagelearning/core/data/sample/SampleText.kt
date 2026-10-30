package com.sazerat.languagelearning.core.data.sample

import com.sazerat.languagelearning.core.domain.model.LessonExample
import com.sazerat.languagelearning.core.domain.model.LocalizedText

// Short helpers so the sample content reads like a list of texts.

/** A text written in the three UI languages. */
internal fun text(en: String, fr: String, ro: String): LocalizedText = LocalizedText.of(en = en, fr = fr, ro = ro)

/** The same text for every UI language, e.g. a minimal pair or an IPA symbol. */
internal fun same(text: String): LocalizedText = LocalizedText.of(en = text)

/** An example in the lesson's language, translated into the other UI languages. */
internal fun example(text: String, en: String? = null, fr: String? = null, ro: String? = null): LessonExample =
    LessonExample(text, LocalizedText.of(en = en, fr = fr, ro = ro))
