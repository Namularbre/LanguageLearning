package com.sazerat.languagelearning.core.data.sample

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.model.LessonSection

/** Placeholder French lessons 1 to 6 for M1. Replaced by packs downloaded from the API in M2. */
internal object SampleFrenchLessons {
    private val french = LanguageCode("fr-FR")

    private val comparisonHeading = text(
        en = "Compared with English and Romanian",
        fr = "Comparaison avec le roumain et l'anglais",
        ro = "Comparație cu româna și engleza",
    )

    val all: List<Lesson> = listOf(
        Lesson(
            id = LessonId("fr-01-articles"),
            languageCode = french,
            order = 1,
            title = text(en = "Articles", fr = "Les articles", ro = "Articolele"),
            summary = text(
                en = "Definite, indefinite and partitive.",
                fr = "Défini, indéfini et partitif.",
                ro = "Hotărât, nehotărât și partitiv.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "Definite and indefinite", fr = "Défini et indéfini", ro = "Hotărât și nehotărât"),
                    body = text(
                        en = "French has two genders. The article comes before the noun and carries its gender:\n" +
                            "definite le (masculine), la (feminine), l' (before a vowel), les (plural);\n" +
                            "indefinite un, une, des.",
                        fr = "Le français a deux genres. L'article se place devant le nom et en porte le genre :\n" +
                            "défini le (masculin), la (féminin), l' (devant une voyelle), les (pluriel) ;\n" +
                            "indéfini un, une, des.",
                        ro = "Franceza are două genuri. Articolul stă înaintea substantivului și îi arată genul:\n" +
                            "hotărât le (masculin), la (feminin), l' (înaintea unei vocale), les (plural);\n" +
                            "nehotărât un, une, des.",
                    ),
                    examples = listOf(
                        example("le livre", en = "the book", ro = "cartea"),
                        example("la maison", en = "the house", ro = "casa"),
                        example("les enfants", en = "the children", ro = "copiii"),
                        example("un garçon", en = "a boy", ro = "un băiat"),
                        example("une fille", en = "a girl", ro = "o fată"),
                        example("des pommes", en = "(some) apples", ro = "niște mere"),
                    ),
                ),
                LessonSection(
                    heading = text(en = "The partitive", fr = "Le partitif", ro = "Partitivul"),
                    body = text(
                        en = "du, de la, de l' express an amount you don't count: du pain, de la soupe, de l'eau.\n" +
                            "After a negative, they become de: je ne bois pas de café.",
                        fr = "du, de la, de l' désignent une quantité qu'on ne compte pas : du pain, de la soupe, de l'eau.\n" +
                            "Après une négation, ils deviennent de : je ne bois pas de café.",
                        ro = "du, de la, de l' exprimă o cantitate care nu se numără: du pain, de la soupe, de l'eau.\n" +
                            "După o negație, devin de: je ne bois pas de café.",
                    ),
                    examples = listOf(
                        example("Je bois du café.", en = "I drink coffee.", ro = "Beau cafea."),
                        example("Je mange de la soupe.", en = "I'm eating soup.", ro = "Mănânc supă."),
                        example("Il y a de l'eau.", en = "There is water.", ro = "Este apă."),
                        example("Je ne bois pas de café.", en = "I don't drink coffee.", ro = "Nu beau cafea."),
                    ),
                ),
                LessonSection(
                    heading = comparisonHeading,
                    body = text(
                        en = "In Romanian, the definite article is attached to the end of the noun (casa, cartea); " +
                            "in French, as in English, it comes before. The partitive exists in neither English nor " +
                            "Romanian: \"Je bois du café\" is simply I drink coffee, Beau cafea. Gender often differs: " +
                            "\"le livre\" is feminine in Romanian (cartea).",
                        fr = "En roumain, l'article défini est collé à la fin du nom (casa, cartea) ; en français, il est " +
                            "toujours devant. Le partitif n'existe ni en roumain ni en anglais : « Je bois du café » se dit " +
                            "simplement Beau cafea, I drink coffee. Le genre change souvent : « le livre » est féminin " +
                            "en roumain (cartea).",
                        ro = "În română, articolul hotărât se lipește la sfârșitul substantivului (casa, cartea); " +
                            "în franceză stă mereu înainte. Partitivul nu există nici în română, nici în engleză: " +
                            "„Je bois du café” înseamnă pur și simplu Beau cafea. Genul diferă adesea: " +
                            "„le livre” este masculin, dar cartea este feminin.",
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("fr-02-passe-compose"),
            languageCode = french,
            order = 2,
            title = text(en = "The passé composé", fr = "Le passé composé", ro = "Passé composé"),
            summary = text(
                en = "avoir or être + past participle.",
                fr = "Avoir ou être + participe passé.",
                ro = "avoir sau être + participiu.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "With avoir", fr = "Avec avoir", ro = "Cu avoir"),
                    body = text(
                        en = "Most verbs form the passé composé with avoir: j'ai mangé, tu as fini, nous avons vu.\n" +
                            "Participles: -er → -é (mangé), -ir → -i (fini), and many irregular ones (vu, pris, fait).",
                        fr = "La plupart des verbes forment le passé composé avec avoir : j'ai mangé, tu as fini, " +
                            "nous avons vu.\n" +
                            "Participes : -er → -é (mangé), -ir → -i (fini), et beaucoup d'irréguliers (vu, pris, fait).",
                        ro = "Majoritatea verbelor formează passé composé cu avoir: j'ai mangé, tu as fini, nous avons vu.\n" +
                            "Participii: -er → -é (mangé), -ir → -i (fini) și multe neregulate (vu, pris, fait).",
                    ),
                    examples = listOf(
                        example("J'ai mangé.", en = "I ate.", ro = "Am mâncat."),
                        example("Tu as fini ?", en = "Have you finished?", ro = "Ai terminat?"),
                        example("Nous avons vu un film.", en = "We saw a film.", ro = "Am văzut un film."),
                    ),
                ),
                LessonSection(
                    heading = text(en = "With être", fr = "Avec être", ro = "Cu être"),
                    body = text(
                        en = "Verbs of movement or change of state (aller, venir, partir, arriver, naître, mourir…) " +
                            "and all reflexive verbs use être.\n" +
                            "The participle then agrees with the subject: elle est partie, nous sommes allés.",
                        fr = "Les verbes de mouvement ou de changement d'état (aller, venir, partir, arriver, naître, " +
                            "mourir…) et tous les verbes pronominaux utilisent être.\n" +
                            "Le participe s'accorde alors avec le sujet : elle est partie, nous sommes allés.",
                        ro = "Verbele de mișcare sau de schimbare de stare (aller, venir, partir, arriver, naître, " +
                            "mourir…) și toate verbele reflexive folosesc être.\n" +
                            "Participiul se acordă atunci cu subiectul: elle est partie, nous sommes allés.",
                    ),
                    examples = listOf(
                        example("Elle est partie.", en = "She left.", ro = "Ea a plecat."),
                        example("Nous sommes allés à Bucarest.", en = "We went to Bucharest.", ro = "Am mers la București."),
                        example("Je me suis levé tôt.", en = "I got up early.", ro = "M-am sculat devreme."),
                    ),
                ),
                LessonSection(
                    heading = comparisonHeading,
                    body = text(
                        en = "The Romanian perfectul compus is always formed with a avea (am mâncat, a plecat) and the " +
                            "participle never agrees. In French you must choose between avoir and être, and with être " +
                            "the participle agrees. English usually translates the passé composé with a past simple: " +
                            "Elle est partie hier, She left yesterday.",
                        fr = "Le perfectul compus roumain se forme toujours avec a avea (am mâncat, a plecat) et le " +
                            "participe ne s'accorde pas. En français, il faut choisir entre avoir et être, puis accorder " +
                            "avec être. L'anglais traduit souvent le passé composé par un prétérit : Elle est partie hier, " +
                            "She left yesterday.",
                        ro = "Perfectul compus românesc se formează mereu cu a avea (am mâncat, a plecat), iar participiul " +
                            "nu se acordă. În franceză trebuie ales între avoir și être, iar cu être participiul se acordă. " +
                            "Engleza traduce de obicei passé composé printr-un past simple: Elle est partie hier, " +
                            "She left yesterday.",
                    ),
                    examples = listOf(
                        example("Elle est arrivée hier.", en = "She arrived yesterday.", ro = "Ea a sosit ieri."),
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("fr-03-negation-questions"),
            languageCode = french,
            order = 3,
            title = text(en = "Negatives and questions", fr = "Négation et questions", ro = "Negația și întrebările"),
            summary = text(
                en = "ne … pas, and three ways to ask a question.",
                fr = "ne … pas, et trois façons de poser une question.",
                ro = "ne … pas și trei moduri de a pune o întrebare.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(
                        en = "The negative ne … pas",
                        fr = "La négation ne … pas",
                        ro = "Negația ne … pas",
                    ),
                    body = text(
                        en = "The negative surrounds the conjugated verb: je ne parle pas. Before a vowel, ne becomes n': " +
                            "je n'ai pas faim.\n" +
                            "In speech, ne often disappears: je sais pas.",
                        fr = "La négation entoure le verbe conjugué : je ne parle pas. Devant une voyelle, ne devient n' : " +
                            "je n'ai pas faim.\n" +
                            "À l'oral, le ne disparaît souvent : je sais pas.",
                        ro = "Negația încadrează verbul conjugat: je ne parle pas. Înaintea unei vocale, ne devine n': " +
                            "je n'ai pas faim.\n" +
                            "În vorbire, ne dispare adesea: je sais pas.",
                    ),
                    examples = listOf(
                        example("Je ne parle pas roumain.", en = "I don't speak Romanian.", ro = "Nu vorbesc română."),
                        example("Il ne travaille pas.", en = "He doesn't work.", ro = "El nu lucrează."),
                        example("Je n'ai pas faim.", en = "I'm not hungry.", ro = "Nu mi-e foame."),
                        example("Je sais pas.", en = "I don't know.", ro = "Nu știu."),
                    ),
                ),
                LessonSection(
                    heading = text(
                        en = "Three ways to ask a question",
                        fr = "Trois façons de poser une question",
                        ro = "Trei moduri de a pune o întrebare",
                    ),
                    body = text(
                        en = "Intonation only (informal): Tu viens ?\n" +
                            "With est-ce que (everyday): Est-ce que tu viens ?\n" +
                            "Subject inversion (formal): Viens-tu ?\n" +
                            "With a question word: Où est-ce que tu habites ?",
                        fr = "Intonation seule (familier) : Tu viens ?\n" +
                            "Avec est-ce que (courant) : Est-ce que tu viens ?\n" +
                            "Inversion du sujet (soutenu) : Viens-tu ?\n" +
                            "Avec un mot interrogatif : Où est-ce que tu habites ?",
                        ro = "Doar prin intonație (familiar): Tu viens ?\n" +
                            "Cu est-ce que (uzual): Est-ce que tu viens ?\n" +
                            "Cu inversiunea subiectului (elevat): Viens-tu ?\n" +
                            "Cu un cuvânt interogativ: Où est-ce que tu habites ?",
                    ),
                    examples = listOf(
                        example("Tu viens ?", en = "Are you coming?", ro = "Vii?"),
                        example("Est-ce que tu viens ?", en = "Are you coming?", ro = "Vii?"),
                        example("Viens-tu ?", en = "Are you coming?", ro = "Vii?"),
                        example("Où est-ce que tu habites ?", en = "Where do you live?", ro = "Unde locuiești?"),
                    ),
                ),
                LessonSection(
                    heading = comparisonHeading,
                    body = text(
                        en = "Romanian only puts nu before the verb (Nu vorbesc) and asks questions with intonation, " +
                            "like informal French. English needs the auxiliary do: Do you speak French?, I don't know.",
                        fr = "Le roumain place seulement nu devant le verbe (Nu vorbesc) et pose ses questions par " +
                            "l'intonation, comme le français familier. L'anglais, lui, a besoin de l'auxiliaire do : " +
                            "Do you speak French?",
                        ro = "Româna pune doar nu înaintea verbului (Nu vorbesc) și pune întrebări prin intonație, ca " +
                            "franceza familiară. Engleza are nevoie de auxiliarul do: Do you speak French?",
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("fr-04-present-er"),
            languageCode = french,
            order = 4,
            title = text(en = "Present tense of -er verbs", fr = "Le présent des verbes en -er", ro = "Prezentul verbelor în -er"),
            summary = text(
                en = "Nine verbs in ten follow parler.",
                fr = "Neuf verbes sur dix suivent parler.",
                ro = "Nouă verbe din zece se conjugă ca parler.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "The endings", fr = "Les terminaisons", ro = "Terminațiile"),
                    body = same(
                        "je parle, tu parles, il / elle parle\nnous parlons, vous parlez, ils / elles parlent",
                    ),
                    examples = listOf(
                        example("Je parle français.", en = "I speak French.", ro = "Vorbesc franceza."),
                        example("Tu habites à Paris ?", en = "Do you live in Paris?", ro = "Locuiești la Paris?"),
                        example("Nous aimons le café.", en = "We love coffee.", ro = "Ne place cafeaua."),
                        example("Ils travaillent ici.", en = "They work here.", ro = "Ei lucrează aici."),
                    ),
                ),
                LessonSection(
                    heading = text(en = "Silent endings", fr = "Les terminaisons muettes", ro = "Terminațiile mute"),
                    body = text(
                        en = "-e, -es and -ent are not pronounced: parle, parles and parlent sound the same. " +
                            "Only the nous and vous forms are heard.",
                        fr = "-e, -es et -ent ne se prononcent pas : parle, parles et parlent se disent pareil. " +
                            "Seules les formes nous et vous s'entendent.",
                        ro = "-e, -es și -ent nu se pronunță: parle, parles și parlent sună la fel. " +
                            "Doar formele cu nous și vous se aud diferit.",
                    ),
                    examples = listOf(
                        example("elle parle", en = "she speaks", ro = "ea vorbește"),
                        example("elles parlent", en = "they speak", ro = "ele vorbesc"),
                    ),
                ),
                LessonSection(
                    heading = comparisonHeading,
                    body = text(
                        en = "In Romanian, every ending is heard and the pronoun can be dropped (vorbesc). In French, " +
                            "since the endings are silent, the pronoun is always needed: je parle, never just parle.",
                        fr = "En roumain, chaque terminaison s'entend et le pronom peut disparaître (vorbesc). En français, " +
                            "les terminaisons étant muettes, le pronom est obligatoire : je parle, jamais parle seul.",
                        ro = "În română, fiecare terminație se aude și pronumele poate lipsi (vorbesc). În franceză, " +
                            "terminațiile fiind mute, pronumele este obligatoriu: je parle, niciodată doar parle.",
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("fr-05-negation"),
            languageCode = french,
            order = 5,
            title = text(en = "Negation", fr = "La négation", ro = "Negația"),
            summary = text(
                en = "ne… pas and its relatives.",
                fr = "ne… pas et ses cousins.",
                ro = "ne… pas și înrudiții săi.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "Around the verb", fr = "Autour du verbe", ro = "În jurul verbului"),
                    body = text(
                        en = "The negation surrounds the conjugated verb: je ne parle pas. ne becomes n' before a vowel: " +
                            "je n'aime pas.",
                        fr = "La négation encadre le verbe conjugué : je ne parle pas. ne devient n' devant une voyelle : " +
                            "je n'aime pas.",
                        ro = "Negația încadrează verbul conjugat: je ne parle pas. ne devine n' înaintea unei vocale: " +
                            "je n'aime pas.",
                    ),
                    examples = listOf(
                        example("Je ne parle pas anglais.", en = "I don't speak English.", ro = "Nu vorbesc engleza."),
                        example("Je n'aime pas le thé.", en = "I don't like tea.", ro = "Nu-mi place ceaiul."),
                    ),
                ),
                LessonSection(
                    heading = text(en = "Never, no longer, nothing", fr = "Jamais, plus, rien", ro = "Niciodată, nu mai, nimic"),
                    body = text(
                        en = "pas can be replaced: ne… jamais (never), ne… plus (no longer), ne… rien (nothing).\n" +
                            "In everyday speech, ne is often dropped: je sais pas.",
                        fr = "pas peut être remplacé : ne… jamais, ne… plus, ne… rien.\n" +
                            "À l'oral courant, ne disparaît souvent : je sais pas.",
                        ro = "pas poate fi înlocuit: ne… jamais (niciodată), ne… plus (nu mai), ne… rien (nimic).\n" +
                            "În vorbirea de zi cu zi, ne dispare adesea: je sais pas.",
                    ),
                    examples = listOf(
                        example("Il ne fume jamais.", en = "He never smokes.", ro = "El nu fumează niciodată."),
                        example("Je n'habite plus ici.", en = "I don't live here any more.", ro = "Nu mai locuiesc aici."),
                        example("Je ne vois rien.", en = "I can't see anything.", ro = "Nu văd nimic."),
                        example("Je sais pas.", en = "Dunno.", ro = "Nu știu."),
                    ),
                ),
            ),
        ),
        Lesson(
            id = LessonId("fr-06-questions"),
            languageCode = french,
            order = 6,
            title = text(en = "Asking questions", fr = "Poser une question", ro = "Cum punem întrebări"),
            summary = text(
                en = "Three ways, from casual to formal.",
                fr = "Trois façons, du familier au soutenu.",
                ro = "Trei moduri, de la familiar la formal.",
            ),
            sections = listOf(
                LessonSection(
                    heading = text(en = "Three ways", fr = "Trois façons", ro = "Trei moduri"),
                    body = text(
                        en = "Rising intonation (casual): Tu viens ?\nest-ce que (neutral): Est-ce que tu viens ?\n" +
                            "Inversion (formal): Viens-tu ?",
                        fr = "Intonation montante (familier) : Tu viens ?\nest-ce que (courant) : Est-ce que tu viens ?\n" +
                            "Inversion (soutenu) : Viens-tu ?",
                        ro = "Intonație ascendentă (familiar): Tu viens ?\nest-ce que (neutru): Est-ce que tu viens ?\n" +
                            "Inversiune (formal): Viens-tu ?",
                    ),
                    examples = listOf(
                        example("Tu viens ?", en = "Are you coming?", ro = "Vii?"),
                        example("Est-ce que tu viens ?", en = "Are you coming?", ro = "Vii?"),
                        example("Viens-tu ?", en = "Are you coming?", ro = "Vii?"),
                    ),
                ),
                LessonSection(
                    heading = text(en = "Question words", fr = "Les mots interrogatifs", ro = "Cuvintele interogative"),
                    body = text(
                        en = "où (where), quand (when), comment (how), pourquoi (why), combien (how much), qui (who), " +
                            "que / quoi (what).",
                        fr = "où, quand, comment, pourquoi, combien, qui, que / quoi.",
                        ro = "où (unde), quand (când), comment (cum), pourquoi (de ce), combien (cât), qui (cine), " +
                            "que / quoi (ce).",
                    ),
                    examples = listOf(
                        example("Où est la gare ?", en = "Where is the station?", ro = "Unde este gara?"),
                        example("Combien ça coûte ?", en = "How much is it?", ro = "Cât costă?"),
                        example("Pourquoi tu ris ?", en = "Why are you laughing?", ro = "De ce râzi?"),
                    ),
                ),
            ),
        ),
    )
}
