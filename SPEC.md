# Specification — Read, tap, review: a Romanian / English / French learning app

> Document for Claude Code. It describes the need, the constraints of the *Mobile Computing* course (UPB, 2026-2027), the target architecture and a breakdown aligned with the course milestones, with acceptance criteria. Work **milestone by milestone**, in order.

---

## 0. Summary

**Native Android app, Kotlin / Jetpack Compose**, **offline-first**, to learn **Romanian, English or French** by **reading and listening to real short texts at the right level**, then **reviewing the words met** with spaced repetition.

Promise: *"Understand real stories from the first week, and don't forget them."*

The core is a **four-step loop**, 10 to 20 minutes a day:

1. **Read** a short text (story, dialogue, simplified news) with its audio.
2. **Tap a word** you don't know: its base form, meaning and pronunciation appear.
3. **Add it to your reviews**: it becomes a card that keeps its original sentence.
4. **Review** the cards that are due (FSRS). The words learnt choose the next text, so the learner stays at the right level without setting anything.

Next to that loop:
- **"How the language works" lessons** (spelling, alphabet, articles, cases…) for those who want to understand the rules. They live in the Library, apart from the texts.
- **Optional pronunciation help**: when enabled in the settings, the IPA transcription and a pronunciation note appear on word sheets and card backs, and the pronunciation exercises are available in the Library.

The **UI is in English, French or Romanian**: by default the phone's language (English if it is none of the three), changeable in the app. The UI language is also taken as the learner's **native language**: explanations and translations are shown in it.

The app is also the **Mobile Computing course project**: it must meet the required technical baseline (§2) and carry a **defensible compute/data placement decision** (on-device vs cloud, §10). The project is graded mainly through a **live defence**: the student must be able to **explain and modify every part of the code**.

---

## 1. Context and users

The app is designed for the **six pairs** between Romanian, English and French. Content first targets **Romanian for French and English speakers** (few competitors, very motivated learners); English and French get a few texts so every pair works.

| Segment | Language learnt | Profile |
|---|---|---|
| French and English speakers | Romanian | Expatriates, mixed couples, diaspora, people moving to Romania or Moldova |
| Romanian speakers | English, French | Students, workers, people planning to emigrate |

Personas used for the designs:
- **Claire, 34, French**, moves to Bucharest for work. Zero Romanian, 20 minutes a day on public transport.
- **Andrei, 22, Romanian**, student. Intermediate English, wants to follow TV series and pass a job interview in French.

| Item | Value |
|---|---|
| Device | Android 14 (API 34) or later |
| Framework | Mobile Computing course project (MSc PDCS, UPB). Team: **to be specified** (solo allowed, 3 max). |
| UI languages | English (default), French, Romanian |

---

## 2. Course framework: requirements and coverage

These requirements are **non-negotiable**: missing one caps the functionality grade.

| Course requirement | Covered in |
|---|---|
| Kotlin only, no Java in the app code | §9.9 |
| Jetpack Compose + Material 3, no XML; stateless composables, state hoisted into a ViewModel | §9.1, §9.9 |
| MVVM + unidirectional data flow, **Hilt**, coroutines/Flow, logic behind interfaces testable with fakes | §9.1, §11 |
| **Navigation Compose with type-safe routes**, several screens, at least one **list → detail** flow | §9.5 (Library → Reader) |
| **Process-death survival** (back stack and screen state restored: `SavedStateHandle`, `rememberSaveable`) | §9.5 |
| Network images with **Coil 3** (`AsyncImage`, placeholder and error state) | §9.7 |
| **Room as the local source of truth** (entity + DAO + reactive reads as `Flow`); the UI observes Room, not the network | §9.2 |
| **Remote tier** chosen deliberately (Retrofit REST) + **offline-first sync with WorkManager** under real constraints (network type, battery) | §9.2, §9.3 |
| Dangerous **runtime permission**: rationale, denial handled without crash (degraded mode), path to ask again | §9.6 (`POST_NOTIFICATIONS`, then `RECORD_AUDIO`) |
| Correct asynchronous work, main-thread safety, survives **Doze** | §9.2, §12 |
| JVM unit tests with fakes, **at least one instrumented test**, blocking **GitHub Actions CI** on every PR | §11.4, §13 |
| **Integrated concept**: on-device inference (ML Kit, LiteRT, MediaPipe), with a contrasting cloud path (Firebase AI Logic / Gemini) behind the same interface, and a **defensible placement decision** | §10 |
| Deliverables: `SYSTEMS.md`, `AI_USE_LOG.md`, contribution, peer review, history of reviewed PRs | §13 |

---

## 3. Priorities and scope

