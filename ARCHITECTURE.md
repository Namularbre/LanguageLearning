# Architecture

A map of the project, to find your way around quickly. The detailed *why* behind each choice and likely defence questions are in [`docs/understanding/architecture.md`](docs/understanding/architecture.md); the full specification is [`SPEC.md`](SPEC.md).

Status: **end of M1**. Screens run on in-memory sample data, speech synthesis runs on the device, and there is no Room, networking or AI yet.

---

## 1. In one sentence

A **multi-module** Android app using **MVVM with unidirectional data flow**: Compose screens observe a `StateFlow` exposed by a ViewModel, which reads **repositories** defined as interfaces; only `:app` picks the implementations, with **Hilt**.

```
Composable  ───event───►  ViewModel  ──►  Repository / TextToSpeech (interfaces)
    ▲                         │                       ▲
    └─── StateFlow<UiState> ──┘          implementations wired by :app (Hilt)
```

---

## 2. Modules

Three families, one rule: **a feature only sees `:core:*` and `*:api` modules**, never an implementation or another feature.

```mermaid
graph TD
    app[":app"]
    subgraph features
        session[":feature:session<br/>(Home)"]
        pron[":feature:pronunciation"]
        grammar[":feature:grammar<br/>(lessons)"]
    end
    subgraph core
        ui[":core:ui"]
        domain[":core:domain"]
        data[":core:data"]
    end
    subgraph speech
        sapi[":speech:api"]
        sandroid[":speech:android"]
    end

    app --> session & pron & grammar
    app --> data & sandroid
    session & pron & grammar --> ui & domain
    pron & grammar --> sapi
    ui --> sapi
    sapi --> domain
    data --> domain
    sandroid --> sapi
```

| Module | Kind | Contents today |
|---|---|---|
| `:app` | Android | `MainActivity`, navigation graph, Hilt modules (`di/`) |
| `:core:domain` | Pure Kotlin | Models (`Lesson`, `PronunciationExercise`, `LanguageCode`, `LocalizedText`…) and repository interfaces |
| `:core:data` | Android | In-memory repositories and sample content (`sample/`) |
| `:core:ui` | Android + Compose | Theme, `AppScaffold`, listening components (`ListenCard`…), localisation helpers (`localized()`, `uiLanguage()`, `displayName()`) |
| `:core:testing` | Pure Kotlin | `MainDispatcherRule` for ViewModel tests |
| `:speech:api` | Pure Kotlin | `TextToSpeech` interface, `SpeechPlayer` (one text at a time) |
| `:speech:android` | Android | `AndroidTextToSpeech`: the phone's speech engine |
| `:speech:fake` | Pure Kotlin | `FakeTextToSpeech` for tests |
| `:feature:session` | Feature | Home: session length, language choice, entry to the modules |
| `:feature:pronunciation` | Feature | Pronunciation exercises, list → detail |
| `:feature:grammar` | Feature | Lessons, list → detail |
| `:lang:api`, `:lang:ro-ro`, `:lang:en-gb`, `:lang:test` | Pure Kotlin | Language-specific rules (e.g. Romanian cedilla → comma-below). **Not wired into the app yet.** |

**Empty modules planned by the spec** (§11.1): `:core:network`, `:core:audio`, `:ai:*`, `:speech:azure`, `:feature:oral`, `:feature:srs`, `:feature:dashboard`, `:feature:settings`.

Pure Kotlin modules (`*:api`, `:core:domain`) use *explicit API mode* and have no Android dependency, so they are tested on the JVM.

---

## 3. Anatomy of a screen

Every screen follows the same pattern (example: `feature/grammar/.../detail/`):

| File | Role |
|---|---|
| `XxxViewModel.kt` | Reads route arguments from the `SavedStateHandle`, turns repository `Flow`s into a `StateFlow<XxxUiState>` (`stateIn(WhileSubscribed(5_000))`), exposes `onXxx()` functions for events. |
| `XxxUiState` (same file) | `sealed interface`: `Loading`, `Success(...)`, `NotFound`. |
| `XxxScreen.kt` | `XxxScreen` (stateful: gets the ViewModel with `hiltViewModel()`) and `XxxContent` (stateless: previewable and testable). |
| `navigation/XxxNavigation.kt` | `@Serializable` routes, a `navigateToXxx()` function, a `NavGraphBuilder.xxxScreens()` extension. |

---

## 4. Navigation

- **Type-safe** routes: `Home`, `PronunciationList(languageCode)`, `PronunciationDetail(exerciseId)`, `LessonList(languageCode)`, `LessonDetail(lessonId)`.
- `:app` assembles them in [`LanguageLearningNavHost.kt`](app/src/main/kotlin/com/sazerat/languagelearning/navigation/LanguageLearningNavHost.kt). The **list of languages** offered on Home is also declared there.
- Home does not know the other features: it receives callbacks (`onOpenLessons(language)`).
- Process death: the back stack is restored by `rememberNavController()`, the user's choices (session length, language, slow speed) by the `SavedStateHandle`.

---

## 5. Dependency injection (Hilt)

Only `:app` knows which implementation sits behind each interface:

