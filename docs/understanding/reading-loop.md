# Understanding — Reading loop

The loop from SPEC §0: **read a text → tap the words you do not know → review them with FSRS → read the next text**. This note explains how each piece works; `ARCHITECTURE.md` has the module map.

## How it works

**Content.** Texts, lexicon and lessons are sample data in `:core:data` (`sample/`), served by in-memory repositories.
- A `ReadingText` is a list of sentences; each sentence is a list of tokens. A word token points to a `Lemma` (its base form): *casei* and *casa* share `ro:casă`.
- Texts are **pre-tokenised** when the content is written. `Tokenizer` (`:core:domain/reading/`) splits a sentence into words and the text between them; `SampleReadingTest` checks that every word token has a lemma. Nothing is tokenised on the learner's tap.
- A `Lemma` has a frequency rank, a part of speech, a gender, meanings (`LocalizedText`), and optionally an IPA transcription and a usage note.

**The learner's data.** Everything the learner does lives in Room (`LanguageLearningDatabase`, version 1, four tables), always filtered by language:

| Table | What | Repository |
|---|---|---|
| `word_status` | Status of each lemma met: NEW, LEARNING, KNOWN, with the time it was set | `WordStatusRepository` |
| `review_card` | FSRS state of each word in the reviews (stability, difficulty, due date…) | `ReviewRepository` |
| `review_log` | One row per answer: card, rating, time | `ReviewRepository` |
| `reading_log` | One row per finished text: text, time, reading minutes | `ReadingLogRepository` |

Settings (learning language, daily goal, pronunciation help, onboarding done) are in **DataStore Preferences**, not Room: a handful of values, no queries. The UI language is not stored by the app at all: it is Android's per-app language (`LocaleManager`, wrapped by `AppLanguage` in `:core:ui`).

**Word status (SPEC §5.2).** A lemma without a row is NEW.
- Tap a word → the word sheet. "Add to review" sets LEARNING and creates a review card; "I already know it" sets KNOWN.
- "I'm done reading" sets every NEW lemma of the text **that the learner did not tap** to KNOWN, then logs the reading. A tapped word was not obvious, so it is left as it is.
- Colours in the Reader: NEW underlined, LEARNING highlighted, KNOWN plain. Colour is never the only cue (underline / background).

**Comprehension.** `ReadingText.comprehension(statuses)` = share of word *occurrences* whose lemma is LEARNING or KNOWN. Occurrences, because a word seen ten times weighs ten times. Shown on every text in the Library and used by `suggestNextText`: the easiest unread text that still teaches something (≤ 98 %), else the hardest unread one.

**Reader gestures.** One `pointerInput` per sentence: a tap finds the word under the finger through the `TextLayoutResult`, a long press translates the sentence. TalkBack gets the same actions as custom accessibility actions ("Look up …" per word, "Show or hide the translation").

**Listening.** Play reads the text sentence by sentence through `SpeechPlayer` (on-device TTS), highlighting the current sentence. "Repeat" replays the current sentence. Rate 0.75× / 1× / 1.25×, kept per screen in the `SavedStateHandle`.

**Reviews (FSRS 4.5, SPEC §8.1).** `FsrsScheduler` in `:core:domain/review/`, plain Kotlin, no library.
- Each card has a stability (days until recall drops to 90 %) and a difficulty (1–10). The answer (Again / Hard / Good / Easy) updates both with the published FSRS 4.5 formulas and the 17 default weights.
- Next interval = the time for the recall probability to fall to the **desired retention, 90 %**.
- Simplified as the SPEC says: no learning steps within the day, except Again → back in 10 minutes. A new card rated Good comes back in 4 days.
- The session takes the cards due when it opens (the queue is saved in the `SavedStateHandle`). A card answered Again is not shown again in the same session.
- Card front: the sentence where the word was met, with the word blanked (cloze); back: the word, its meaning, the four buttons with their intervals.

**Progress (SPEC §8.2).** Pure functions in `:core:domain/progress/`, computed from the logs:
- Active day = at least one review or one text finished. **Streak** = consecutive active days; today does not break it until it is over.
- **Minutes** = 15 s per review + the reading time of each finished text.
- **Known words by day**: only the current status and the time it was set are stored, so a word counts from the day it became known.
- **Level comprehension**: average comprehension of the highest level read at 75 % or more.

**Today.** Daily goal (minutes) and today's minutes, reviews due, the suggested next text with its comprehension.

**Onboarding.** Three steps, kept in the `SavedStateHandle`: languages (UI and learning; the UI language is not offered as a learning language) → level test (the 40 most frequent lemmas; ticked words become KNOWN, "complete beginner" skips it) → daily goal. Finishing saves everything and `onboardingDone`; the NavHost then replaces the onboarding with Today.

**Lessons and pronunciation.** Kept from M1 as Library sections (SPEC §6, §7). Pronunciation is optional: when the setting is on, the word sheet and the review card back show the IPA and the pronunciation note, and the Library shows the Pronunciation section.

## Where to look in the code

| Question | File |
|---|---|
| Tokenizer, comprehension, next text | `core/domain/.../reading/` |
| FSRS | `core/domain/.../review/FsrsScheduler.kt` |
| Streak, minutes, known words | `core/domain/.../progress/Progress.kt` |
| Room tables and DAOs | `core/data/.../database/` |
| Room and DataStore repositories | `core/data/.../repository/RoomRepositories.kt`, `DataStoreUserPreferencesRepository.kt` |
| Sample texts and lexicon | `core/data/.../sample/Sample*Reading.kt`, `Lexicon.kt` |
| Reader, word sheet | `feature/reading/.../reader/` |
| Review session | `feature/srs/.../review/` |
| Fakes for tests | `core/testing/.../FakeRepositories.kt` |

## Likely defence questions

- **Why pre-tokenise the texts?** Lemmatisation (*casei* → *casă*) needs a dictionary or a model; done once when writing the content, it is exact and costs nothing on the phone. The tokenizer stays to build and check the content.
- **Why Room for statuses but DataStore for settings?** Statuses, cards and logs are many rows, queried by language and observed as `Flow`s. Settings are four values read together.
- **Why write FSRS by hand?** About 100 lines of arithmetic, fully tested (`FsrsSchedulerTest`), no dependency to check or update. Weights are a parameter, so personalised weights could be plugged in later.
- **Why 90 % retention?** FSRS's recommended default: higher costs many more reviews for little gain.
- **Why is "Again" not re-queued in the same session?** Simpler session state; the card comes back 10 minutes later anyway. To be revisited if it feels wrong.
- **Why mark untapped words KNOWN at the end?** Asking for every word would be tedious; not tapping a word is the signal the learner understood it (SPEC §5.2). A mistake is fixed from the word sheet.
- **How is process death handled in the Reader?** The open word, the translated sentences, the current sentence, the speech rate and the tapped words are in the `SavedStateHandle`; statuses are already in Room.
- **Why is the UI language not in DataStore?** Android 13+ stores the per-app language itself (`LocaleManager`); keeping a copy would mean two sources of truth.