| Priority | Feature | Milestone |
|---|---|---|
| **P1** | Reading loop: Library → Reader → word sheet → reviews (FSRS) | Prototype on sample content (done after M1), M2 (content from the API) |
| **P2** | Today, Progress, onboarding (languages, level test, daily goal) | Prototype (done after M1), M2 (reminder) |
| **P3** | "How the language works" lessons, optional pronunciation help | M1 lessons kept; M2 more lessons |
| P4 | Sentence translation on the device or in the cloud (integrated concept) | M3 |
| Phase 2 | Shadowing with speech recognition and pronunciation feedback, conversation with an AI limited to known words, correction of short written texts, import of personal content | After the course |

The scope of the course milestones is deliberately **reduced**: a solid, tested, explainable base is better than many fragile features.

**Design principles**: calm and centred on reading (the text is the hero, the interface steps back); usable with one hand, main actions at the bottom; reading text at 18–20 pt at least with generous line spacing; dark mode; three word-status colours used everywhere (new, learning, known); light gamification only (day streak and daily goal, no points or leagues); honest feedback (known words and comprehension rate, not abstract rewards).

---

## 4. Navigation and screens

A **four-tab bar** (Today, Library, Review, Progress) plus an **onboarding** on first launch.

| Area | Screen | Role |
|---|---|---|
| Onboarding | Languages | "I speak" pre-filled with the UI language; the learner only picks the language to learn among the two others |
| Onboarding | Level test | 30 to 50 words from the most to the least frequent; the learner ticks those they know. "I'm a complete beginner" skips it |
| Onboarding | Daily goal | 5, 10, 20 or 30 minutes a day |
| Today | Today | Day streak, ring towards the daily goal, "To review" card (cards due, estimated time), "Read next" card (suggested text, level, length, estimated comprehension), new words this week |
| Library | Catalogue | Texts filtered by level (A1–C1) and type (story, dialogue, news, lessons, and pronunciation when the help is on); comprehension badge (green ≥ 90 %, orange 75–90 %, grey below); read texts ticked |
| Library | Reader | Text sentence by sentence, words coloured by status, audio player (play/pause, speed 0.75× / 1× / 1.25×, repeat sentence), sentence being read highlighted, long press on a sentence to translate it, "I'm done reading" marks the untouched new words as known |
| Library | Word sheet (bottom sheet) | Word as written and its base form (*casei* → *casă*, noun, feminine), main meaning and 1–2 others, audio, original sentence, "Add to review" and "I already know it" |
| Library | Lesson | "How the language works" lesson (kept from M1) |
| Review | Review session | Card front: the original sentence with the word hidden; back: meaning, base form, audio; four FSRS ratings (Again, Hard, Good, Easy) with the next interval under each; session progress bar |
| Review | End of session | Cards reviewed, success rate, next review; "Read a text now?" with the suggested text |
| Progress | Statistics | Known words, known words over 30 days, regularity calendar, "You understand about 85 % of an A1 text" |
| Progress | Settings | Languages, UI language, daily goal, pronunciation help |

---

## 5. Reading and words

### 5.1 Content

- A **text** has a language, a level (A1–C1), a type (story, dialogue, news), a title, an estimated reading time and **sentences**. Each sentence carries its translation in the UI languages.
- Texts ship **already split into words**, each word linked to its **base form** (lemma). The phone does not lemmatise: Romanian articles and cases change the end of the word (*casă, casa, casei, casele*) and an on-device lemmatiser would not be reliable. Prototype: a dictionary of forms in the sample content; M2: tokens delivered by the API.
- A **lemma** has its base form, its part of speech (and gender), its meanings in the UI languages, a frequency rank (for the level test), and optionally its IPA and a pronunciation note.
- Language rules to respect: **French** elisions (*l'homme*, *qu'il*) are two words; **English** phrasal verbs (*give up*) are one unit (phase 2: a multi-word selection gesture); **Romanian** always shows the base form; diacritics with a comma below (§6.1).

### 5.2 Word status

Every lemma has a status for the learner: **new** (never met or untouched), **learning** (added to the reviews), **known**. It is tracked per **base form**, so meeting *casei* after *casa* is the same word.

- Tapping a word opens its sheet. "Add to review" → learning + a review card; "I already know it" → known.
- "I'm done reading" marks the **new** words of the text that were not added to the reviews as **known**.
- **Comprehension** of a text = share of its words (occurrences) whose lemma is known or learning. Shown in the Library and on Today.
- The **suggested text** is the unread text whose comprehension is the highest while staying at or below 98 % (a text where everything is known teaches nothing), favouring the learner's level.

---

## 6. "How the language works" lessons

### 6.1 Curriculum (A1 → B1)

