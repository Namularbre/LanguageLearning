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
| GitHub Actions | checkout v7, setup-java v6, gradle/actions v6, upload-artifact v7 |

Coil, Retrofit, Room and WorkManager will be added and checked in M2.

## To check at the start of M2

- [ ] `ro-RO` and `en-GB` support in Azure Pronunciation Assessment; prosody in `en-GB`.
- [ ] ML Kit GenAI Speech Recognition locales (Basic mode), `en-GB` and `ro-RO`, on the test device.
- [ ] Quality of ML Kit Translation French↔Romanian (English pivot).
- [ ] Availability of Gemini Nano on the device.
- [ ] Gemini model, prices, free quotas; Azure quotas.
- [ ] Android version of the phone (Settings › About phone).
