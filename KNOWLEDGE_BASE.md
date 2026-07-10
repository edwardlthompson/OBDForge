# Knowledge Base

> Repository of stack-specific edge cases, resolved complex bugs, anti-patterns, and reusable project solutions.
> **Do not populate with generic framework definitions.**

## How to use

1. Add entries only after resolving a non-obvious issue specific to this project.
2. Include: symptom, root cause, fix, and prevention.
3. Link to relevant ADRs or PRs when available.

## Entries

### KB-001 — UTF-16 file corruption on Windows

| Field | Detail |
|-------|--------|
| **Symptom** | `check-json` / `npm` / `json.load` fails; git ignore rules stop working; `.gitignore` shows as untracked patterns not applied |
| **Cause** | Cursor `StrReplace` or Windows editor saves text as UTF-16 LE (NUL bytes between ASCII chars) |
| **Fix** | Rewrite affected files with Python `Path.write_text(..., encoding='utf-8')`; re-run `scripts/check-file-encoding.sh` |
| **Prevention** | Bulk edits on Windows via Python/PowerShell UTF-8 write; include root `.gitignore` in encoding scan |
### KB-002 — Invalid `trivy-action@0.28.0` ref

| Field | Detail |
|-------|--------|
| **Symptom** | Security Scan workflow fails at setup: action version not found |
| **Cause** | Bare semver `@0.28.0` is not a valid GitHub Action ref tag |
| **Fix** | Pin to full SHA: `aquasecurity/trivy-action@a9c7b0f06e461e9d4b4d1711f154ee024b8d7ab8 # v0.36.0` |
| **Prevention** | Run `validate-workflow-actions.sh` pre-push; use `check-workflow-action-ref-format.sh` locally |
### KB-003 — `gh api --silent` false CI failures

| Field | Detail |
|-------|--------|
| **Symptom** | `validate-workflow-actions.sh` fails in CI with unknown `gh` flag error |
| **Cause** | `gh api` has no `--silent` flag; stderr not suppressed correctly |
| **Fix** | Redirect to `/dev/null` instead: `gh api ... >/dev/null 2>&1` |
| **Prevention** | Test validation scripts in CI job with `GH_TOKEN`; avoid undocumented `gh` flags |
### KB-004 — Lighthouse performance flake on shared runners

| Field | Detail |
|-------|--------|
| **Symptom** | CI fails with performance 0.88 vs required 0.90 on a single Lighthouse run |
| **Cause** | GitHub-hosted runner CPU variance; single-run assertion is noisy |
| **Fix** | Set `numberOfRuns: 3` in `.lighthouserc.json`; LHCI uses median; keep `minScore: 0.9` |
| **Prevention** | Do not lower performance budget for CI flake; use multi-run median in `modules/web/MODULE.md` |
### KB-005 — Playwright webServer duplicate build

| Field | Detail |
|-------|--------|
| **Symptom** | E2E hangs or serves stale assets; double `vite build` in CI |
| **Cause** | `webServer` runs build while CI already built; wrong host binding |
| **Fix** | Use `vite preview` on `127.0.0.1`; CI runs `npm run build` once before Playwright |
| **Prevention** | Golden Path `examples/web/playwright.config.ts` documents preview-only webServer |
### KB-006 — TypeScript strict null in render handlers

| Field | Detail |
|-------|--------|
| **Symptom** | `tsc` / ESLint error: Object is possibly null inside `render()` callback |
| **Cause** | `strictNullChecks` + `document.getElementById` return type includes null |
| **Fix** | Assign narrowed ref at module scope: `const root = document.getElementById('root')!` or guard once |
| **Prevention** | Module-level `const root = app` pattern in `examples/web/src/main.ts` |
### KB-007 — npm/pip overrides policy for transitive CVEs