Kept from M1, for Romanian first:

1. Alphabet, **diacritics** (ă, â, î, ș, ț), pronunciation rules (ce/ci, che/chi, ge/gi, â vs î, *ea*, *oa*). ș and ț with a **comma below** (U+0219, U+021B), never a cedilla (U+015F, U+0163): normalise all input and content.
2. Genders, including the **neuter**.
3. Plural of nouns.
4. Indefinite article and **enclitic definite article**.
5. Adjective agreement.
6. *A fi*, *a avea*, present tense.
7. Present tense of regular verbs.
8. Stressed and **clitic** pronouns.
9. **Cases** (nominative/accusative, genitive/dative with *al, a, ai, ale*, vocative).
10. **Subjunctive** with *să*.
11. *Perfect compus*.
12. Future.
13. Prepositions and cases.

English and French have their own first lessons (from M1). Explanations rely on **comparison with the app's other languages**.

### 6.2 Content and reliability

- **Lessons**: fixed, reviewed content, distributed by the content API (§9.3), stored in Room. An initial set ships in the APK for the first offline launch.
- Every text meant for the learner (title, explanation, instructions, translations) exists in **each UI language**; the app shows the one matching the UI language, English otherwise.
- Content produced with the help of an AI is **reviewed by native speakers** before publication.

---

## 7. Optional pronunciation help

Off by default, enabled in the settings or during onboarding later. When on:

- word sheets and card backs show the **IPA** of the base form and a **pronunciation note** (e.g. "ă is the vowel of the French *le*");
- the Library shows a **Pronunciation** filter with the exercises kept from M1 (minimal pairs, targeted sentences, listening with slow speed).

Phase 2: shadowing (listen, record, compare), on-device speech recognition for immediate feedback and cloud phonetic assessment (Azure) per phoneme, as described in §9.4 and §10. It then needs `RECORD_AUDIO` (§9.6).

---

## 8. Cross-cutting features

### 8.1 Spaced repetition
**FSRS** (version 4.5, default parameters, desired retention 0.9). One card per lemma, created from the word sheet, keeping the sentence where the word was met. Simplification: no intra-day learning steps, except that **Again** brings the card back after 10 minutes.

### 8.2 Today and progress
- **Day streak**: consecutive days with at least one review or one text read (today counts once something was done).
- **Daily goal ring**: estimated practice minutes today (15 s per review, the reading time of each text finished) against the goal.
- **Known words** over time, **regularity calendar**, estimated comprehension at the learner's level.

### 8.3 Reminders
Daily notification at a configurable time (WorkManager, requires `POST_NOTIFICATIONS`). M2.

---

## 9. Technical architecture

### 9.1 Android app

| Item | Choice |
|---|---|
| Language | Kotlin **2.4 minimum** (latest stable at start), K2 compiler |
| UI | Jetpack Compose + Material 3, no XML layout, edge-to-edge |
| Localisation | String resources in `values/` (English, default), `values-fr/`, `values-ro/`; per-app language support (`generateLocaleConfig`) |
| Architecture | MVVM + **unidirectional data flow**; multi-module Gradle (§11) |
| Injection | **Hilt** (KSP) |
| Asynchronous | Coroutines + Flow; **WorkManager** for deferred work |
| Persistence | **Room** (source of truth), DataStore (preferences) |
| Networking | **Retrofit** + OkHttp + `kotlinx.serialization` converter |
| Images | **Coil 3** (`coil-compose` + `coil-network-okhttp`) |
| Audio | PCM 16 kHz mono recording (AudioRecord) → WAV; Media3 playback |
| SDK | `minSdk = 34`; `compileSdk` / `targetSdk` = latest stable level (37 to date) |
| Build | AGP 9.x, Gradle Kotlin DSL, JDK 21 |

### 9.2 Persistence and offline-first

**Principle**: the UI observes **only Room** (DAOs exposing `Flow`s). The network is only used to **refresh** the local cache or to **process** pending items.

| Data | Source of truth | Synchronisation |
|---|---|---|
| Learning content (texts with their words, lemmas, lessons, pronunciation exercises, image URLs) | Room | `ContentSyncWorker`: downloads language packs from the API (§9.3) |
| Word statuses, review cards and review log, texts read | Room | `ProgressBackupWorker`: backup to the API |
| Preferences (language learnt, daily goal, pronunciation help, onboarding done) | DataStore | None (device only) |
| Recordings to score | Room (`PENDING` status) + local WAV file | `EvaluationWorker`: cloud scoring when the network is back |

**Workers and constraints** (to be justified in `SYSTEMS.md`):

