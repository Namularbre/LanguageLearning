package com.sazerat.languagelearning.core.data.sample

import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.ADJECTIVE
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.ADVERB
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.ARTICLE
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.CONJUNCTION
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.NOUN
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.NUMERAL
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.PREPOSITION
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.PRONOUN
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech.VERB
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.TextKind

/** English texts of the Library and the words they use (British English). */
internal object SampleEnglishReading {
    private val presentHeShe = text("present, he/she/it", "présent, il/elle", "prezent, el/ea")
    private val plural = text("plural", "pluriel", "plural")
    private val thNote = text(
        "th: the tip of the tongue between the teeth.",
        "th : le bout de la langue entre les dents.",
        "th: vârful limbii între dinți.",
    )

    private val lexicon = Lexicon(LanguageCode("en-GB")).apply {
        names("London", "Tom")

        word("the", ARTICLE, 1, meaning(fr = "le, la, les", ro = "articolul hotărât (-ul, -a)"), ipa = "ðə", note = thNote)
        word("be", VERB, 2, meaning(fr = "être", ro = "a fi"), ipa = "biː", forms = mapOf("is" to presentHeShe))
        word("and", CONJUNCTION, 3, meaning(fr = "et", ro = "și"), ipa = "ænd")
        word("a", ARTICLE, 4, meaning(fr = "un, une", ro = "un, o"), ipa = "ə")
        word("to", PREPOSITION, 5, meaning(fr = "à, vers", ro = "la, spre"), ipa = "tuː")
        word("in", PREPOSITION, 6, meaning(fr = "dans, à", ro = "în"), ipa = "ɪn")
        word("it", PRONOUN, 7, meaning(fr = "il, elle (une chose)", ro = "el, ea (un lucru)"), ipa = "ɪt")
        word(
            "he", PRONOUN, 8, meaning(fr = "il", ro = "el"), ipa = "hiː",
            forms = mapOf("his" to text("possessive: his", "possessif : son, sa, ses", "posesiv: lui")),
        )
        word("I", PRONOUN, 9, meaning(fr = "je", ro = "eu"), ipa = "aɪ")
        word("you", PRONOUN, 10, meaning(fr = "tu, vous", ro = "tu, voi, dumneavoastră"), ipa = "juː")
        word("that", PRONOUN, 11, meaning(fr = "cela, ça", ro = "acela, asta"), ipa = "ðæt", note = thNote)
        word("this", PRONOUN, 12, meaning(fr = "ce, cette, ceci", ro = "acest, această"), ipa = "ðɪs", note = thNote)
        word("can", VERB, 13, meaning(fr = "pouvoir", ro = "a putea"), ipa = "kæn")
        word(
            "would", VERB, 14, meaning(fr = "conditionnel : would like = voudrais", ro = "condițional: would like = aș vrea"),
            ipa = "wʊd",
        )
        word("like", VERB, 15, meaning(fr = "aimer", ro = "a plăcea"), ipa = "laɪk")
        word("take", VERB, 16, meaning(fr = "prendre", ro = "a lua"), ipa = "teɪk", forms = mapOf("takes" to presentHeShe))
        word(
            "walk", VERB, 17, meaning(fr = "marcher, aller à pied", ro = "a merge pe jos"),
            ipa = "wɔːk", forms = mapOf("walks" to presentHeShe),
        )
        word("buy", VERB, 18, meaning(fr = "acheter", ro = "a cumpăra"), ipa = "baɪ", forms = mapOf("buys" to presentHeShe))
        word("wait", VERB, 19, meaning(fr = "attendre", ro = "a aștepta"), ipa = "weɪt", forms = mapOf("waits" to presentHeShe))
        word(
            "rain", VERB, 20, meaning(fr = "pleuvoir", ro = "a ploua"), ipa = "reɪn",
            forms = mapOf(
                "raining" to text(
                    "-ing form: it is raining = it rains now",
                    "forme en -ing : it is raining = il pleut (en ce moment)",
                    "forma -ing: it is raining = plouă (acum)",
                ),
            ),
        )
        word("help", VERB, 21, meaning(fr = "aider", ro = "a ajuta"), ipa = "help")
        word(
            "thank", VERB, 22, meaning(fr = "remercier", ro = "a mulțumi"),
            other = meaning(fr = "thank you = merci", ro = "thank you = mulțumesc"), ipa = "θæŋk", note = thNote,
        )
        word("morning", NOUN, 23, meaning(fr = "matin", ro = "dimineață"), ipa = "ˈmɔːnɪŋ")
        word("afternoon", NOUN, 24, meaning(fr = "après-midi", ro = "după-amiază"), ipa = "ˌɑːftəˈnuːn")
        word("good", ADJECTIVE, 25, meaning(fr = "bon, bien", ro = "bun"), ipa = "ɡʊd")
        word("umbrella", NOUN, 26, meaning(fr = "parapluie", ro = "umbrelă"), ipa = "ʌmˈbrelə")
        word("station", NOUN, 27, meaning(fr = "gare", ro = "gară"), ipa = "ˈsteɪʃn")
        word("train", NOUN, 28, meaning(fr = "train", ro = "tren"), ipa = "treɪn")
        word("late", ADJECTIVE, 29, meaning(fr = "en retard", ro = "întârziat"), ipa = "leɪt")
        word("again", ADVERB, 30, meaning(fr = "encore, de nouveau", ro = "din nou"), ipa = "əˈɡen")
        word("hot", ADJECTIVE, 31, meaning(fr = "chaud", ro = "fierbinte"), ipa = "hɒt")
        word("coffee", NOUN, 32, meaning(fr = "café", ro = "cafea"), ipa = "ˈkɒfi")
        word("yes", ADVERB, 33, meaning(fr = "oui", ro = "da"), ipa = "jes")
        word("no", ADVERB, 34, meaning(fr = "non", ro = "nu"), ipa = "nəʊ")
        word("two", NUMERAL, 35, meaning(fr = "deux", ro = "doi, două"), ipa = "tuː")
        word("three", NUMERAL, 36, meaning(fr = "trois", ro = "trei"), ipa = "θriː", note = thNote)
        word("fifty", NUMERAL, 37, meaning(fr = "cinquante", ro = "cincizeci"), ipa = "ˈfɪfti")
        word("croissant", NOUN, 38, meaning(fr = "croissant", ro = "corn (croissant)"), ipa = "ˈkwæsɒ̃", forms = mapOf("croissants" to plural))
        word("please", ADVERB, 39, meaning(fr = "s'il vous plaît", ro = "vă rog, te rog"), ipa = "pliːz")
        word("anything", PRONOUN, 40, meaning(fr = "quelque chose", ro = "ceva, orice"), ipa = "ˈeniθɪŋ")
        word("else", ADVERB, 41, meaning(fr = "d'autre", ro = "altceva (anything else)"), ipa = "els")
        word("how", ADVERB, 42, meaning(fr = "comment", ro = "cum"), ipa = "haʊ")
        word(
            "much", ADVERB, 43, meaning(fr = "beaucoup", ro = "mult"),
            other = meaning(fr = "how much = combien", ro = "how much = cât"), ipa = "mʌtʃ",
        )
        word("pound", NOUN, 44, meaning(fr = "livre (monnaie)", ro = "liră (monedă)"), ipa = "paʊnd", forms = mapOf("pounds" to plural))
    }

