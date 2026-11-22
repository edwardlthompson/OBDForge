# Build Plan

<!-- remaining-tally -->
**Remaining:** AGENT 11 · LOCAL 3 · CLOUD 0 · AUTO 6 · HUMAN 10 · ADB 8 · **35 open**
<!-- /remaining-tally -->

Live board. Canonical shape: agent-project-bootstrap `BUILD_PLAN_TEMPLATE.md`.
Finished work: [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

**Who:** `AGENT` code · `HUMAN` person · `ADB` device · `AUTO` CI/scripts
**Venue (AGENT only):** `[LOCAL]` This Computer · `[CLOUD]` Cursor Cloud
**State:** 🔲 open · ✅ done · ❌ blocked

Format: `🔲 [AGENT][LOCAL] Short task — scope: path/prefix`.

### Open PRs (synced)

> Auto-managed. Do not hand-edit inside the markers.

<!-- open-prs-sync:begin -->
_No open Dependabot or Release Please PRs._
<!-- open-prs-sync:end -->

### Template gaps (synced)

> Auto-managed. Do not hand-edit inside the markers.

<!-- template-gaps-sync:begin -->
_No template gaps._
<!-- template-gaps-sync:end -->

### Waiting on a person

1. 🔲 [HUMAN] Optional: live `app-update.json` (fetcher already hardcodes `edwardlthompson/OBDForge`)
2. 🔲 [ADB] Bump `versionCode` above 25 before next F-Droid/store upload (v1.3.0 APK still code 25)
3. 🔲 [HUMAN] Shop operator review (optional)
4. 🔲 [HUMAN] Post-release monitoring — Issues → `KNOWLEDGE_BASE.md`
5. 🔲 [HUMAN] F-008 — Decide backup/export redaction policy for VIN/shop PII
6. 🔲 [ADB] F-Droid fdroiddata MR — refresh after Sprint 23 FOSS/version fixes · GitLab submit pending
7. 🔲 [ADB] Bench connect / flash USB-C on device — F-009/F-010 · `docs/FLASH_HARDWARE.md`
- 🔲 [HUMAN] Product URL / shop review / post-release monitoring (see Human & device)
- 🔲 [ADB] Hardware bench + F-Droid MR when ready

### Local agent (This Computer)

Standing queue for This Computer. Rows: `🔲 [AGENT][LOCAL] … — scope: path`.

<!-- local-agent-lane:begin -->
- | 🔲 | Open |
- | ❌ | Blocked — add brief reason |
1. ❌ [AGENT][LOCAL] Live-data charting / time-series dashboard — blocked: hardware bench required
2. 🔲 [AGENT][LOCAL] Dependabot bumps; continue OpenSSF Scorecard pin campaign (F-007) — weekly when alerts open
3. 🔲 [AGENT][LOCAL] Adapter compatibility notes → `KNOWLEDGE_BASE.md` — monthly cadence
- 🔲 [AUTO] `check-security-triage.sh --wait-ci 300`
- 🔲 [AUTO] `check-template-updates.sh`
- 🔲 [AUTO] `check-license-compliance.sh` + SBOM on latest release
<!-- local-agent-lane:end -->

### Cloud agent (Cursor Cloud)

Standing queue for Cursor Cloud. Rows: `🔲 [AGENT][CLOUD] … — scope: path`.

<!-- cloud-agent-lane:begin -->
_No cloud agent items._
<!-- cloud-agent-lane:end -->

## Child Repo Playbook

### Sprint News — News Bot follow-ups

<!-- parallel_exception: independent news follow-ups; run sequentially -->

1. 🔲 [AGENT] Bump Compose BOM 2026.09.00 → 2026.11.01 (Compose 1.13 / Material3 1.5.0; needs Kotlin 2.2+ and compileSdk 37.1, check AGP) and fix Slider/RangeSlider call sites (`onValueChange` now required) — scope: examples/android/ (news P2, 2026-11-19) https://developer.android.com/blog/posts/jetpack-compose-november-2026-release
2. 🔲 [AGENT] Bump the Gradle wrapper 9.9.1 → 9.9.2 and commit the regenerated wrapper jar and distributionSha256Sum (4 High security fixes: SFTP host keys, snapshot dependency verification, TestKit temp dir, daemon registry) — scope: examples/android/gradle/wrapper/ (news P2, 2026-11-20) https://docs.gradle.org/9.9.2/release-notes.html

## Ongoing Maintenance

- 🔲 [AGENT] Dependabot bumps; continue Scorecard pin campaign (F-007)
- 🔲 [AGENT] Adapter compatibility notes → `KNOWLEDGE_BASE.md`


## Archive

Older sprints: [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

## Preserved source board

Quoted so no prior task is lost. Prefer the lanes above; do not invent work.

<details>
<summary>Previous board</summary>

# Build Plan

> OBDForge prioritized task board. Milestones M1–M13: `docs/EXECUTION_PLAN.md`. **Finished work:** [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) (Sprints 0–23). Template baseline: agent-project-bootstrap **v0.21.0** (`docs/BOOTSTRAP_ALIGNMENT.md`).

## Owner labels

| Label | Owner |
|-------|-------|
| `AGENT` | Cursor Agent — code, docs, tests, CI |
| `HUMAN` | Human — approvals, credentials, product decisions |
| `ADB` | Human (Android) — device/emulator, F-Droid submission |
| `AUTO` | CI/scripts — Actions, Dependabot, gates |

## Status markers

| Marker | State |
|--------|-------|
| 🔲 | Open |
| ✅ | Done — archive to `COMPLETED_TASKS.md` |
| ❌ | Blocked — add brief reason |

**Agent rule:** Execute all `[AGENT]` **Sequential** items first, then dispatch **Parallel** agents with isolated file scopes (`docs/PARALLEL_AGENT_SCOPES.md`). Shared schema/types are Sequential-only.

**Autonomous `/build`:** Runs `[AGENT]`/`[AUTO]` and Parallel work first, then attempts **Human & device (after automation)** via `scripts/attempt-build-plan-row.sh`. Failures append [`HUMAN_BACKLOG.md`](HUMAN_BACKLOG.md) and continue. Status: `bash scripts/build-sprint-status.sh --json --lane child`.

---

## Active board — post v1.3.0

> **v1.3.0** shipping 2026-08-21 ([release](https://github.com/edwardlthompson/OBDForge/releases/tag/v1.3.0) · `OBDForge-1.3.0.apk`). Donate + daily update check. HUMAN/ADB backlog below.

### Sequential

1. ❌ [AGENT] Live-data charting / time-series dashboard — blocked: hardware bench required
2. 🔲 [AGENT] Dependabot bumps; continue OpenSSF Scorecard pin campaign (F-007) — weekly when alerts open
3. 🔲 [AGENT] Adapter compatibility notes → `KNOWLEDGE_BASE.md` — monthly cadence

### Parallel (safe after Sequential schema lock)

<!-- parallel_exception: no multi-agent split until charting unblocked; single Android container -->

| Agent | Scope | Task |
|-------|-------|------|
| — | — | _None open — add non-overlapping `examples/android/**` rows when charting unblocks_ |

### Human & device (after automation)

> Address after `/build` completes AGENT/AUTO and Parallel work above. Automation failures → [`HUMAN_BACKLOG.md`](HUMAN_BACKLOG.md).

1. 🔲 [HUMAN] Optional: live `app-update.json` (fetcher already hardcodes `edwardlthompson/OBDForge`)
2. 🔲 [ADB] Bump `versionCode` above 25 before next F-Droid/store upload (v1.3.0 APK still code 25)
3. 🔲 [HUMAN] Shop operator review (optional)
4. 🔲 [HUMAN] Post-release monitoring — Issues → `KNOWLEDGE_BASE.md`
5. 🔲 [HUMAN] F-008 — Decide backup/export redaction policy for VIN/shop PII
6. 🔲 [ADB] F-Droid fdroiddata MR — refresh after Sprint 23 FOSS/version fixes · GitLab submit pending
7. 🔲 [ADB] Bench connect / flash USB-C on device — F-009/F-010 · `docs/FLASH_HARDWARE.md`

### Blocked — hardware bench (`[ADB]` / `[HUMAN]`)

No OBD adapter or bench ECU on hand. Pull when hardware is available. Log results in `docs/ADB_BENCH_RESULTS.md`.

| Area | Task |
|------|------|
| Transports | BLE GATT, Classic SPP, USB, Wi‑Fi/Ethernet smoke with real adapter |
| Protocol | ELM327 DTC + PID read; STN vs ELM latency |
| Live data | 10+ PID stability (5 min); unblocks Sequential charting |
| Bidirectional | Staged bench ECU test (authorized vehicles only) |
| Connect UX | One-tap reconnect car-to-car (`AdapterConnectCard`) |
| Flash | USB-C OTG + EX/SX; demo then one ECU family `[HUMAN]` |

---

## Ongoing maintenance

### Weekly

- 🔲 [AUTO] `check-security-triage.sh --wait-ci 300`
- 🔲 [AGENT] Dependabot bumps; continue Scorecard pin campaign (F-007)
- ✅ [AUTO] Local feature-gate green after Sprint 22 (2026-07-10); CI green on `main` for v1.2.7 prepare commit
- ✅ [AUTO] Release Please / Dependabot auto-merge enabled (2026-07-22); PR #24 merged 2026-08-10

### Monthly

- 🔲 [AUTO] `check-template-updates.sh`
- 🔲 [AUTO] `check-license-compliance.sh` + SBOM on latest release
- 🔲 [AGENT] Adapter compatibility notes → `KNOWLEDGE_BASE.md`

### Pre-release

- ✅ `pre-release-gate.sh` + reproducible APK verify (v1.0.0–v1.2.6)

### Human (after automation)

- 🔲 [HUMAN] Product URL / shop review / post-release monitoring (see Human & device)
- 🔲 [ADB] Hardware bench + F-Droid MR when ready

---

## Archive

Sprints **0–23** complete (Sprint 23 AGENT). Detail: [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md). Release notes: [`CHANGELOG.md`](CHANGELOG.md).

</details>