| Worker | Type | Constraints | Reason |
|---|---|---|---|
| `ContentSyncWorker` | Periodic (24 h) + on demand | **Unmetered** network (Wi-Fi), **battery not low** | Large packs (images), not urgent |
| `EvaluationWorker` | One-off, chained after each offline recording | **Connected** network (any type) | Small volume, the user is waiting for the result |
| `ProgressBackupWorker` | Periodic (12 h) | Connected network, battery not low | Backup, not urgent |
| `ReminderWorker` | Periodic (24 h) | None | Daily reminder |

- Every worker is **idempotent** (running it again duplicates nothing), uses exponential *backoff* and respects Doze (WorkManager defers execution to maintenance windows; the daily reminder is therefore **inexact by design**).
- At the end of a sync or a deferred evaluation, a "synced" / "3 evaluations ready" notification is posted (if the permission is granted).
- **Conflict resolution**: learning content is read-only in the app (the server wins, versioned per pack); progress is append-only (log of timestamped events), so conflict-free.

### 9.3 Remote tier

**Choice**: a **REST API** consumed with **Retrofit**, rather than Firestore. Reasons to defend: explicit control of caching and sync (no "magic" offline layer), alignment with the labs, and the app only needs read-only content and append-only backup.

**Hosting**: Cloud Functions for Firebase (Python, 2nd gen) in the same Firebase project as the AI; content (JSON, images, audio) in Cloud Storage; progress stored server-side in Firestore, **behind the API** (the app never uses the Firestore SDK).

**Endpoints** (first version):

| Method | Route | Role |
|---|---|---|
| GET | `/languages` | List of available language packs and their version |
| GET | `/packs/{languageCode}?since={version}` | Content of a pack (incremental) |
| POST | `/progress` | Upload a batch of progress events |
| GET | `/progress?since={timestamp}` | Restore (reinstall, new device) |
| POST | `/speech/token` | Temporary Azure Speech token |
| POST | `/ai/proxy` | Proxy to a provider whose key cannot ship in the app (Claude…) — after the course |

This is what makes it possible to **add a language on the API side**: publishing a new pack on the server is enough for it to appear in the app (see §11.2 for the code side).

### 9.4 AI services

| Use | On-device | Cloud |
|---|---|---|
| Transcription (immediate feedback) | **ML Kit GenAI Speech Recognition** (Basic mode) | Azure Speech STT |
| Phonetic assessment (per-phoneme scores) | — (after the course: LiteRT model) | **Azure Pronunciation Assessment** |
| Translation between English, French and Romanian | **ML Kit Translation** | Gemini via **Firebase AI Logic** |
| Explanations, correction, exercise generation | Gemini Nano (ML Kit Prompt API) **if the device supports it** | Gemini via Firebase AI Logic |
| Speech synthesis | **Android TTS** (default, §10.1) | Azure TTS (neural voices) only if a system voice is judged insufficient |

- Everything goes through **capability interfaces** (§11.3); the on-device / cloud choice is made by the **placement policy** (§10).
- Firebase AI Logic is protected by **App Check** (mandatory). The Azure key is **never** in the APK: temporary token via `/speech/token`.
- Prompts centralised in `prompts/`, versioned, with **structured JSON** output.

### 9.5 Navigation and process-death survival

- **Navigation Compose** with **type-safe routes** (`@Serializable`). No Navigation 3, no string routes.
- Graph:
    - `Onboarding` (first launch only), then the four tabs below, each a top-level destination of the bottom bar;
    - `Today`;
    - `Library` → `Reader(textId)` — **main list → detail flow** (the word sheet is a bottom sheet of the Reader, not a route); `Library` → `LessonDetail(lessonId)`; `Library` → `PronunciationDetail(exerciseId)` (pronunciation help on);
    - `Review` (session, then end of session on the same route);
    - `Progress` → `Settings`.
- **Process death** (not just rotation):
    - route arguments and ViewModel screen state go through `SavedStateHandle`;
    - local UI state (text being typed, tab, scroll) through `rememberSaveable`;
    - long-lived business state (word statuses, review cards, ratings already given) is **persisted in Room** as it changes;
    - an **audio recording in progress** is lost by definition: the screen returns to the "ready to record" state with a message, without crashing.
- **Test**: an instrumented test checks that the back stack and state are restored after recreation (scenario documented for the demo: `adb shell am kill` on the app in the background, then return).

### 9.6 Runtime permissions

| Permission | Use | If denied |
|---|---|---|
| `POST_NOTIFICATIONS` | Daily reminder, sync notifications (M2) | The app works normally; a banner in Settings offers to enable it |
| `RECORD_AUDIO` | Shadowing and pronunciation feedback (phase 2, §7) | **Degraded mode**: reading, reviews and listening stay available; speaking exercises show an explanation and a button to allow |

