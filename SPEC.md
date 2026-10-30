# Specification — English (pronunciation) & Romanian training app

> Document for Claude Code. It describes the need, the constraints of the *Mobile Computing* course (UPB, 2026-2027), the target architecture and a breakdown aligned with the course milestones, with acceptance criteria. Work **milestone by milestone**, in order.

---

## 0. Summary

**Native Android app, Kotlin / Jetpack Compose**, **offline-first**, for personal use. The **UI is in English**, with **French and Romanian translations** chosen automatically from the phone's language (or the per-app language set in the system settings). Learning content (explanations, instructions, translations of examples) follows the same rule. Goals:

1. **Polish English pronunciation** (target accent: **Standard Southern British English, SSBE**) for an advanced user (TOEIC 900).
2. **Understand how Romanian works** (grammar, rules, spelling) from an **A1-A2** level.
3. **Practise speaking Romanian**.

Daily sessions of **5, 10 or 15 minutes**.

The app is also the **Mobile Computing course project**: it must meet the required technical baseline (§2) and carry a **defensible compute/data placement decision** (on-device vs cloud, §10). The project is graded mainly through a **live defence**: the student must be able to **explain and modify every part of the code**.

---

## 1. Context and user

| Item | Value |
|---|---|
| User | 1 person, French speaker |
| English | Advanced (TOEIC 900). Need: fine pronunciation, prosody, natural speech. |
| Romanian | Beginner (A1-A2). Need: grammar and spelling, then speaking. |
| Available time | 5 to 15 min/day |
| Device | **Android 14 (API 34)** smartphone — to be confirmed in Settings › About phone |
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
| **Navigation Compose with type-safe routes**, several screens, at least one **list → detail** flow | §9.5 |
| **Process-death survival** (back stack and screen state restored: `SavedStateHandle`, `rememberSaveable`) | §9.5 |
| Network images with **Coil 3** (`AsyncImage`, placeholder and error state) | §9.7 |
| **Room as the local source of truth** (entity + DAO + reactive reads as `Flow`); the UI observes Room, not the network | §9.2 |
| **Remote tier** chosen deliberately (Retrofit REST) + **offline-first sync with WorkManager** under real constraints (network type, battery) | §9.2, §9.3 |
| Dangerous **runtime permission**: rationale, denial handled without crash (degraded mode), path to ask again | §9.6 (`RECORD_AUDIO` + `POST_NOTIFICATIONS`) |
| Correct asynchronous work, main-thread safety, survives **Doze** | §9.2, §12 |
| JVM unit tests with fakes, **at least one instrumented test**, blocking **GitHub Actions CI** on every PR | §11.4, §13 |
| **Integrated concept**: on-device inference (ML Kit, LiteRT, MediaPipe), with a contrasting cloud path (Firebase AI Logic / Gemini) behind the same interface, and a **defensible placement decision** | §10 |
| Deliverables: `SYSTEMS.md`, `AI_USE_LOG.md`, contribution, peer review, history of reviewed PRs | §13 |

---

## 3. Priorities and scope

| Priority | Feature | Milestone |
|---|---|---|
| **P1** | English pronunciation with phoneme-level feedback | M2 (basic version), M3 (on-device/cloud placement) |
| **P2** | How Romanian works: lessons + corrected exercises | M2 (lessons 1 to 3), after the course (rest) |
| **P3** | Romanian speaking: listening, scored repetition | M3 (simple repetition), after the course (dialogues) |
| P4 | Daily session, spaced repetition, dashboard | M3 (simple session), after the course (complete) |
| Later | Free writing, corrected translation, free conversation, lessons 7-13 | After the course |

The scope of the course milestones is deliberately **reduced**: a solid, tested, explainable base is better than many fragile features. The rest is built after the course on the same architecture.

---

## 4. Session format

The user picks **5 min**, **10 min** or **15 min** (5 min by default).

| Length | Content |
|---|---|
| 5 min | 1 review (2 min) + 1 exercise targeting the main weakness (3 min) |
| 10 min | Review (3 min) + English pronunciation (4 min) + Romanian grammar OR speaking (3 min) |
| 15 min | Review (3 min) + English pronunciation (5 min) + Romanian grammar (4 min) + Romanian speaking (3 min) |

Rules:
- Content chosen automatically from the error log (§8.2).
- An interrupted session (including by **process death**) resumes exactly where it was (§9.5).
- "Free practice" mode to open any module directly.

---

## 5. English module — Pronunciation (P1)

### 5.1 Phonetic reference

Target accent: **SSBE**, non-rhotic, British IPA transcriptions.

