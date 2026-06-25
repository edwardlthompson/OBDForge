# Changelog

All notable changes to OBDForge are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.3.0](https://github.com/edwardlthompson/OBDForge/compare/v1.2.6...v1.3.0) (2026-06-25)


### Added

* **android:** add launcher icon and automated release signing ([ca11726](https://github.com/edwardlthompson/OBDForge/commit/ca117260b2efff208cab2b733a8491fa1f21ea6e))


### Fixed

* add manufacturer catalog string resource ([202e545](https://github.com/edwardlthompson/OBDForge/commit/202e545211efd535aa9d7507444d06c889675f54))
* **android:** audit sprint — icon, insets, CI hygiene ([ae1f114](https://github.com/edwardlthompson/OBDForge/commit/ae1f114d9148408c5eaf99bd9829cdc53eb5f6fb))
* **android:** dismiss welcome screen in instrumented UI tests ([8a2ab2f](https://github.com/edwardlthompson/OBDForge/commit/8a2ab2f1272275a74ef7d2ac86d6cb63dffbe8f8))
* **android:** guard Bluetooth discovery without permission ([32d1e95](https://github.com/edwardlthompson/OBDForge/commit/32d1e952b901c93f3a1076af6a4e737588eb3f73))
* **android:** prevent debug install blocking release sideload ([d0ee831](https://github.com/edwardlthompson/OBDForge/commit/d0ee83152f35b562f8403e183beaab3994024833))
* **android:** use try/catch for welcome dismiss in UI tests ([723810a](https://github.com/edwardlthompson/OBDForge/commit/723810a31ace791ae7ca316e438f31648fa4e843))
* **ci:** allow release workflow when signing secrets unset ([dc41f9f](https://github.com/edwardlthompson/OBDForge/commit/dc41f9f5741c8a330f691347956f84ab6672716d))
* **ci:** restore design-cohesion scripts and sync template version to 1.1.0 ([400cc7b](https://github.com/edwardlthompson/OBDForge/commit/400cc7ba09013856a88c2d1aa13865bb731532c5))
* **release:** always attach signed installable APK to GitHub releases ([f6e081d](https://github.com/edwardlthompson/OBDForge/commit/f6e081d1eda039695f04ab44d8ffad018d360d85))
* **release:** stable signing key and APK-only GitHub releases ([e81088f](https://github.com/edwardlthompson/OBDForge/commit/e81088f97bfd5e9af58cb667abfad43b7698b7ee))


### Changed

* **release:** prepare v1.2.1 release ([9f03505](https://github.com/edwardlthompson/OBDForge/commit/9f03505b5dc5e14e1f90e3285e4f28960f5d35a2))
* **release:** prepare v1.2.2 release ([c53ec19](https://github.com/edwardlthompson/OBDForge/commit/c53ec19d269ad4260bd306870af9f44613663ab0))
* **release:** prepare v1.2.4 release ([c95c446](https://github.com/edwardlthompson/OBDForge/commit/c95c4463e1b32f73e5c91ee04cfc3a382d86b2a9))
* **release:** prepare v1.2.5 release ([3cdc380](https://github.com/edwardlthompson/OBDForge/commit/3cdc3802d2647f73dcbde5e25675d54dfeebbd21))
* **release:** prepare v1.2.6 release ([8d7d37b](https://github.com/edwardlthompson/OBDForge/commit/8d7d37ba7f456e2edbbbcae5ccf04bf76fb7f628))


### Documentation

* KB-010 welcome screen instrumented test regression ([c1b7642](https://github.com/edwardlthompson/OBDForge/commit/c1b76421265dac40f50f38821129725cd497bb25))
* KB-011 release workflow secrets if-expression regression ([f6b0650](https://github.com/edwardlthompson/OBDForge/commit/f6b0650d839592b071b4080c3fe6e54b25b1b115))
* record KB-009 design-cohesion script corruption from v1.1.0 ship ([1e6391a](https://github.com/edwardlthompson/OBDForge/commit/1e6391a1da250348c0fb1b37c41556d02152b3fc))

## [Unreleased]

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

[Unreleased]: https://github.com/edwardlthompson/OBDForge/compare/v1.2.6...HEAD
[1.2.6]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.6
[1.2.5]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.5
[1.2.4]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.4
[1.2.3]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.3
[1.2.2]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.2
[1.2.1]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.1
[1.2.0]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.0
[1.1.0]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.1.0
[1.0.0]: https://github.com/edwardlthompson/OBDForge/releases/tag/v1.0.0