For each:
- declared in the manifest;
- requested from the UI with `rememberLauncherForActivityResult(RequestPermission())`, **when it is needed** (not at launch), preceded by a rationale screen;
- checked with `ContextCompat.checkSelfPermission(...)`;
- **path to ask again**: if permanently denied, a button to the app's system settings;
- never a crash if the permission is revoked while in use.

### 9.7 Network images (Coil)

- Images served by the content API: **cover illustrations** of the texts in the Library and on Today, and later articulation diagrams for the pronunciation help.
- `AsyncImage(model = url, contentDescription = …)` with a **placeholder** and an **error state**; Coil disk cache for offline use; no blocking load on the main thread.

### 9.8 Data model (indicative)

Every content entity carries a `languageCode` (BCP-47). No language-specific table. Texts shown to the learner are stored per UI language (`en`, `fr`, `ro`).

- `LanguagePackEntity` (code, name, version, sync date)
- `TextEntity` (id, languageCode, level, type, title, minutes, coverUrl?) and `SentenceEntity` (textId, index, translations)
- `TokenEntity` (textId, sentence index, position, written form, lemmaId?, grammatical note?)
- `LemmaEntity` (id, languageCode, base form, part of speech, gender?, meanings per UI language, frequency rank, IPA?, pronunciation note?)
- `LessonEntity` (id, languageCode, title, order, JSON content), `PronunciationExerciseEntity`
- `WordStatusEntity` (languageCode, lemmaId, status, updated at) — **implemented**
- `ReviewCardEntity` (lemmaId, languageCode, context sentence, FSRS state: stability, difficulty, due, reps, lapses, last review) — **implemented**
- `ReviewLogEntity` (cardId, rating, reviewed at) and `ReadingLogEntity` (textId, minutes, finished at) — **implemented**; append-only, they feed the statistics and the backup

### 9.9 Modern Kotlin and Android standards

Since `minSdk = 34`, **no compatibility code** for older versions.

**Build**: Gradle Kotlin DSL, version catalog, **convention plugins** (`build-logic/`), AGP 9 with built-in Kotlin, **KSP only** (never kapt), configuration cache, up-to-date versions (Renovate or Dependabot).

**Language**: K2, `allWarningsAsErrors`, **explicit API mode** on `*:api` and `:core:domain`, `sealed interface` for states and errors, `data object`, `@JvmInline value class` for identifiers, *context parameters* (stable since Kotlin 2.4) **when they make things clearer**, no `!!`.

| Need | Required | Forbidden |
|---|---|---|
| Asynchronous | Coroutines, `Flow`, `StateFlow`, WorkManager | LiveData, RxJava, AsyncTask |
| Serialisation | `kotlinx.serialization` | Gson, Moshi |
| Networking | Retrofit + OkHttp + kotlinx.serialization converter | Gson converter, `HttpURLConnection` |
| Persistence | Room, DataStore | SharedPreferences, raw SQLite |
| Navigation | Navigation Compose, `@Serializable` routes | String routes, Navigation 3 |
| Images | Coil 3 | Glide, Picasso |
| Injection | Hilt (KSP) | Hand-written service locator |
| Dates | `kotlinx-datetime` (pure modules), `java.time` (Android) | `java.util.Date`, `Calendar` |
| UI | Compose + Material 3 | XML, Views |
| Language | Kotlin | Java |

**UI**: `ViewModel` → `StateFlow<UiState>`, collected with `collectAsStateWithLifecycle()`, stateless composables, light and dark `@Preview`.

**Quality**: blocking Detekt and ktlint, Lint with no warnings.

---

## 10. Integrated concept: on-device / cloud placement decision

**Chosen concept**: **on-device inference**, with the **contrasting cloud path behind the same interface**, and an **empirical measurement** of the trade-off.

### 10.1 Placement decisions

| Task | Default placement | Why | What would move it |
|---|---|---|---|
| Immediate pronunciation feedback (recognised words) | **On-device** (ML Kit Speech Recognition) | Low latency, works offline, the voice never leaves the device | Locale not supported on the device → cloud |
| Per-phoneme scores | **Cloud** (Azure), **deferred** when offline | No on-device model gives comparable phonetic accuracy on mobile | A LiteRT phoneme model reaching sufficient accuracy on the evaluation set |
| Translation of a sentence held in the Reader (when the content has no reviewed translation, e.g. imported texts) | **On-device** (ML Kit) for short sentences | Free, offline, private, instant | Insufficient quality (ML Kit pivots through English for French↔Romanian) or an explanation is needed → cloud |
| Explanations and corrections | **Cloud** (Gemini) | Quality, grammatical knowledge | Gemini Nano-capable device + measured sufficient quality → on-device |
| Speech synthesis (listening) | **On-device** (Android TTS engine) | Responsiveness (no network round trip on tap), offline once the voice is installed, free, no key to protect, nothing leaves the device | A system voice judged too poor for a language → Azure TTS for that language, system engine as fallback |