| Interface | Current implementation | File |
|---|---|---|
| `PronunciationRepository` | `InMemoryPronunciationRepository` | [`di/DataModule.kt`](app/src/main/kotlin/com/sazerat/languagelearning/di/DataModule.kt) |
| `LessonRepository` | `InMemoryLessonRepository` | same |
| `TextToSpeech` | `AndroidTextToSpeech` (singleton) | [`di/SpeechModule.kt`](app/src/main/kotlin/com/sazerat/languagelearning/di/SpeechModule.kt) |

Replacing in-memory data with Room (M2), or adding Azure, only touches these files and the new module, never a feature.

---

## 6. Content

- All M1 content is hard-coded in [`core/data/.../sample/`](core/data/src/main/kotlin/com/sazerat/languagelearning/core/data/sample/): lessons 1 to 3 for each language, plus pronunciation exercises (9 in English `en-GB`, 5 in Romanian `ro-RO`, 5 in French `fr-FR`).
- Every lesson and exercise carries its language (`languageCode`): repositories filter by language, and screens never assume a particular language.
- Every text shown to the learner (titles, explanations, instructions, translations of examples) is a `LocalizedText` written in English, French and Romanian (see §7).
- In M2, content will come from the API as language packs and be stored in Room (SPEC §9.2–9.3).

---

## 7. UI languages

The app has two separate notions of language:

| | What | Where it comes from |
|---|---|---|
| **Language being learnt** | English, Romanian or French | Chips on Home → route argument → `languageCode` of the content |
| **UI language** | English (default), French or Romanian | The phone's language, or the per-app language in the system settings |

- **Buttons, titles, messages**: string resources in `values/` (English), `values-fr/`, `values-ro/` of each module. Lint fails the build if a translation is missing.
- **Learning content**: `LocalizedText` (`:core:domain`), one text per UI language; `localized()` (`:core:ui`) picks the UI language, English otherwise. A single English entry means "same for everyone" (e.g. "ship / sheep").
- **Per-app language**: `generateLocaleConfig` in `app/build.gradle.kts` lets Android 13+ offer the app's languages in Settings › Apps › Languages.
- Translations of lesson examples are hidden when the UI language is the language being learnt.

---

## 8. Speech synthesis

```
Tap on a card ─► ViewModel.onExampleClick() ─► SpeechPlayer.play()
    ─► TextToSpeech.speak(text, language, rate)   (suspend, cancellable)
    ─► AndroidTextToSpeech: background thread, phone's speech engine
```

- **On-device** by choice (SPEC §10.1): responsive, works offline, free, no key to protect.
- `SpeechPlayer` plays one text at a time: a new tap cancels the previous one.
- Engine calls never run on the main thread (that was the cause of the lag fixed in M1).
- Missing voice: a snackbar with a shortcut to the system text-to-speech settings.

---

## 9. Build, quality, CI

- **Convention plugins** in [`build-logic/convention/`](build-logic/convention/src/main/kotlin/): a module only declares `languagelearning.android.feature` (or `.library`, `.jvm.library`…) and inherits the SDK levels, Java 21, Compose, Hilt/KSP.
- Versions are centralised in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).
- Kotlin **and** Lint warnings are treated as errors.
- GitHub Actions CI ([`.github/workflows/ci.yml`](.github/workflows/ci.yml)): `assembleDebug`, `test`, `lintDebug`.

Run before every PR:

```bash
./gradlew assembleDebug test lintDebug
```

---

## 10. Tests

| Where | What |
|---|---|
| `core/domain/src/test` | `LocalizedText` (language fallback) |
| `core/data/src/test` | Content consistency (unique ids, IPA, every text in the three UI languages, no cedilla in Romanian…), repository filtering |
| `speech/api/src/test` | `SpeechPlayer` (cancellation, errors) |
| `feature/*/src/test` | ViewModels, with fake repositories and `FakeTextToSpeech` |
| `feature/pronunciation/src/androidTest` | One instrumented Compose test (emulator) |

---

## 11. Recipes

**Add a language**
1. Add its code to `studiedLanguages` in `LanguageLearningNavHost.kt`.
2. Add content with that `languageCode` in `core/data/.../sample/` (from M2: publish a pack on the API).
3. If the language has its own writing rules: a `:lang:<code>` module implementing `LanguagePack`.

No screen changes; the displayed name ("Roumain") comes from Android.

**Add a UI language**
1. A `values-xx/strings.xml` in every module that has strings.
2. Add `xx` to `uiLanguages` in `core/ui/.../Localization.kt`, and an `xx` translation to every `LocalizedText` of the content.

Lint and `SampleContentTest` list what is missing.

**Add a screen**
1. In the relevant feature: `XxxViewModel` + `XxxUiState`, `XxxScreen` + `XxxContent`, a `@Serializable` route.
2. Register it in the feature's `NavGraphBuilder.xxxScreens()`.
3. A ViewModel test using fakes.

**Plug in a new implementation** (Room, Azure…)
1. A new module (or class) implementing the domain interface.
2. Change the `@Provides` in `app/.../di/`.

---

## 12. Further reading

| Topic | Where |
|---|---|
| Why each choice was made, defence questions | [`docs/understanding/architecture.md`](docs/understanding/architecture.md) |
| Manual checks performed | [`docs/verifications.md`](docs/verifications.md) |
| Specification, M2/M3 milestones | [`SPEC.md`](SPEC.md) (§9 architecture, §10 placement, §11 modularity, §14 milestones) |
| AI use log | [`AI_USE_LOG.md`](AI_USE_LOG.md) |
