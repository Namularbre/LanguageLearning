package com.sazerat.languagelearning.core.data.sample

import com.sazerat.languagelearning.core.domain.model.CefrLevel
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

/** English texts of the Library and the words they use (British English). */
internal object SampleEnglishReading {
    private val presentHeShe = text("present, he/she/it", "présent, il/elle", "prezent, el/ea")
    private val plural = text("plural", "pluriel", "plural")
    private val past = text("past simple: -ed", "prétérit (past simple) : -ed", "past simple: -ed")
    private val pastIrregular = text("past simple, irregular", "prétérit irrégulier", "past simple, neregulat")
    private val thNote = text(
        "th: the tip of the tongue between the teeth.",
        "th : le bout de la langue entre les dents.",
        "th: vârful limbii între dinți.",
    )

    private val lexicon = Lexicon(LanguageCode("en-GB")).apply {
        names("Anna", "London", "Tom")

        word("the", ARTICLE, 1, meaning(fr = "le, la, les", ro = "articolul hotărât (-ul, -a)"), ipa = "ðə", note = thNote)
        word(
            "be", VERB, 2, meaning(fr = "être", ro = "a fi"), ipa = "biː",
            forms = mapOf("is" to presentHeShe, "are" to text("present, you/we/they", "présent, tu/nous/vous/ils", "prezent, tu/noi/voi/ei")),
        )
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
        word("like", VERB, 15, meaning(fr = "aimer", ro = "a plăcea"), ipa = "laɪk", forms = mapOf("likes" to presentHeShe))
        word("take", VERB, 16, meaning(fr = "prendre", ro = "a lua"), ipa = "teɪk", forms = mapOf("takes" to presentHeShe))
        word(
            "walk", VERB, 17, meaning(fr = "marcher, aller à pied", ro = "a merge pe jos"),
            ipa = "wɔːk", forms = mapOf("walks" to presentHeShe),
        )
        word(
            "buy", VERB, 18, meaning(fr = "acheter", ro = "a cumpăra"), ipa = "baɪ",
            forms = mapOf("buys" to presentHeShe, "bought" to pastIrregular),
        )
        word("wait", VERB, 19, meaning(fr = "attendre", ro = "a aștepta"), ipa = "weɪt", forms = mapOf("waits" to presentHeShe))
        word(
            "rain", VERB, 20, meaning(fr = "pleuvoir", ro = "a ploua"), ipa = "reɪn",
            forms = mapOf(
                "raining" to text(
                    "-ing form: it is raining = it rains now",
                    "forme en -ing : it is raining = il pleut (en ce moment)",
                    "forma -ing: it is raining = plouă (acum)",
                ),
                "rained" to past,
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
        word(
            "she", PRONOUN, 45, meaning(fr = "elle", ro = "ea"), ipa = "ʃiː",
            forms = mapOf("her" to text("possessive: her", "possessif : son, sa, ses (à elle)", "posesiv: ei")),
        )
        word("dog", NOUN, 46, meaning(fr = "chien", ro = "câine"), ipa = "dɒɡ")
        word("park", NOUN, 47, meaning(fr = "parc", ro = "parc"), ipa = "pɑːk")
        word("every", ADJECTIVE, 48, meaning(fr = "chaque, tous les", ro = "fiecare"), ipa = "ˈevri")
        word("evening", NOUN, 49, meaning(fr = "soir", ro = "seară"), ipa = "ˈiːvnɪŋ")
        word("green", ADJECTIVE, 50, meaning(fr = "vert", ro = "verde"), ipa = "ɡriːn")
        word("grass", NOUN, 51, meaning(fr = "herbe", ro = "iarbă"), ipa = "ɡrɑːs")
        word("sit", VERB, 52, meaning(fr = "s'asseoir, être assis", ro = "a sta jos"), ipa = "sɪt", forms = mapOf("sits" to presentHeShe))
        word("on", PREPOSITION, 53, meaning(fr = "sur", ro = "pe"), ipa = "ɒn")
        word("bench", NOUN, 54, meaning(fr = "banc", ro = "bancă"), ipa = "bentʃ")
        word("read", VERB, 55, meaning(fr = "lire", ro = "a citi"), ipa = "riːd", forms = mapOf("reads" to presentHeShe))
        word("book", NOUN, 56, meaning(fr = "livre", ro = "carte"), ipa = "bʊk")
        word("then", ADVERB, 57, meaning(fr = "ensuite, puis", ro = "apoi"), ipa = "ðen", note = thNote)
        word("they", PRONOUN, 58, meaning(fr = "ils, elles", ro = "ei, ele"), ipa = "ðeɪ", note = thNote)
        word("go", VERB, 59, meaning(fr = "aller", ro = "a merge"), ipa = "ɡəʊ", forms = mapOf("went" to pastIrregular))
        word("home", ADVERB, 60, meaning(fr = "à la maison, chez soi", ro = "acasă"), ipa = "həʊm")
        word("hello", INTERJECTION, 61, meaning(fr = "bonjour", ro = "bună ziua, salut"), ipa = "həˈləʊ")
        word("what", PRONOUN, 62, meaning(fr = "que, quoi", ro = "ce"), ipa = "wɒt")
        word("tea", NOUN, 63, meaning(fr = "thé", ro = "ceai"), ipa = "tiː")
        word("with", PREPOSITION, 64, meaning(fr = "avec", ro = "cu"), ipa = "wɪð", note = thNote)
        word("milk", NOUN, 65, meaning(fr = "lait", ro = "lapte"), ipa = "mɪlk")
        word("small", ADJECTIVE, 66, meaning(fr = "petit", ro = "mic"), ipa = "smɔːl")
        word("or", CONJUNCTION, 67, meaning(fr = "ou", ro = "sau"), ipa = "ɔː")
        word("large", ADJECTIVE, 68, meaning(fr = "grand", ro = "mare"), ipa = "lɑːdʒ")
        word("four", NUMERAL, 69, meaning(fr = "quatre", ro = "patru"), ipa = "fɔː")
        word(
            "here", ADVERB, 70, meaning(fr = "ici", ro = "aici"),
            other = meaning(fr = "here you are = voilà", ro = "here you are = poftiți"), ipa = "hɪə",
        )
        word("last", ADJECTIVE, 71, meaning(fr = "dernier", ro = "trecut, ultimul"), ipa = "lɑːst")
        word("Saturday", NOUN, 72, meaning(fr = "samedi", ro = "sâmbătă"), ipa = "ˈsætədeɪ")
        word("market", NOUN, 73, meaning(fr = "marché", ro = "piață"), ipa = "ˈmɑːkɪt")
        word("sister", NOUN, 74, meaning(fr = "sœur", ro = "soră"), ipa = "ˈsɪstə")
        word("bread", NOUN, 75, meaning(fr = "pain", ro = "pâine"), ipa = "bred")
        word("cheese", NOUN, 76, meaning(fr = "fromage", ro = "brânză"), ipa = "tʃiːz")
        word("apple", NOUN, 77, meaning(fr = "pomme", ro = "măr"), ipa = "ˈæpl", forms = mapOf("apples" to plural))
        word("so", CONJUNCTION, 78, meaning(fr = "donc, alors", ro = "așa că"), ipa = "səʊ")
        word("drink", VERB, 79, meaning(fr = "boire", ro = "a bea"), ipa = "drɪŋk", forms = mapOf("drank" to pastIrregular))
        word("watch", VERB, 80, meaning(fr = "regarder", ro = "a se uita la"), ipa = "wɒtʃ", forms = mapOf("watched" to past))
        word("film", NOUN, 81, meaning(fr = "film", ro = "film"), ipa = "fɪlm")
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
        lexicon.readingText(
            "en-park", CefrLevel.A1, TextKind.STORY, "A walk in the park", minutes = 2,
            "Anna and her dog walk in the park every evening." means
                meaning(
                    fr = "Anna et son chien se promènent au parc chaque soir.",
                    ro = "Anna și câinele ei se plimbă în parc în fiecare seară.",
                ),
            "The dog likes the green grass." means
                meaning(fr = "Le chien aime l'herbe verte.", ro = "Câinelui îi place iarba verde."),
            "Anna sits on a bench and reads a book." means
                meaning(fr = "Anna s'assoit sur un banc et lit un livre.", ro = "Anna stă pe o bancă și citește o carte."),
            "Then they walk home." means
                meaning(fr = "Ensuite, ils rentrent à pied à la maison.", ro = "Apoi merg pe jos acasă."),
        ),
        lexicon.readingText(
            "en-cafe", CefrLevel.A1, TextKind.DIALOGUE, "At the café", minutes = 2,
            "— Hello! What would you like?" means
                meaning(fr = "— Bonjour ! Qu'est-ce que vous désirez ?", ro = "— Bună ziua! Ce doriți?"),
            "— A tea with milk, please." means
                meaning(fr = "— Un thé au lait, s'il vous plaît.", ro = "— Un ceai cu lapte, vă rog."),
            "— Small or large?" means
                meaning(fr = "— Petit ou grand ?", ro = "— Mic sau mare?"),
            "— Small, please. And a croissant." means
                meaning(fr = "— Petit, s'il vous plaît. Et un croissant.", ro = "— Mic, vă rog. Și un corn."),
            "— That is four pounds." means
                meaning(fr = "— Ça fait quatre livres.", ro = "— Face patru lire."),
            "— Here you are. Thank you!" means
                meaning(fr = "— Voilà. Merci !", ro = "— Poftiți. Mulțumesc!"),
        ),
        lexicon.readingText(
            "en-weekend", CefrLevel.A2, TextKind.STORY, "Last weekend", minutes = 2,
            "Last Saturday, Tom went to the market with his sister." means
                meaning(
                    fr = "Samedi dernier, Tom est allé au marché avec sa sœur.",
                    ro = "Sâmbăta trecută, Tom a mers la piață cu sora lui.",
                ),
            "They bought bread, cheese and apples." means
                meaning(fr = "Ils ont acheté du pain, du fromage et des pommes.", ro = "Au cumpărat pâine, brânză și mere."),
            "In the afternoon, it rained, so they went home." means
                meaning(
                    fr = "L'après-midi, il a plu, alors ils sont rentrés.",
                    ro = "După-amiază a plouat, așa că s-au dus acasă.",
                ),
            "They drank tea and watched a film." means
                meaning(fr = "Ils ont bu du thé et regardé un film.", ro = "Au băut ceai și s-au uitat la un film."),
        ),
    )
}