### 10.2 Placement policy

A `PlacementPolicy` (in `:core:domain`, unit tested) decides for each request based on:
- **connectivity** (none, metered, Wi-Fi);
- **battery** (level, power saver);
- the user's **privacy setting** ("keep everything local when possible");
- **capabilities available** on the device (model downloaded, locale supported);
- **foreground / background**: ML Kit GenAI APIs only work while the app is in the foreground. Background processing (WorkManager) therefore **has to go through the cloud** — a system constraint to explain in the defence.

### 10.3 Measurements

Built-in instrumentation (can be enabled in Settings), results stored in Room and exportable as CSV:
- **latency** (p50, p95) per task and per placement;
- **energy** (`BatteryManager` charge counter before/after a series, and the Android Studio profiler);
- **data transferred**;
- estimated **cost** (cloud requests);
- **quality** on the evaluation set (§11.4).

The figures feed `SYSTEMS.md`.

### 10.4 `SYSTEMS.md`

One page, in prose, structured by the course's five questions, applied to the decisions above:
1. **What binds?** — constraints: phonetic accuracy, offline, voice privacy, foreground requirement for on-device GenAI, quotas, cost.
2. **What will you do?** — the placement policy and the workers.
3. **What are you giving up?** — offline accuracy, freshness of scores, battery, costs.
4. **What breaks?** — network down, Doze, process killed, model not downloaded, quota reached, permission revoked.
5. **What would change your mind?** — measured thresholds that would move each task.

---

## 11. Modularity

**Goal**: add a language or change AI provider / model **without changing feature code**. Blocking constraints.

### 11.1 Gradle modules

```
:app                      → assembly, Hilt, configuration
:core:domain              → models, use cases, PlacementPolicy (pure Kotlin)
:core:data                → Room, DataStore, repositories, workers
:core:network             → Retrofit, content and progress API
:core:audio               → recording, playback, WAV
:core:ui                  → theme, shared components, localisation helpers
:ai:api                   → AI interfaces + normalised results (pure Kotlin)
:ai:gemini                → cloud Gemini (Firebase AI Logic)
:ai:mlkit                 → on-device ML Kit (translation, transcription, Gemini Nano)
:ai:fake                  → fakes for tests and offline development
:speech:api               → assessment, STT, TTS (pure Kotlin)
:speech:android           → Android system TTS (default speech synthesis)
:speech:azure             → Azure Speech (phonetic assessment, STT)
:speech:fake
:lang:api                 → LanguagePack + content schemas (pure Kotlin)
:lang:en-gb, :lang:ro-ro  → language-specific rules (normalisation, keyboard)
:lang:test                → minimal test pack
:feature:reading          → Library, Reader, word sheet
:feature:srs              → review session
:feature:session          → Today
:feature:dashboard        → Progress
:feature:settings         → Settings
:feature:onboarding       → first launch: languages, level test, daily goal
:feature:grammar          → "How the language works" lessons
:feature:pronunciation    → pronunciation exercises (optional help)
:feature:oral             → shadowing, conversation (phase 2)
```

**Rules** (automatic architecture test, Konsist or equivalent):
- `:feature:*` depends only on `:core:*` and `*:api` modules; **never** on an implementation.
- ViewModels depend on **repositories and use cases**, never directly on Retrofit, Room, Firebase, ML Kit or Azure.
- Only `:app` wires implementations through Hilt.

### 11.2 Adding languages

```kotlin
public interface LanguagePack {
    public val code: LanguageCode            // "ro-RO", "en-GB"
    public val displayName: String
    public val variants: List<LanguageCode>
    public val phonemeInventory: List<Phoneme>   // IPA
    public val textNormalizer: TextNormalizer    // e.g. ş → ș
    public val specialKeys: List<String>
}
```

- **Content** (lessons, exercises, curriculum, images) comes from the **API** (§9.3); **code rules** (normalisation, keyboard) from a `:lang:<code>` module. A language without its own module uses a generic data-driven pack (normalisation table and keys provided by the API).
- No `if (language == "ro")` in `:feature:*`.
- Parameterised prompts: target language, variant, level, **explanation language** (the UI language).
- The UI queries the capability registry to adapt the exercises available per locale.
- Two different notions: the **language being learnt** (content `languageCode`) and the **UI language** (English, French or Romanian, from the system).
- Documented procedure: `docs/adding-a-language.md`.

