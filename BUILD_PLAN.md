# Build Plan

> OBDForge prioritized task board. Milestones M1–M13: `docs/EXECUTION_PLAN.md`. **Finished work:** [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) (Sprints 0–22). Template baseline: agent-project-bootstrap **v0.15.1** (`docs/BOOTSTRAP_ALIGNMENT.md`).

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

## Active board — post v1.2.8

> **v1.2.10** shipping 2026-07-22 ([release](https://github.com/edwardlthompson/OBDForge/releases/tag/v1.2.10)). Prior: v1.2.8 (2026-07-12).

### Sequential

1. ❌ [AGENT] Live-data charting / time-series dashboard — blocked: hardware bench required
2. 🔲 [AGENT] Dependabot bumps; triage OpenSSF Scorecard SARIF (F-007) — weekly when alerts open
3. 🔲 [AGENT] Adapter compatibility notes → `KNOWLEDGE_BASE.md` — monthly cadence
4. ✅ [AUTO] Release Please + Dependabot auto-merge (F-010) — settings + `AUTOMERGE_TOKEN` + automerge workflows; branch protection job names fixed; proven on v1.2.9 / **v1.2.10** auto-merge (2026-07-22)

### Parallel (safe after Sequential schema lock)

<!-- parallel_exception: no multi-agent split until charting unblocked; single Android container -->

| Agent | Scope | Task |
|-------|-------|------|
| — | — | _None open — add non-overlapping `examples/android/**` rows when charting unblocks_ |

### Human & device (after automation)

> Address after `/build` completes AGENT/AUTO and Parallel work above. Automation failures → [`HUMAN_BACKLOG.md`](HUMAN_BACKLOG.md).

1. 🔲 [HUMAN] Fill `app-update.json` release-check URL (donations done — Venmo)
2. 🔲 [HUMAN] Shop operator review (optional)
3. 🔲 [HUMAN] Post-release monitoring — Issues → `KNOWLEDGE_BASE.md`
4. 🔲 [ADB] F-Droid fdroiddata MR — draft ready · GitLab submit pending
5. 🔲 [ADB] Bench connect / flash USB-C on device — F-008/F-009 · `docs/FLASH_HARDWARE.md`

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
- 🔲 [AGENT] Dependabot bumps; triage OpenSSF Scorecard SARIF (F-007)
- ✅ [AUTO] Local feature-gate green after Sprint 22 (2026-07-10); CI green on `main` for v1.2.7 prepare commit
- ✅ [AUTO] Release Please / Dependabot auto-merge enabled (2026-07-22)

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

Sprints **0–22** complete (Sprint 22 AGENT subset). Detail: [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md). Release notes: [`CHANGELOG.md`](CHANGELOG.md).