| Field | Detail |
|-------|--------|
| **Symptom** | Dependabot or `npm audit` / `uv pip audit` reports CVE in a transitive dependency with no direct upgrade path |
| **Cause** | Parent package pins or bundles a vulnerable sub-dependency; fix not yet published upstream |
| **Fix** | **npm:** add `overrides` in `package.json` to force patched semver (see `examples/web` `@lhci/cli` overrides). **Python:** prefer `uv`/`pip` constraint or bump direct dep; document in DECISION_LOG if override is temporary |
| **Prevention** | Prefer overrides over `--force` installs; remove overrides when upstream ships fix; weekly triage per `docs/SECURITY_TRIAGE.md`; see KB-007 before dismissing Dependabot alerts |
### KB-009 — Release Please `pr` output is JSON, not a PR number

| Field | Detail |
|-------|--------|
| **Symptom** | `release-please.yml` sync step fails: `syntax error near unexpected token '('` on `gh pr checkout` |
| **Cause** | `steps.release.outputs.pr` is a JSON PullRequest object string, not the numeric PR id |
| **Fix** | Guard with `prs_created == 'true'`; use `fromJSON(steps.release.outputs.pr).number` for `gh pr checkout` |
| **Prevention** | See release-please-action outputs table; never pass `outputs.pr` directly to shell commands |
### KB-008 — `android-release` APK hash compare policy

| Field | Detail |
|-------|--------|
| **Symptom** | `Android - assembleRelease` fails: APK hashes differ between two clean `assembleRelease` runs on CI |
| **Cause** | Usually a reproducibility regression (non-hermetic timestamp, path, or dependency drift). Rare runner flakes are possible but treated as failures to catch real regressions early |
| **Fix** | Rebuild locally with `SOURCE_DATE_EPOCH=1700000000 ./gradlew clean assembleRelease` twice; compare `sha256sum` of release APK. Align `build.gradle.kts`, `gradle.properties`, and dependency lockfiles with `modules/android/MODULE.md` |
| **Prevention** | Keep `SOURCE_DATE_EPOCH` pinned in CI; use `scripts/verify-reproducible-apk.sh --strict` before release tags. Do not downgrade the job to WARN — strict compare is intentional (M17 P2) |
### KB-009 — PowerShell bulk replace corrupts bash gate scripts

| Field | Detail |
|-------|--------|
| **Symptom** | CI `Validate Bootstrap Artifacts` / `Template Upgrade Simulation` fail: `ce: command not found`, `eirname: command not found` in `check-design-cohesion.sh` |
| **Cause** | PowerShell `.Replace()` on file contents during package rename stripped `d` from substrings (`cd`→`ce`, `dirname`→`eirname`, `design`→`eesign`) |
| **Fix** | Restore scripts from last good git commit; re-apply path edits with targeted `StrReplace` or `git show REV:path > path` |
| **Prevention** | Never bulk-replace across shell scripts on Windows; run `bash scripts/check-design-cohesion.sh` before push; CI bootstrap job catches this |
### KB-010 — Welcome screen blocks instrumented UI tests

| Field | Detail |
|-------|--------|
| **Symptom** | `connectedDebugAndroidTest` fails: cannot find Settings/About content descriptions |
| **Cause** | First-run `WelcomeHost` covers home until user taps **Continue to app** |
| **Fix** | In `GoldenPathUiTest`, dismiss welcome via `onNodeWithText("Continue to app").performClick()` (try/catch if already completed) |
| **Prevention** | Any new first-run gate must update androidTest smoke helpers before `/ship` |
### KB-011 — `secrets` in GitHub Actions `if:` expressions

| Field | Detail |
|-------|--------|
| **Symptom** | `release.yml` fails to parse at dispatch or tag push: `Unrecognized named-value: 'secrets'` |
| **Cause** | GitHub Actions forbids referencing `secrets.*` directly in job/step `if:` conditions |
| **Fix** | Pass secret into step `env:` and gate inside the shell script (`if [ -z "$OBDFORGE_KEYSTORE_BASE64" ]; then exit 0; fi`) |
| **Prevention** | Never use `secrets.FOO != ''` in `if:`; optional CI steps skip in-script when env is empty |
### KB-012 — Unsigned release APK fails to install on device

