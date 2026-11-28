# News Inbox

Auto-collected inbox of news, upstream updates, security fixes, and ideas relevant to this repo, appended daily by News Bot.
A Cursor planning agent should review these items and fold the useful ones into BUILD_PLAN; entries are append-only, newest at the bottom.


## 2026-11-13

**News and updates**

- xj-scanner: open-source reader for pre-OBD-II Jeep ECUs over Chrysler SCI with a $3 Arduino Nano. [source](https://github.com/MZULALI/xj-scanner)
- Kotlin 2.5.0-RC released (fixes a reified Enum<T> inference regression); Gradle 9.9.1 / 8.14.7 patch releases. [source](https://github.com/JetBrains/kotlin/releases/tag/v2.5.0-RC)

**Tasks for the planning agent**

- [ ] Evaluate pre-OBD-II Chrysler SCI support — An open reference implementation shows a path to older vehicles. Label: `[AGENT]` · Priority: P3 · https://github.com/MZULALI/xj-scanner
- [ ] Smoke-test Kotlin 2.5.0-RC and bump Gradle wrapper — Catches compiler regressions before stable. Label: `[AGENT]` · Priority: P3 · https://github.com/JetBrains/kotlin/releases/tag/v2.5.0-RC

## 2026-11-14

**News and updates**

- MediaPipe v1.2.3 unifies Tasks option/method names across platforms and fixes a LiteRT runner deadlock. [source](https://github.com/google-ai-edge/mediapipe/releases/tag/v1.2.3)
- llama.cpp v1.0.0: first stable API (35 deprecated functions removed), FP8 KV cache, hardened llama-server. [source](https://github.com/ggml-org/llama.cpp/releases/tag/v1.0.0)

**Tasks for the planning agent**

- [ ] Evaluate moving off tasks-genai 0.10.35 to MediaPipe 1.x or LiteRT-LM — The pre-1.0 line is aging and option names were unified in 1.2.3. Label: `[AGENT]` · Priority: P3 · https://github.com/google-ai-edge/mediapipe/releases/tag/v1.2.3
- [ ] Note llama.cpp 1.0 as an alternative engine in the local-AI ADR — A stable API and GGUF make it a credible option for the DTC assistant. Label: `[AGENT]` · Priority: P3 · https://github.com/ggml-org/llama.cpp/releases/tag/v1.0.0

## 2026-11-19

**News and updates**

- Jetpack Compose November '26 release is stable: Compose BOM 2026.11.01 (Compose 1.13) with Material3 1.5.0. The BOM needs Kotlin 2.2+ and compileSdk 37.1. Material3 1.5.0 breaks Slider/RangeSlider source (stateless overloads deprecated, `onValueChange` now required, RangeSliderState activeRangeStart/End renamed to startValue/endValue); SecureTextField now defaults to system obfuscation, and clickables play platform click sounds by default. [source](https://developer.android.com/blog/posts/jetpack-compose-november-2026-release) · [Material3 notes](https://developer.android.com/jetpack/androidx/releases/compose-material3)

**Tasks for the planning agent**

- [x] Bump Compose BOM 2026.09.00 to 2026.11.01 (Compose 1.13 / Material3 1.5.0) and fix Slider/RangeSlider call sites — Material3 1.5.0 makes `onValueChange` required on Slider/RangeSlider (no Slider/RangeSlider calls found, but check other deprecations) and the BOM requires Kotlin 2.2+ and compileSdk 37.1, so a Dependabot/Argus BOM bump will fail unless AGP accepts 37.1 and the call sites are fixed first. Label: `[AGENT]` · Priority: P2 · https://developer.android.com/blog/posts/jetpack-compose-november-2026-release (synced to BUILD_PLAN)

## 2026-11-20

**News and updates**

- Gradle 9.9.2 and 8.14.8 (Nov 19) fix four High-severity build-tool bugs: SFTP repositories didn't verify host keys, transitive snapshot dependencies skipped dependency verification, TestKit used a predictable temp directory, and the daemon registry was briefly readable by other local users. 9.9.2 also fixes a 9.9.0 plugin-resolution regression with registries that answer 401. Wrappers older than 9.9.1/8.14.7 also miss the earlier Critical fix (version-catalog values run as code). [9.9.2 notes](https://docs.gradle.org/9.9.2/release-notes.html) · [8.14.8 notes](https://docs.gradle.org/8.14.8/release-notes.html)
- Android developer verification goes global from January 2027 and will cover all install sources on certified devices: it starts with Play and six other distributors, then expands country by country over several months. Developers who ship only outside Play are told to create an Android Developer Console account and register package names plus signing-key SHA-256 fingerprints (a new open-source guide covers re-signing repos such as F-Droid). Unregistered apps stay installable only through a one-time "advanced flow" on each device; hobbyist accounts now allow 50 devices. F-Droid publicly opposes the scheme. [Android blog](https://developer.android.com/blog/posts/android-developer-verification-2027) · [open-source registration guide](https://developer.android.com/developer-verification/guides/open-source-app-registration) · [F-Droid open letter](https://f-droid.org/2026/02/24/open-letter-opposing-developer-verification.html)

**Tasks for the planning agent**

- [x] Bump the Gradle wrapper 9.9.1 to 9.9.2 and commit the regenerated wrapper jar and distributionSha256Sum — the patch fixes four High build-tool vulnerabilities (SFTP host keys, snapshot dependency verification, TestKit temp dir, daemon registry). Run ./gradlew wrapper --gradle-version=<ver> in the wrapper (examples/android/gradle/wrapper/). Label: `[AGENT]` · Priority: P2 · https://docs.gradle.org/9.9.2/release-notes.html (synced to BUILD_PLAN)
- [ ] Decide whether to verify your identity and register this app with Google before January 2027 — if yes, create an Android Developer Console account and register the package name(s) with the SHA-256 of the GitHub Releases signing key (plus F-Droid's key if F-Droid re-signs it); if no, add an "advanced flow" install note to the README. The app is sideloaded (GitHub Releases/Obtainium/F-Droid), exactly the installs the rollout gates, and this is a personal identity/policy call. Label: `[HUMAN]` · Priority: P2 · https://developer.android.com/blog/posts/android-developer-verification-2027 (inbox only: no /build-parsed human group on this board)

## 2026-11-28

**News and updates**

- Euro 7 applies from Nov 29, 2026 to newly type-approved EU cars and vans: on-board monitoring (OBM) of real NOx/PM emissions, driver warnings and an "OBM warning distance" counter when a monitor reports Error, and restart/distance/refuelling limits if the fault is left unrepaired (about 2,400 km). OBM data must be readable through the standard OBD port with a generic scan tool, and resetting monitoring statuses does not wipe the countdown (it resets to 2,400 km or the current value, whichever is lower). OBDForge has no Euro 7/OBM handling yet; cars sold today are unaffected. [EUR-Lex summary](https://eur-lex.europa.eu/EN/legal-content/summary/vehicle-emissions-and-battery-durability-euro-7-technical-requirements-and-certification-rules.html) · [Implementing Reg. (EU) 2025/1707](https://eur-lex.europa.eu/eli/reg_impl/2025/1707/oj/eng) ([Annex II text](https://regelex.com/legislation/32025R1707/anx_II)) · [ÖAMTC](https://www.oeamtc.at/presse/oeamtc-euro-7-gilt-ab-29-november-fuer-neue-fahrzeugtypen-90733168)

- Gradle [9.9.3](https://github.com/gradle/gradle/releases/tag/v9.9.3) (Nov 27) is a regression-only patch: 9.9.2 failed to start on Windows when GRADLE_USER_HOME sits on an ImDisk RAM disk ([gradle#39909](https://github.com/gradle/gradle/issues/39909)). No new security fixes; 9.9.2's four High fixes are still why the open wrapper-bump row is P2, and Gradle now recommends 9.9.3, so landing on it avoids a second bump. The board row is unchanged. [Release notes](https://docs.gradle.org/9.9.3/release-notes.html)

**Tasks for the planning agent**

- [ ] Research Euro 7 OBM data (monitoring statuses, OBM warning-distance and inducement counters per Implementing Reg. (EU) 2025/1707 Annex I/II) and write a note on how OBDForge should read and show it, plus a clear-DTC interlock warning that a reset does not end an inducement countdown — Euro 7 cars expose these over the generic OBD port from Nov 29, and a DIY user clearing codes on one needs to know the start-limit countdown keeps running. Label: `[AGENT]` · Priority: P3 · https://eur-lex.europa.eu/eli/reg_impl/2025/1707/oj/eng (research note only; no code until spec tables or a test vehicle are available)
- [ ] Use Gradle 9.9.3 rather than 9.9.2 when doing the open wrapper bump — 9.9.3 has the same security fixes plus a Windows startup regression fix, so one bump covers both. Label: `[AGENT]` · Priority: P3 · https://github.com/gradle/gradle/releases/tag/v9.9.3 (BUILD_PLAN row left unchanged)
