package com.sazerat.languagelearning.core.data.sample

import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.PronunciationExercise
import com.sazerat.languagelearning.core.domain.model.PronunciationExerciseType
import com.sazerat.languagelearning.core.domain.model.PronunciationItem

/** Placeholder Romanian pronunciation content for M1. Replaced by packs downloaded from the API in M2. */
internal object SampleRomanianPronunciation {
    private val romanian = LanguageCode("ro-RO")

    val all: List<PronunciationExercise> = listOf(
        PronunciationExercise(
            id = ExerciseId("ro-casa-casa"),
            languageCode = romanian,
            type = PronunciationExerciseType.MINIMAL_PAIR_LISTENING,
            title = same("casă / casa"),
            focus = same("/ə/ – /a/"),
            instructions = text(
                en = "Listen and pick the word you hear. ă is a neutral vowel, like the a in \"about\"; " +
                    "a final -a is the definite article: casă \"a house\", casa \"the house\".",
                fr = "Écoute et choisis le mot entendu. ă est un « e » neutre comme dans « le » ; " +
                    "le -a final est l'article défini : casă « une maison », casa « la maison ».",
                ro = "Ascultă și alege cuvântul auzit. Atenție la final: ă în formele nearticulate (casă), " +
                    "a în formele cu articol hotărât (casa).",
            ),
            items = listOf(
                PronunciationItem("casă", "/ˈkasə/"),
                PronunciationItem("casa", "/ˈkasa/"),
                PronunciationItem("fată", "/ˈfatə/"),
                PronunciationItem("fata", "/ˈfata/"),
                PronunciationItem("masă", "/ˈmasə/"),
                PronunciationItem("masa", "/ˈmasa/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("ro-var-var"),
            languageCode = romanian,
            type = PronunciationExerciseType.MINIMAL_PAIR_SPEAKING,
            title = same("văr / vâr"),
            focus = same("/ə/ – /ɨ/"),
            instructions = text(
                en = "Say each pair. For â / î, keep your tongue in the middle of the mouth, " +
                    "higher than for ă, with unrounded lips.",
                fr = "Prononce chaque paire. Pour â / î, garde la langue au milieu de la bouche, " +
                    "plus haute que pour ă, lèvres non arrondies.",
                ro = "Pronunță fiecare pereche. Pentru â / î, limba stă în mijlocul gurii, " +
                    "mai sus decât pentru ă, cu buzele nerotunjite.",
            ),
            items = listOf(
                PronunciationItem("văr", "/vər/"),
                PronunciationItem("vâr", "/vɨr/"),
                PronunciationItem("răul", "/ˈrəul/"),
                PronunciationItem("râul", "/ˈrɨul/"),
                PronunciationItem("pâine", "/ˈpɨjne/"),
                PronunciationItem("mâine", "/ˈmɨjne/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("ro-cos-cos"),
            languageCode = romanian,
            type = PronunciationExerciseType.MINIMAL_PAIR_SPEAKING,
            title = same("coș / cos"),
            focus = same("/ʃ/ – /s/, /t͡s/"),
            instructions = text(
                en = "ș sounds like English \"sh\", ț like \"ts\". A comma under the letter changes the word.",
                fr = "ș se prononce « ch », ț se prononce « ts ». Une virgule sous la lettre change le mot.",
                ro = "ș se pronunță /ʃ/, ț se pronunță /t͡s/. Virgula de sub literă schimbă cuvântul.",
            ),
            items = listOf(
                PronunciationItem("coș", "/koʃ/"),
                PronunciationItem("cos", "/kos/"),
                PronunciationItem("pași", "/paʃʲ/"),
                PronunciationItem("pați", "/pat͡sʲ/"),
                PronunciationItem("țară", "/ˈt͡sarə/"),
                PronunciationItem("școală", "/ˈʃko̯alə/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("ro-ce-che"),
            languageCode = romanian,
            type = PronunciationExerciseType.MINIMAL_PAIR_SPEAKING,
            title = same("cer / cheie"),
            focus = same("/t͡ʃ/ – /k/, /d͡ʒ/ – /ɡ/"),
            instructions = text(
                en = "Before e and i, c is read like the ch in \"church\" and g like the j in \"jam\". " +
                    "An h in between makes the sound hard: che, chi, ghe, ghi.",
                fr = "Devant e et i, c se lit « tch » et g « dj ». Un h intercalé rend le son dur : che, chi, ghe, ghi.",
                ro = "Înainte de e și i, c se citește /t͡ʃ/, iar g se citește /d͡ʒ/. " +
                    "Un h intercalat face sunetul dur: che, chi, ghe, ghi.",
            ),
            items = listOf(
                PronunciationItem("cer", "/t͡ʃer/"),
                PronunciationItem("cheie", "/ˈkeje/"),
                PronunciationItem("ger", "/d͡ʒer/"),
                PronunciationItem("ghete", "/ˈɡete/"),
                PronunciationItem("cinci", "/t͡ʃint͡ʃʲ/"),
                PronunciationItem("ochi", "/okʲ/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("ro-greetings"),
            languageCode = romanian,
            type = PronunciationExerciseType.TARGETED_SENTENCE,
            title = text(en = "Greetings", fr = "Salutations", ro = "Saluturi"),
            focus = text(
                en = "Diphthongs ea, oa; initial e",
                fr = "Diphtongues ea, oa ; e initial",
                ro = "Diftongii ea, oa; e inițial",
            ),
            instructions = text(
                en = "ea and oa form a single syllable. In eu and este, the initial e is pronounced \"ye\".",
                fr = "ea et oa forment une seule syllabe. Dans eu et este, le e initial se prononce « ié ».",
                ro = "ea și oa formează o singură silabă. În eu și este, e inițial se pronunță „ie”.",
            ),
            items = listOf(
                PronunciationItem("Bună dimineața!", "/ˈbunə dimiˈne̯at͡sa/"),
                PronunciationItem("Bună seara!", "/ˈbunə ˈse̯ara/"),
                PronunciationItem("Noapte bună!", "/ˈno̯apte ˈbunə/"),
                PronunciationItem("Eu sunt student.", "/je̯w sunt stuˈdent/"),
            ),
        ),
    )
}
