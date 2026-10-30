package com.sazerat.languagelearning.core.data.sample

import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.PronunciationExercise
import com.sazerat.languagelearning.core.domain.model.PronunciationExerciseType
import com.sazerat.languagelearning.core.domain.model.PronunciationItem

/** Placeholder French pronunciation content for M1. Replaced by packs downloaded from the API in M2. */
internal object SampleFrenchPronunciation {
    private val french = LanguageCode("fr-FR")

    val all: List<PronunciationExercise> = listOf(
        PronunciationExercise(
            id = ExerciseId("fr-tu-tout"),
            languageCode = french,
            type = PronunciationExerciseType.MINIMAL_PAIR_LISTENING,
            title = same("tu / tout"),
            focus = same("/y/ – /u/"),
            instructions = text(
                en = "Listen and pick the word you hear. For /y/, say \"ee\" with rounded lips; " +
                    "/u/ is further back, like \"oo\".",
                fr = "Écoute et choisis le mot entendu. Pour /y/, dis « i » en arrondissant les lèvres ; " +
                    "/u/ se prononce plus en arrière.",
                ro = "Ascultă și alege cuvântul auzit. Pentru /y/, spune „i” cu buzele rotunjite; " +
                    "/u/ se pronunță mai în spate, ca „u”.",
            ),
            items = listOf(
                PronunciationItem("tu", "/ty/"),
                PronunciationItem("tout", "/tu/"),
                PronunciationItem("rue", "/ʁy/"),
                PronunciationItem("roue", "/ʁu/"),
                PronunciationItem("dessus", "/dəsy/"),
                PronunciationItem("dessous", "/dəsu/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("fr-vent-vin-vont"),
            languageCode = french,
            type = PronunciationExerciseType.MINIMAL_PAIR_SPEAKING,
            title = same("vent / vin / vont"),
            focus = same("/ɑ̃/ – /ɛ̃/ – /ɔ̃/"),
            instructions = text(
                en = "Say each set. Air goes through the nose and the mouth; no \"n\" is pronounced at the end.",
                fr = "Prononce chaque série. L'air passe par le nez et la bouche ; on ne prononce pas de « n » à la fin.",
                ro = "Pronunță fiecare serie. Aerul trece pe nas și pe gură; nu se pronunță niciun „n” la final.",
            ),
            items = listOf(
                PronunciationItem("vent", "/vɑ̃/"),
                PronunciationItem("vin", "/vɛ̃/"),
                PronunciationItem("vont", "/vɔ̃/"),
                PronunciationItem("lent", "/lɑ̃/"),
                PronunciationItem("lin", "/lɛ̃/"),
                PronunciationItem("long", "/lɔ̃/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("fr-r"),
            languageCode = french,
            type = PronunciationExerciseType.MINIMAL_PAIR_SPEAKING,
            title = text(en = "The French r", fr = "Le r français", ro = "R-ul francez"),
            focus = same("/ʁ/"),
            instructions = text(
                en = "The r is made at the back of the throat, without rolling the tip of the tongue.",
                fr = "Le r se forme au fond de la gorge, sans rouler la pointe de la langue.",
                ro = "R-ul se formează în fundul gâtului, fără să vibreze vârful limbii, spre deosebire de r-ul românesc.",
            ),
            items = listOf(
                PronunciationItem("rouge", "/ʁuʒ/"),
                PronunciationItem("Paris", "/paʁi/"),
                PronunciationItem("très", "/tʁɛ/"),
                PronunciationItem("merci", "/mɛʁsi/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("fr-photographe-stress"),
            languageCode = french,
            type = PronunciationExerciseType.WORD_STRESS,
            title = same("photographe / photographie"),
            focus = text(
                en = "Stress on the last syllable",
                fr = "Accent sur la dernière syllabe",
                ro = "Accent pe ultima silabă",
            ),
            instructions = text(
                en = "In French, stress always falls on the last pronounced syllable of the word or word group: " +
                    "it moves when the word gets longer, unlike in English.",
                fr = "En français, l'accent tombe toujours sur la dernière syllabe prononcée du mot ou du groupe : " +
                    "il se déplace quand le mot s'allonge, contrairement à l'anglais.",
                ro = "În franceză, accentul cade mereu pe ultima silabă pronunțată a cuvântului sau a grupului: " +
                    "se mută când cuvântul se lungește, spre deosebire de engleză.",
            ),
            items = listOf(
                PronunciationItem("photographe", "/fɔtɔˈɡʁaf/"),
                PronunciationItem("photographie", "/fɔtɔɡʁaˈfi/"),
                PronunciationItem("photographique", "/fɔtɔɡʁaˈfik/"),
            ),
        ),
        PronunciationExercise(
            id = ExerciseId("fr-liaison"),
            languageCode = french,
            type = PronunciationExerciseType.TARGETED_SENTENCE,
            title = text(en = "Liaison", fr = "Liaisons", ro = "Legătura (liaison)"),
            focus = same("les‿amis, un‿enfant"),
            instructions = text(
                en = "A silent final consonant is pronounced before a vowel: s becomes /z/, t and n are heard.",
                fr = "Une consonne finale muette se prononce devant une voyelle : le s devient /z/, le t et le n s'entendent.",
                ro = "O consoană finală mută se pronunță înaintea unei vocale: s devine /z/, t și n se aud.",
            ),
            items = listOf(
                PronunciationItem("Les amis arrivent.", "/lez‿ami aʁiv/"),
                PronunciationItem("Vous avez un enfant ?", "/vuz‿ave œ̃n‿ɑ̃fɑ̃/"),
                PronunciationItem("C'est un petit ami.", "/sɛt‿œ̃ pətit‿ami/"),
            ),
        ),
    )
}
