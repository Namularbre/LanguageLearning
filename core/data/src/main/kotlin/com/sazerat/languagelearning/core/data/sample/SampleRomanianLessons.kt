package com.sazerat.languagelearning.core.data.sample

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.model.LessonSection

/** Placeholder Romanian lessons 1 to 6 for M1. Replaced by packs downloaded from the API in M2. */
internal object SampleRomanianLessons {
    private val romanian = LanguageCode("ro-RO")

    private val comparisonHeading = text(
        en = "Compared with English and French",
        fr = "Comparaison avec le français et l'anglais",
        ro = "Comparație cu engleza și franceza",
    )

    val all: List<Lesson> = listOf(
        Lesson(
            id = LessonId("ro-01-alphabet"),
            languageCode = romanian,
            order = 1,
            title = text(en = "Alphabet and diacritics", fr = "Alphabet et diacritiques", ro = "Alfabetul și diacriticele"),
            summary = text(
                en = "The five letters specific to Romanian and the reading rules.",
                fr = "Les cinq lettres propres au roumain et les règles de lecture.",
                ro = "Cele cinci litere specifice limbii române și regulile de citire.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(
                        en = "The five letters with diacritics",
                        fr = "Les cinq lettres à diacritiques",
                        ro = "Cele cinci litere cu diacritice",
                    ),
                    body = text(
                        en = "ă sounds like the a in \"about\" (schwa).\n" +
                            "â and î spell the same sound, a closed central vowel with no English equivalent: " +
                            "î at the start and end of a word (în, a coborî), â inside (român, mâine).\n" +
                            "ș sounds like English \"sh\": școală.\n" +
                            "ț sounds like \"ts\": țară.\n" +
                            "Careful: ș and ț are written with a comma below, never with a cedilla.",
                        fr = "ă se prononce comme le « e » de « le » (schwa).\n" +
                            "â et î notent le même son, une voyelle centrale fermée sans équivalent en français : " +
                            "î en début et en fin de mot (în, a coborî), â à l'intérieur (român, mâine).\n" +
                            "ș se prononce comme le « ch » français : școală.\n" +
                            "ț se prononce « ts » : țară.\n" +
                            "Attention : ș et ț s'écrivent avec une virgule dessous, jamais avec une cédille.",
                        ro = "ă notează vocala neutră /ə/.\n" +
                            "â și î notează același sunet, /ɨ/: î la începutul și la sfârșitul cuvântului " +
                            "(în, a coborî), â în interior (român, mâine).\n" +
                            "ș notează sunetul /ʃ/: școală.\n" +
                            "ț notează sunetul /t͡s/: țară.\n" +
                            "Atenție: ș și ț se scriu cu virgulă dedesubt, niciodată cu sedilă.",
                    ),
                    examples = listOf(
                        example("casă", en = "house", fr = "maison"),
                        example("în", en = "in", fr = "dans"),
                        example("mâine", en = "tomorrow", fr = "demain"),
                        example("român", en = "Romanian", fr = "roumain"),
                        example("școală", en = "school", fr = "école"),
                        example("țară", en = "country", fr = "pays"),
                    ),
                ),
                LessonSection(
                    heading = text(
                        en = "c and g before e and i",
                        fr = "c et g devant e et i",
                        ro = "c și g înainte de e și i",
                    ),
                    body = text(
                        en = "ce, ci are read with the ch of \"church\": cer, cinci.\n" +
                            "che, chi are read with a hard k: ochi, unchi.\n" +
                            "ge, gi are read with the j of \"jam\": ger, gimnaziu.\n" +
                            "ghe, ghi are read with the hard g of \"get\": ghete, ghid.\n" +
                            "As in Italian, the h makes the sound hard.",
                        fr = "ce, ci se lisent « tché », « tchi » : cer, cinci.\n" +
                            "che, chi se lisent « ké », « ki » : ochi, unchi.\n" +
                            "ge, gi se lisent « djé », « dji » : ger, gimnaziu.\n" +
                            "ghe, ghi se lisent « gué », « gui » : ghete, ghid.\n" +
                            "Comme en italien, le h rend le son dur.",
                        ro = "ce, ci se citesc /t͡ʃe/, /t͡ʃi/: cer, cinci.\n" +
                            "che, chi se citesc /ke/, /ki/: ochi, unchi.\n" +
                            "ge, gi se citesc /d͡ʒe/, /d͡ʒi/: ger, gimnaziu.\n" +
                            "ghe, ghi se citesc /ɡe/, /ɡi/: ghete, ghid.\n" +
                            "Ca în italiană, h face sunetul dur.",
                    ),
                    examples = listOf(
                        example("cer", en = "sky", fr = "ciel"),
                        example("cinci", en = "five", fr = "cinq"),
                        example("ochi", en = "eyes", fr = "yeux"),
                        example("unchi", en = "uncle", fr = "oncle"),
                        example("ger", en = "frost", fr = "gel"),
                        example("ghid", en = "guide", fr = "guide"),
                    ),
                ),
                LessonSection(
                    heading = text(
                        en = "The diphthongs ea and oa",
                        fr = "Les diphtongues ea et oa",
                        ro = "Diftongii ea și oa",
                    ),
                    body = text(
                        en = "ea is pronounced as one syllable, a gliding \"ya\": seară, dimineață.\n" +
                            "oa is pronounced \"wa\" in one syllable, with a very short o: noapte, școală.",
                        fr = "ea se prononce en une seule syllabe, « éa » glissé : seară, dimineață.\n" +
                            "oa se prononce « oa » en une syllabe, le o très bref : noapte, școală.",
                        ro = "ea se pronunță într-o singură silabă: seară, dimineață.\n" +
                            "oa se pronunță tot într-o singură silabă, cu un o foarte scurt: noapte, școală.",
                    ),
                    examples = listOf(
                        example("seară", en = "evening", fr = "soir"),
                        example("dimineață", en = "morning", fr = "matin"),
                        example("noapte", en = "night", fr = "nuit"),
                    ),
                ),
                LessonSection(
                    heading = comparisonHeading,
                    body = text(
                        en = "Romanian spelling is almost phonetic: every letter is pronounced, including final ones " +
                            "(final -i is sometimes very short: pomi). Unlike in English or French, a letter almost " +
                            "always keeps the same sound. In a few very common words, initial e is pronounced \"ye\": " +
                            "eu (\"yeoo\"), este (\"yeste\").",
                        fr = "L'orthographe roumaine est presque phonétique : chaque lettre se prononce, " +
                            "y compris les finales (le -i final est parfois très bref : pomi), contrairement au français " +
                            "et à l'anglais. Dans quelques mots très courants, e initial se prononce « ié » : " +
                            "eu (« iéou »), este (« iéste »).",
                        ro = "Ortografia românească este aproape fonetică: fiecare literă se pronunță, inclusiv la final " +
                            "(-i final este uneori foarte scurt: pomi). În engleză și în franceză, multe litere sunt mute " +
                            "sau se citesc diferit după context. În câteva cuvinte foarte frecvente, e inițial se " +
                            "pronunță „ie”: eu, este.",
                    ),
                    examples = listOf(
                        example("eu", en = "I", fr = "je"),
                        example("este", en = "is", fr = "est"),
                        example("pomi", en = "trees", fr = "arbres"),
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("ro-02-genres"),
            languageCode = romanian,
            order = 2,
            title = text(en = "Noun gender", fr = "Le genre des noms", ro = "Genul substantivelor"),
            summary = text(
                en = "Masculine, feminine… and neuter.",
                fr = "Masculin, féminin… et neutre.",
                ro = "Masculin, feminin… și neutru.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "Three genders", fr = "Trois genres", ro = "Trei genuri"),
                    body = text(
                        en = "Romanian has three genders: masculine, feminine and neuter.\n" +
                            "Masculine: un băiat (a boy), un pom (a tree).\n" +
                            "Feminine, often in -ă or -e: o fată (a girl), o casă (a house), o carte (a book).",
                        fr = "Le roumain a trois genres : masculin, féminin et neutre.\n" +
                            "Masculin : un băiat (un garçon), un pom (un arbre).\n" +
                            "Féminin, souvent en -ă ou -e : o fată (une fille), o casă (une maison), o carte (un livre).",
                        ro = "Româna are trei genuri: masculin, feminin și neutru.\n" +
                            "Masculin: un băiat, un pom.\n" +
                            "Feminin, adesea în -ă sau -e: o fată, o casă, o carte.",
                    ),
                    examples = listOf(
                        example("un băiat", en = "a boy", fr = "un garçon"),
                        example("un pom", en = "a tree", fr = "un arbre"),
                        example("o fată", en = "a girl", fr = "une fille"),
                        example("o casă", en = "a house", fr = "une maison"),
                        example("o carte", en = "a book", fr = "un livre"),
                    ),
                ),
                LessonSection(
                    heading = text(en = "The neuter", fr = "Le neutre", ro = "Neutrul"),
                    body = text(
                        en = "A neuter noun behaves like a masculine one in the singular and like a feminine one " +
                            "in the plural.\n" +
                            "un tren → două trenuri; un scaun → două scaune.\n" +
                            "Tip: say \"un …\" then \"două …\". If both sound right, the noun is neuter. " +
                            "A masculine noun takes \"un\" / \"doi\" (doi băieți), a feminine one \"o\" / \"două\" (două fete).",
                        fr = "Un nom neutre se comporte comme un masculin au singulier et comme un féminin au pluriel.\n" +
                            "un tren → două trenuri ; un scaun → două scaune.\n" +
                            "Astuce : dis « un … » puis « două … ». Si les deux sonnent juste, le nom est neutre. " +
                            "Un masculin donne « un » / « doi » (doi băieți), un féminin « o » / « două » (două fete).",
                        ro = "Un substantiv neutru se comportă ca un masculin la singular și ca un feminin la plural.\n" +
                            "un tren → două trenuri; un scaun → două scaune.\n" +
                            "Truc: spune „un …”, apoi „două …”. Dacă ambele sună corect, substantivul este neutru. " +
                            "Masculinul cere „un” / „doi” (doi băieți), femininul „o” / „două” (două fete).",
                    ),
                    examples = listOf(
                        example("un tren, două trenuri", en = "a train, two trains", fr = "un train, deux trains"),
                        example("un scaun, două scaune", en = "a chair, two chairs", fr = "une chaise, deux chaises"),
                        example("doi băieți", en = "two boys", fr = "deux garçons"),
                        example("două fete", en = "two girls", fr = "deux filles"),
                    ),
                ),
                LessonSection(
                    heading = comparisonHeading,
                    body = text(
                        en = "English nouns have no grammatical gender and French has two; Romanian has three, and " +
                            "gender often differs from French: \"le livre\" is feminine in Romanian (o carte), " +
                            "\"le train\" is neuter (un tren). Always learn a noun with its article: un / o.",
                        fr = "Le français n'a que deux genres, l'anglais aucun pour les noms, et le genre change souvent " +
                            "d'une langue à l'autre : « le livre » devient o carte (féminin), « le train » un tren (neutre). " +
                            "Apprends toujours le nom avec son article : un / o.",
                        ro = "Engleza nu are gen gramatical pentru substantive, iar franceza are doar două genuri. " +
                            "Genul diferă adesea: o carte este feminin, dar „le livre” este masculin în franceză; " +
                            "neutrul nu există în franceză, așa că un tren devine masculin: „le train”.",
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("ro-03-pluriel"),
            languageCode = romanian,
            order = 3,
            title = text(en = "Plural of nouns", fr = "Le pluriel des noms", ro = "Pluralul substantivelor"),
            summary = text(
                en = "Plural endings and sound changes.",
                fr = "Terminaisons du pluriel et alternances.",
                ro = "Terminații de plural și alternanțe.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "Masculine: -i", fr = "Masculin : -i", ro = "Masculin: -i"),
                    body = text(
                        en = "pom → pomi, câine → câini.\n" +
                            "The -i often changes the consonant before it: student → studenți (t → ț), băiat → băieți.",
                        fr = "pom → pomi, câine → câini.\n" +
                            "Le -i entraîne souvent un changement de consonne : student → studenți (t → ț), " +
                            "băiat → băieți.",
                        ro = "pom → pomi, câine → câini.\n" +
                            "-i provoacă adesea o schimbare de consoană: student → studenți (t → ț), băiat → băieți.",
                    ),
                    examples = listOf(
                        example("un pom, doi pomi", en = "a tree, two trees", fr = "un arbre, deux arbres"),
                        example("un câine, doi câini", en = "a dog, two dogs", fr = "un chien, deux chiens"),
                        example("un student, doi studenți", en = "a student, two students", fr = "un étudiant, deux étudiants"),
                    ),
                ),
                LessonSection(
                    heading = text(en = "Feminine: -e or -i", fr = "Féminin : -e ou -i", ro = "Feminin: -e sau -i"),
                    body = text(
                        en = "-ă becomes -e: casă → case, fată → fete (a → e).\n" +
                            "-e becomes -i: carte → cărți (a → ă, t → ț).\n" +
                            "-ea becomes -ele: stea → stele.\n" +
                            "oa often becomes o: școală → școli.",
                        fr = "-ă devient -e : casă → case, fată → fete (a → e).\n" +
                            "-e devient -i : carte → cărți (a → ă, t → ț).\n" +
                            "-ea devient -ele : stea → stele.\n" +
                            "oa devient souvent o : școală → școli.",
                        ro = "-ă devine -e: casă → case, fată → fete (a → e).\n" +
                            "-e devine -i: carte → cărți (a → ă, t → ț).\n" +
                            "-ea devine -ele: stea → stele.\n" +
                            "oa devine adesea o: școală → școli.",
                    ),
                    examples = listOf(
                        example("o casă, două case", en = "a house, two houses", fr = "une maison, deux maisons"),
                        example("o fată, două fete", en = "a girl, two girls", fr = "une fille, deux filles"),
                        example("o carte, două cărți", en = "a book, two books", fr = "un livre, deux livres"),
                        example("o stea, două stele", en = "a star, two stars", fr = "une étoile, deux étoiles"),
                        example("o școală, două școli", en = "a school, two schools", fr = "une école, deux écoles"),
                    ),
                ),
                LessonSection(
                    heading = text(en = "Neuter: -uri or -e", fr = "Neutre : -uri ou -e", ro = "Neutru: -uri sau -e"),
                    body = same("tren → trenuri, scaun → scaune, oraș → orașe."),
                    examples = listOf(
                        example("un tren, două trenuri", en = "a train, two trains", fr = "un train, deux trains"),
                        example("un oraș, două orașe", en = "a city, two cities", fr = "une ville, deux villes"),
                    ),
                ),
                LessonSection(
                    heading = comparisonHeading,
                    body = text(
                        en = "In English, the plural is mostly a regular -s; in French, the plural -s is silent. " +
                            "In Romanian, the plural is always heard and often changes the vowel or consonant of the " +
                            "stem: learn the plural together with the word.",
                        fr = "En français, le -s du pluriel est muet ; en anglais, le pluriel est presque toujours régulier. " +
                            "En roumain, le pluriel s'entend toujours et modifie souvent la voyelle ou la consonne du " +
                            "radical : apprends le pluriel avec le mot.",
                        ro = "În engleză, pluralul se formează aproape mereu regulat cu -s; în franceză, -s de plural nu " +
                            "se pronunță. În română, pluralul se aude întotdeauna și schimbă adesea vocala sau consoana " +
                            "rădăcinii: învață pluralul odată cu cuvântul.",
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("ro-04-definite-article"),
            languageCode = romanian,
            order = 4,
            title = text(en = "The definite article", fr = "L'article défini", ro = "Articolul hotărât"),
            summary = text(
                en = "\"The\" is glued to the end of the noun.",
                fr = "« Le, la » se collent à la fin du nom.",
                ro = "Articolul hotărât se adaugă la sfârșitul substantivului.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "Masculine and neuter: -ul, -le", fr = "Masculin et neutre : -ul, -le", ro = "Masculin și neutru: -ul, -le"),
                    body = text(
                        en = "A noun ending in a consonant takes -ul: băiat → băiatul, tren → trenul.\n" +
                            "A noun ending in -e takes -le: frate → fratele, nume → numele.",
                        fr = "Un nom terminé par une consonne prend -ul : băiat → băiatul, tren → trenul.\n" +
                            "Un nom terminé par -e prend -le : frate → fratele, nume → numele.",
                        ro = "Substantivele terminate în consoană primesc -ul: băiat → băiatul, tren → trenul.\n" +
                            "Cele terminate în -e primesc -le: frate → fratele, nume → numele.",
                    ),
                    examples = listOf(
                        example("băiatul", en = "the boy", fr = "le garçon"),
                        example("trenul", en = "the train", fr = "le train"),
                        example("fratele", en = "the brother", fr = "le frère"),
                    ),
                ),
                LessonSection(
                    heading = text(en = "Feminine: -a", fr = "Féminin : -a", ro = "Feminin: -a"),
                    body = text(
                        en = "The final -ă becomes -a: casă → casa. A final -e becomes -ea: carte → cartea.\n" +
                            "Nouns in -ea take -ua: cafea → cafeaua.",
                        fr = "Le -ă final devient -a : casă → casa. Un -e final devient -ea : carte → cartea.\n" +
                            "Les noms en -ea prennent -ua : cafea → cafeaua.",
                        ro = "-ă final devine -a: casă → casa. -e final devine -ea: carte → cartea.\n" +
                            "Substantivele în -ea primesc -ua: cafea → cafeaua.",
                    ),
                    examples = listOf(
                        example("casa", en = "the house", fr = "la maison"),
                        example("cartea", en = "the book", fr = "le livre"),
                        example("cafeaua", en = "the coffee", fr = "le café"),
                    ),
                ),
                LessonSection(
                    heading = comparisonHeading,
                    body = text(
                        en = "English and French put the article before the noun. In Romanian, \"a\" stays in front " +
                            "(un tren, o casă) but \"the\" is an ending: listen for the end of the word.",
                        fr = "Le français et l'anglais placent l'article devant le nom. En roumain, l'article indéfini " +
                            "reste devant (un tren, o casă), mais l'article défini est une terminaison : écoute la fin du mot.",
                        ro = "Engleza și franceza pun articolul înaintea substantivului. În română, articolul nehotărât " +
                            "stă înainte (un tren, o casă), dar cel hotărât este o terminație.",
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("ro-05-a-fi"),
            languageCode = romanian,
            order = 5,
            title = text(en = "The verb a fi (to be)", fr = "Le verbe a fi (être)", ro = "Verbul a fi"),
            summary = text(
                en = "Personal pronouns and the present of \"to be\".",
                fr = "Les pronoms personnels et le présent d'« être ».",
                ro = "Pronumele personale și prezentul verbului a fi.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "The present", fr = "Le présent", ro = "Prezentul"),
                    body = same(
                        "eu sunt, tu ești, el / ea este (e)\nnoi suntem, voi sunteți, ei / ele sunt",
                    ),
                    examples = listOf(
                        example("Eu sunt Ana.", en = "I am Ana.", fr = "Je suis Ana."),
                        example("Tu ești acasă?", en = "Are you at home?", fr = "Tu es à la maison ?"),
                        example("Ea este profesoară.", en = "She is a teacher.", fr = "Elle est professeure."),
                        example("Noi suntem prieteni.", en = "We are friends.", fr = "Nous sommes amis."),
                    ),
                ),
                LessonSection(
                    heading = text(en = "Dropping the pronoun", fr = "Le pronom omis", ro = "Pronumele omis"),
                    body = text(
                        en = "The verb ending already says who: the pronoun is usually left out, " +
                            "and kept only for emphasis. \"Sunt din Franța\" = I am from France.",
                        fr = "La terminaison du verbe indique déjà la personne : le pronom est souvent omis et ne sert " +
                            "qu'à insister. « Sunt din Franța » = Je suis de France.",
                        ro = "Terminația verbului arată deja persoana, așa că pronumele se omite de obicei " +
                            "și se folosește doar pentru accent.",
                    ),
                    examples = listOf(
                        example("Sunt din Franța.", en = "I am from France.", fr = "Je suis de France."),
                        example("Suntem obosiți.", en = "We are tired.", fr = "Nous sommes fatigués."),
                        example("E bine.", en = "It's fine.", fr = "C'est bien."),
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("ro-06-numbers"),
            languageCode = romanian,
            order = 6,
            title = text(en = "Numbers and prices", fr = "Les nombres et les prix", ro = "Numerele și prețurile"),
            summary = text(
                en = "Counting to twenty and asking how much.",
                fr = "Compter jusqu'à vingt et demander combien.",
                ro = "Numărăm până la douăzeci și întrebăm cât costă.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "One to ten", fr = "De un à dix", ro = "De la unu la zece"),
                    body = text(
                        en = "unu, doi, trei, patru, cinci, șase, șapte, opt, nouă, zece.\n" +
                            "Before a noun, one and two agree: un leu, o bancnotă; doi lei, două mere.",
                        fr = "unu, doi, trei, patru, cinci, șase, șapte, opt, nouă, zece.\n" +
                            "Devant un nom, un et deux s'accordent : un leu, o bancnotă ; doi lei, două mere.",
                        ro = "unu, doi, trei, patru, cinci, șase, șapte, opt, nouă, zece.\n" +
                            "Înaintea unui substantiv, unu și doi se acordă: un leu, o bancnotă; doi lei, două mere.",
                    ),
                    examples = listOf(
                        example("trei", en = "three", fr = "trois"),
                        example("șapte", en = "seven", fr = "sept"),
                        example("doi lei", en = "two lei", fr = "deux lei"),
                        example("două mere", en = "two apples", fr = "deux pommes"),
                    ),
                ),
                LessonSection(
                    heading = text(en = "From twenty: add de", fr = "À partir de vingt : ajoutez de", ro = "De la douăzeci: se adaugă de"),
                    body = text(
                        en = "From 20 on, de comes between the number and the noun: douăzeci de lei, o sută de grame.",
                        fr = "À partir de 20, on place de entre le nombre et le nom : douăzeci de lei, o sută de grame.",
                        ro = "De la 20 în sus, între numeral și substantiv apare de: douăzeci de lei, o sută de grame.",
                    ),
                    examples = listOf(
                        example("Cât costă?", en = "How much is it?", fr = "Combien ça coûte ?"),
                        example("Douăzeci de lei.", en = "Twenty lei.", fr = "Vingt lei."),
                        example("Cincizeci de lei.", en = "Fifty lei.", fr = "Cinquante lei."),
                    ),
                ),
            ),
        ),
    )
}
