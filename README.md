# OBDForge

<p align="center">
  <img src="docs/assets/icon.png" alt="OBDForge app icon" width="128" height="128" />
</p>

![Version](https://img.shields.io/badge/version-1.2.11-blue?style=flat-square)
![GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-blue?style=flat-square)
![Android](https://img.shields.io/badge/platform-Android-3DDC84?style=flat-square)
![FOSS](https://img.shields.io/badge/FOSS-F--Droid-2ea043?style=flat-square)

Free open-source Android OBD-II diagnostics with multi-transport adapters, live data, local AI, and safety-gated bidirectional controls.

Built on [agent-project-bootstrap](https://github.com/edwardlthompson/agent-project-bootstrap) (Android stack, pruned).

## Purpose

OBDForge connects to ELM327 and OBDLink STN/STPX adapters over Bluetooth, USB, WiFi, or Ethernet to read and clear DTCs, stream live PIDs (including narrowband O2 voltage and wideband lambda when the ECU reports them), resolve VIN ECU-first, and run shop workflows — all on-device under GPL-3.0-or-later with no proprietary SDKs.

**Coverage honesty:** OBDForge targets **legislated OBD-II** via a working adapter. Bus selection is adapter firmware (`ATSP0`). It does **not** claim support for every OBD-II vehicle, proprietary OEM diagnostic packs, or production ECU flash on Bluetooth. OBDLink **MX** uses Classic Bluetooth SPP for diagnostics/coding only. Stage A flash scaffolding (USB-C host / Simulated demo ISO-TP) is documented in [`docs/FLASH_HARDWARE.md`](docs/FLASH_HARDWARE.md) — real reprogramming needs `[HUMAN]` bench and a user-supplied security plugin. Local AI does not invent security keys.

## Features

| Area | Capability |
|------|------------|
| Transports | BLE GATT + Classic SPP (Auto or Classic-first for OBDLink), USB serial, Wi‑Fi/Ethernet TCP, pairing + one-click reconnect |
| Protocols | ELM327, OBDLink STN/STPX, UDS DID coding (gated), KWP/J1939 VIN helpers |
| Live data | Mode 01 PIDs (fuel trims, MAF/MAP, NB/WB O2), Mode 02 freeze frame, Mode 07 pending DTCs |
| Safety | Interlocks, expert mode, local audit log (ADR-0003); flash services rejected |
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

**Sideload from GitHub Releases:** download **`OBDForge-X.Y.Z.apk`** only. Do **not** install unsigned or debug-signed builds from release assets.

If upgrade fails with **signatures do not match**, uninstall the old app once, then install the new APK (see KB-014 in `KNOWLEDGE_BASE.md`). This often happens after **`./gradlew installDebug`** — debug builds now use `dev.foss.obdforge.debug` so they no longer block release installs.

**OnePlus / ADB bench:** `pwsh scripts/install-github-release.ps1` downloads the signed release APK and auto-uninstalls on signature mismatch.

```bash
cd examples/android
./gradlew assembleDebug
# Installs dev.foss.obdforge.debug — does not conflict with GitHub release APK
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

## How agents should work in this repo

1. Read [`docs/START_HERE.md`](docs/START_HERE.md) → pick mode via [`docs/CURSOR_MODES.md`](docs/CURSOR_MODES.md).
2. Follow [`AGENTS.md`](AGENTS.md) and [`docs/FOR_AGENTS.md`](docs/FOR_AGENTS.md).
3. Execute [`BUILD_PLAN.md`](BUILD_PLAN.md) **Sequential** before Parallel; HUMAN/ADB under Human & device → [`HUMAN_BACKLOG.md`](HUMAN_BACKLOG.md) when automation cannot finish.
4. Active stack: **Android only** (`modules/android/MODULE.md`, `examples/android/`). License: **GPL-3.0-or-later**.
5. Slash commands: [`docs/help/BATCH_COMMANDS.md`](docs/help/BATCH_COMMANDS.md). Template alignment: [`docs/BOOTSTRAP_ALIGNMENT.md`](docs/BOOTSTRAP_ALIGNMENT.md).

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

Product milestones **M1–M13** in [`docs/EXECUTION_PLAN.md`](docs/EXECUTION_PLAN.md). Archived sprints: [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

## Project Docs

| Doc | Purpose |
|-----|---------|
| [`docs/START_HERE.md`](docs/START_HERE.md) | Agent/human entry point |
| [`modules/android/MODULE.md`](modules/android/MODULE.md) | F-Droid compliance checklist |
| [`docs/EXECUTION_PLAN.md`](docs/EXECUTION_PLAN.md) | Milestones M1–M13 |
| [`docs/BOOTSTRAP_ALIGNMENT.md`](docs/BOOTSTRAP_ALIGNMENT.md) | Template alignment / migration notes |
| [`docs/adr/`](docs/adr/) | Architecture decisions |
| [`docs/THREAT_MODEL.md`](docs/THREAT_MODEL.md) | STRIDE + adapter trust |
| [`docs/PRIVACY.md`](docs/PRIVACY.md) | VIN privacy, local-first |
| [`docs/RUNBOOK.md`](docs/RUNBOOK.md) | Release + F-Droid ops |
| [`docs/FDROID_SUBMISSION.md`](docs/FDROID_SUBMISSION.md) | F-Droid fdroiddata MR checklist |
| [`AGENT_MEMORY.md`](AGENT_MEMORY.md) | Stack index for agents |
| [`HUMAN_BACKLOG.md`](HUMAN_BACKLOG.md) | HUMAN/ADB items automation could not finish |
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
