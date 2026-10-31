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
    private val presentThey = text("present, ils/elles", "présent, ils/elles", "prezent, ei/ele")
    private val participle = text("past participle", "participe passé", "participiu")
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
        names("Lyon", "Marie", "Paul")

        word("le", ARTICLE, 1, meaning(en = "the", ro = "articolul hotărât"), ipa = "lə", forms = mapOf("la" to feminine, "l'" to elided))
        word(
            "de", PREPOSITION, 2, meaning(en = "of, from", ro = "de, din"), ipa = "də",
            forms = mapOf("du" to text("de + le: some (partitive)", "de + le : article partitif", "de + le: articol partitiv")),
        )
        word("un", ARTICLE, 3, meaning(en = "a, an", ro = "un, o"), ipa = "œ̃", note = nasalNote, forms = mapOf("des" to text("plural: some", "pluriel", "plural: niște")))
        word("à", PREPOSITION, 4, meaning(en = "to, at", ro = "la"), ipa = "a", forms = mapOf("au" to text("à + le", "à + le", "à + le")))
        word("et", CONJUNCTION, 5, meaning(en = "and", ro = "și"), ipa = "e")
        word("il", PRONOUN, 6, meaning(en = "he, it", ro = "el"), ipa = "il", forms = mapOf("ils" to text("plural: they", "pluriel", "plural: ei")))
        word("vous", PRONOUN, 7, meaning(en = "you (plural or polite)", ro = "voi, dumneavoastră"), ipa = "vu")
        word("lui", PRONOUN, 8, meaning(en = "(to) him, (to) her", ro = "lui, ei"), ipa = "lɥi")
        word("ce", ADJECTIVE, 9, meaning(en = "this, that", ro = "acest, această"), ipa = "sə")
        word(
            "si", CONJUNCTION, 10, meaning(en = "if", ro = "dacă"),
            other = meaning(en = "s'il vous plaît = please", ro = "s'il vous plaît = vă rog"), ipa = "si", forms = mapOf("s'" to elided),
        )
        word("aller", VERB, 11, meaning(en = "to go", ro = "a merge"), ipa = "ale", forms = mapOf("va" to presentHeShe, "allé" to participle))
        word("partir", VERB, 12, meaning(en = "to leave", ro = "a pleca"), ipa = "paʁtiʁ", note = frenchRNote, forms = mapOf("part" to presentHeShe))
        word("acheter", VERB, 13, meaning(en = "to buy", ro = "a cumpăra"), ipa = "aʃte", forms = mapOf("achète" to presentHeShe))
        word("sourire", VERB, 14, meaning(en = "to smile", ro = "a zâmbi"), ipa = "suʁiʁ", note = frenchRNote, forms = mapOf("sourit" to presentHeShe))
        word(
            "rentrer", VERB, 15, meaning(en = "to go back home", ro = "a se întoarce acasă"),
            ipa = "ʁɑ̃tʁe", note = nasalNote, forms = mapOf("rentre" to presentHeShe, "rentrent" to presentThey, "rentrés" to participle),
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
        word("café", NOUN, 39, meaning(en = "coffee; café", ro = "cafea; cafenea"), gender = MASCULINE, ipa = "kafe")
        word("lait", NOUN, 40, meaning(en = "milk", ro = "lapte"), gender = MASCULINE, ipa = "lɛ")
        word("non", ADVERB, 41, meaning(en = "no", ro = "nu"), ipa = "nɔ̃", note = nasalNote)
        word("croissant", NOUN, 42, meaning(en = "croissant", ro = "corn (croissant)"), gender = MASCULINE, ipa = "kʁwasɑ̃", note = nasalNote)
        word("aussi", ADVERB, 43, meaning(en = "also, too", ro = "și, de asemenea"), ipa = "osi")
        word("voilà", ADVERB, 44, meaning(en = "here you are, there", ro = "poftiți, iată"), ipa = "vwala")
        word("ça", PRONOUN, 45, meaning(en = "that, it", ro = "asta"), ipa = "sa")
        word(
            "faire", VERB, 46, meaning(en = "to do, to make", ro = "a face"),
            other = meaning(en = "ça fait… = that comes to…", ro = "ça fait… = face…"), ipa = "fɛʁ", forms = mapOf("fait" to presentHeShe),
        )
        word("trois", NUMERAL, 47, meaning(en = "three", ro = "trei"), ipa = "tʁwa", note = frenchRNote)
        word("euro", NOUN, 48, meaning(en = "euro", ro = "euro"), gender = MASCULINE, ipa = "øʁo", forms = mapOf("euros" to plural))
        word("bon", ADJECTIVE, 49, meaning(en = "good", ro = "bun"), ipa = "bɔ̃", note = nasalNote, forms = mapOf("bonne" to feminine))
        word("journée", NOUN, 50, meaning(en = "day", ro = "zi"), gender = FEMININE, ipa = "ʒuʁne")
        word("soir", NOUN, 51, meaning(en = "evening", ro = "seară"), gender = MASCULINE, ipa = "swaʁ")
        word("promener", VERB, 52, meaning(en = "to walk (a dog), to take for a walk", ro = "a plimba"), ipa = "pʁɔmne", forms = mapOf("promène" to presentHeShe))
        word(
            "son", ADJECTIVE, 53, meaning(en = "his, her, its", ro = "său, sa"), ipa = "sɔ̃", note = nasalNote,
            forms = mapOf("sa" to feminine),
        )
        word("chien", NOUN, 54, meaning(en = "dog", ro = "câine"), gender = MASCULINE, ipa = "ʃjɛ̃", note = nasalNote)
        word("parc", NOUN, 55, meaning(en = "park", ro = "parc"), gender = MASCULINE, ipa = "paʁk")
        word("courir", VERB, 56, meaning(en = "to run", ro = "a alerga"), ipa = "kuʁiʁ", note = frenchRNote, forms = mapOf("court" to presentHeShe))
        word("dans", PREPOSITION, 57, meaning(en = "in", ro = "în"), ipa = "dɑ̃", note = nasalNote)
        word("herbe", NOUN, 58, meaning(en = "grass", ro = "iarbă"), gender = FEMININE, ipa = "ɛʁb")
        word("lire", VERB, 59, meaning(en = "to read", ro = "a citi"), ipa = "liʁ", forms = mapOf("lit" to presentHeShe))
        word("livre", NOUN, 60, meaning(en = "book", ro = "carte"), gender = MASCULINE, ipa = "livʁ")
        word("sur", PREPOSITION, 61, meaning(en = "on", ro = "pe"), ipa = "syʁ")
        word("banc", NOUN, 62, meaning(en = "bench", ro = "bancă"), gender = MASCULINE, ipa = "bɑ̃", note = nasalNote)
        word("ensuite", ADVERB, 63, meaning(en = "then, afterwards", ro = "apoi"), ipa = "ɑ̃sɥit", note = nasalNote)
        word(
            "être", VERB, 64, meaning(en = "to be", ro = "a fi"), ipa = "ɛtʁ",
            other = meaning(en = "auxiliary of the passé composé", ro = "auxiliar pentru passé composé"),
            forms = mapOf("est" to presentHeShe, "sont" to presentThey),
        )
        word(
            "avoir", VERB, 65, meaning(en = "to have", ro = "a avea"), ipa = "avwaʁ",
            other = meaning(en = "auxiliary of the passé composé", ro = "auxiliar pentru passé composé"),
            forms = mapOf("a" to presentHeShe, "ont" to presentThey),
        )
        word("dernier", ADJECTIVE, 66, meaning(en = "last", ro = "trecut, ultimul"), ipa = "dɛʁnje")
        word("en", PREPOSITION, 67, meaning(en = "in, by (en train = by train)", ro = "în, cu (en train = cu trenul)"), ipa = "ɑ̃", note = nasalNote)
        word("visiter", VERB, 68, meaning(en = "to visit", ro = "a vizita"), ipa = "vizite", forms = mapOf("visité" to participle))
        word("vieux", ADJECTIVE, 69, meaning(en = "old", ro = "vechi, bătrân"), ipa = "vjø", forms = mapOf("vieille" to feminine))
        word("ville", NOUN, 70, meaning(en = "town, city", ro = "oraș"), gender = FEMININE, ipa = "vil")
        word("sœur", NOUN, 71, meaning(en = "sister", ro = "soră"), gender = FEMININE, ipa = "sœʁ")
        word("manger", VERB, 72, meaning(en = "to eat", ro = "a mânca"), ipa = "mɑ̃ʒe", note = nasalNote, forms = mapOf("mangé" to participle))
        word("petit", ADJECTIVE, 73, meaning(en = "small, little", ro = "mic"), ipa = "pəti")
        word("restaurant", NOUN, 74, meaning(en = "restaurant", ro = "restaurant"), gender = MASCULINE, ipa = "ʁɛstɔʁɑ̃", note = nasalNote)
        word("dimanche", NOUN, 75, meaning(en = "Sunday", ro = "duminică"), gender = MASCULINE, ipa = "dimɑ̃ʃ", note = nasalNote)
        word("pleuvoir", VERB, 76, meaning(en = "to rain", ro = "a ploua"), ipa = "pløvwaʁ", forms = mapOf("plu" to participle))
        word("alors", ADVERB, 77, meaning(en = "so, then", ro = "așa că, atunci"), ipa = "alɔʁ")
        word("tôt", ADVERB, 78, meaning(en = "early", ro = "devreme"), ipa = "to")
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
        lexicon.readingText(
            "fr-cafe", CefrLevel.A1, TextKind.DIALOGUE, "Au café", minutes = 2,
            "— Bonjour ! Un café, s'il vous plaît." means
                meaning(en = "— Hello! A coffee, please.", ro = "— Bună ziua! O cafea, vă rog."),
            "— Avec du lait ?" means
                meaning(en = "— With milk?", ro = "— Cu lapte?"),
            "— Non, merci. Et un croissant aussi." means
                meaning(en = "— No, thank you. And a croissant too.", ro = "— Nu, mulțumesc. Și un corn."),
            "— Voilà. Ça fait trois euros." means
                meaning(en = "— Here you are. That's three euros.", ro = "— Poftiți. Face trei euro."),
            "— Merci, bonne journée !" means
                meaning(en = "— Thank you, have a nice day!", ro = "— Mulțumesc, o zi bună!"),
        ),
        lexicon.readingText(
            "fr-park", CefrLevel.A1, TextKind.STORY, "Une promenade au parc", minutes = 2,
            "Le soir, Marie promène son chien au parc." means
                meaning(en = "In the evening, Marie walks her dog in the park.", ro = "Seara, Marie își plimbă câinele în parc."),
            "Le chien court dans l'herbe." means
                meaning(en = "The dog runs in the grass.", ro = "Câinele aleargă prin iarbă."),
            "Marie lit un livre sur un banc." means
                meaning(en = "Marie reads a book on a bench.", ro = "Marie citește o carte pe o bancă."),
            "Ensuite, ils rentrent à la maison." means
                meaning(en = "Then they go back home.", ro = "Apoi se întorc acasă."),
        ),
        lexicon.readingText(
            "fr-weekend", CefrLevel.A2, TextKind.STORY, "Le week-end dernier", minutes = 2,
            "Samedi dernier, Paul est allé à Lyon en train." means
                meaning(en = "Last Saturday, Paul went to Lyon by train.", ro = "Sâmbăta trecută, Paul a mers la Lyon cu trenul."),
            "Il a visité la vieille ville avec sa sœur." means
                meaning(en = "He visited the old town with his sister.", ro = "A vizitat orașul vechi cu sora lui."),
            "Le soir, ils ont mangé dans un petit restaurant." means
                meaning(en = "In the evening, they ate in a small restaurant.", ro = "Seara au mâncat într-un restaurant mic."),
            "Dimanche, il a plu, alors ils sont rentrés tôt." means
                meaning(en = "On Sunday it rained, so they came back early.", ro = "Duminică a plouat, așa că s-au întors devreme."),
        ),
    )
}