Priority targets for a French speaker:
- Consonants: /θ/, /ð/, aspirated /h/, /ŋ/, British /r/ (not pronounced at the end of a syllable), *dark l*.
- Vowels: /ɪ/–/iː/, /ʊ/–/uː/, /æ/–/ʌ/–/ɑː/, /ɒ/–/ɔː/, /ɜː/, schwa /ə/.
- Diphthongs: /əʊ/, /eɪ/, /aɪ/, /aʊ/, /ɪə/, /eə/.
- Word stress (*PHOtograph / phoTOgraphy*), sentence stress, **weak forms** (*can* /kən/, *of* /əv/).
- *Connected speech*: linking, *linking r* / *intrusive r*, elision, assimilation.
- Intonation: rising / falling, *tag questions*.

### 5.2 Exercises

1. **Listening discrimination** (minimal pairs, pick the word heard) — works **without microphone or network**.
2. **Minimal pairs to say**.
3. **Targeted sentences** (one phoneme or phenomenon).
4. **Shadowing** (listen to the model, record, compare playback).
5. **Word stress**.

### 5.3 Expected feedback

- **Immediate on-device feedback** (approximate): local transcription, words recognised or not.
- **Detailed cloud feedback**: overall score, per word and **per phoneme** (colours), expected sound in IPA, short explanation in the UI language with an articulation tip, illustration of the mouth position (network image, §9.7).
- When offline: the recording is **queued** and scored later by WorkManager (§9.2), with a notification when the result is ready.
- Every error feeds the **error log**.

---

## 6. Romanian module — How the language works (P2)

### 6.1 Curriculum (A1 → B1)

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

Explanations rely on **comparison with the app's other languages** (English and French).

### 6.2 Exercises

Multiple choice, fill-in-the-blank sentences, transformations, short translations into Romanian, built-in diacritics keyboard.

### 6.3 Content and reliability

- **Lessons**: fixed, reviewed content, distributed by the content API (§9.3), stored in Room. An initial set ships in the APK for the first offline launch.
- Every text meant for the learner (title, explanation, instructions, translation of an example) exists in **each UI language** (English, French, Romanian); the app shows the one matching the UI language, English otherwise.
- **Generated exercises** from the LLM: JSON output validated against a schema.
- **Correction** of free answers by the LLM: verdict, correction, explanation in the UI language, identifier of the rule involved.

---

## 7. Romanian module — Speaking (P3)

1. **Listening** (on-device `ro-RO` TTS, speed 0.75× / 1×). Works offline once the system voice is installed (§10.1).
2. **Scored repetition**: same pipeline as English if `ro-RO` phonetic assessment is available (to be checked, §15), otherwise transcription + word-by-word comparison + qualitative LLM feedback, flagged as **indicative**.
3. **Mini-dialogues** from everyday life (after the course).
4. **Dictation**.

---

## 8. Cross-cutting features (P4)

### 8.1 Spaced repetition
**FSRS** algorithm. Cards: Romanian words, Romanian rules, English minimal pairs, failed English sentences. Created automatically from recurring errors.

### 8.2 Error log
Language, type (phoneme, rule, spelling), target, date, score. Drives the choice of session content.

### 8.3 Dashboard
Day streak, practice time, progress per target phoneme, success rate per Romanian rule, top 5 weaknesses.

### 8.4 Reminders
Daily notification at a configurable time (WorkManager, requires `POST_NOTIFICATIONS`).

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
| Learning content (lessons, minimal pairs, sentences, image URLs) | Room | `ContentSyncWorker`: downloads language packs from the API (§9.3) |
| Attempts, scores, errors, SRS cards | Room | `ProgressBackupWorker`: backup to the API |
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
- Graph (first version):
    - `Home` (session length, language, access to the modules)
    - `PronunciationList(languageCode)` → `PronunciationDetail(exerciseId)` → `PronunciationResult(attemptId)` — **main list → detail flow**
    - `LessonList(languageCode)` → `LessonDetail(lessonId)` → `Exercise(exerciseId)`
    - `Session(sessionId)`
    - `Settings` (including permissions, placement, providers)
- **Process death** (not just rotation):
    - route arguments and ViewModel screen state go through `SavedStateHandle`;
    - local UI state (text being typed, tab, scroll) through `rememberSaveable`;
    - long-lived business state (current session, answers already given, evaluation queue) is **persisted in Room** as it changes;
    - an **audio recording in progress** is lost by definition: the screen returns to the "ready to record" state with a message, without crashing.
- **Test**: an instrumented test checks that the back stack and state are restored after recreation (scenario documented for the demo: `adb shell am kill` on the app in the background, then return).

### 9.6 Runtime permissions

| Permission | Use | If denied |
|---|---|---|
| `RECORD_AUDIO` | Pronunciation and speaking exercises | **Degraded mode**: listening discrimination, grammar, listening and dictation stay available; speaking exercises show an explanation and a button to allow |
| `POST_NOTIFICATIONS` | Sync and evaluation notifications, daily reminder | The app works normally; a banner in Settings offers to enable it |

