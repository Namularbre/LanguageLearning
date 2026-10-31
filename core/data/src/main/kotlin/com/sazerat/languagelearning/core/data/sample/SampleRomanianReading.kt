package com.sazerat.languagelearning.core.data.sample

import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.Gender.FEMININE
import com.sazerat.languagelearning.core.domain.model.Gender.MASCULINE
import com.sazerat.languagelearning.core.domain.model.Gender.NEUTER
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

/** Romanian texts of the Library and the words they use. To be reviewed by a native speaker. */
internal object SampleRomanianReading {
    private val definite = text("definite article", "article défini", "articulat hotărât")
    private val genitiveDefinite = text("genitive-dative, definite", "génitif-datif, défini", "genitiv-dativ, articulat")
    private val plural = text("plural", "pluriel", "plural")
    private val pluralDefinite = text("plural, definite", "pluriel, défini", "plural, articulat")
    private val feminine = text("feminine", "féminin", "feminin")
    private val presentI = text("present, I", "présent, je", "prezent, eu")
    private val presentYou = text("present, you", "présent, tu", "prezent, tu")
    private val presentHeShe = text("present, he/she", "présent, il/elle", "prezent, el/ea")
    private val presentYouPlural = text("present, you (plural or polite)", "présent, vous", "prezent, voi")
    private val presentThey = text("present, they", "présent, ils/elles", "prezent, ei/ele")
    private val participle = text("past participle", "participe passé", "participiu")
    private val imperativeYouPlural = text("imperative, you (plural or polite)", "impératif, vous", "imperativ, voi")

    private val schwaNote = text(
        "ă is a short, relaxed vowel, like the a in “about”.",
        "ă est une voyelle brève et neutre, comme le e de « le ».",
        "ă: vocală centrală, scurtă și relaxată.",
    )
    private val closeCentralNote = text(
        "â (and î) is a vowel between i and u, said with spread lips.",
        "â (et î) est une voyelle entre i et ou, lèvres étirées.",
        "â și î notează același sunet: o vocală centrală închisă.",
    )
    private val shNote = text("ș is the sh of “shoe”.", "ș se prononce comme le ch de « chat ».", "ș: ca în „școală”.")
    private val tsNote = text("ț is the ts of “cats”.", "ț se prononce ts, comme dans « tsar ».", "ț: ca în „țară”.")

