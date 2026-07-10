# Build Plan

> OBDForge prioritized task board. Milestones M1–M13: `docs/EXECUTION_PLAN.md`. **Finished work:** [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) (Sprints 0–22).

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

**Agent rule:** Sequential `[AGENT]` items first; shared schema/types are Sequential-only (`docs/PARALLEL_AGENT_SCOPES.md`).

---

## Active board

> **v1.2.6** shipped 2026-06-25. Sprint 22 audit AGENT fixes local (2026-07-10). See [`CHANGELOG.md`](CHANGELOG.md) `[Unreleased]`.

### Open work

| # | Owner | Task | Notes |
|---|-------|------|-------|
| 1 | HUMAN | Merge Dependabot PR #8 (github-actions) | F-004 — CI green; branch protection blocked agent |
| 2 | HUMAN | Dependabot PR #9 android deps | F-005 — CodeQL Analyze failed; do not merge until green |
| 3 | HUMAN | Release Please Actions PR permission | F-010 — carried |
| 4 | HUMAN | Fill `app-update.json` release-check URL | Donations done (Venmo) |
| 5 | ADB | F-Droid fdroiddata MR | Draft ready · GitLab submit pending |
| 6 | ADB | Bench connect / flash USB-C on device | F-008/F-009 — `docs/FLASH_HARDWARE.md` |
| 7 | AGENT | Live-data charting / time-series dashboard | ❌ Blocked — hardware bench |
| 8 | HUMAN | Shop operator review | Optional |
| 9 | HUMAN | Post-release monitoring | Issues → `KNOWLEDGE_BASE.md` |

### Blocked — hardware bench

No OBD adapter or bench ECU on hand. Pull when hardware is available.

| Area | Task |
|------|------|
| Transports | BLE GATT, Classic SPP, USB, Wi‑Fi/Ethernet smoke with real adapter |
| Protocol | ELM327 DTC + PID read; STN vs ELM latency |
| Live data | 10+ PID stability (5 min); unblocks charting (#7) |
| Bidirectional | Staged bench ECU test (authorized vehicles only) |
| Connect UX | One-tap reconnect car-to-car (`AdapterConnectCard`) |
| Flash | USB-C OTG + EX/SX; demo then one ECU family `[HUMAN]` |

Log when bench arrives: `docs/ADB_BENCH_RESULTS.md`

---

## Ongoing maintenance

### Weekly

- 🔲 [AUTO] `check-security-triage.sh --wait-ci 300`
- 🔲 [AGENT] Dependabot bumps; triage OpenSSF Scorecard SARIF (F-007)
- ✅ [AUTO] Local feature-gate green after Sprint 22 (2026-07-10); re-verify CI on `main` after PR #8 merge

### Monthly

- 🔲 [AUTO] `check-template-updates.sh`
- 🔲 [AUTO] `check-license-compliance.sh` + SBOM on latest release
- 🔲 [AGENT] Adapter compatibility notes → `KNOWLEDGE_BASE.md`

### Pre-release

- ✅ `pre-release-gate.sh` + reproducible APK verify (v1.0.0–v1.2.6)

---

## Archive

Sprints **0–22** complete (Sprint 22 AGENT subset). Detail: [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md). Release notes: [`CHANGELOG.md`](CHANGELOG.md).