For each:
- declared in the manifest;
- requested from the UI with `rememberLauncherForActivityResult(RequestPermission())`, **when it is needed** (not at launch), preceded by a rationale screen;
- checked with `ContextCompat.checkSelfPermission(...)`;
- **path to ask again**: if permanently denied, a button to the app's system settings;
- never a crash if the permission is revoked while in use.

### 9.7 Network images (Coil)

- Images served by the content API: **articulation diagrams** per phoneme (tongue and lip position), Romanian vocabulary illustrations.
- `AsyncImage(model = url, contentDescription = …)` with a **placeholder** and an **error state**; Coil disk cache for offline use; no blocking load on the main thread.

### 9.8 Data model (indicative)

Every content entity carries a `languageCode` (BCP-47). No language-specific table. Texts shown to the learner are stored per UI language (`en`, `fr`, `ro`).

- `LanguagePackEntity` (code, name, version, sync date)
- `LessonEntity` (id, languageCode, title, order, JSON content)
- `ExerciseEntity` (id, lessonId?, languageCode, type, instructions, expected answer, variants, imageUrl?)
- `AttemptEntity` (id, exerciseId, date, text answer or audio path, **status**: `PENDING` / `EVALUATED` / `FAILED`, score, JSON feedback, **where it ran**: `ON_DEVICE` / `CLOUD`, measured latency)
- `PhonemeScoreEntity` (attemptId, word, IPA phoneme, score)
- `ErrorLogEntity`, `SrsCardEntity`, `SessionEntity` (with the current step, to resume)
- `ProgressEventEntity` (append-only log, sync status)

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
| Translation | **On-device** (ML Kit) for short sentences | Free, offline, private | Insufficient quality (ML Kit pivots through English for French↔Romanian) or an explanation is needed → cloud |
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
:feature:pronunciation, :feature:grammar, :feature:oral,
:feature:session, :feature:srs, :feature:dashboard, :feature:settings
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

- **JVM unit tests** with fakes: `PlacementPolicy`, `TaskRouter`, FSRS, session selection, diacritics normalisation, JSON parsers, repositories (with fake API and fake DAO), content completeness in every UI language.
- **Contract tests**: the same suite against every implementation of an interface.
- **Add-a-language test**: everything works with `:lang:test` without changing `:feature:*`.
- **Worker tests** (`work-testing`): constraints, idempotence, resumption.
- **Instrumented tests** (at least): real Room DAO; restoration after process death (§9.5).
- **Evaluation set** (`eval/`): reference cases per task to compare placements, providers and models.

---

## 12. Non-functional requirements

- **Offline**: lessons, already-synced exercises, reviews, listening discrimination, listening (on-device synthesis) and on-device feedback usable without network; cloud evaluations are queued.
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

### M2 — Data layer (due: Sun. 6 Dec. 2026)
- **Room**: entities, reactive DAOs, repositories; the UI only observes Room.
- **REST API** (Cloud Functions): `/languages`, `/packs`, `/progress`; **Retrofit** client.
- **WorkManager**: `ContentSyncWorker`, `ProgressBackupWorker`, `EvaluationWorker` with their constraints; end-of-sync notification.
- **`POST_NOTIFICATIONS`** and **`RECORD_AUDIO`** permissions with rationale, degraded mode and asking again.
- **Process-death survival** (`SavedStateHandle`, `rememberSaveable`, session state in Room).
- **Coil**: articulation diagrams and illustrations.
- Content: 20 English minimal pairs + Romanian lessons 1 to 3.
- Basic English pronunciation: recording + Azure `en-GB` assessment + Gemini explanation (through the §11.3 interfaces).

**Criteria**: in airplane mode, the app shows the synced content and accepts a recording, set to `PENDING`; when the network is back, `EvaluationWorker` scores it and posts a notification. After `adb shell am kill` in the background, the app comes back to the same screen with the same state. Denying `RECORD_AUDIO` does not crash the app and leaves the microphone-free exercises available.

### M3 — DI, tests, integrated concept (due: Sun. 11 Jan. 2027, defence 14 or 21 Jan.)
- Complete Hilt, fakes, contract tests, architecture test, worker tests, **at least one instrumented test** (DAO + restoration).
- **On-device paths**: ML Kit Speech Recognition (immediate feedback), ML Kit Translation; `PlacementPolicy` and `TaskRouter`.
- **Instrumentation and measurements** (§10.3), evaluation set.
- Simple daily session (5/10/15 min) and simple Romanian speaking repetition.
- **`SYSTEMS.md`** written from the measurements.

**Criteria**: toggling connectivity or the privacy setting changes the placement, and it is visible on the result screen ("scored on the device" / "in the cloud"); measurements can be exported; CI is green with all tests.

> Early defence possible on 17 Dec. if M3 is submitted before 14 Dec.

### After the course
Romanian lessons 4 to 13, spoken dialogues, complete SRS and dashboard, corrected writing and translation, Claude provider through the proxy, LiteRT phoneme model.

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
