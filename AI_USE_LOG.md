# AI Use Log

Append-only. One section per team member. Never rewrite past entries; add a new one instead.

Each entry: goal · tool + prompt · what was produced · what was accepted / rejected / modified and why · how it was verified.

---

## Thomas Sazerat

### 2026-10-28 — M1 scaffold (draft, to be completed by the author)

- **Goal:** Set up milestone M1 of `SPEC.md`: multi-module Gradle project with convention plugins and a version catalog, Compose screens on fake data (Home, pronunciation list → detail, lesson list → detail), type-safe Navigation Compose, minimal GitHub Actions CI.
- **Tool + prompt:** Claude Code (Claude Opus 5.5). Prompt: "read the spec.md file", then "ok now start m1".
- **What was produced:**
  - `build-logic/` convention plugins (Android app/library, Compose, feature, Hilt via KSP, pure JVM library) and `gradle/libs.versions.toml` (latest stable versions checked against Maven on 2026-10-28: Kotlin 2.4.21, AGP 9.4.1, KSP 2.3.12, Hilt 2.60.1, Navigation 2.10.2, Compose BOM 2026.09.00).
  - The §11.1 module layout (empty placeholders for modules used from M2 on), plus `:core:testing` for shared test helpers.
  - Domain models and repository interfaces (`:core:domain`), in-memory repositories with sample content (`:core:data`: 9 English exercises, Romanian lessons 1–3), `LanguagePack` API with en-GB / ro-RO / test packs and the Romanian cedilla → comma-below normalizer.
  - Screens + Hilt ViewModels for Home, pronunciation list/detail, lesson list/detail; type-safe routes; `:app` NavHost and Hilt `DataModule`.
  - 21 JVM unit tests, 1 Compose instrumented test, `.github/workflows/ci.yml`, `.github/dependabot.yml`.
- **Notable choices proposed by the AI:**
  - ViewModels read route arguments from `SavedStateHandle` by key instead of `toRoute()`, because `toRoute()` needs a real `Bundle` and would force Robolectric into JVM tests.
  - Lint warnings are errors, except "newer version available" checks (left to Dependabot).
- **Accepted / rejected / modified and why:** _TODO (author)_
- **How I verified it:** _TODO (author). The AI ran: `./gradlew assembleDebug test lintDebug` (green), the instrumented test on an API 37 emulator, and a manual process-death check (`adb shell am kill` in background, reopen from Recents: same screen, same back stack, same selected duration)._

### 2026-10-28 — Listen to lesson examples (draft, to be completed by the author)

- **Goal:** Let the learner hear the pronunciation of Romanian words and phrases in lessons.
- **Tool + prompt:** Claude Code (Claude Opus 5.5). Prompt: "Pour les leçons, ça serait bien de pouvoir entendre les prononciations".
- **What was produced:**
  - `TextToSpeech` interface, `SpeechRate` (1× / 0.75×) and `SpeakResult` in `:speech:api`.
  - `AndroidTextToSpeech` in a new `:speech:android` module (system TTS engine, callbacks bridged to a suspend function), `FakeTextToSpeech` in `:speech:fake`, Hilt `SpeechModule` in `:app`.
  - `LessonExample` (text + French translation) on lesson sections, with examples for lessons 1–3.
  - Lesson detail: tappable examples with a playing indicator, a slow-speed chip, and a snackbar with a shortcut to TTS settings when no voice is installed.
  - 5 new unit tests (26 total).
- **Notable choices proposed by the AI:** on-device system TTS first (offline, free, private), Azure neural voices left for later behind the same interface; a new `:speech:android` module not listed in SPEC §11.1.
- **Accepted / rejected / modified and why:** _TODO (author)_
- **How I verified it:** _TODO (author). The AI ran `./gradlew assembleDebug test lintDebug` (green) and tapped an example on an API 37 emulator: Google TTS downloaded the ro-RO voice on first use, the playing indicator appeared, then cleared when speech ended. Audio itself was not listened to (headless emulator)._

### 2026-10-28 — Listen to English pronunciation items (draft, to be completed by the author)

- **Goal:** Same listening feature for the words and sentences of the English pronunciation exercises.
- **Tool + prompt:** Claude Code (Claude Opus 5.5). Prompt: "Oui, ajoute-la" (answer to the AI offering to extend listening to pronunciation exercises).
- **What was produced:**
  - `SpeechPlayer` in `:speech:api`: the playback logic previously inside `LessonDetailViewModel`, extracted so both screens share it; 3 unit tests.
  - `ListenCard`, `SlowSpeedChip`, `SpeechErrorSnackbar` moved to `:core:ui` (`:core:ui` now depends on `:speech:api`).
  - `languageCode` added to `PronunciationExercise` (spec §9.8), set to en-GB in the sample content.
  - Pronunciation detail: each item is a listen card (text + IPA), slow-speed chip, error snackbar; 1 new ViewModel test (30 unit tests total).
