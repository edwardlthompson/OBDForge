# ADB Bench Results

> Device verification log for OBDForge v1.0.0. Hardware OBD adapter bench items require a separate adapter session.

## Device

| Field | Value |
|-------|-------|
| Model | OnePlus CPH2583 |
| Android | 16 |
| Root | Magisk (uid=0) |
| Transport | USB ADB (`b5214fc6`) |
| Date | 2026-06-21 |

## Release smoke (Sprint 13 / 14)

| Check | Result | Notes |
|-------|--------|-------|
| Release APK build (`SOURCE_DATE_EPOCH=1700000000`) | PASS | `app-release-unsigned.apk` |
| Debug-keystore sign for local install | PASS | `scripts/sign-apk-debug.ps1` |
| Install release APK | PASS | `versionName=1.0.0`, `versionCode=14` |
| Cold start | PASS | `dev.foss.goldenpath.MainActivity`, ~320 ms |
| Upgrade reinstall | PASS | `adb install -r` over existing |
| Logcat crash scan | PASS | No `FATAL EXCEPTION` |
| Demo mode home | PASS | Simulation banner, connected demo, VIN resolved |
| DTC explain screen | PASS | P0133 prefilled, catalog fallback label |
| Live data navigation | PASS | Screen opens without crash |

## F-Droid dry-run (Sprint 13)

| Check | Result | Notes |
|-------|--------|-------|
| `verify-fdroid-metadata.sh` | PASS | CI gate |
| Device install + launch | PASS | See release smoke above |
| fdroiddata MR draft | READY | `packaging/fdroid/dev.foss.obdforge.yml` |
| `fdroid lint` | SKIP | fdroidserver not installed on bench host |
| GitLab MR opened | PENDING | Requires maintainer GitLab account |

## Hardware bench (blocked — no OBD adapter)

No USB serial OBD adapter (`/dev/ttyUSB*`, `/dev/ttyACM*`) detected on device during this session. The phone was connected to the development host via USB for ADB only.

| Sprint | Task | Status |
|--------|------|--------|
| 2 | Transport smoke (BT/USB/Wi‑Fi/TCP) | BLOCKED — connect ELM327 or OBDLink adapter |
| 3 | ELM327 DTC + PID bench | BLOCKED |
| 4 | STN vs ELM latency | BLOCKED |
| 5 | 10+ PID 5 min stability | BLOCKED |
| 9 | Staged ECU bidirectional bench | BLOCKED |
| 10 | Mode 09 + barcode on vehicle | PARTIAL — demo Mode 09 + VIN UI verified; barcode/camera not exercised |

Re-run hardware rows when an OBD adapter is connected to the phone (USB-OTG, Bluetooth paired, or Wi‑Fi TCP to adapter AP).
