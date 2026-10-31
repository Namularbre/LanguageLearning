package com.sazerat.languagelearning.core.data.sample

import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.Gender.FEMININE
import com.sazerat.languagelearning.core.domain.model.Gender.MASCULINE
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.ADJECTIVE
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.ADVERB
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.ARTICLE
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.CONJUNCTION
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.INTERJECTION
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.NOUN
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.NUMERAL
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.PREPOSITION
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.PRONOUN
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.VERB
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.TextKind

/** French texts of the Library and the words they use. */
internal object SampleFrenchReading {
    private val presentHeShe = text("present, il/elle", "présent, il/elle", "prezent, el/ea")
    private val plural = text("plural", "pluriel", "plural")
    private val feminine = text("feminine", "féminin", "feminin")
    private val elided = text("elided before a vowel", "élidé devant une voyelle", "elidat înaintea unei vocale")
    private val nasalNote = text(
        "Nasal vowel: the n is not pronounced, the air goes through the nose.",
        "Voyelle nasale : on ne prononce pas le n.",
        "Vocală nazală: n nu se pronunță, aerul trece pe nas.",
    )
    private val frenchRNote = text(
        "The French r is made at the back of the throat.",
        "Le r français se prononce au fond de la gorge.",
        "r-ul francez se pronunță în fundul gâtului.",
    )

    private val lexicon = Lexicon(LanguageCode("fr-FR")).apply {
        names("Paul", "Lyon")

        word("le", ARTICLE, 1, meaning(en = "the", ro = "articolul hotărât"), ipa = "lə", forms = mapOf("la" to feminine, "l'" to elided))
        word(
            "de", PREPOSITION, 2, meaning(en = "of, from", ro = "de, din"), ipa = "də",
            forms = mapOf("du" to text("de + le: some (partitive)", "de + le : article partitif", "de + le: articol partitiv")),
        )
        word("un", ARTICLE, 3, meaning(en = "a, an", ro = "un, o"), ipa = "œ̃", note = nasalNote, forms = mapOf("des" to text("plural: some", "pluriel", "plural: niște")))
        word("à", PREPOSITION, 4, meaning(en = "to, at", ro = "la"), ipa = "a", forms = mapOf("au" to text("à + le", "à + le", "à + le")))
        word("et", CONJUNCTION, 5, meaning(en = "and", ro = "și"), ipa = "e")
        word("il", PRONOUN, 6, meaning(en = "he, it", ro = "el"), ipa = "il")
        word("vous", PRONOUN, 7, meaning(en = "you (plural or polite)", ro = "voi, dumneavoastră"), ipa = "vu")
        word("lui", PRONOUN, 8, meaning(en = "(to) him, (to) her", ro = "lui, ei"), ipa = "lɥi")
        word("ce", ADJECTIVE, 9, meaning(en = "this, that", ro = "acest, această"), ipa = "sə")
        word(
            "si", CONJUNCTION, 10, meaning(en = "if", ro = "dacă"),
            other = meaning(en = "s'il vous plaît = please", ro = "s'il vous plaît = vă rog"), ipa = "si", forms = mapOf("s'" to elided),
        )
        word("aller", VERB, 11, meaning(en = "to go", ro = "a merge"), ipa = "ale", forms = mapOf("va" to presentHeShe))
        word("partir", VERB, 12, meaning(en = "to leave", ro = "a pleca"), ipa = "paʁtiʁ", note = frenchRNote, forms = mapOf("part" to presentHeShe))
        word("acheter", VERB, 13, meaning(en = "to buy", ro = "a cumpăra"), ipa = "aʃte", forms = mapOf("achète" to presentHeShe))
        word("sourire", VERB, 14, meaning(en = "to smile", ro = "a zâmbi"), ipa = "suʁiʁ", note = frenchRNote, forms = mapOf("sourit" to presentHeShe))
        word(
            "rentrer", VERB, 15, meaning(en = "to go back home", ro = "a se întoarce acasă"),
            ipa = "ʁɑ̃tʁe", note = nasalNote, forms = mapOf("rentre" to presentHeShe),
        )
        word(
            "plaire", VERB, 16, meaning(en = "to please", ro = "a plăcea"),
            other = meaning(en = "s'il vous plaît = please", ro = "s'il vous plaît = vă rog"),
            ipa = "plɛʁ", forms = mapOf("plaît" to presentHeShe),
        )
        word("chaque", ADJECTIVE, 17, meaning(en = "every, each", ro = "fiecare"), ipa = "ʃak")
        word("samedi", NOUN, 18, meaning(en = "Saturday", ro = "sâmbătă"), gender = MASCULINE, ipa = "samdi")
        word("marché", NOUN, 19, meaning(en = "market", ro = "piață"), gender = MASCULINE, ipa = "maʁʃe")
        word("marchand", NOUN, 20, meaning(en = "seller, merchant", ro = "vânzător, negustor"), gender = MASCULINE, ipa = "maʁʃɑ̃", note = nasalNote)
        word("pomme", NOUN, 21, meaning(en = "apple", ro = "măr"), gender = FEMININE, ipa = "pɔm", forms = mapOf("pommes" to plural))
        word("pain", NOUN, 22, meaning(en = "bread", ro = "pâine"), gender = MASCULINE, ipa = "pɛ̃", note = nasalNote)
        word("fromage", NOUN, 23, meaning(en = "cheese", ro = "brânză"), gender = MASCULINE, ipa = "fʁɔmaʒ")
        word("bonjour", INTERJECTION, 24, meaning(en = "hello, good morning", ro = "bună ziua"), ipa = "bɔ̃ʒuʁ", note = nasalNote)
        word("maison", NOUN, 25, meaning(en = "house, home", ro = "casă"), gender = FEMININE, ipa = "mɛzɔ̃", note = nasalNote)
        word("avec", PREPOSITION, 26, meaning(en = "with", ro = "cu"), ipa = "avɛk")
        word("grand", ADJECTIVE, 27, meaning(en = "big, tall", ro = "mare, înalt"), ipa = "ɡʁɑ̃", note = nasalNote)
        word("sac", NOUN, 28, meaning(en = "bag", ro = "sac, geantă"), gender = MASCULINE, ipa = "sak")
        word("billet", NOUN, 29, meaning(en = "ticket", ro = "bilet"), gender = MASCULINE, ipa = "bijɛ")
        word("pour", PREPOSITION, 30, meaning(en = "for", ro = "pentru"), ipa = "puʁ")
        word("matin", NOUN, 31, meaning(en = "morning", ro = "dimineață"), gender = MASCULINE, ipa = "matɛ̃", note = nasalNote)
        word("oui", ADVERB, 32, meaning(en = "yes", ro = "da"), ipa = "wi")
        word("quel", ADJECTIVE, 33, meaning(en = "which, what", ro = "care, ce"), ipa = "kɛl", forms = mapOf("quelle" to feminine))
        word("heure", NOUN, 34, meaning(en = "hour; o'clock", ro = "oră"), gender = FEMININE, ipa = "œʁ", forms = mapOf("heures" to plural))
        word("train", NOUN, 35, meaning(en = "train", ro = "tren"), gender = MASCULINE, ipa = "tʁɛ̃", note = nasalNote)
        word("dix", NUMERAL, 36, meaning(en = "ten", ro = "zece"), ipa = "dis")
        word("merci", INTERJECTION, 37, meaning(en = "thank you", ro = "mulțumesc"), ipa = "mɛʁsi")
        word("beaucoup", ADVERB, 38, meaning(en = "a lot, very much", ro = "mult"), ipa = "boku")
    }