    private val lexicon = Lexicon(LanguageCode("ro-RO")).apply {
        names("Ana", "Brașov", "Mihai")

        word("și", CONJUNCTION, 1, meaning(en = "and", fr = "et"), ipa = "ʃi", note = shNote)
        word(
            "în", PREPOSITION, 2, meaning(en = "in, into", fr = "dans, en"), ipa = "ɨn", note = closeCentralNote,
            forms = mapOf("într" to text("before a vowel or un/o: într-un, într-o", "devant une voyelle ou un/o : într-un, într-o", "înaintea unei vocale sau a lui un/o: într-un, într-o")),
        )
        word("la", PREPOSITION, 3, meaning(en = "at, to", fr = "à"), ipa = "la")
        word(
            "a fi", VERB, 4, meaning(en = "to be", fr = "être"), ipa = "a fi",
            forms = mapOf("este" to presentHeShe, "fi" to text("infinitive", "infinitif", "infinitiv")),
        )
        word("un", ARTICLE, 5, meaning(en = "a, an", fr = "un, une"), ipa = "un", forms = mapOf("o" to feminine))
        word("de", PREPOSITION, 6, meaning(en = "of, from", fr = "de"), ipa = "de")
        word("pe", PREPOSITION, 7, meaning(en = "on", fr = "sur"), ipa = "pe")
        word("ce", PRONOUN, 8, meaning(en = "what", fr = "que, quoi"), ipa = "t͡ʃe")
        word(
            "el", PRONOUN, 9, meaning(en = "he", fr = "il"), ipa = "jel",
            forms = mapOf("lui" to text("genitive: his", "génitif : son, sa", "genitiv: al lui")),
        )
        word("ea", PRONOUN, 10, meaning(en = "she", fr = "elle"), ipa = "ja")
        word("mai", ADVERB, 11, meaning(en = "more, still", fr = "plus, encore"), ipa = "maj")
        word(
            "a face", VERB, 12, meaning(en = "to do, to make", fr = "faire"),
            other = meaning(en = "Ce mai faci? = How are you?", fr = "Ce mai faci ? = Comment vas-tu ?"),
            ipa = "a ˈfat͡ʃe", forms = mapOf("faci" to presentYou, "face" to presentHeShe),
        )
        word("pentru", PREPOSITION, 13, meaning(en = "for", fr = "pour"), ipa = "ˈpentru")
        word("din", PREPOSITION, 14, meaning(en = "from, out of", fr = "de, hors de"), ipa = "din")
        word("sau", CONJUNCTION, 15, meaning(en = "or", fr = "ou"), ipa = "saw")
        word("dacă", CONJUNCTION, 16, meaning(en = "if", fr = "si"), ipa = "ˈdakə", note = schwaNote)
        word("tot", ADJECTIVE, 17, meaning(en = "all, whole", fr = "tout"), ipa = "tot")
        word("bun", ADJECTIVE, 18, meaning(en = "good", fr = "bon"), ipa = "bun", forms = mapOf("bună" to feminine))
        word("bine", ADVERB, 19, meaning(en = "well, fine", fr = "bien"), ipa = "ˈbine")
        word(
            "a merge", VERB, 20, meaning(en = "to go, to walk", fr = "aller, marcher"),
            other = meaning(en = "to work (a machine)", fr = "fonctionner"),
            ipa = "a ˈmerd͡ʒe", forms = mapOf("merge" to presentHeShe, "merg" to presentThey, "mers" to participle),
        )
        word(
            "zi", NOUN, 21, meaning(en = "day", fr = "jour"), gender = FEMININE,
            other = meaning(en = "Bună ziua = Hello", fr = "Bună ziua = Bonjour"),
            ipa = "zi", forms = mapOf("ziua" to definite),
        )
        word(
            "casă", NOUN, 22, meaning(en = "house", fr = "maison"), gender = FEMININE,
            other = meaning(en = "home, household", fr = "foyer, ménage"),
            ipa = "ˈkasə", note = schwaNote, forms = mapOf("casa" to definite, "casei" to genitiveDefinite),
        )
        word("a veni", VERB, 23, meaning(en = "to come", fr = "venir"), ipa = "a veˈni", forms = mapOf("vine" to presentHeShe))
        word("a lua", VERB, 24, meaning(en = "to take", fr = "prendre"), ipa = "a lu̯a", forms = mapOf("luați" to imperativeYouPlural))
        word(
            "oră", NOUN, 25, meaning(en = "hour; o'clock", fr = "heure"), gender = FEMININE,
            ipa = "ˈorə", note = schwaNote, forms = mapOf("ora" to definite),
        )
        word(
            "dimineață", NOUN, 26, meaning(en = "morning", fr = "matin"), gender = FEMININE,
            ipa = "dimiˈne̯at͡sə", note = tsNote, forms = mapOf("dimineața" to definite),
        )
        word("fiecare", ADJECTIVE, 27, meaning(en = "every, each", fr = "chaque"), ipa = "fi̯eˈkare")
        word("azi", ADVERB, 28, meaning(en = "today", fr = "aujourd'hui"), ipa = "azʲ")
        word("mâine", ADVERB, 29, meaning(en = "tomorrow", fr = "demain"), ipa = "ˈmɨjne", note = closeCentralNote)
        word(
            "a cumpăra", VERB, 30, meaning(en = "to buy", fr = "acheter"),
            ipa = "a kumpəˈra", note = schwaNote, forms = mapOf("cumpără" to presentHeShe),
        )
        word("a costa", VERB, 31, meaning(en = "to cost", fr = "coûter"), ipa = "a kosˈta", forms = mapOf("costă" to presentHeShe))
        word("cât", ADVERB, 32, meaning(en = "how much", fr = "combien"), ipa = "kɨt", note = closeCentralNote)
        word(
            "a mulțumi", VERB, 33, meaning(en = "to thank", fr = "remercier"),
            other = meaning(en = "mulțumesc = thank you", fr = "mulțumesc = merci"),
            ipa = "a mult͡suˈmi", note = tsNote, forms = mapOf("mulțumesc" to presentI),
        )
        word(
            "a ruga", VERB, 34, meaning(en = "to ask, to beg", fr = "prier, demander"),
            other = meaning(en = "vă rog = please", fr = "vă rog = s'il vous plaît"),
            ipa = "a ruˈɡa", forms = mapOf("rog" to presentI),
        )
        word("vă", PRONOUN, 35, meaning(en = "you (plural or polite)", fr = "vous"), ipa = "və", note = schwaNote)
        word("aproape", ADVERB, 36, meaning(en = "near, close", fr = "près, proche"), ipa = "aˈpro̯ape")
        word("lângă", PREPOSITION, 37, meaning(en = "next to", fr = "à côté de"), ipa = "ˈlɨnɡə", note = closeCentralNote)
        word(
            "stradă", NOUN, 38, meaning(en = "street", fr = "rue"), gender = FEMININE,
            ipa = "ˈstradə", note = schwaNote, forms = mapOf("străzii" to genitiveDefinite),
        )
        word(
            "capăt", NOUN, 39, meaning(en = "end", fr = "bout"), gender = NEUTER,
            ipa = "ˈkapət", note = schwaNote, forms = mapOf("capătul" to definite),
        )
        word(
            "ușă", NOUN, 40, meaning(en = "door", fr = "porte"), gender = FEMININE,
            ipa = "ˈuʃə", note = shNote, forms = mapOf("ușa" to definite),
        )
        word(
            "câine", NOUN, 41, meaning(en = "dog", fr = "chien"), gender = MASCULINE, ipa = "ˈkɨjne", note = closeCentralNote,
            forms = mapOf("câinele" to definite),
        )
        word("a dormi", VERB, 42, meaning(en = "to sleep", fr = "dormir"), ipa = "a dorˈmi", forms = mapOf("doarme" to presentHeShe))
        word("soare", NOUN, 43, meaning(en = "sun", fr = "soleil"), gender = MASCULINE, ipa = "ˈso̯are")
        word(
            "piață", NOUN, 44, meaning(en = "market", fr = "marché"), gender = FEMININE,
            other = meaning(en = "square", fr = "place"), ipa = "ˈpjat͡sə", note = tsNote, forms = mapOf("piața" to definite),
        )
        word(
            "sâmbătă", NOUN, 45, meaning(en = "Saturday", fr = "samedi"), gender = FEMININE, ipa = "ˈsɨmbətə", note = closeCentralNote,
            forms = mapOf("sâmbăta" to definite),
        )
        word("duminică", NOUN, 46, meaning(en = "Sunday", fr = "dimanche"), gender = FEMININE, ipa = "duˈminikə", note = schwaNote)
        word(
            "roșie", NOUN, 47, meaning(en = "tomato", fr = "tomate"), gender = FEMININE,
            ipa = "ˈroʃi.e", note = shNote, forms = mapOf("roșii" to plural, "roșiile" to pluralDefinite),
        )
        word("pâine", NOUN, 48, meaning(en = "bread", fr = "pain"), gender = FEMININE, ipa = "ˈpɨjne", note = closeCentralNote)
        word("brânză", NOUN, 49, meaning(en = "cheese", fr = "fromage"), gender = FEMININE, ipa = "ˈbrɨnzə", note = closeCentralNote)
        word("bilet", NOUN, 50, meaning(en = "ticket", fr = "billet"), gender = NEUTER, ipa = "biˈlet", forms = mapOf("biletul" to definite))
        word("tren", NOUN, 51, meaning(en = "train", fr = "train"), gender = NEUTER, ipa = "tren", forms = mapOf("trenul" to definite))
        word(
            "a pleca", VERB, 52, meaning(en = "to leave", fr = "partir"), ipa = "a pleˈka",
            forms = mapOf("pleacă" to presentHeShe, "plecat" to participle),
        )
        word("zece", NUMERAL, 53, meaning(en = "ten", fr = "dix"), ipa = "ˈzet͡ʃe")
        word("cincizeci", NUMERAL, 54, meaning(en = "fifty", fr = "cinquante"), ipa = "t͡ʃint͡ʃˈzet͡ʃʲ")
        word(
            "leu", NOUN, 55, meaning(en = "leu (Romanian currency)", fr = "leu (monnaie roumaine)"), gender = MASCULINE,
            other = meaning(en = "lion", fr = "lion"), ipa = "lew", forms = mapOf("lei" to plural),
        )
        word("a bea", VERB, 56, meaning(en = "to drink", fr = "boire"), ipa = "a be̯a", forms = mapOf("bea" to presentHeShe))
        word("cafea", NOUN, 57, meaning(en = "coffee", fr = "café"), gender = FEMININE, ipa = "kaˈfe̯a", forms = mapOf("cafeaua" to definite))
        word("fierbinte", ADJECTIVE, 58, meaning(en = "hot", fr = "chaud"), ipa = "fi̯erˈbinte")
        word("a citi", VERB, 59, meaning(en = "to read", fr = "lire"), ipa = "a t͡ʃiˈti", forms = mapOf("citește" to presentHeShe))
        word("carte", NOUN, 60, meaning(en = "book", fr = "livre"), gender = FEMININE, ipa = "ˈkarte")
        word("fereastră", NOUN, 61, meaning(en = "window", fr = "fenêtre"), gender = FEMININE, ipa = "feˈre̯astrə", note = schwaNote)
        word("pisică", NOUN, 62, meaning(en = "cat", fr = "chat"), gender = FEMININE, ipa = "piˈsikə", forms = mapOf("pisica" to definite))
        word(
            "masă", NOUN, 63, meaning(en = "table", fr = "table"), gender = FEMININE,
            other = meaning(en = "meal", fr = "repas"), ipa = "ˈmasə", note = schwaNote,
        )
        word("apoi", ADVERB, 64, meaning(en = "then", fr = "ensuite"), ipa = "aˈpoj")
        word("birou", NOUN, 65, meaning(en = "office", fr = "bureau"), gender = NEUTER, other = meaning(en = "desk", fr = "bureau (meuble)"), ipa = "biˈrow")
        word(
            "a vrea", VERB, 66, meaning(en = "to want", fr = "vouloir"), ipa = "a vre̯a",
            forms = mapOf("va" to text("future auxiliary: va fi = will be", "auxiliaire du futur : va fi = sera", "auxiliar de viitor")),
        )
        word("oraș", NOUN, 67, meaning(en = "town, city", fr = "ville"), gender = NEUTER, ipa = "oˈraʃ", note = shNote, forms = mapOf("orașul" to definite))
        word("ploaie", NOUN, 68, meaning(en = "rain", fr = "pluie"), gender = FEMININE, ipa = "ˈplo̯aje", forms = mapOf("ploaia" to definite))
        word("seară", NOUN, 69, meaning(en = "evening", fr = "soir"), gender = FEMININE, ipa = "ˈse̯arə", forms = mapOf("seara" to definite))
        word(
            "temperatură", NOUN, 70, meaning(en = "temperature", fr = "température"), gender = FEMININE,
            ipa = "temperaˈturə", forms = mapOf("temperaturile" to pluralDefinite),
        )
        word(
            "a scădea", VERB, 71, meaning(en = "to fall, to decrease", fr = "baisser, diminuer"),
            ipa = "a skəˈde̯a", forms = mapOf("scad" to presentThey),
        )
        word("grad", NOUN, 72, meaning(en = "degree", fr = "degré"), gender = NEUTER, ipa = "ɡrad", forms = mapOf("grade" to plural))
        word("umbrelă", NOUN, 73, meaning(en = "umbrella", fr = "parapluie"), gender = FEMININE, ipa = "umˈbrelə")
        word("a ieși", VERB, 74, meaning(en = "to go out", fr = "sortir"), ipa = "a jeˈʃi", note = shNote, forms = mapOf("ieșiți" to presentYouPlural))
        word("cu", PREPOSITION, 75, meaning(en = "with", fr = "avec"), ipa = "ku")
        word("parc", NOUN, 76, meaning(en = "park", fr = "parc"), gender = NEUTER, ipa = "park")
        word("a alerga", VERB, 77, meaning(en = "to run", fr = "courir"), ipa = "a alerˈɡa", forms = mapOf("aleargă" to presentHeShe))
        word("prin", PREPOSITION, 78, meaning(en = "through, in", fr = "à travers, dans"), ipa = "prin")
        word("iarbă", NOUN, 79, meaning(en = "grass", fr = "herbe"), gender = FEMININE, ipa = "ˈjarbə", note = schwaNote)
        word("bancă", NOUN, 80, meaning(en = "bench", fr = "banc"), gender = FEMININE, other = meaning(en = "bank", fr = "banque"), ipa = "ˈbankə", note = schwaNote)
        word("acasă", ADVERB, 81, meaning(en = "(at) home", fr = "à la maison"), ipa = "aˈkasə", note = schwaNote)
        word("lapte", NOUN, 82, meaning(en = "milk", fr = "lait"), gender = NEUTER, ipa = "ˈlapte")
        word("nu", ADVERB, 83, meaning(en = "no, not", fr = "non, ne… pas"), ipa = "nu")
        word("corn", NOUN, 84, meaning(en = "croissant", fr = "croissant"), gender = NEUTER, other = meaning(en = "horn", fr = "corne"), ipa = "korn")
        word(
            "a pofti", VERB, 85, meaning(en = "to invite", fr = "inviter"),
            other = meaning(en = "poftiți = here you are", fr = "poftiți = voilà, je vous en prie"),
            ipa = "a pofˈti", forms = mapOf("poftiți" to presentYouPlural),
        )
        word("cincisprezece", NUMERAL, 86, meaning(en = "fifteen", fr = "quinze"), ipa = "ˈt͡ʃint͡ʃsprezet͡ʃe")
        word("trecut", ADJECTIVE, 87, meaning(en = "last, past", fr = "dernier, passé"), ipa = "treˈkut", forms = mapOf("trecută" to feminine))
        word(
            "a avea", VERB, 88, meaning(en = "to have", fr = "avoir"),
            other = meaning(en = "auxiliary of the past: a mers = went", fr = "auxiliaire du passé : a mers = est allé"),
            ipa = "a aˈve̯a", forms = mapOf("a" to presentHeShe, "au" to presentThey),
        )
        word("a vizita", VERB, 89, meaning(en = "to visit", fr = "visiter"), ipa = "a viziˈta", forms = mapOf("vizitat" to participle))
        word("vechi", ADJECTIVE, 90, meaning(en = "old", fr = "vieux"), ipa = "vekʲ")
        word("soră", NOUN, 91, meaning(en = "sister", fr = "sœur"), gender = FEMININE, ipa = "ˈsorə", note = schwaNote, forms = mapOf("sora" to definite))
        word("a mânca", VERB, 92, meaning(en = "to eat", fr = "manger"), ipa = "a mɨnˈka", note = closeCentralNote, forms = mapOf("mâncat" to participle))
        word("restaurant", NOUN, 93, meaning(en = "restaurant", fr = "restaurant"), gender = NEUTER, ipa = "restawˈrant")
        word("mic", ADJECTIVE, 94, meaning(en = "small", fr = "petit"), ipa = "mik")
        word("a ploua", VERB, 95, meaning(en = "to rain", fr = "pleuvoir"), ipa = "a ploˈwa", forms = mapOf("plouat" to participle))
        word("așa", ADVERB, 96, meaning(en = "so, like this", fr = "ainsi, comme ça"), ipa = "aˈʃa", note = shNote)
        word("că", CONJUNCTION, 97, meaning(en = "that (așa că = so)", fr = "que (așa că = donc)"), ipa = "kə", note = schwaNote)
        word("devreme", ADVERB, 98, meaning(en = "early", fr = "tôt"), ipa = "deˈvreme")
    }