| Field | Detail |
|-------|--------|
| **Symptom** | Sideload or `adb install` fails: `INSTALL_PARSE_FAILED_NO_CERTIFICATES` |
| **Cause** | GitHub Release attached `app-release-unsigned.apk` — reproducible F-Droid build artifact, not installable on Android |
| **Fix** | Install **`OBDForge-X.Y.Z.apk`** or **`app-release-signed.apk`** from the release; locally run `scripts/sign-apk-sideload.ps1` on the unsigned APK |
| **Prevention** | Release workflow always signs before upload; unsigned kept for reproducible hash verify only |
### KB-013 — AI-generated icon PNG exceeds 500 KB gate

| Field | Detail |
|-------|--------|
| **Symptom** | CI `Repo Hygiene` / `Feature Gate` fail: `LARGE TRACKED FILE: docs/assets/icon.png (1173 KB > 500 KB)` |
| **Cause** | Cursor `GenerateImage` PNGs copied verbatim to `docs/assets/` and F-Droid metadata without resize/compress |
| **Fix** | Resize to 512×512, PNG `optimize=True`, `compress_level=9`; verify `<500 KB` before commit |
| **Prevention** | Run `scripts/check-large-tracked-files.sh` before push; keep launcher foreground in `drawable-nodpi` under same budget |
### KB-014 — APK upgrade fails: signatures do not match

| Field | Detail |
|-------|--------|
| **Symptom** | `adb install` or sideload fails: `INSTALL_FAILED_UPDATE_INCOMPATIBLE: signatures do not match` |
| **Cause** | Phone has an APK signed with a **different key** (debug keystore, old CI ephemeral sideload key, or manual local sign) than the GitHub release APK |
| **Fix** | Uninstall OBDForge once, then install **`OBDForge-X.Y.Z.apk`** from GitHub Releases. Future updates work in-place when all releases use the same `OBDFORGE_KEYSTORE_BASE64` secret |
| **Prevention** | Never upload unsigned or debug-signed APKs to GitHub Releases; require stable release keystore secrets; release workflow uploads **only** `OBDForge-X.Y.Z.apk`; use `dev.foss.obdforge.debug` for local debug builds (`applicationIdSuffix`) |
### KB-015 — Debug install blocks GitHub release upgrade

| Field | Detail |
|-------|--------|
| **Symptom** | GitHub `OBDForge-X.Y.Z.apk` fails after `./gradlew installDebug` or `sign-apk-sideload` with debug keystore |
| **Cause** | Debug-signed APK uses package `dev.foss.obdforge` (same as release) but a different certificate |
| **Fix** | `adb uninstall dev.foss.obdforge` then install release APK, or run `pwsh scripts/install-github-release.ps1` |
| **Prevention** | Debug builds use `applicationIdSuffix = ".debug"`; bench script auto-uninstalls on signature mismatch |
### KB-016 — OBDLink MX only connects in the OBDLink app

| Field | Detail |
|-------|--------|
| **Symptom** | OBDForge cannot connect to OBDLink MX; official OBDLink app works |
| **Cause** | MX is Classic SPP (not BLE FFF0/NUS). Auto link tried BLE first; OBDLink app may hold the RFCOMM socket; device may be unpaired for third-party apps |
| **Fix** | Force-close OBDLink app → Pair adapter in OBDForge (or Android Bluetooth settings) → set Bluetooth link to **Classic (SPP)** → Save & connect. Busy/refused errors mean another app still holds the socket |
| **Prevention** | Name match `OBDLink`/`MX` defaults link kind to Classic; SPP connect times out at 8s with actionable copy |
