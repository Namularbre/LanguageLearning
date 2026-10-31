# Understanding — Architecture

> Written at the end of M1. Since the reading loop, Home is replaced by four tabs and the language being learnt comes from the settings (DataStore), not from chips on Home: see [reading-loop.md](reading-loop.md) and `ARCHITECTURE.md`. The principles below are unchanged.

## How it works

**Modules.** The project is split as described in `SPEC.md` §11.1. Three families:

| Family | Examples | Role | Kind |
|---|---|---|---|
| `*:api`, `:core:domain` | `:lang:api`, `:ai:api` | Interfaces and models. No Android dependency. | Pure Kotlin, *explicit API mode* |
| Implementations | `:core:data`, `:lang:ro-ro`, `:ai:gemini` | Concrete code behind the interfaces | Android or pure Kotlin |
| `:feature:*` | `:feature:pronunciation` | Screens + ViewModels | Android + Compose |

Key rule: a *feature* only sees `:core:*` and `*:api` modules. Only `:app` knows the implementations and wires them with Hilt (`app/.../di/DataModule.kt`). Replacing the M1 fake data with Room in M2 only changes that file.

**Unidirectional data flow (MVVM).**

```
Repository (Flow) ──► ViewModel (StateFlow<UiState>) ──► Composable (displays)
                                  ▲                               │
                                  └────────── events (tap) ───────┘
```

- The ViewModel turns the repository `Flow` into a `StateFlow<UiState>` with `stateIn(WhileSubscribed(5_000))`: the upstream stays active 5 s after the screen goes away (e.g. rotation), then stops.
- `UiState` is a `sealed interface` (`Loading`, `Success`, `NotFound`): the composable's `when` is exhaustive.
- Each screen has two composables: `XxxScreen` (gets the ViewModel with `hiltViewModel()`) and `XxxContent` (stateless, testable and previewable).

**Navigation.** Type-safe `@Serializable` routes (`PronunciationList(languageCode)`, `PronunciationDetail(exerciseId)`…). Each feature exposes a `NavGraphBuilder.xxxScreens(...)` extension. `:app` assembles everything in `LanguageLearningNavHost`. Features do not know each other: Home receives callbacks (`onOpenPronunciation`) instead of importing the other features.

**Two kinds of language.** Do not mix them up:
- the **language being learnt** (English, Romanian, French): carried by every piece of content (`languageCode`), chosen with the chips on Home;
- the **UI language** (English, French, Romanian): the phone's language, or the per-app language chosen in the system settings.

**Choosing the language being learnt.** Home shows one chip per language above the modules. The chosen language is passed to the route (`PronunciationList(languageCode)`, `LessonList(languageCode)`) and each list filters by language (`observeExercises(languageCode)`, `observeLessons(languageCode)`).
- The list of languages is declared once, in `:app` (`LanguageLearningNavHost`), and passed to Home: the `session` feature hard-codes no language. In M2 it will come from the downloaded packs.
- The displayed name ("Romanian", "Roumain", "Română") comes from the platform (`Locale.getDisplayLanguage`, `LanguageCode.displayName()` helper in `:core:ui`): adding a language needs no new string.

**UI language (localisation).**
- Buttons, titles and messages: Android string resources, `values/` (English, default), `values-fr/`, `values-ro/`. Android picks the folder matching the system language. Lint fails the build if a string is missing in one language.
- `generateLocaleConfig = true` in `app/build.gradle.kts` lists those languages for Android 13+, which then offers a per-app language choice in the system settings (Settings › Apps › Languages › App language). `res/resources.properties` says the default folder is English.
- Learning content: every text shown to the learner (title, explanation, instructions, translation of an example) is a `LocalizedText` (`:core:domain`), a map from language ("en", "fr", "ro") to text. `LocalizedText.localized()` (`:core:ui`) picks the UI language, English otherwise. A text with a single English entry is the same for everyone (e.g. "ship / sheep", an IPA symbol).
- `uiLanguage()` (`:core:ui`) applies the same rule as Android resources (supported language, else English), so content always matches the buttons around it.
- Example translations are given in the UI languages other than the lesson's own language; in an English lesson with an English UI, the translation is hidden because it would add nothing.
- `SampleContentTest` checks that every content text exists in all three languages.

**Process-death survival.**
- The back stack is saved by `rememberNavController()`.
- Route arguments are in the ViewModel's `SavedStateHandle` (restored automatically).
- The session length and language chosen on Home are stored in the `SavedStateHandle` (`getStateFlow`). The language is stored as a `String` (its code), because the handle cannot save a `value class`.

**Build.** `build-logic/convention` contains Gradle plugins that share the configuration (SDK, Java 21, warnings as errors, Compose, Hilt/KSP). A module only declares `alias(libs.plugins.languagelearning.android.feature)`.