    val lemmas: List<Lemma> = lexicon.all

    val texts: List<ReadingText> = listOf(
        lexicon.readingText(
            "ro-market", CefrLevel.A1, TextKind.STORY, "Sâmbăta la piață", minutes = 3,
            "Ana merge la piață în fiecare sâmbătă." means
                meaning(en = "Ana goes to the market every Saturday.", fr = "Ana va au marché chaque samedi."),
            "Piața este aproape, la capătul străzii." means
                meaning(en = "The market is close, at the end of the street.", fr = "Le marché est proche, au bout de la rue."),
            "Lângă ușa casei, un câine doarme la soare." means
                meaning(
                    en = "Next to the door of the house, a dog sleeps in the sun.",
                    fr = "À côté de la porte de la maison, un chien dort au soleil.",
                ),
            "Ea cumpără roșii, pâine și brânză." means
                meaning(en = "She buys tomatoes, bread and cheese.", fr = "Elle achète des tomates, du pain et du fromage."),
            "„Bună dimineața, Ana! Ce mai faci?”" means
                meaning(en = "“Good morning, Ana! How are you?”", fr = "« Bonjour, Ana ! Comment vas-tu ? »"),
            "„Bine, mulțumesc. Cât costă roșiile azi?”" means
                meaning(
                    en = "“Fine, thank you. How much are the tomatoes today?”",
                    fr = "« Bien, merci. Combien coûtent les tomates aujourd'hui ? »",
                ),
        ),
        lexicon.readingText(
            "ro-station", CefrLevel.A1, TextKind.DIALOGUE, "La gară", minutes = 2,
            "— Bună ziua! Un bilet pentru Brașov, vă rog." means
                meaning(en = "— Hello! A ticket to Brașov, please.", fr = "— Bonjour ! Un billet pour Brașov, s'il vous plaît."),
            "— Pentru azi sau pentru mâine?" means
                meaning(en = "— For today or for tomorrow?", fr = "— Pour aujourd'hui ou pour demain ?"),
            "— Pentru azi. La ce oră pleacă trenul?" means
                meaning(en = "— For today. What time does the train leave?", fr = "— Pour aujourd'hui. À quelle heure part le train ?"),
            "— Trenul pleacă la ora zece." means
                meaning(en = "— The train leaves at ten o'clock.", fr = "— Le train part à dix heures."),
            "— Mulțumesc! Cât costă biletul?" means
                meaning(en = "— Thank you! How much is the ticket?", fr = "— Merci ! Combien coûte le billet ?"),
            "— Cincizeci de lei." means
                meaning(en = "— Fifty lei.", fr = "— Cinquante lei."),
        ),
        lexicon.readingText(
            "ro-coffee", CefrLevel.A1, TextKind.STORY, "Cafeaua de dimineață", minutes = 2,
            "Mihai bea cafea în fiecare dimineață." means
                meaning(en = "Mihai drinks coffee every morning.", fr = "Mihai boit du café chaque matin."),
            "Cafeaua este fierbinte și bună." means
                meaning(en = "The coffee is hot and good.", fr = "Le café est chaud et bon."),
            "El citește o carte lângă fereastră." means
                meaning(en = "He reads a book next to the window.", fr = "Il lit un livre près de la fenêtre."),
            "Pisica lui doarme pe masă." means
                meaning(en = "His cat sleeps on the table.", fr = "Son chat dort sur la table."),
            "Apoi Mihai merge la birou." means
                meaning(en = "Then Mihai goes to the office.", fr = "Ensuite, Mihai va au bureau."),
        ),
        lexicon.readingText(
            "ro-weather", CefrLevel.A2, TextKind.NEWS, "Vremea în weekend", minutes = 2,
            "Sâmbătă va fi soare în tot orașul." means
                meaning(en = "On Saturday it will be sunny in the whole city.", fr = "Samedi, il y aura du soleil dans toute la ville."),
            "Duminică seara vine ploaia." means
                meaning(en = "On Sunday evening the rain comes.", fr = "Dimanche soir, la pluie arrive."),
            "Temperaturile scad la zece grade." means
                meaning(en = "Temperatures fall to ten degrees.", fr = "Les températures baissent à dix degrés."),
            "Luați o umbrelă dacă ieșiți din casă!" means
                meaning(en = "Take an umbrella if you leave the house!", fr = "Prenez un parapluie si vous sortez de la maison !"),
        ),
        lexicon.readingText(
            "ro-park", CefrLevel.A1, TextKind.STORY, "În parc", minutes = 2,
            "Seara, Ana merge cu câinele în parc." means
                meaning(en = "In the evening, Ana goes to the park with the dog.", fr = "Le soir, Ana va au parc avec le chien."),
            "Câinele aleargă prin iarbă." means
                meaning(en = "The dog runs in the grass.", fr = "Le chien court dans l'herbe."),
            "Ana citește o carte pe o bancă." means
                meaning(en = "Ana reads a book on a bench.", fr = "Ana lit un livre sur un banc."),
            "Apoi merg acasă." means
                meaning(en = "Then they go home.", fr = "Ensuite, ils rentrent à la maison."),
        ),
        lexicon.readingText(
            "ro-cafe", CefrLevel.A1, TextKind.DIALOGUE, "La cafenea", minutes = 2,
            "— Bună ziua! O cafea, vă rog." means
                meaning(en = "— Hello! A coffee, please.", fr = "— Bonjour ! Un café, s'il vous plaît."),
            "— Cu lapte?" means
                meaning(en = "— With milk?", fr = "— Avec du lait ?"),
            "— Nu, mulțumesc. Și un corn, vă rog." means
                meaning(en = "— No, thank you. And a croissant, please.", fr = "— Non, merci. Et un croissant, s'il vous plaît."),
            "— Poftiți. Face cincisprezece lei." means
                meaning(en = "— Here you are. That's fifteen lei.", fr = "— Voilà. Ça fait quinze lei."),
            "— Mulțumesc. O zi bună!" means
                meaning(en = "— Thank you. Have a nice day!", fr = "— Merci. Bonne journée !"),
        ),
        lexicon.readingText(
            "ro-weekend", CefrLevel.A2, TextKind.STORY, "Weekendul trecut", minutes = 2,
            "Sâmbăta trecută, Mihai a mers la Brașov cu trenul." means
                meaning(en = "Last Saturday, Mihai went to Brașov by train.", fr = "Samedi dernier, Mihai est allé à Brașov en train."),
            "A vizitat orașul vechi cu sora lui." means
                meaning(en = "He visited the old town with his sister.", fr = "Il a visité la vieille ville avec sa sœur."),
            "Seara, au mâncat într-un restaurant mic." means
                meaning(en = "In the evening, they ate in a small restaurant.", fr = "Le soir, ils ont mangé dans un petit restaurant."),
            "Duminică a plouat, așa că au plecat acasă devreme." means
                meaning(
                    en = "On Sunday it rained, so they went home early.",
                    fr = "Dimanche, il a plu, alors ils sont rentrés tôt.",
                ),
        ),
    )
}
