# Verifications (SPEC.md §15)

## Chosen versions — 2026-10-28

Latest stable versions read from Google Maven / Maven Central / the GitHub API.

| Tool | Version |
|---|---|
| Kotlin | 2.4.21 |
| AGP | 9.4.1 (built-in Kotlin) |
| Gradle | 9.6.0 |
| KSP | 2.3.12 |
| JDK | 21 |
| compileSdk / targetSdk / minSdk | 37 / 37 / 34 |
| Compose BOM | 2026.09.00 |
| Navigation Compose | 2.10.2 |
| Lifecycle | 2.11.0 |
| Hilt / androidx.hilt | 2.60.1 / 1.4.0 |
| kotlinx.coroutines / serialization | 1.11.0 / 1.12.0 |
| Room | 2.8.5 (added 2026-10-30) |
| DataStore Preferences | 1.3.0 (added 2026-10-30) |
| GitHub Actions | checkout v7, setup-java v6, gradle/actions v6, upload-artifact v7 |

Coil, Retrofit and WorkManager will be added and checked in M2.

## Reading loop — 2026-10-31

Checked on an API 37 emulator (`Medium_Phone`), debug build:

- [x] `connectedDebugAndroidTest`: `RoomRepositoriesTest` (3/3) and `PronunciationListContentTest` (1/1).
- [x] Onboarding: the UI language is not offered as a learning language; the level test shows 40 words; ticked words count as known.
- [x] Reader: word colours, tap → word sheet, long press → sentence translation, playback highlight, "I'm done reading" summary.
- [x] Review: cloze front and back, intervals 10 min / 1 d / 4 d / 14 d, end screen with a suggested text.
- [x] Progress and Today numbers match the actions above; Settings switches the UI to French live.
- [ ] IPA on the word sheet with pronunciation help on (not confirmed on the emulator).
- [ ] Audio quality of the sentence-by-sentence playback (not listened to).
- [ ] Process death on the Reader and the review session.

## To check at the start of M2

- [ ] `ro-RO` and `en-GB` support in Azure Pronunciation Assessment; prosody in `en-GB`.
- [ ] ML Kit GenAI Speech Recognition locales (Basic mode), `en-GB` and `ro-RO`, on the test device.
- [ ] Quality of ML Kit Translation French↔Romanian (English pivot).
- [ ] Availability of Gemini Nano on the device.
- [ ] Gemini model, prices, free quotas; Azure quotas.
- [ ] Android version of the phone (Settings › About phone).
