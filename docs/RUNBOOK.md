# Runbook

> OBDForge operational guide — Android / F-Droid distribution.

## Health Checks

| Check | Command / action | Expected |
|-------|------------------|----------|
| Unit + compile | `cd examples/android && ./gradlew test assembleDebug` | Exit 0 |
| Feature gate | `bash scripts/feature-gate.sh --stack android` | Exit 0 |
| CI on main | `bash scripts/check-github-ci.sh --wait 300` | CI, CodeQL, Security Scan green |
| Reproducible APK | `bash scripts/verify-reproducible-apk.sh` | Matching hashes |
| F-Droid metadata | `bash scripts/verify-fdroid-metadata.sh` | No errors |
| Device smoke | `adb install -r app/build/outputs/apk/release/*.apk` | Cold start, no crash |

## Structured Logging

- Android: `Log` tags per feature (`ObdTransport`, `VinResolver`, `SafetyGate`)
- **Never** log VIN, full adapter serial, or ECU write payloads
- Use `BuildConfig.DEBUG` guards for verbose adapter transcripts
- Field debugging: user-initiated export of sanitized session log

## Deploy (Release)

1. `[AUTO]` CI green on `main`
2. `[AGENT]` CHANGELOG + version bump (Release Please)
3. `[AUTO]` `pre-release-gate.sh` + reproducible APK job
4. `[HUMAN]` Approve GitHub release tag
5. `[AUTO]` Attach APK + SBOM to GitHub Release
6. `[ADB]` Install release APK smoke on physical device
7. `[ADB]` Trigger or update F-Droid fdroiddata merge request — see `docs/FDROID_SUBMISSION.md`

## Rollback

1. Yank bad GitHub Release (mark pre-release / advisory)
2. Revert `main` to previous tag; rebuild reproducible APK
3. `[ADB]` Confirm previous APK installs over bad version
4. `[HUMAN]` Notify F-Droid maintainers if bad build reached repo
5. Log incident in `DECISION_LOG.md`

## Common Failures

| Symptom | Check | Fix |
|---------|-------|-----|
| Gradle FOSS grep fail | Proprietary dep in `build.gradle.kts` | Remove GMS/Firebase |
| Reproducible hash drift | `SOURCE_DATE_EPOCH` unset | Pin epoch in CI + local |
| BT connect timeout | Android 12+ permissions | `BLUETOOTH_CONNECT` manifest + runtime |
| USB permission loop | Missing intent filter | Fix `device_filter.xml` VID/PID |
| Adapter garbage responses | Clone ELM firmware | Fall back to Elm327Protocol probe |
| Room migration crash | Schema bump without test | Add `MigrationTest` |
| CI emulator flake | API 34 AOSP job | Re-run; check `connectedDebugAndroidTest` logs |

## OBD Adapter Troubleshooting

1. Confirm transport (BT paired **before** in system settings for SPP adapters)
2. Capture sanitized transcript (Settings → Export debug log) — `[ADB]`
3. Try demo mode to isolate app vs adapter
4. Document adapter firmware in GitHub issue template

## Backup & Restore

| Target | RPO | RTO | Procedure |
|--------|-----|-----|-----------|
| User diagnostics data | User responsibility | Uninstall = loss | Export session JSON (Shop) |
| Signing keys | N/A | Immediate | Offline keystore; never in repo |
| Repository | N/A (git) | Immediate | `git clone` |

## F-Droid Reproducible Build Procedure

```bash
export SOURCE_DATE_EPOCH=1700000000  # fixed project epoch
bash scripts/build-release-apk.sh --clean
bash scripts/verify-reproducible-apk.sh
bash scripts/verify-fdroid-metadata.sh
```

CI `android-release` job runs the same verification on tagged builds.

## SLOs (`[HUMAN]` defines)

| Surface | SLI | Target |
|---------|-----|--------|
| App cold start | Time to interactive | < 2s mid-range device |
| PID refresh | p95 interval accuracy | ±100 ms of configured rate |
| VIN resolve (ECU) | p95 latency | < 10s on bench |

## Escalation

1. Check `BUILD_PLAN.md` blocked tasks (❌)
2. Security issues → `docs/SECURITY_TRIAGE.md` + private reporting
3. Vehicle safety incident → `[HUMAN]` lead; preserve audit log export
4. Contact maintainers in `.github/CODEOWNERS`

## Secret Rotation

Applies to GitHub Actions signing secrets and any future update-manifest tokens:

1. **`[HUMAN]`** Revoke compromised keys in GitHub Environments
2. **`[AGENT]`** Rotate keystore only with `[HUMAN]` approval (invalidates F-Droid sig chain)
3. **`[AUTO]`** Re-run release pipeline; verify APK signature
4. Log in `DECISION_LOG.md`
