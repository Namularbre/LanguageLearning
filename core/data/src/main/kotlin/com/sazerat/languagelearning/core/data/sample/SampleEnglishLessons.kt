package com.sazerat.languagelearning.core.data.sample

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.model.LessonSection

/** Placeholder English (UK) lessons 1 to 6 for M1. Replaced by packs downloaded from the API in M2. */
internal object SampleEnglishLessons {
    private val englishUk = LanguageCode("en-GB")

    private val comparisonHeading = text(
        en = "Compared with French and Romanian",
        fr = "Comparaison avec le français et le roumain",
        ro = "Comparație cu franceza și româna",
    )

    val all: List<Lesson> = listOf(
        Lesson(
            id = LessonId("en-01-present"),
            languageCode = englishUk,
            order = 1,
            title = text(
                en = "Present simple and present continuous",
                fr = "Present simple et present continuous",
                ro = "Present simple și present continuous",
            ),
            summary = text(
                en = "Two present tenses where French and Romanian have one.",
                fr = "Deux présents là où le français et le roumain n'en ont qu'un.",
                ro = "Două timpuri prezente, acolo unde franceza și româna au unul singur.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(
                        en = "Present simple: habits and facts",
                        fr = "Present simple : les habitudes et les vérités",
                        ro = "Present simple: obiceiuri și adevăruri generale",
                    ),
                    body = text(
                        en = "It describes what is generally true or what you do regularly.\n" +
                            "In the third person singular, the verb takes an -s: he works, she lives.\n" +
                            "Common words: always, usually, often, never, every day.",
                        fr = "Il décrit ce qui est vrai en général ou ce qu'on fait régulièrement.\n" +
                            "À la 3e personne du singulier, le verbe prend un -s : he works, she lives.\n" +
                            "Mots fréquents : always, usually, often, never, every day.",
                        ro = "Descrie ce este adevărat în general sau ce faci în mod regulat.\n" +
                            "La persoana a III-a singular, verbul primește -s: he works, she lives.\n" +
                            "Cuvinte frecvente: always, usually, often, never, every day.",
                    ),
                    examples = listOf(
                        example("I work in London.", fr = "Je travaille à Londres.", ro = "Lucrez la Londra."),
                        example("She lives near the station.", fr = "Elle habite près de la gare.", ro = "Locuiește lângă gară."),
                        example(
                            "Water boils at a hundred degrees.",
                            fr = "L'eau bout à cent degrés.",
                            ro = "Apa fierbe la o sută de grade.",
                        ),
                        example(
                            "We usually have tea at five.",
                            fr = "Nous prenons généralement le thé à cinq heures.",
                            ro = "De obicei bem ceai la ora cinci.",
                        ),
                    ),
                ),
                LessonSection(
                    heading = text(
                        en = "Present continuous: what is happening now",
                        fr = "Present continuous : ce qui se passe maintenant",
                        ro = "Present continuous: ce se întâmplă acum",
                    ),
                    body = text(
                        en = "be + verb + -ing. It describes an action in progress or a temporary situation.\n" +
                            "Common words: now, at the moment, this week.",
                        fr = "be + verbe en -ing. Il décrit une action en cours ou une situation temporaire.\n" +
                            "Mots fréquents : now, at the moment, this week.",
                        ro = "be + verb cu -ing. Descrie o acțiune în desfășurare sau o situație temporară.\n" +
                            "Cuvinte frecvente: now, at the moment, this week.",
                    ),
                    examples = listOf(
                        example("I'm working at the moment.", fr = "Je suis en train de travailler.", ro = "Lucrez în acest moment."),
                        example("It's raining.", fr = "Il pleut (en ce moment).", ro = "Plouă (acum)."),
                        example(
                            "She's staying with friends this week.",
                            fr = "Elle loge chez des amis cette semaine.",
                            ro = "Stă la niște prieteni săptămâna asta.",
                        ),
                    ),
                ),
                LessonSection(
                    heading = comparisonHeading,
                    body = text(
                        en = "French \"je travaille\" and Romanian \"lucrez\" translate as I work (in general) or " +
                            "I'm working (now), depending on the context. State verbs (know, like, want, understand) " +
                            "stay in the present simple: I know, never I'm knowing.",
                        fr = "« Je travaille » (comme le roumain « lucrez ») se traduit selon le contexte : I work (en général) " +
                            "ou I'm working (maintenant). Les verbes d'état (know, like, want, understand) restent au " +
                            "present simple : on dit I know, jamais I'm knowing.",
                        ro = "„Lucrez” (ca și francezul „je travaille”) se traduce după context: I work (în general) " +
                            "sau I'm working (acum). Verbele de stare (know, like, want, understand) rămân la " +
                            "present simple: I know, niciodată I'm knowing.",
                    ),
                    examples = listOf(
                        example("I know the answer.", fr = "Je connais la réponse.", ro = "Știu răspunsul."),
                        example("Do you understand?", fr = "Tu comprends ?", ro = "Înțelegi?"),
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("en-02-questions"),
            languageCode = englishUk,
            order = 2,
            title = text(
                en = "Questions and negatives with do",
                fr = "Questions et négations avec do",
                ro = "Întrebări și negații cu do",
            ),
            summary = text(
                en = "The auxiliary do / does / did.",
                fr = "L'auxiliaire do / does / did.",
                ro = "Auxiliarul do / does / did.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "Asking a question", fr = "Poser une question", ro = "Cum pui o întrebare"),
                    body = text(
                        en = "In the present simple, a question puts do (does in the third person) before the subject. " +
                            "The verb then goes back to its base form: Does she work? (not Does she works?).\n" +
                            "With be and modal verbs (can, must…), just swap them with the subject: Are you ready? Can you swim?",
                        fr = "Au present simple, la question se forme avec do (does à la 3e personne) devant le sujet. " +
                            "Le verbe revient alors à la forme de base : Does she work? (pas Does she works?).\n" +
                            "Avec be et les modaux (can, must…), on inverse simplement : Are you ready? Can you swim?",
                        ro = "La present simple, întrebarea se formează cu do (does la persoana a III-a) înaintea subiectului. " +
                            "Verbul revine apoi la forma de bază: Does she work? (nu Does she works?).\n" +
                            "Cu be și cu verbele modale (can, must…), doar se inversează: Are you ready? Can you swim?",
                    ),
                    examples = listOf(
                        example("Do you like coffee?", fr = "Tu aimes le café ?", ro = "Îți place cafeaua?"),
                        example("Does he speak French?", fr = "Est-ce qu'il parle français ?", ro = "Vorbește franceză?"),
                        example("Where do they live?", fr = "Où habitent-ils ?", ro = "Unde locuiesc?"),
                        example("Are you ready?", fr = "Tu es prêt ?", ro = "Ești gata?"),
                    ),
                ),
                LessonSection(
                    heading = text(en = "Negatives", fr = "La négation", ro = "Negația"),
                    body = text(
                        en = "do not (don't) / does not (doesn't) + base verb.\n" +
                            "In the past, did not (didn't) for every person, and the verb stays in its base form: " +
                            "I didn't go (not I didn't went).",
                        fr = "do not (don't) / does not (doesn't) + verbe de base.\n" +
                            "Au passé, did not (didn't) pour toutes les personnes, et le verbe reste à la base : " +
                            "I didn't go (pas I didn't went).",
                        ro = "do not (don't) / does not (doesn't) + verbul la forma de bază.\n" +
                            "La trecut, did not (didn't) pentru toate persoanele, iar verbul rămâne la forma de bază: " +
                            "I didn't go (nu I didn't went).",
                    ),
                    examples = listOf(
                        example("I don't eat meat.", fr = "Je ne mange pas de viande.", ro = "Nu mănânc carne."),
                        example("She doesn't drive.", fr = "Elle ne conduit pas.", ro = "Nu conduce."),
                        example("We didn't go out yesterday.", fr = "Nous ne sommes pas sortis hier.", ro = "Nu am ieșit ieri."),
                    ),
                ),
                LessonSection(
                    heading = comparisonHeading,
                    body = text(
                        en = "French and Romanian can ask a question with intonation alone (\"Tu viens ?\", \"Vii?\"). " +
                            "In English, the auxiliary is required: Are you coming?, Do you want some? " +
                            "For negatives, English needs don't / doesn't / didn't, where Romanian puts nu before the verb " +
                            "and French surrounds it with ne … pas.",
                        fr = "Le français, comme le roumain (« Vii? »), peut poser une question par l'intonation seule " +
                            "(« Tu viens ? »). En anglais, l'auxiliaire est obligatoire : Are you coming?, Do you want some? " +
                            "Pour la négation, l'anglais a besoin de don't / doesn't / didn't, là où le français entoure " +
                            "le verbe (ne … pas) et le roumain place nu devant.",
                        ro = "Româna, ca și franceza („Tu viens ?”), poate pune o întrebare doar prin intonație („Vii?”). " +
                            "În engleză, auxiliarul este obligatoriu: Are you coming?, Do you want some? " +
                            "Pentru negație, engleza are nevoie de don't / doesn't / didn't, acolo unde româna pune nu " +
                            "înaintea verbului, iar franceza îl încadrează cu ne … pas.",
                    ),
                    examples = listOf(
                        example("Are you coming?", fr = "Tu viens ?", ro = "Vii?"),
                        example("Did you see it?", fr = "Tu l'as vu ?", ro = "L-ai văzut?"),
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("en-03-present-perfect"),
            languageCode = englishUk,
            order = 3,
            title = text(en = "The present perfect", fr = "Le present perfect", ro = "Present perfect"),
            summary = text(
                en = "have + past participle: a past linked to the present.",
                fr = "have + participe passé : un passé relié au présent.",
                ro = "have + participiu trecut: un trecut legat de prezent.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "Form", fr = "Formation", ro = "Formare"),
                    body = text(
                        en = "have / has + past participle: I have finished, she has eaten.\n" +
                            "Irregular participles must be learnt by heart: go → gone, see → seen, be → been.",
                        fr = "have / has + participe passé : I have finished, she has eaten.\n" +
                            "Les participes irréguliers s'apprennent par cœur : go → gone, see → seen, be → been.",
                        ro = "have / has + participiu trecut: I have finished, she has eaten.\n" +
                            "Participiile neregulate se învață pe de rost: go → gone, see → seen, be → been.",
                    ),
                    examples = listOf(
                        example("I've finished my homework.", fr = "J'ai fini mes devoirs.", ro = "Mi-am terminat temele."),
                        example("She has lost her keys.", fr = "Elle a perdu ses clés.", ro = "Și-a pierdut cheile."),
                        example(
                            "Have you ever been to Scotland?",
                            fr = "Es-tu déjà allé en Écosse ?",
                            ro = "Ai fost vreodată în Scoția?",
                        ),
                    ),
                ),
                LessonSection(
                    heading = text(
                        en = "Present perfect or past simple?",
                        fr = "Present perfect ou prétérit ?",
                        ro = "Present perfect sau past simple?",
                    ),
                    body = text(
                        en = "Past simple (I saw): a finished action at a definite time in the past " +
                            "(yesterday, in 2020, last week).\n" +
                            "Present perfect (I have seen): no definite time, or a link with now " +
                            "(ever, never, already, yet, just, since, for).",
                        fr = "Prétérit (I saw) : action terminée à un moment précis du passé (yesterday, in 2020, last week).\n" +
                            "Present perfect (I have seen) : pas de moment précis, ou lien avec maintenant " +
                            "(ever, never, already, yet, just, since, for).",
                        ro = "Past simple (I saw): o acțiune încheiată la un moment precis din trecut " +
                            "(yesterday, in 2020, last week).\n" +
                            "Present perfect (I have seen): fără moment precis sau cu legătură cu prezentul " +
                            "(ever, never, already, yet, just, since, for).",
                    ),
                    examples = listOf(
                        example(
                            "I saw that film last week.",
                            fr = "J'ai vu ce film la semaine dernière.",
                            ro = "Am văzut filmul acela săptămâna trecută.",
                        ),
                        example("I've already seen that film.", fr = "J'ai déjà vu ce film.", ro = "Am văzut deja filmul acela."),
                        example(
                            "I've lived here for three years.",
                            fr = "J'habite ici depuis trois ans.",
                            ro = "Locuiesc aici de trei ani.",
                        ),
                    ),
                ),
                LessonSection(
                    heading = comparisonHeading,
                    body = text(
                        en = "The French passé composé and the Romanian perfect compus often translate as a past simple: " +
                            "\"Je l'ai vu hier\" / \"L-am văzut ieri\" is I saw him yesterday, never I have seen him yesterday.\n" +
                            "French \"depuis\" and Romanian \"din\" / \"de\" with the present become a present perfect: " +
                            "\"J'habite ici depuis 2020\" / \"Locuiesc aici din 2020\" is I've lived here since 2020.",
                        fr = "Le passé composé français (comme le perfect compus roumain) se traduit souvent par un prétérit : " +
                            "« Je l'ai vu hier » donne I saw him yesterday, jamais I have seen him yesterday.\n" +
                            "« Depuis » + présent devient un present perfect : « J'habite ici depuis 2020 » donne " +
                            "I've lived here since 2020.",
                        ro = "Perfectul compus românesc (ca și passé composé francez) se traduce adesea printr-un past simple: " +
                            "„L-am văzut ieri” devine I saw him yesterday, niciodată I have seen him yesterday.\n" +
                            "„Din” / „de” + prezent devine present perfect: „Locuiesc aici din 2020” devine " +
                            "I've lived here since 2020.",
                    ),
                    examples = listOf(
                        example("I saw him yesterday.", fr = "Je l'ai vu hier.", ro = "L-am văzut ieri."),
                        example("I've lived here since 2020.", fr = "J'habite ici depuis 2020.", ro = "Locuiesc aici din 2020."),
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("en-04-articles"),
            languageCode = englishUk,
            order = 4,
            title = text(en = "a, an and the", fr = "a, an et the", ro = "a, an și the"),
            summary = text(
                en = "When to use an article, and when not to.",
                fr = "Quand mettre un article, et quand s'en passer.",
                ro = "Când folosim articolul și când nu.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "a or an", fr = "a ou an", ro = "a sau an"),
                    body = text(
                        en = "The choice depends on the first sound, not the first letter: a book, a university; " +
                            "an apple, an hour.",
                        fr = "Le choix dépend du premier son, pas de la première lettre : a book, a university ; " +
                            "an apple, an hour (le h ne se prononce pas).",
                        ro = "Alegerea depinde de primul sunet, nu de prima literă: a book, a university; " +
                            "an apple, an hour (h nu se pronunță).",
                    ),
                    examples = listOf(
                        example("a book", fr = "un livre", ro = "o carte"),
                        example("an apple", fr = "une pomme", ro = "un măr"),
                        example("an hour", fr = "une heure", ro = "o oră"),
                        example("a university", fr = "une université", ro = "o universitate"),
                    ),
                ),
                LessonSection(
                    heading = text(en = "No article", fr = "Pas d'article", ro = "Fără articol"),
                    body = text(
                        en = "Plural or uncountable nouns used in general take no article: I like coffee, " +
                            "dogs are friendly. the means a specific one: the coffee in this cup.",
                        fr = "Un nom au pluriel ou indénombrable pris au sens général n'a pas d'article : I like coffee " +
                            "(j'aime le café), dogs are friendly. the désigne une chose précise : the coffee in this cup.",
                        ro = "Substantivele la plural sau nenumărabile, luate în general, nu au articol: I like coffee " +
                            "(îmi place cafeaua), dogs are friendly. the arată ceva anume: the coffee in this cup.",
                    ),
                    examples = listOf(
                        example("I like coffee.", fr = "J'aime le café.", ro = "Îmi place cafeaua."),
                        example("Dogs are friendly.", fr = "Les chiens sont gentils.", ro = "Câinii sunt prietenoși."),
                        example("The coffee is cold.", fr = "Le café est froid.", ro = "Cafeaua e rece."),
                    ),
                ),
                LessonSection(
                    heading = comparisonHeading,
                    body = text(
                        en = "French and Romanian use the definite article for general statements (j'aime le café, " +
                            "îmi place cafeaua); English does not.",
                        fr = "En français, on dit « j'aime le café » avec un article ; en anglais, le sens général se dit " +
                            "sans article : I like coffee. C'est l'erreur la plus fréquente des francophones.",
                        ro = "În română spunem „îmi place cafeaua”, cu articol; în engleză, sensul general se exprimă " +
                            "fără articol: I like coffee.",
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("en-05-past-simple"),
            languageCode = englishUk,
            order = 5,
            title = text(en = "The past simple", fr = "Le past simple", ro = "Past simple"),
            summary = text(
                en = "Finished actions: -ed and irregular verbs.",
                fr = "Les actions terminées : -ed et verbes irréguliers.",
                ro = "Acțiuni încheiate: -ed și verbe neregulate.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "Regular verbs: -ed", fr = "Verbes réguliers : -ed", ro = "Verbe regulate: -ed"),
                    body = text(
                        en = "Add -ed, the same for every person: I worked, she played, they visited.\n" +
                            "It is used with a finished time: yesterday, last week, in 2020.",
                        fr = "On ajoute -ed, identique à toutes les personnes : I worked, she played, they visited.\n" +
                            "Il s'emploie avec un moment terminé : yesterday, last week, in 2020.",
                        ro = "Se adaugă -ed, la fel pentru toate persoanele: I worked, she played, they visited.\n" +
                            "Se folosește cu un moment încheiat: yesterday, last week, in 2020.",
                    ),
                    examples = listOf(
                        example("I worked yesterday.", fr = "J'ai travaillé hier.", ro = "Am lucrat ieri."),
                        example("She visited London.", fr = "Elle a visité Londres.", ro = "Ea a vizitat Londra."),
                    ),
                ),
                LessonSection(
                    heading = text(en = "Irregular verbs", fr = "Verbes irréguliers", ro = "Verbe neregulate"),
                    body = text(
                        en = "The most common verbs are irregular and must be learnt: go → went, see → saw, " +
                            "have → had, buy → bought.\nQuestions and negatives use did + base form: Did you go? I didn't go.",
                        fr = "Les verbes les plus courants sont irréguliers et s'apprennent par cœur : go → went, " +
                            "see → saw, have → had, buy → bought.\n" +
                            "Questions et négations : did + base verbale. Did you go? I didn't go.",
                        ro = "Cele mai folosite verbe sunt neregulate și trebuie învățate: go → went, see → saw, " +
                            "have → had, buy → bought.\nÎntrebările și negațiile folosesc did + forma de bază: Did you go? I didn't go.",
                    ),
                    examples = listOf(
                        example("We went to the market.", fr = "Nous sommes allés au marché.", ro = "Am mers la piață."),
                        example("I bought some bread.", fr = "J'ai acheté du pain.", ro = "Am cumpărat pâine."),
                        example("Did you see the film?", fr = "Tu as vu le film ?", ro = "Ai văzut filmul?"),
                        example("I didn't go.", fr = "Je n'y suis pas allé.", ro = "Nu m-am dus."),
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("en-06-phrasal-verbs"),
            languageCode = englishUk,
            order = 6,
            title = text(en = "Phrasal verbs", fr = "Les phrasal verbs", ro = "Phrasal verbs"),
            summary = text(
                en = "Verb + particle, with a meaning of its own.",
                fr = "Verbe + particule, avec un sens à part.",
                ro = "Verb + particulă, cu un sens propriu.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "One meaning, two words", fr = "Un sens, deux mots", ro = "Un sens, două cuvinte"),
                    body = text(
                        en = "A phrasal verb is learnt as a whole: its meaning often cannot be guessed from its parts. " +
                            "give up = stop trying, get up = leave your bed, look for = search.",
                        fr = "Un phrasal verb s'apprend en bloc : son sens se devine rarement à partir de ses mots. " +
                            "give up = abandonner, get up = se lever, look for = chercher.",
                        ro = "Un phrasal verb se învață ca întreg: sensul lui rareori se deduce din părți. " +
                            "give up = a renunța, get up = a se trezi, look for = a căuta.",
                    ),
                    examples = listOf(
                        example("Don't give up!", fr = "N'abandonne pas !", ro = "Nu renunța!"),
                        example("I get up at seven.", fr = "Je me lève à sept heures.", ro = "Mă trezesc la șapte."),
                        example("I'm looking for my keys.", fr = "Je cherche mes clés.", ro = "Îmi caut cheile."),
                    ),
                ),
                LessonSection(
                    heading = text(en = "Where the object goes", fr = "La place du complément", ro = "Locul complementului"),
                    body = text(
                        en = "With many phrasal verbs, a noun can go before or after the particle: turn off the light, " +
                            "turn the light off. A pronoun always goes in the middle: turn it off.",
                        fr = "Avec beaucoup de phrasal verbs, un nom peut se placer avant ou après la particule : " +
                            "turn off the light, turn the light off. Un pronom se met toujours au milieu : turn it off.",
                        ro = "La multe phrasal verbs, substantivul poate sta înainte sau după particulă: turn off the light, " +
                            "turn the light off. Pronumele stă mereu la mijloc: turn it off.",
                    ),
                    examples = listOf(
                        example("Turn off the light.", fr = "Éteins la lumière.", ro = "Stinge lumina."),
                        example("Turn it off.", fr = "Éteins-la.", ro = "Stinge-o."),
                    ),
                ),
            ),
        ),
    )
}
