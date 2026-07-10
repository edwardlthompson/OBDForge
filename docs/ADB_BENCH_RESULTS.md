# ADB Bench Results

> Device verification log for OBDForge. Hardware OBD adapter rows require a separate session with an adapter connected to the phone (not the PC).

## Device

| Field | Value |
|-------|-------|
| Model | OnePlus CPH2583 |
| Android | 16 (API 36) |
| Root | Magisk (uid=0) |
| ADB serial | `b5214fc6` |
| Last session | 2026-06-22 |
## Release smoke — v1.1.0 (2026-06-22)

| Check | Result | Notes |
|-------|--------|-------|
| Release APK build (`SOURCE_DATE_EPOCH=1700000000`) | PASS | `app-release-unsigned.apk` (~102 MB) |
| Debug-keystore sign (`sign-apk-debug.ps1`) | PASS | `app-release-adb-smoke.apk` |
| Install release APK | PASS | `versionName=1.1.0`, `versionCode=15` |
| Cold start (release) | PASS | ~200 ms, no compat dialog |
| Upgrade reinstall (`adb install -r`) | PASS | Over prior debug + release |
| Logcat crash scan | PASS | No `FATAL EXCEPTION` |
| Welcome screen (fresh install) | PASS | Permission cards + **Grant access** buttons |
| Welcome → Continue → home | PASS | Demo connected, VIN resolved |
| Runtime grants (`BLUETOOTH_*`, `CAMERA`) | PASS | Via system dialog + `pm grant` |
| Demo mode home | PASS | Simulation banner, live-data nav enabled |
| F-Droid metadata gate | PASS | `verify-fdroid-metadata.sh` |
### Debug APK note (Android 16)

Debug builds trigger a one-time **Android App Compatibility** dialog (16 KB page-size / native `.so` alignment from ML Kit, MediaPipe, TFLite). Tap **Don't show again** once; release APK does not show this dialog.

## F-Droid dry-run (2026-06-22)

| Check | Result | Notes |
|-------|--------|-------|
| `verify-fdroid-metadata.sh` | PASS | |
| Device install + launch (release) | PASS | `dev.foss.obdforge/.MainActivity` |
| `fdroid-device-dry-run.ps1` / `.sh` | FIXED | Restored corrupted scripts; launcher updated for namespace rename |
| `fdroid lint` | SKIP | fdroidserver not on bench host |
| GitLab fdroiddata MR | PENDING | Draft: `packaging/fdroid/dev.foss.obdforge.yml` — needs maintainer GitLab account |
## Features not exercised on device this session

| Feature | Status | Notes |
|---------|--------|-------|
| Settings → diagnostic log export | SKIP | UI not automated; path `Android/data/dev.foss.obdforge/files/logs/` empty until export |
| Settings → Review app permissions | SKIP | Welcome flow covers same grants |
| DTC explain / live data screens | SKIP | Manual only this session |
| Camera VIN barcode scanner | SKIP | `CAMERA` granted; scanner UI not opened |
| Release update check | SKIP | Needs hosted `app-update.json` URL |
## Hardware bench (blocked — no OBD adapter on phone)

Phone was on USB **for ADB only**. No `/dev/ttyUSB*` / `/dev/ttyACM*` nodes; no USB-serial OBD adapter on OTG.

| Sprint | Task | Status |
|--------|------|--------|
| 2 | Transport smoke (BT/USB/Wi‑Fi/TCP) | BLOCKED — pair/connect ELM327 or OBDLink MX to **phone** |
| 3 | ELM327 DTC + PID bench | BLOCKED |
| 4 | STN vs ELM latency | BLOCKED |
| 5 | 10+ PID 5 min stability | BLOCKED — unblocks live-data charting |
| 9 | Staged ECU bidirectional bench | BLOCKED |
| — | OBDLink MX Classic pairing + O2 PIDs | BLOCKED — `[HUMAN]` close OBDLink app, Pair adapter, Classic link, confirm STN + live O2 |
| — | Stage A ECU flash (USB) | BLOCKED — `[HUMAN]` see `docs/FLASH_HARDWARE.md`; demo profile `demo-isotp-v1` only in CI |

**Flash note:** Only USB-C host (`UsbSerial`) or Simulated may run `WriteOperation.EcuFlash`. Bluetooth/MX and Wi‑Fi are blocked. Prefer OBDLink EX/SX + USB-C OTG — see `docs/FLASH_HARDWARE.md`.

**How to re-run:** connect adapter to phone (in-app Pair or BT settings; for MX use Classic SPP and close OBDLink app), USB-OTG, or Wi‑Fi to adapter AP; use app with simulation **off**, then export **Settings → Connection & crash log** and copy `latest-diagnostic-log.json` over USB.

## Prior session — v1.0.0 (2026-06-21)

See git history of this file. Namespace was `dev.foss.goldenpath`; launcher is now `dev.foss.obdforge/.MainActivity`.