    val lemmas: List<Lemma> = lexicon.all

    val texts: List<ReadingText> = listOf(
        lexicon.readingText(
            "fr-market", CefrLevel.A1, TextKind.STORY, "Le marché du samedi", minutes = 2,
            "Chaque samedi, Paul va au marché." means
                meaning(en = "Every Saturday, Paul goes to the market.", ro = "În fiecare sâmbătă, Paul merge la piață."),
            "Il achète des pommes, du pain et du fromage." means
                meaning(en = "He buys apples, bread and cheese.", ro = "Cumpără mere, pâine și brânză."),
            "Le marchand lui sourit : « Bonjour, Paul ! »" means
                meaning(en = "The seller smiles at him: “Hello, Paul!”", ro = "Vânzătorul îi zâmbește: „Bună ziua, Paul!”"),
            "Paul rentre à la maison avec un grand sac." means
                meaning(en = "Paul goes back home with a big bag.", ro = "Paul se întoarce acasă cu o sacoșă mare."),
        ),
        lexicon.readingText(
            "fr-station", CefrLevel.A1, TextKind.DIALOGUE, "À la gare", minutes = 2,
            "— Bonjour, un billet pour Lyon, s'il vous plaît." means
                meaning(en = "— Hello, a ticket to Lyon, please.", ro = "— Bună ziua, un bilet pentru Lyon, vă rog."),
            "— Pour ce matin ?" means
                meaning(en = "— For this morning?", ro = "— Pentru dimineața aceasta?"),
            "— Oui. À quelle heure part le train ?" means
                meaning(en = "— Yes. What time does the train leave?", ro = "— Da. La ce oră pleacă trenul?"),
            "— Le train part à dix heures." means
                meaning(en = "— The train leaves at ten o'clock.", ro = "— Trenul pleacă la ora zece."),
            "— Merci beaucoup !" means
                meaning(en = "— Thank you very much!", ro = "— Mulțumesc mult!"),
        ),
    )
}
