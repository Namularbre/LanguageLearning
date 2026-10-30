package com.sazerat.languagelearning.core.data.sample

import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.PronunciationExercise
import com.sazerat.languagelearning.core.domain.model.PronunciationExerciseType
import com.sazerat.languagelearning.core.domain.model.PronunciationItem

/** Placeholder pronunciation content for M1. Replaced by packs downloaded from the API in M2. */
internal object SamplePronunciationExercises {
    private val englishUk = LanguageCode("en-GB")

    private val english: List<PronunciationExercise> = listOf(
        PronunciationExercise(
            id = ExerciseId("en-ship-sheep"),
            languageCode = englishUk,
            type = PronunciationExerciseType.MINIMAL_PAIR_LISTENING,
            title = same("ship / sheep"),
            focus = same("/ɪ/ – /iː/"),
            instructions = text(
                en = "Listen and pick the word you hear. /ɪ/ is short and relaxed, /iː/ is long and tense.",
                fr = "Écoute et choisis le mot entendu. /ɪ/ est bref et relâché, /iː/ est long et tendu.",
                ro = "Ascultă și alege cuvântul auzit. /ɪ/ este scurt și relaxat, /iː/ este lung și încordat.",
            ),
            items = listOf(
                PronunciationItem("ship", "/ʃɪp/"),
                PronunciationItem("sheep", "/ʃiːp/"),
                PronunciationItem("live", "/lɪv/"),
                PronunciationItem("leave", "/liːv/"),
                PronunciationItem("bit", "/bɪt/"),
                PronunciationItem("beat", "/biːt/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("en-full-fool"),
            languageCode = englishUk,
            type = PronunciationExerciseType.MINIMAL_PAIR_LISTENING,
            title = same("full / fool"),
            focus = same("/ʊ/ – /uː/"),
            instructions = text(
                en = "Listen and pick the word you hear. /ʊ/ is short, with slightly rounded lips; /uː/ is long.",
                fr = "Écoute et choisis le mot entendu. /ʊ/ est bref, lèvres peu arrondies ; /uː/ est long.",
                ro = "Ascultă și alege cuvântul auzit. /ʊ/ este scurt, cu buzele puțin rotunjite; /uː/ este lung.",
            ),
            items = listOf(
                PronunciationItem("full", "/fʊl/"),
                PronunciationItem("fool", "/fuːl/"),
                PronunciationItem("pull", "/pʊl/"),
                PronunciationItem("pool", "/puːl/"),
                PronunciationItem("look", "/lʊk/"),
                PronunciationItem("Luke", "/luːk/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("en-cat-cut-cart"),
            languageCode = englishUk,
            type = PronunciationExerciseType.MINIMAL_PAIR_SPEAKING,
            title = same("cat / cut / cart"),
            focus = same("/æ/ – /ʌ/ – /ɑː/"),
            instructions = text(
                en = "Say each set. /æ/ with the mouth wide open, /ʌ/ short and central, /ɑː/ long and at the back.",
                fr = "Prononce chaque série. /æ/ bouche très ouverte, /ʌ/ bref et central, /ɑː/ long et en arrière.",
                ro = "Pronunță fiecare serie. /æ/ cu gura foarte deschisă, /ʌ/ scurt și central, /ɑː/ lung și în spate.",
            ),
            items = listOf(
                PronunciationItem("cat", "/kæt/"),
                PronunciationItem("cut", "/kʌt/"),
                PronunciationItem("cart", "/kɑːt/"),
                PronunciationItem("hat", "/hæt/"),
                PronunciationItem("hut", "/hʌt/"),
                PronunciationItem("heart", "/hɑːt/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("en-cot-caught"),
            languageCode = englishUk,
            type = PronunciationExerciseType.MINIMAL_PAIR_SPEAKING,
            title = same("cot / caught"),
            focus = same("/ɒ/ – /ɔː/"),
            instructions = text(
                en = "Say each pair. /ɒ/ is short and open, /ɔː/ is long with rounded lips.",
                fr = "Prononce chaque paire. /ɒ/ est bref et ouvert, /ɔː/ est long avec les lèvres arrondies.",
                ro = "Pronunță fiecare pereche. /ɒ/ este scurt și deschis, /ɔː/ este lung, cu buzele rotunjite.",
            ),
            items = listOf(
                PronunciationItem("cot", "/kɒt/"),
                PronunciationItem("caught", "/kɔːt/"),
                PronunciationItem("pot", "/pɒt/"),
                PronunciationItem("port", "/pɔːt/"),
                PronunciationItem("don", "/dɒn/"),
                PronunciationItem("dawn", "/dɔːn/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("en-think-sink"),
            languageCode = englishUk,
            type = PronunciationExerciseType.MINIMAL_PAIR_SPEAKING,
            title = same("think / sink"),
            focus = same("/θ/ – /s/"),
            instructions = text(
                en = "For /θ/, put the tip of your tongue between your teeth and blow, without voice.",
                fr = "Pour /θ/, place la pointe de la langue entre les dents et souffle, sans voix.",
                ro = "Pentru /θ/, pune vârful limbii între dinți și suflă, fără voce.",
            ),
            items = listOf(
                PronunciationItem("think", "/θɪŋk/"),
                PronunciationItem("sink", "/sɪŋk/"),
                PronunciationItem("thick", "/θɪk/"),
                PronunciationItem("sick", "/sɪk/"),
                PronunciationItem("mouth", "/maʊθ/"),
                PronunciationItem("mouse", "/maʊs/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("en-then-den"),
            languageCode = englishUk,
            type = PronunciationExerciseType.MINIMAL_PAIR_SPEAKING,
            title = same("then / den"),
            focus = same("/ð/ – /d/"),
            instructions = text(
                en = "/ð/ is made like /θ/, but voiced: your vocal cords vibrate.",
                fr = "/ð/ se place comme /θ/, mais avec la voix : les cordes vocales vibrent.",
                ro = "/ð/ se formează ca /θ/, dar cu voce: coardele vocale vibrează.",
            ),
            items = listOf(
                PronunciationItem("then", "/ðen/"),
                PronunciationItem("den", "/den/"),
                PronunciationItem("though", "/ðəʊ/"),
                PronunciationItem("dough", "/dəʊ/"),
                PronunciationItem("they", "/ðeɪ/"),
                PronunciationItem("day", "/deɪ/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("en-hair-air"),
            languageCode = englishUk,
            type = PronunciationExerciseType.MINIMAL_PAIR_LISTENING,
            title = same("hair / air"),
            focus = text(en = "Aspirated /h/", fr = "/h/ aspiré", ro = "/h/ aspirat"),
            instructions = text(
                en = "The English /h/ is an audible breath. Without it, \"hair\" becomes \"air\".",
                fr = "Le /h/ anglais est un souffle audible. Sans lui, « hair » devient « air ».",
                ro = "/h/ englezesc este o expirație audibilă. Fără el, „hair” devine „air”.",
            ),
            items = listOf(
                PronunciationItem("hair", "/heə/"),
                PronunciationItem("air", "/eə/"),
                PronunciationItem("heat", "/hiːt/"),
                PronunciationItem("eat", "/iːt/"),
                PronunciationItem("hold", "/həʊld/"),
                PronunciationItem("old", "/əʊld/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("en-photograph-stress"),
            languageCode = englishUk,
            type = PronunciationExerciseType.WORD_STRESS,
            title = same("PHOtograph / phoTOgraphy"),
            focus = text(en = "Stress shift", fr = "Déplacement de l'accent", ro = "Deplasarea accentului"),
            instructions = text(
                en = "The stressed syllable moves with the suffix; unstressed vowels often become /ə/.",
                fr = "La syllabe accentuée se déplace selon le suffixe ; les voyelles non accentuées deviennent souvent /ə/.",
                ro = "Silaba accentuată se mută în funcție de sufix; vocalele neaccentuate devin adesea /ə/.",
            ),
            items = listOf(
                PronunciationItem("photograph", "/ˈfəʊtəɡrɑːf/"),
                PronunciationItem("photography", "/fəˈtɒɡrəfi/"),
                PronunciationItem("photographic", "/ˌfəʊtəˈɡræfɪk/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("en-weak-forms"),
            languageCode = englishUk,
            type = PronunciationExerciseType.TARGETED_SENTENCE,
            title = text(en = "Weak forms", fr = "Formes faibles", ro = "Forme slabe"),
            focus = same("can /kən/, of /əv/"),
            instructions = text(
                en = "In a sentence, unstressed grammar words are reduced. Don't pronounce them in full.",
                fr = "Dans une phrase, les mots grammaticaux non accentués se réduisent. Ne les prononce pas en entier.",
                ro = "Într-o propoziție, cuvintele gramaticale neaccentuate se reduc. Nu le pronunța complet.",
            ),
            items = listOf(
                PronunciationItem("A cup of tea.", "/ə ˈkʌp əv ˈtiː/"),
                PronunciationItem("I can do it.", "/aɪ kən ˈduː ɪt/"),
                PronunciationItem("Some of them are here.", "/ˈsʌm əv ðəm ə ˈhɪə/"),
            ),
        ),
    )

    val all: List<PronunciationExercise> =
        english + SampleRomanianPronunciation.all + SampleFrenchPronunciation.all
}
