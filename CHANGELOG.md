# Changelog

All notable changes to OBDForge are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Adaptive launcher icon (OBD-II port + check-engine, high contrast) shipped in APK and README
- Automated release APK signing: `scripts/sign-release-apk.sh`, `build-release-apk.sh --sign`, optional CI upload of `app-release-signed.apk`

## [1.2.0] - 2026-06-22

### Added

- Wal33D (MIT) manufacturer DTC overlay (~9.4k entries, 33 makes) with VIN-aware lookup in DTC assistant
- On-device connection/crash diagnostic log (Room-backed, sanitized) with Settings export and USB file path for PC inspection
- First-run **Welcome** screen explaining Bluetooth, Camera, Network, and USB permissions with per-permission **Grant access** buttons
- Settings → **Review app permissions** to reopen the welcome flow

## [1.1.0] - 2026-06-22

### Added

- BLE GATT transport with Auto link mode (BLE first, Classic SPP fallback)
- One-click **Connect** button to reconnect to the last saved Bluetooth OBD-II adapter
- DTC assistant: automatic vehicle scan on open, active code list, out-of-range PID highlights, theme-aware UI
- Venmo donation link in About screen
- OBDex (CC0) import: ~9,500 generic DTC explanations + 95 Mode 01 PID normal ranges (`scripts/import-obdex-data.py`)
- PID 0x00 bitmap discovery filters live-data dashboard to ECU-supported PIDs
- CSV session export alongside JSON (AndrOBD-style flat rows)
- On-device LLM: **Gemma 3 1B IT** (MediaPipe INT4) with optional in-app download; OBDex catalog fallback when absent (`docs/LOCAL_AI.md`)

### Changed

- Android namespace unified: `dev.foss.goldenpath` → `dev.foss.obdforge` (applicationId unchanged)

## [1.0.0] - 2026-06-21

First public release — product milestones M1–M13 (Sprints 0–14).

### Added

- Multi-transport OBD connectivity: Bluetooth SPP, USB serial, Wi-Fi/Ethernet TCP
- Protocol plugins: ELM327, OBDLink STN/STPX with graceful ELM327 fallback
- Live PID streaming with persona-aware dashboard layouts (DIY, Semi-pro, Shop, Racing)
- Room persistence: diagnostic sessions, DTC snapshots, freeze frames, JSON export
- Demo mode with deterministic mock transport for CI and UI flows without hardware
- Safety interlocks, expert unlock, and local audit log export (ADR-0003)
- Bidirectional controls (clear DTC, UDS write, actuator test) gated by persona policy
- ECU-first VIN resolution with CameraX + ML Kit barcode fallback (ADR-0005)
- Shop workflows: customers, work orders, intake → inspect → close-out
- On-device DTC explain via MediaPipe LLM hooks with offline catalog fallback
- F-Droid store metadata, anti-features documentation, and fdroiddata handoff draft
- Reproducible release builds with pinned `SOURCE_DATE_EPOCH=1700000000`

### Documentation

- Architecture ADRs 0001–0005, personas spec, threat model, privacy policy, and runbook
- F-Droid submission checklist in `docs/FDROID_SUBMISSION.md`

[Unreleased]: https://github.com/edwardlthompson/OBDForge/compare/v1.2.0...HEAD
[1.2.0]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.0
[1.1.0]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.1.0
[1.0.0]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.0.0
