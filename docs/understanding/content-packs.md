# Understanding — Content packs

Texts, their lexicon and the "How the language works" lessons are **data, not code**: one JSON file per language, called a *language pack* (SPEC §9.2–9.3, §6.2). Adding or fixing a text means editing a file, not recompiling Kotlin, and the same file will be served by the API in M2.

## How it works

**Where the packs are.** `core/data/src/main/assets/packs/<languageCode>.json` (`ro-RO.json`, `en-GB.json`, `fr-FR.json`). They ship in the APK, so the app has content on its first launch, offline.

**The format (version 1).** One object per pack:

```jsonc
{
  "format": 1,                 // file format: the app refuses a format it does not know
  "languageCode": "ro-RO",     // language taught by the pack
  "version": 1,                // content version, raised at each publication
  "lemmas":  [ { "id": "ro:casă", "text": "casă", "partOfSpeech": "NOUN", "gender": "FEMININE",
                 "meaning": {"en": "house", "fr": "maison"}, "frequencyRank": 12 } ],
  "texts":   [ { "id": "ro-market", "level": "A1", "kind": "STORY", "title": "…", "minutes": 3,
                 "sentences": [ { "tokens": [ {"text": "Casa", "lemma": "ro:casă",
                                               "formNote": {"en": "definite article", …}},
                                              {"text": "."} ],
                                  "translation": {"en": "The house.", "fr": "La maison."} } ] } ],
  "lessons": [ { "id": "ro-01-alphabet", "order": 1, "title": {…}, "summary": {…},
                 "sections": [ { "heading": {…}, "body": {…},
                                 "examples": [ {"text": "casă", "translation": {…}} ] } ] } ]
}
```

- A text in several UI languages is an object `{"en": …, "fr": …, "ro": …}` (a `LocalizedText`).
- Sentences arrive **already cut into tokens**; a word token carries its `lemma`, spaces and punctuation do not. Joining the tokens gives the sentence back. The app never guesses a base form (SPEC §5.1): that is done when the pack is produced.
- `formNote` explains an inflected form ("genitive-dative, definite"). Optional.
- Unknown keys are ignored, so a newer pipeline can add optional fields; a change old apps cannot read raises `format`.

**Reading a pack in the app.**

1. `BundledPacks` lists `assets/packs/`, reads each file once (off the main thread) and keeps them in memory.
2. `ContentPackParser` decodes the JSON with kotlinx.serialization into `ContentPack` (classes that mirror the file) and checks `format`.
3. `ContentPack.toTexts()`, `toLemmas()`, `toLessons()` turn it into domain models (`ReadingText`, `Lemma`, `Lesson`).
4. `PackTextRepository`, `PackLexiconRepository`, `PackLessonRepository` take a `Flow<List<ContentPack>>` and filter by language. `:app`'s `DataModule` feeds them `BundledPacks.all`.

Features only see the domain interfaces (`TextRepository`…), so none of them changed.

**Checking a pack.** `PackContentTest` (`:core:data` unit tests) checks every bundled pack: unique ids, every word points to a lemma of its language, every lemma is used, translations present in the right UI languages, lessons numbered 1 to 6, Romanian ș/ț with a comma below, never a cedilla. A new or edited pack must pass it.

**Where packs are produced.** Not in this repository: the content pipeline (AI draft, lemmatisation, review) is a separate project that writes these files. This app only reads them.

## Where to look in the code

| Question | File |
|---|---|
| The packs | `core/data/src/main/assets/packs/` |
| JSON ↔ domain models | `core/data/.../pack/ContentPack.kt` |
| Parsing, format check | `core/data/.../pack/ContentPackParser.kt` |
| Reading the assets | `core/data/.../pack/BundledPacks.kt` |
| Repositories | `core/data/.../repository/PackRepositories.kt` |
| Which implementation is used | `app/.../di/DataModule.kt` |
| Content checks | `core/data/src/test/.../PackContentTest.kt` |

## Choices and rejected alternatives

- **JSON files rather than Kotlin objects**: content can be produced and reviewed outside the code (AI drafts, native speakers), and the API will serve the same format. Kotlin objects needed a rebuild for every typo.
- **Tokens stored in the pack rather than a dictionary of forms resolved by the app**: a form can belong to two lemmas (French *est*: *être* or *est*), and an on-device lemmatiser is not reliable for Romanian. The pipeline decides, a reviewer checks, the app only displays.
- **One file per language rather than one per text**: one download per language in M2 (`/packs/{languageCode}`), one version number to compare.
- **Assets read in memory rather than copied into Room now**: Room storage comes with the sync in M2; the repositories take a `Flow` of packs, so switching the source will not change them.
- **Pronunciation exercises not moved yet**: kept for a follow-up, to keep this change small.

## Likely defence questions

- *Why is the text already cut into words in the file?* Because the app must not guess base forms (Romanian cases change word endings); the pipeline lemmatises and a native speaker reviews.
- *What happens if the server publishes format 2?* `ContentPackParser` refuses it; the app keeps its current packs. New optional fields in format 1 are ignored.
- *Where is the file read, on which thread?* In `BundledPacks`, inside a `flow` with `flowOn(Dispatchers.IO)`, once (`lazy`).
- *How do you add a language?* A new `<code>.json` pack that passes `PackContentTest`, and the code in `studiedLanguages`.
