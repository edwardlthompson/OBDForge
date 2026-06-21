# OBDForge Execution Plan

> Product milestones **M1–M13**. Agent ops and sprint board: `BUILD_PLAN.md`. Architecture decisions: `docs/adr/`.

**Stack:** Android-only · Kotlin · Compose · Room · `examples/android/`

---

## Milestone summary

| Milestone | Goal | Module / path |
|-----------|------|---------------|
| **M1** | Clean Architecture scaffold, registries, demo mode stub | `domain/`, `data/`, `ui/`, `demo/` |
| **M2** | Multi-transport layer | `transport/` — BT, USB, WiFi, Ethernet |
| **M3** | Protocol registry + ELM327 plugin | `protocol/elm327/` |
| **M4** | OBDLink STN/STPX plugins | `protocol/stn/`, `protocol/stpx/` |
| **M5** | Live data / PID streaming | `feature/livedata/` |
| **M6** | Room persistence (sessions, DTCs) | `data/db/` |
| **M7** | Demo mode (mock transport/protocol) | `demo/` |
| **M8** | Safety interlocks + audit log | `safety/`, ADR-0003 |
| **M9** | Bidirectional controls (gated) | `feature/bidirectional/` |
| **M10** | VIN resolution (ECU-first) | `feature/vin/`, ADR-0005 |
| **M11** | Persona modes + shop workflow | `feature/shop/`, ADR-0004 |
| **M12** | Local AI (MediaPipe LLM + TFLite) | `feature/ai/` |
| **M13** | F-Droid release + reproducible build | `metadata/`, CI release job |

---

## M1 — Core architecture

- Package layout: `domain`, `data`, `ui` under `examples/android/app/src/main/java/dev/foss/obdforge/`
- `TransportRegistry` + `ProtocolRegistry` interfaces and empty registrations
- Room schema v1: `Session`, `AuditLog` stubs
- Compose shell: connect placeholder, About/Settings from Golden Path
- **Gate:** `./gradlew test` green; ADR-0001 accepted

## M2 — Transports

- `ObdTransport` port: connect, read, write, disconnect, metrics
- BT classic SPP, USB serial (permission flow), TCP WiFi, TCP Ethernet
- Transport picker UI + last-used preference (DataStore)
- **Gate:** unit tests with fake streams; `[ADB]` one real adapter smoke

## M3 — ELM327 protocol plugin

- `DiagnosticProtocol` SPI; `Elm327Protocol` probe + Mode 01/03/04/09
- Protocol auto-select on connect; manual override in Semi-pro+
- Recorded transcript fixtures for tests
- **Gate:** read DTC + single PID on bench adapter

## M4 — STN/STPX

- `StnProtocol`, `StpxProtocol` with capability detection
- Fast PID streaming path; fallback to ELM327 when probe fails
- **Gate:** OBDLink device comparison log (STN vs ELM path)

## M5 — Live data

- PID catalog (mode 01); customizable dashboard per persona
- Streaming loop with backpressure; pause on background
- **Gate:** 10+ PIDs stable 5 min on bench

## M6 — Persistence

- Room: sessions, DTC snapshots, freeze frames, user notes
- Migrations tested; export session JSON
- **Gate:** migration test + session restore

## M7 — Demo mode

- Mock transport/protocol with deterministic fixtures
- Settings toggle; banner in UI when active
- **Gate:** CI tests run full connect → DTC → PID flow without hardware

## M8 — Safety + audit

- `SafetyGate` interlocks (ADR-0003); expert mode unlock
- Audit log writes on every ECU write attempt
- **Gate:** unit tests for blocked/allowed paths; `[HUMAN]` review interlock copy

## M9 — Bidirectional controls

- Actuator tests / UDS writes behind SafetyGate
- Persona policy enforcement (DIY blocked)
- **Gate:** `[ADB]` staged test on bench ECU only; audit entries verified

## M10 — VIN resolution

- `VinResolver` chain: Mode 09 → UDS F190 → KWP → J1939 → barcode → manual
- Provenance UI badge; Room cache
- **Gate:** spec in `docs/features/vin-resolution.md` satisfied

## M11 — Personas + shop

- Persona switcher; filtered navigation
- Shop: customer, vehicle, work order entities in Room
- **Gate:** shop intake → inspect → close-out smoke

## M12 — Local AI

- MediaPipe LLM integration for DTC explanation; TFLite optional classifiers
- On-device only; no network inference
- **Gate:** airplane mode AI smoke; model size documented for F-Droid

## M13 — F-Droid release

- `SOURCE_DATE_EPOCH`, reproducible APK verification
- F-Droid metadata complete; anti-features accurate
- **Gate:** `verify-reproducible-apk.sh` + `verify-fdroid-metadata.sh`; `[ADB]` install smoke

---

## Dependency order

```text
M1 → M2 → M3 → M4 → M5 → M6
              ↘ M7 (parallel after M3)
M6 → M8 → M9
M5 + M6 → M10
M6 + M8 → M11
M5 + M10 → M12
All → M13
```

## CI gate (every milestone)

- `./gradlew test` + `assembleDebug` on `main`
- Security Scan + CodeQL green before marking milestone complete
- Feature gate: `bash scripts/feature-gate.sh --stack android`

## References

- `BUILD_PLAN.md` — sprints 0–14 task board
- `docs/PERSONAS.md` — persona matrix
- `docs/DESIGN_SYSTEM.md` — M3 + Garage themes
- `modules/android/MODULE.md` — F-Droid checklist
