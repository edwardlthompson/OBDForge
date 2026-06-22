# Agent Memory

> Centralized index of tech stack, threat models, persistent context, and retrospectives.
> Update only at session startups, milestone boundaries, or major architectural pivots.

## Tech Stack

| Layer | Technology | Version | Notes |
|-------|-----------|---------|-------|
| Platform | Android (API 26+) | — | Android-only; pruned multi-stack template |
| Language | Kotlin | 2.x | Strict nullability; coroutines + Flow |
| UI | Jetpack Compose + Material 3 | — | Material You dynamic color; Garage themes |
| Architecture | Clean Architecture | — | ADR-0001; domain/use-case/data/ui layers |
| Persistence | Room + DataStore | — | Sessions, DTCs, audit log, preferences |
| Local AI | MediaPipe LLM + TFLite | — | On-device diagnostics assistant; no cloud inference |
| Transports | BT SPP, USB serial, WiFi TCP, Ethernet | — | `TransportRegistry` selects adapter |
| Protocols | ELM327, OBDLink STN/STPX, UDS, KWP, J1939 | — | `ProtocolRegistry` + DiagnosticProtocol SPI |
| License | GPL-3.0-or-later | — | F-Droid compatible |
| Distribution | F-Droid + GitHub Releases | — | Reproducible APK builds; no Play Services |

## Active Modules

- ✅ Android / F-Droid (`modules/android/MODULE.md`) — **only active module**
- ⬜ Web, Python, Node, Lightroom, Rust, Go — pruned at Sprint 0

## OBDLink / STPX Notes

- **OBDLink adapters** expose STN11xx/STN22xx/STPX command sets beyond generic ELM327.
- Prefer STN/STPX native commands for faster PID streaming, header control, and manufacturer-specific modes.
- Generic ELM327 clones remain supported via fallback protocol plugin; detect adapter family at connect time.
- Never assume AT command compatibility across clone firmware — probe and degrade gracefully.
- USB serial: enumerate via Android USB host API; request permission per device VID/PID allowlist.
- WiFi/Ethernet: TCP socket to adapter AP or vehicle gateway; timeout and reconnect policy required.

## F-Droid Constraints

- **No proprietary SDKs:** no Google Play Services, Firebase, Crashlytics, or closed telemetry.
- **Reproducible builds:** `SOURCE_DATE_EPOCH`, pinned Gradle wrapper/deps, CI `verify-reproducible-apk.sh`.
- **Anti-features:** document network use (adapter WiFi only), no ads, no tracking.
- **Metadata:** `examples/android/metadata/` + Fastlane stubs; validate with `verify-fdroid-metadata.sh`.
- **Updates:** F-Droid builds show informational update only; sideload APK path uses GitHub Releases matcher.

## Threat Model Checklist

- ✅ `docs/THREAT_MODEL.md` drafted (STRIDE, MASVS, OBD adapter trust boundary)
- ✅ No proprietary closed-source SDKs in production path
- ✅ Opt-in only telemetry (GDPR/CCPA compliant); see `docs/PRIVACY.md`
- ✅ Secrets excluded from VCS (Gitleaks pre-commit)
- ✅ Dependency vulnerability scanning enabled (CodeQL + Trivy + Dependabot)
- ✅ Input validation at all data boundaries (adapter I/O, VIN, barcode, AI prompts)
- ✅ Bidirectional control interlocks documented (ADR-0003); expert mode gated
- ✅ Audit log for safety-sensitive ECU writes (Room, local-only)
- ✅ VIN treated as PII — local storage, no cloud upload by default
- ✅ `SECURITY.md` and private vulnerability reporting enabled
- 🔲 `[ADB]` Physical adapter fuzzing on real vehicle bench (post-M8)
- ✅ `[ADB]` Release v1.0.0 device smoke on CPH2583 — see `docs/ADB_BENCH_RESULTS.md`

## Persistent Context

### Project Purpose

OBDForge is a FOSS Android OBD-II diagnostics app: multi-transport adapter support, live data, DTC read/clear, bidirectional controls with safety interlocks, ECU-first VIN resolution, persona modes (Shop/DIY/Semi-pro/Racing), and on-device AI assistance — distributed via F-Droid under GPL-3.0-or-later.

### Key Constraints

- Max 250 lines per view file, 150 lines per logic file
- Trunk-based development with Conventional Commits
- Android-only Golden Path in `examples/android/`
- Demo mode must run without hardware (deterministic mock transport)
- Expert/bidirectional features require explicit unlock + audit trail
- Local-first: no mandatory cloud; AI inference on-device only

## Session Retrospectives

| Date | Milestone | What worked | What to improve |
|------|-----------|-------------|-----------------|
| 2026-06-21 | Sprint 0 bootstrap | Android stack prune + doc scaffold from template | Run CI sign-off after first push |

## Template Provenance

- **Source template:** `edwardlthompson/agent-project-bootstrap`
- **Template version:** `0.11.1` (bootstrap provenance)
- **Product version:** `1.0.0` (see `.template-version` / Release Please)
- **Last update check:** See `.template-update.json`