**Listening (speech synthesis).** Two screens have "listen" cards: lesson examples (text + translation) and the words of pronunciation exercises (text + IPA). Every piece of content carries its language (`Lesson.languageCode`, `PronunciationExercise.languageCode`): the screen never assumes "Romanian" or "English".
- Interface: `TextToSpeech` in `:speech:api`. Current implementation: `AndroidTextToSpeech` (`:speech:android`), the system engine: on the device, offline once the voice is installed (Google TTS downloads it on first use). Azure cloud voices (§9.4) could replace it in `SpeechModule` without touching the features.
- Shared behaviour in `SpeechPlayer` (`:speech:api`): one text at a time (a new tap cancels the previous one with `cancelAndJoin`), text being played exposed as a `StateFlow`, last error kept until shown. Each ViewModel creates one with its `viewModelScope`: leaving the screen stops the sound.
- Shared UI in `:core:ui` (`component/Speech.kt`): `ListenCard`, `SlowSpeedChip`, `SpeechErrorSnackbar`.
- Slow speed (0.75×) is kept in each screen's `SavedStateHandle`.
- Engine calls (`setLanguage`, `speak`, `stop`) are synchronous calls into another process: `setLanguage` was measured at 25–180 ms. They therefore run on a single background thread (`Dispatchers.IO.limitedParallelism(1)`), never on the main thread, and the language is only changed when it differs from the previous one.
- The error snackbar has a `Long` duration: with an action button the default is "indefinite", so the error would stay set and a new error of the same kind would not show.

## Where to look in the code

| Question | File |
|---|---|
| Shared module configuration | `build-logic/convention/src/main/kotlin/` |
| Versions | `gradle/libs.versions.toml` |
| Navigation graph, languages offered | `app/src/main/kotlin/.../navigation/LanguageLearningNavHost.kt` |
| Wiring of implementations | `app/src/main/kotlin/.../di/DataModule.kt` |
| Full list → detail example | `feature/pronunciation/src/main/kotlin/.../` |
| Localised content | `core/domain/.../model/LocalizedText.kt`, `core/ui/.../Localization.kt` |
| Sample content | `core/data/src/main/kotlin/.../sample/` |
| UI strings | `*/src/main/res/values{,-fr,-ro}/strings.xml` |
| Contract of a language | `lang/api/src/main/kotlin/.../LanguagePack.kt` |
| CI | `.github/workflows/ci.yml` |

## Likely defence questions

- **Why `SavedStateHandle[KEY]` rather than `toRoute()` in ViewModels?** `toRoute()` decodes through a real `android.os.Bundle`, which JVM tests do not have; it would need Robolectric. The key is the name of the route property (`EXERCISE_ID_KEY` constant next to the class).
- **How do you demonstrate process death?** Open a detail screen, pick 15 min, press Home, `adb shell am kill com.sazerat.languagelearning`, reopen from Recents: same screen, same back stack, 15 min still selected. (Relaunching with `adb shell am start` creates a second activity: go through Recents.)
- **Why `WhileSubscribed(5_000)`?** So that a rotation does not restart the query, while work stops when the user really leaves the screen.
- **Where do you add a language?** Content: on the API side (M2). Code rules: a `:lang:<code>` module implementing `LanguagePack`. No change to `:feature:*`.
- **How do you add a UI language?** A `values-xx/strings.xml` in every module with strings, and an `xx` entry in every `LocalizedText` of the content. Lint and `SampleContentTest` point out what is missing.
- **Why string resources for the UI but `LocalizedText` for the content?** Resources are compiled into the APK; content will come from the API in M2 and cannot be a resource. Both follow the same language rule.
- **Why KSP and not kapt?** kapt generates Java stubs (slow, in maintenance mode); KSP reads the Kotlin code directly.
- **Why system speech synthesis rather than Azure?** Decision of 29/10/2026: stay on the device for responsiveness (no network round trip on the first tap), and also because it is free, has no key to protect, and the voice never leaves the device. Trade-off: lower quality than neural voices, and the voice must be downloaded once (otherwise a "No voice installed" message with a button to the settings). It is a first example of a placement decision (§10). What would change it: a system voice judged too poor for a language. Azure would then plug in behind the same `TextToSpeech` interface, with the system engine as fallback.
- **Why is `SpeechPlayer` a class rather than code in each ViewModel?** Both screens had exactly the same logic (one text at a time, "playing" state, error): a single copy, tested once (`SpeechPlayerTest`). It is pure Kotlin, so testable without Android.
- **How does the engine callback become a `suspend` function?** Each utterance has an id; a `CompletableDeferred` is stored under that id and completed by `UtteranceProgressListener.onDone/onError`.
- **Why did the app lag on every tap on an example?** `setLanguage` was called on the main thread on every tap (25–180 ms, i.e. several dropped frames). Fix: dedicated background thread + remembered language. Measured with `adb shell dumpsys gfxinfo`: janky frames 26 % → 9 %, 90th percentile 77 → 44 ms (debug build on an emulator).
- **Why a single thread (`limitedParallelism(1)`) and not all of `Dispatchers.IO`?** The engine is not designed for concurrent calls, and `currentLanguage` is only read and written from that thread: no lock needed.
- **Why `<queries>` in the `:speech:android` manifest?** Since Android 11, an app cannot see other apps (including the TTS engine) without declaring it.
- **Why `:core:testing`?** It contains `MainDispatcherRule`, shared by every ViewModel test: `viewModelScope` uses `Dispatchers.Main`, which the JVM does not have.
