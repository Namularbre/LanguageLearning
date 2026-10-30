package com.sazerat.languagelearning.core.data.sample

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.model.LessonSection

/** Placeholder English (UK) lessons 1 to 3 for M1. Replaced by packs downloaded from the API in M2. */
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
    )
}
