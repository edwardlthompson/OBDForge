# Changelog

All notable changes to OBDForge are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- Align agent tooling with agent-project-bootstrap **v0.15.1** (Cursor FOSS hooks/skills/agents, parallel `/build` + `HUMAN_BACKLOG.md`, Sequential/Parallel BUILD_PLAN shape); `.template-version` now tracks template semver (app remains 1.2.8). See `docs/BOOTSTRAP_ALIGNMENT.md`.
- Dependencies: Android Gradle Plugin **9.3.0**, `usb-serial-for-android` **3.11.0**; GitHub Actions `setup-node` / `setup-go` / `stale` **v7** (Kotlin stays **2.4.0** for CodeQL — KB-019).

### Fixed

- Dependabot Android group PR CodeQL failure by pinning Kotlin below 2.4.10; Dependabot ignores Kotlin `>=2.4.10` until the extractor catches up.
- Drop Dependabot ecosystems for pruned stacks (`examples/web`, `examples/node`, `examples/python`) that fail with `dependency_file_not_found`.

## [1.2.8] - 2026-07-12

### Added

- Live **Fuel loop** status (Mode 01 PID `0x03`) — open vs closed loop labels on the dashboard (monitor-only)

## [1.2.7] - 2026-07-10

### Added

- Bluetooth pairing UX for Classic SPP (OBDLink MX hints, bond checks, busy-socket messaging)
- Narrowband / wideband O2 and fuel-trim / MAF / MAP live PIDs; Mode 02 / 07 freeze-frame / pending DTC support
- Gated UDS DID coding (`22` / `2E`) for Shop/Racing with expert unlock
- Stage A ECU flash scaffold: USB-C host / Simulated only, ISO-TP + programming session demo engine, `SecurityAccessPlugin` SPI, `docs/FLASH_HARDWARE.md`

### Changed

- Coverage honesty in README / F-Droid copy — not universal OBD-II; MX is diagnostics/coding only
- Flash transport policy: Bluetooth and Wi‑Fi blocked for `WriteOperation.EcuFlash`
- `pre-release-gate.sh` uses `.cursor/stack-selection.json` stack (android) instead of always `multi`

### Fixed

- `scripts/check-file-limits.sh` CRLF corruption breaking feature-gate on Windows
- `GoldenPathApp.kt` view line-limit overrun after connect/coding wiring
- Flash SafetyGate battery voltage floor (≥12.0 V) for non-demo programming
- `PidSupportDiscoveryTest` expectations aligned with expanded demo PID bitmaps

## [1.2.6] - 2026-06-25

### Changed

- Launcher icon: vector foreground with dark `#0B0F14` background, clean check-engine outline, and crisp OBD-II port (replaces distorted PNG)

### Added

- `scripts/export-launcher-icon.py` — regenerate store/README PNGs from vector geometry

## [1.2.5] - 2026-06-25

### Added

- `scripts/install-github-release.sh/.ps1` — ADB install of signed GitHub APK with auto-uninstall on signature mismatch

### Changed

- Debug builds use `dev.foss.obdforge.debug` so `./gradlew installDebug` no longer blocks release sideload upgrades

### Fixed

- Document and prevent recurring `INSTALL_FAILED_UPDATE_INCOMPATIBLE` when debug and release share package id (KB-015)

## [1.2.4] - 2026-06-25

### Added

- Home-screen **AdapterConnectCard** — one-tap connect/reconnect to saved OBD adapter from main screen
- Unified `AdapterConnectUseCase` for Bluetooth, Wi‑Fi, USB, and Ethernet (replaces Bluetooth-only reconnect)
- **Save & connect** in transport picker for first-time adapter setup
- `SavedTransportConnectTest` for last-known Bluetooth fallback logic

### Changed

- Default transport preference to Bluetooth (mobile-first)
- Transport picker collapses behind **Change adapter**; feature nav buttons follow connect card
- Removed misleading network status line from home (was labeled as adapter status)

### Fixed

- Restored corrupted `watch-agent-gates.sh` (d-character mangling since v1.1.0)
- `check-file-limits.sh` excludes `build/` (Room KSP local false positives)
- Guard Bluetooth device discovery when `BLUETOOTH_CONNECT` not yet granted (fixes emulator crash)

## [1.2.3] - 2026-06-24

### Changed

- GitHub Releases attach **only** signed `OBDForge-X.Y.Z.apk` (no unsigned APK or SBOM clutter)

### Fixed

- Stable release signing key via GitHub secrets — sideload updates no longer break when CI cache rotates keys
- Document one-time uninstall when switching from debug- or old-key installs (`INSTALL_FAILED_UPDATE_INCOMPATIBLE`)

## [1.2.2] - 2026-06-24

### Added

- `ObdScaffold` edge-to-edge layout helpers with navigation-bar gutter on all bottom-fixed UI

### Changed

- Launcher icon: Option A PNG (check-engine + OBD-II port, no signal waves); compressed under 500 KB CI gate

### Fixed

- GitHub releases attach signed installable `OBDForge-X.Y.Z.apk` (fixes `INSTALL_PARSE_FAILED_NO_CERTIFICATES` on sideload)
- Release workflow signing step when GitHub secrets unset (KB-011)
- Welcome and app screens: top inset no longer double-counts status bar; bottom buttons clear 3-button nav bar

## [1.2.1] - 2026-06-21

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

[Unreleased]: https://github.com/edwardlthompson/OBDForge/compare/v1.2.8...HEAD
[1.2.8]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.8
[1.2.7]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.7
[1.2.6]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.6
[1.2.5]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.5
[1.2.4]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.4
[1.2.3]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.3
[1.2.2]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.2
[1.2.1]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.1
[1.2.0]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.0
[1.1.0]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.1.0
[1.0.0]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.0.0
