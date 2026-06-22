# F-Droid Submission Checklist

> `[ADB]` steps to submit OBDForge to F-Droid. Agent gates must be green before starting.

## Prerequisites (AUTO / AGENT)

- ✅ `bash scripts/verify-fdroid-metadata.sh` — store listing text complete
- ✅ `bash scripts/verify-reproducible-apk.sh --strict` — reproducible release APK (or CI `android-release` job)
- ✅ `bash scripts/pre-release-gate.sh` — CI, security triage, version coherence
- ✅ GitHub Release `v1.0.0` published with SBOM assets (Release Please + `release.yml`)

## Device verification (ADB)

1. Build release APK: `bash scripts/build-release-apk.sh --clean`
2. Sign for local install: `pwsh scripts/sign-apk-debug.ps1 -InputApk examples/android/app/build/outputs/apk/release/app-release-unsigned.apk`
3. Install: `adb install -r examples/android/app/build/outputs/apk/release/app-release-adb-smoke.apk`
4. Or run: `pwsh scripts/fdroid-device-dry-run.ps1` (Windows) / `bash scripts/fdroid-device-dry-run.sh` (Linux/macOS)
5. Bench log: `docs/ADB_BENCH_RESULTS.md`

## fdroiddata merge request

1. Use ready-to-submit payload: `packaging/fdroid/dev.foss.obdforge.yml`
3. Fastlane paths: point `Fastlane` metadata URL to this repo's `examples/android/metadata/en-US/`
4. Verify build recipe matches CI: `SOURCE_DATE_EPOCH=1700000000`, subdir `examples/android`
5. Add screenshots to `examples/android/metadata/en-US/images/` before MR (phone + feature graphic)
6. Run `fdroid lint` locally if fdroidserver is installed, or rely on MR CI
7. Open MR; reference GitHub Release `v1.0.0` and reproducible build verification

## Human sign-off

- `[HUMAN]` Liability disclaimer in app + F-Droid description (Sprint 14 human backlog)
- `[HUMAN]` Approve v1.0.0 tag before F-Droid MR merge

## Post-submission

- Monitor fdroiddata MR for build failures (Gradle deps, reproducible hash drift)
- Update `BUILD_PLAN.md` ADB row when MR is merged or blocked
