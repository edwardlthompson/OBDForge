# OBDForge

<p align="center">
  <img src="docs/assets/icon.png" alt="OBDForge app icon" width="128" height="128" />
</p>

![Version](https://img.shields.io/badge/version-1.2.1-blue?style=flat-square)
![GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-blue?style=flat-square)
![Android](https://img.shields.io/badge/platform-Android-3DDC84?style=flat-square)
![FOSS](https://img.shields.io/badge/FOSS-F--Droid-2ea043?style=flat-square)

Free open-source Android OBD-II diagnostics with multi-transport adapters, live data, local AI, and safety-gated bidirectional controls.

Built on [agent-project-bootstrap](https://github.com/edwardlthompson/agent-project-bootstrap) (Android stack, pruned).

## Purpose

OBDForge connects to ELM327 and OBDLink STN/STPX adapters over Bluetooth, USB, WiFi, or Ethernet to read and clear DTCs, stream live PIDs, resolve VIN ECU-first, and run shop workflows — all on-device under GPL-3.0-or-later with no proprietary SDKs.

## Features

| Area | Capability |
|------|------------|
| Transports | BLE GATT + Classic SPP (Auto: BLE first), USB serial, Wi‑Fi/Ethernet TCP, one-click reconnect |
| Protocols | ELM327, OBDLink STN/STPX, UDS, KWP, J1939 (plugin SPI) |
| Live data | Customizable PID dashboards per persona |
| Safety | Interlocks, expert mode, local audit log (ADR-0003) |
| VIN | ECU-first chain with barcode fallback (ADR-0005) |
| Personas | DIY, Semi-pro, Shop, Racing (`docs/PERSONAS.md`) |
| AI | On-device DTC assistant — auto vehicle scan, code list, out-of-range PIDs, local explain |
| Demo mode | Full UI flow without hardware |

## Stack

- **Kotlin** + **Jetpack Compose** (Material 3 / Material You)
- **Room** + **DataStore**
- **Clean Architecture** — `TransportRegistry`, `ProtocolRegistry`
- **F-Droid** reproducible builds — see `modules/android/MODULE.md`

## Quick Start

**Sideload from GitHub Releases:** download **`OBDForge-X.Y.Z.apk`** (signed). Do **not** install `app-release-unsigned.apk` — it is for F-Droid reproducible builds only and will fail with `INSTALL_PARSE_FAILED_NO_CERTIFICATES`.

```bash
cd examples/android
./gradlew assembleDebug
# Install: adb install app/build/outputs/apk/debug/app-debug.apk
```

**Build + sign release locally:**

```bash
export SOURCE_DATE_EPOCH=1700000000
bash scripts/build-release-apk.sh --clean
bash scripts/sign-apk-sideload.sh   # release keystore or debug keystore fallback
adb install -r examples/android/app/build/outputs/apk/release/app-release-signed.apk
```

Windows: `.\gradlew.bat assembleDebug`

**Agent bootstrap:** read [`docs/START_HERE.md`](docs/START_HERE.md) and follow `BUILD_PLAN.md` Sequential lane.

## BUILD_PLAN Labels

| Label | Owner |
|-------|-------|
| `AGENT` | Cursor Agent — code, docs, tests |
| `HUMAN` | Approvals, GitHub settings, product calls |
| `ADB` | Device/emulator testing, F-Droid |
| `AUTO` | CI, Dependabot, scripts |

**Status:** 🔲 open · ✅ done · ❌ blocked

```bash
grep '\[AGENT\]' BUILD_PLAN.md
```

Sprints **0–14** in [`BUILD_PLAN.md`](BUILD_PLAN.md). Product milestones **M1–M13** in [`docs/EXECUTION_PLAN.md`](docs/EXECUTION_PLAN.md).

## Project Docs

| Doc | Purpose |
|-----|---------|
| [`docs/START_HERE.md`](docs/START_HERE.md) | Agent/human entry point |
| [`modules/android/MODULE.md`](modules/android/MODULE.md) | F-Droid compliance checklist |
| [`docs/EXECUTION_PLAN.md`](docs/EXECUTION_PLAN.md) | Milestones M1–M13 |
| [`docs/adr/`](docs/adr/) | Architecture decisions |
| [`docs/THREAT_MODEL.md`](docs/THREAT_MODEL.md) | STRIDE + adapter trust |
| [`docs/PRIVACY.md`](docs/PRIVACY.md) | VIN privacy, local-first |
| [`docs/RUNBOOK.md`](docs/RUNBOOK.md) | Release + F-Droid ops |
| [`docs/FDROID_SUBMISSION.md`](docs/FDROID_SUBMISSION.md) | F-Droid fdroiddata MR checklist |
| [`AGENT_MEMORY.md`](AGENT_MEMORY.md) | Stack index for agents |

## Security

- **Threat model:** OBD adapters are untrusted input; ECU writes require SafetyGate + expert mode.
- **Privacy:** VIN and session data stay on-device; see [`docs/PRIVACY.md`](docs/PRIVACY.md).
- **Reporting:** [`SECURITY.md`](SECURITY.md) — private vulnerability reporting preferred.
- **CI:** CodeQL, Trivy, Dependabot — weekly triage in [`docs/SECURITY_TRIAGE.md`](docs/SECURITY_TRIAGE.md).

## Architecture (summary)

```text
UI (Compose) → Use Cases → Repositories → Transports / Protocols / Room
                     ↓
              SafetyGate + AuditLog
```

Details: [`docs/adr/0001-core-architecture.md`](docs/adr/0001-core-architecture.md)

## License

OBDForge is free software: you can redistribute it and/or modify it under the terms of the **GNU General Public License v3.0 or later**. See [`LICENSE`](LICENSE).

Third-party notices: [`THIRD_PARTY_LICENSES.md`](THIRD_PARTY_LICENSES.md)

## Contributing

See [`CONTRIBUTING.md`](CONTRIBUTING.md). Conventional Commits; trunk-based flow.

GitHub About text: [`docs/GITHUB_ABOUT.md`](docs/GITHUB_ABOUT.md)
