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