### 11.3 Interchangeable providers and models

Interfaces **per capability**: `TextGenerator` (including `generateStructured<T>`), `Translator`, `AudioUnderstanding`, `PronunciationAssessor`, `SpeechToText`, `TextToSpeech`. Each implementation declares its **capabilities** (locales, on-device or cloud, foreground required, JSON output, streaming).

Normalised results: `PronunciationResult` in IPA; `AiError` (`Network`, `QuotaExceeded`, `UnsupportedLocale`, `InvalidOutput`, `ForegroundRequired`, `ProviderError`).

Routing per task, **configurable without recompiling** (embedded configuration, overridden by Remote Config then by an advanced settings screen):

```json
{
  "tasks": {
    "translate":            { "chain": [ {"provider": "mlkit"}, {"provider": "gemini", "model": "<model>"} ] },
    "pronunciation.quick":  { "chain": [ {"provider": "mlkit"}, {"provider": "azure"} ] },
    "pronunciation.assess": { "chain": [ {"provider": "azure"} ] },
    "grammar.correct":      { "chain": [ {"provider": "gemini", "model": "<model>"} ] }
  }
}
```

The `TaskRouter` combines this chain with the `PlacementPolicy` (§10.2) and falls back to the next link on error. **No model or voice name hard-coded.** Documented procedure: `docs/adding-a-provider.md`.

### 11.4 Tests

- **JVM unit tests** with fakes: FSRS, comprehension and text suggestion, streak and daily minutes, tokenisation, `PlacementPolicy`, `TaskRouter`, diacritics normalisation, JSON parsers, repositories (with fake API and fake DAO), content completeness in every UI language (every word of a text linked to a lemma).
- **Contract tests**: the same suite against every implementation of an interface.
- **Add-a-language test**: everything works with `:lang:test` without changing `:feature:*`.
- **Worker tests** (`work-testing`): constraints, idempotence, resumption.
- **Instrumented tests** (at least): real Room DAO; restoration after process death (§9.5).
- **Evaluation set** (`eval/`): reference cases per task to compare placements, providers and models.

---

## 12. Non-functional requirements

- **Offline**: texts and lessons already synced, reading with on-device audio, word sheets, reviews and statistics usable without network; cloud requests (translation, later evaluations) are queued or fall back to the device.
- **Main thread**: no I/O, no audio or image decoding on it (StrictMode enabled in debug).
- **Costs**: usage counter per provider, configurable monthly cap that switches to on-device or disables cloud evaluation.
- **Privacy**: audio stored locally, purged after 30 days; "keep everything local when possible" setting.
- **Latency**: on-device feedback < 1 s; cloud feedback < 4 s (normal network).
- **Secrets**: no key in the **public** repository; `local.properties` and CI secrets only.
- **Localisation**: every user-facing string and content text exists in English, French and Romanian; missing translations fail the build (Lint) or the tests.

---

## 13. Git process and course deliverables

### 13.1 Repository and CI
- **Public GitHub** repository in the course organisation.
- **Protected** `main`: PR required, at least **one approving review from a teammate**, **green CI**. (Solo project: ask the teacher how the review applies.)
- **Merge commits** (no squash). Commits under the student's name. `Co-authored-by:` for pair work.
- **GitHub Actions** on every PR: build, unit tests, Detekt, ktlint, Lint, architecture test; instrumented tests on an emulator (at least on `main` and on milestone PRs).
- **Regular** work throughout the semester (a last-minute rush of commits is penalised).

### 13.2 Ownership per area
In M2: owner of **architecture**, **networking**, **persistence**. In M3: owner of **DI/tests** and **integrated concept**. Each owner must be able to explain and modify their area live.

### 13.3 Required files
- `SYSTEMS.md` (§10.4).
- `AI_USE_LOG.md`: **append-only** log, one section per member: goal, tool + prompt, what was produced, what was accepted / rejected / modified and why, how it was verified. Ends with a paragraph: one thing the AI did badly or could not tell you about this project.
- `ARCHITECTURE.md`: overview of the project's architecture.
- Contribution statement and peer review (outside the repository).

---

## 14. Milestones

### M1 — Prototype (due: Sun. 1 Nov. 2026)
- Android project with the module structure (§11.1), convention plugins, version catalog.
- Main screens in Compose with **fake data**: `Home`, `PronunciationList` → `PronunciationDetail`, `LessonList` → `LessonDetail`.
- Type-safe Navigation Compose in place.
- Minimal GitHub Actions CI (build + unit tests) and protection of `main`.
- `AI_USE_LOG.md` initialised + reflection required by the course.

**Criteria**: the app installs on Android 14, list → detail navigation works, CI is green on the milestone PR.