- **Accepted / rejected / modified and why:** _TODO (author)_
- **How I verified it:** _TODO (author). The AI ran `./gradlew assembleDebug test lintDebug` (green) and on an API 37 emulator enabled slow speed and tapped "sheep": Google TTS downloaded the en-GB voice, the playing indicator appeared then cleared. Audio not listened to (headless emulator)._

### 2026-10-28 — Fix listening lag (draft, to be completed by the author)

- **Goal:** Fix "the Romanian examples are broken, I can't replay them, and overall it lags".
- **Tool + prompt:** Claude Code (Claude Opus 5.5). Prompt: "la voix est ok mais parcontre les exemples Roumain sont cassés, je peux pas les rejoué et globalement ça lag".
- **What was produced:**
  - Diagnosis with temporary logs (removed): `TextToSpeech.setLanguage()` ran on the main thread on every tap and took 25–180 ms.
  - `AndroidTextToSpeech`: engine calls moved to a single background thread (`Dispatchers.IO.limitedParallelism(1)`); the language is only set when it changes.
  - `SpeechErrorSnackbar`: `SnackbarDuration.Long` instead of the default indefinite duration, so a later error is shown again.
- **Not solved:** the replay failure could not be reproduced on the emulator (replay, tap during speech and English → Romanian all worked). Needs the author's phone model / TTS engine and logs.
- **Accepted / rejected / modified and why:** _TODO (author)_
- **How I verified it:** _TODO (author). The AI ran `./gradlew assembleDebug test lintDebug` (green, 30 unit tests) and measured with `adb shell dumpsys gfxinfo` on an API 37 emulator (debug build), same tap scenario: janky frames 26.5 % → 9.2 %, 90th percentile 77 → 44 ms, 99th 150 → 81 ms._

### 2026-10-28 — English lessons, Romanian and French pronunciation (draft, to be completed by the author)

- **Goal:** More content: English lessons, Romanian and French pronunciation exercises.
- **Tool + prompt:** Claude Code (Claude Opus 5.5). Prompt: "Ajoute des leçons d'anglais, des prononciations en roumain et en français".
- **What was produced:**
  - Sample content in `:core:data`: English lessons 1–3 (present simple / continuous, questions and negation with do, present perfect), 5 Romanian pronunciation exercises (ă / a, ă / â, ș / s / ț, c / ch and g / gh, greetings with ea / oa), 5 French ones (y / u, nasal vowels, r, final stress, liaison). All with IPA and French instructions.
  - `PronunciationRepository.observeExercises(languageCode)`; `PronunciationList` route now carries a language code, like `LessonList`.
  - Home: language chips (Anglais / Roumain / Français) above the modules, choice kept in `SavedStateHandle`; the language list is declared in `:app` only. List titles show the language ("Prononciation — Français").
  - Tests: content checks (unique ids, unique items, no cedilla in Romanian items, lessons 1–3 per language), language filtering, Home language state (37 unit tests).
- **To check by a human:** the IPA transcriptions and the grammar explanations were written by the AI; the Romanian and English content should be reviewed by someone fluent.
- **Accepted / rejected / modified and why:** _TODO (author)_
- **How I verified it:** _TODO (author). The AI ran `./gradlew assembleDebug test lintDebug assembleDebugAndroidTest` (green) and, on an API 37 emulator, read the screens with uiautomator: Home chips, French and Romanian pronunciation lists, French exercise detail, English lesson list and lesson 3 detail. Audio not listened to._

### 2026-10-28 — French lessons (draft, to be completed by the author)