    val lemmas: List<Lemma> = lexicon.all

    val texts: List<ReadingText> = listOf(
        lexicon.readingText(
            "en-rain", CefrLevel.A1, TextKind.STORY, "A rainy morning", minutes = 2,
            "It is raining in London this morning." means
                meaning(fr = "Il pleut à Londres ce matin.", ro = "Plouă la Londra în această dimineață."),
            "Tom takes his umbrella and walks to the station." means
                meaning(
                    fr = "Tom prend son parapluie et marche jusqu'à la gare.",
                    ro = "Tom își ia umbrela și merge pe jos până la gară.",
                ),
            "The train is late again." means
                meaning(fr = "Le train est encore en retard.", ro = "Trenul întârzie din nou."),
            "He buys a hot coffee and waits." means
                meaning(fr = "Il achète un café chaud et attend.", ro = "Își cumpără o cafea fierbinte și așteaptă."),
        ),
        lexicon.readingText(
            "en-bakery", CefrLevel.A2, TextKind.DIALOGUE, "At the bakery", minutes = 2,
            "— Good afternoon! Can I help you?" means
                meaning(fr = "— Bonjour ! Je peux vous aider ?", ro = "— Bună ziua! Vă pot ajuta?"),
            "— Yes, I would like two croissants, please." means
                meaning(fr = "— Oui, je voudrais deux croissants, s'il vous plaît.", ro = "— Da, aș vrea două cornuri, vă rog."),
            "— Anything else?" means
                meaning(fr = "— Autre chose ?", ro = "— Altceva?"),
            "— No, thank you. How much is that?" means
                meaning(fr = "— Non, merci. Ça fait combien ?", ro = "— Nu, mulțumesc. Cât costă?"),
            "— That is three pounds fifty." means
                meaning(fr = "— Ça fait trois livres cinquante.", ro = "— Face trei lire și cincizeci."),
        ),
    )
}