### After M1 — Reading loop prototype (branch `feature/reading-loop`)
- New concept (§0, §4, §5): four tabs, Library → Reader → word sheet, reviews with FSRS, Today, Progress, Settings, onboarding.
- Sample content in the APK (a few texts per language, forms dictionary); word statuses, review cards and logs in **Room**, preferences in **DataStore**.
- Not yet: content from the API, reminder, Coil covers, multi-word selection, sentence translation by ML Kit / Gemini.

### M2 — Data layer (due: Sun. 6 Dec. 2026)
- **Room**: entities, reactive DAOs, repositories; the UI only observes Room.
- **REST API** (Cloud Functions): `/languages`, `/packs`, `/progress`; **Retrofit** client.
- **WorkManager**: `ContentSyncWorker`, `ProgressBackupWorker`, `EvaluationWorker` with their constraints; end-of-sync notification.
- **`POST_NOTIFICATIONS`** and **`RECORD_AUDIO`** permissions with rationale, degraded mode and asking again.
- **Process-death survival** (`SavedStateHandle`, `rememberSaveable`, session state in Room).
- **Coil**: text covers.
- Content: a starting catalogue (aim: 60 to 100 short texts with audio, A1 to B1) as language packs, Romanian lessons 1 to 3.
- Daily reminder (`ReminderWorker`, `POST_NOTIFICATIONS`).

**Criteria**: in airplane mode, the app shows the synced texts and the reviews work; when the network is back, new packs are downloaded and progress is backed up. After `adb shell am kill` in the background, the app comes back to the same screen with the same state. Denying `POST_NOTIFICATIONS` does not crash the app.

### M3 — DI, tests, integrated concept (due: Sun. 11 Jan. 2027, defence 14 or 21 Jan.)
- Complete Hilt, fakes, contract tests, architecture test, worker tests, **at least one instrumented test** (DAO + restoration).
- **On-device paths**: ML Kit Translation for sentences held in the Reader (cloud: Gemini), then ML Kit Speech Recognition for shadowing; `PlacementPolicy` and `TaskRouter`.
- **Instrumentation and measurements** (§10.3), evaluation set.
- **`SYSTEMS.md`** written from the measurements.

**Criteria**: toggling connectivity or the privacy setting changes the placement, and it is visible on the result screen ("scored on the device" / "in the cloud"); measurements can be exported; CI is green with all tests.

> Early defence possible on 17 Dec. if M3 is submitted before 14 Dec.

### After the course
Phase 2 (§3): shadowing and pronunciation feedback, AI conversation limited to known words, corrected writing, import of personal content; Romanian lessons 4 to 13; Claude provider through the proxy; LiteRT phoneme model.

---

## 15. Points to check at the start of M2

To be documented in `docs/verifications.md`, with dates:
- `ro-RO` and `en-GB` support in Azure Pronunciation Assessment; prosody in `en-GB`.
- Locales supported by ML Kit GenAI Speech Recognition (Basic mode), especially `en-GB` and `ro-RO`, and availability on the test device.
- Quality of ML Kit Translation French↔Romanian (English pivot) on the evaluation set.
- Availability of Gemini Nano on the device.
- Gemini model to use, prices and free quotas; Azure quotas.
- Chosen versions: Kotlin, AGP, Gradle, compileSdk, Compose BOM, Navigation Compose, Coil, Retrofit, Room, WorkManager.

---

## 16. Instructions for Claude Code

**Understanding first**: the project is graded through a defence where the student must explain and **modify the code live**. Therefore:
- Prefer **simple, readable** code over clever abstractions; every abstraction needs a clear reason.
- Work in **small PRs** (one area, one goal), with a description explaining **what was done, why, and the alternatives that were rejected**.
- For each area (architecture, networking, persistence, DI/tests, integrated concept), keep a short note `docs/understanding/<area>.md` up to date: how it works, where to look in the code, likely defence questions.
- **Never** squash; commits under the student's Git identity.
- At the end of each task, propose a **draft entry** for `AI_USE_LOG.md` (goal, summarised prompt, what was produced), leaving the "accepted / rejected / modified and why" and "how I verified it" fields to the student. Never rewrite existing entries (append-only log).

**Technical**:
- Respect §2 (course baseline), §9.9 (Kotlin standards) and §11 (modularity) in **every** PR.
- Check the **latest stable versions** at the start rather than relying on remembered versions.
- No key or secret in the repository (it is public).
- Ask for approval before adding a heavy dependency or a paid service.
- Write tests together with the logic.
- If something cannot be done (unsupported API, quota), propose an alternative before coding.
- Code, comments and documentation in English. User-facing text in English, French and Romanian.