- **Goal:** French lessons 1–3, so every language has both modules.
- **Tool + prompt:** Claude Code (Claude Opus 5.5). Prompt: "Ok, il ne reste plus qu'a faire les leçons. Je propose que le TTS soit côté cloud, car l'application est clairement destinée à un usage dommestique".
- **What was produced:** `SampleFrenchLessons` (articles and partitive, passé composé with avoir / être, negation and questions), examples translated into Romanian, comparison sections with Romanian and English; content tests extended (38 unit tests).
- **Choice made by the AI, to confirm:** example translations in Romanian rather than English.
- **Not done:** cloud TTS. The AI pointed out that SPEC §9.4 already plans Azure TTS with the Android engine as fallback, but that it needs an Azure resource (paid service, author's validation) and a token endpoint, since the key must never be in the APK.
- **Accepted / rejected / modified and why:** _TODO (author)_
- **How I verified it:** _TODO (author). The AI ran `./gradlew assembleDebug test lintDebug` (green). Not checked on the emulator; Romanian translations to be reviewed by a fluent speaker._

### 2026-10-30 — English UI, French and Romanian translations (draft, to be completed by the author)

- **Goal:** Everything in English for the course; the UI and the learning content also available in French and Romanian, following the phone's language.
- **Tool + prompt:** Claude Code (Claude Opus 5.5). Prompt: "oui, tous en anglais, l'UI doit être anglais, ou avoir une traduction automatique", then the author chose "English + French + Romanian depending on the phone" for the content.
- **What was produced:**
  - UI strings: English in `values/`, the previous French text moved to `values-fr/`, new Romanian `values-ro/`; per-app language support (`generateLocaleConfig`).
  - `LocalizedText` in `:core:domain` (text per language, English fallback) used for every learner-facing content field; `localized()`, `uiLanguage()` and a locale-aware `displayName()` in `:core:ui`; translations of an example hidden when the UI language is the lesson's language.
  - All sample lessons and exercises rewritten in English, French and Romanian; "comparison" sections now compare with the two other languages.
  - Tests: `LocalizedTextTest`, content completeness in the three languages, no cedilla in any Romanian text (44 unit tests).
  - `SPEC.md`, `docs/verifications.md` and the architecture notes translated to English; `docs/comprendre/` renamed `docs/understanding/`; `ARCHITECTURE.md` gained a "UI languages" section.
- **To check by a human:** all Romanian text (UI and content) and the English/French content were written by the AI; a fluent speaker should review them.
- **Accepted / rejected / modified and why:** _TODO (author)_
- **How I verified it:** _TODO (author). The AI ran `./gradlew assembleDebug test lintDebug assembleDebugAndroidTest` (green) and, on an API 37 emulator, switched the app language with `adb shell cmd locale set-app-locales` to en-US, fr-FR and ro-RO, reading Home, Romanian lesson 1 and the French pronunciation list with uiautomator in each language._

### 2026-10-31 — Reading loop: Library, Reader, reviews, progress, onboarding (draft, to be completed by the author)

- **Goal:** Implement the reading-loop concept and the "Lumo" design (both shared as claude.ai artifacts) on a separate branch, keeping spelling lessons as their own section and pronunciation as an option.
- **Tool + prompt:** Claude Code (Claude Opus 5.5). Prompt: "Repart de la branche m1 et implémente tout, quite à faire ça sur une branche à part. Je relirai demain", after the note "Je garderai juste les leçons d'orthographe et de prononciation quand même…". The AI worked alone overnight, in small commits on `feature/reading-loop`.
- **What was produced:**
  - `SPEC.md` rewritten for the reading loop; the design's theme.
  - Domain: texts, lemmas, word statuses, review cards; tokenizer, comprehension and next-text suggestion; FSRS 4.5 scheduler written in plain Kotlin; streak, minutes and known-words functions.
  - Data: sample texts and lexicon (mainly Romanian, some English and French); Room (word statuses, review cards, review and reading logs) and DataStore (settings).
  - Screens: Library, Reader with word sheet and sentence-by-sentence listening, Review session, Progress, Settings, Today, four tabs, onboarding with a level test.
  - Tests: domain rules, content consistency, every new ViewModel, Room repositories (instrumented); fakes moved to `:core:testing`.
  - Docs: `ARCHITECTURE.md`, `docs/understanding/reading-loop.md`, `docs/verifications.md`.
- **Choices made by the AI, to confirm:** app name kept as "Languages"; lessons and pronunciation as Library sections; pronunciation off by default; UI language through Android's per-app language; no backdrop blur and system fonts; FSRS without a library; texts pre-tokenised; on-device TTS; no reminder (M2) and no multi-word selection; "Again" cards not shown again in the same session; old list routes left registered.
- **To check by a human:** all texts, translations, meanings and IPA were written by the AI; the Romanian content especially needs a native speaker.
- **Accepted / rejected / modified and why:** _TODO (author)_
- **How I verified it:** _TODO (author). The AI ran `./gradlew assembleDebug test lintDebug` (green) and `connectedDebugAndroidTest` on an API 37 emulator, then went through onboarding, Today, Library, Reader, Review, Progress and Settings (English and French UI) with uiautomator and screenshots. That walkthrough found and fixed four issues (status-bar overlap, long press opening the word sheet, "3 d" shown for a 4-day interval, oversized calendar squares). Not checked: audio quality, process death on the new screens, IPA on the word sheet._

---

## Reflection

Thomas SAZERAT : The AI could not tell some decision about the project if it's not done by me. (ex: the UI in French)
Thomas SAZERAT : I don't like the way vibe coding the app make me ask Claude things and letting him do as it wants. I prefer making small changes so I can review the code easily
