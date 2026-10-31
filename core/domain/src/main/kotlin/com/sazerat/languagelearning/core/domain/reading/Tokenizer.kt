package com.sazerat.languagelearning.core.domain.reading

/**
 * Splits a sentence into words and the text between them, keeping everything: joining the pieces
 * gives the sentence back. Used when content is prepared, not on the learner's taps.
 *
 * A word is a run of letters. An apostrophe right after letters stays with them, so French elisions
 * give two words (spec §5.1): "l'homme" → "l'", "homme". A hyphen separates words: Romanian
 * "într-o" → "într", "-", "o".
 */
public object Tokenizer {
    private val word = Regex("""\p{L}+['’]?""")

    public fun split(sentence: String): List<Piece> {
        val pieces = mutableListOf<Piece>()
        var end = 0
        for (match in word.findAll(sentence)) {
            if (match.range.first > end) pieces += Piece(sentence.substring(end, match.range.first), isWord = false)
            pieces += Piece(match.value, isWord = true)
            end = match.range.last + 1
        }
        if (end < sentence.length) pieces += Piece(sentence.substring(end), isWord = false)
        return pieces
    }

    public data class Piece(val text: String, val isWord: Boolean)
}
