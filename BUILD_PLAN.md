# Build Plan

> OBDForge prioritized task board. Milestones M1–M13: `docs/EXECUTION_PLAN.md`. **Finished work:** [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) (Sprints 0–15, v1.0.0 shipped).

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
| ✅ | Done — archive detail to `COMPLETED_TASKS.md` |
| ❌ | Blocked — add brief reason |

**Format:** `🔲 [OWNER] Description`

**Agent rule:** Sequential `[AGENT]` items first; shared schema/types are Sequential-only (`docs/PARALLEL_AGENT_SCOPES.md`).

---

## Active board

> v1.0.0 released. Post-release features (BLE, DTC assistant scan, donations) are on `main` under CHANGELOG `[Unreleased]` — cut **v1.1.0** when ready.

### Sprint 17 — Diagnostic data parity (in progress)

1. ✅ [AGENT] Import OBDex CC0 DTC catalog (~9.5k codes) + 95 Mode 01 PID ranges (`scripts/import-obdex-data.py`)
2. ✅ [AGENT] PID 0x00 bitmap discovery — filter live-data PIDs to ECU-supported subset
3. ✅ [AGENT] CSV session export (AndrOBD-style flat export alongside JSON)
4. 🔲 [AGENT] Wal33D manufacturer DTC import ([MIT, ~12k codes](https://github.com/Wal33D/dtc-database)) — optional overlay
5. 🔲 [AGENT] Live-data charting / time-series dashboard — defer until hardware bench validates polling

Detail: `docs/DIAGNOSTIC_DATA.md`

### Sprint 16 — v1.1.0 release (queued)

1. 🔲 [AGENT] Move CHANGELOG `[Unreleased]` → `[1.1.0]`; Release Please version bump
2. 🔲 [AUTO] `pre-release-gate.sh` + reproducible APK verify on release tag
3. 🔲 [ADB] Device smoke — BLE Connect + DTC assistant auto-scan on CPH2583

### Blocked — hardware bench

| Area | Task | Blocker |
|------|------|---------|
| Transports | Smoke BLE GATT, Classic SPP, USB, Wi‑Fi/Ethernet with real adapter | No OBD adapter on bench — `docs/ADB_BENCH_RESULTS.md` |
| Protocol | ELM327 DTC + PID read; STN vs ELM latency | Same |
| Live data | 10+ PID stability (5 min) | Same |
| Bidirectional | Staged bench ECU test (authorized vehicles only) | No bench ECU |

Pull into Sprint 16+ when hardware is available.

---

## Human backlog

| Task | Notes |
|------|-------|
| Fill `app-update.json` URL | Donations (Venmo) done; release-check URL still pending |
| F-Droid fdroiddata MR | Draft at `packaging/fdroid/dev.foss.obdforge.yml` — GitLab submit |
| Approve bundled LLM size | Optional MediaPipe/TFLite assets for F-Droid (~MB budget) |
| Shop operator review | Optional real-world workflow feedback |
| Package rename approval | `dev.foss.goldenpath` → `dev.foss.obdforge` when ready |
| Post-release monitoring | Issues, adapter compatibility reports |
| Approve v1.1.0 tag | When CHANGELOG + device smoke are ready |

---

## Ongoing maintenance

### Weekly

- 🔲 [AUTO] `check-security-triage.sh --wait-ci 300`
- 🔲 [AGENT] Dependabot bumps; triage OpenSSF Scorecard SARIF
- 🔲 [AUTO] CI + Feature Gate green on `main`

### Monthly

- 🔲 [AUTO] `check-template-updates.sh`
- 🔲 [AUTO] `check-license-compliance.sh` + SBOM on latest release
- 🔲 [AGENT] Adapter compatibility notes → `KNOWLEDGE_BASE.md`

### Pre-release

- ✅ `pre-release-gate.sh` + reproducible APK verify (v1.0.0)

---

## Archived sprints

| Sprint | Status | Detail |
|--------|--------|--------|
| 0 — Bootstrap | ✅ Complete | `COMPLETED_TASKS.md` |
| 1 — Core Architecture (M1) | ✅ Complete | `COMPLETED_TASKS.md` |
| 2 — Transports (M2) | ✅ Complete · ADB smoke pending | `COMPLETED_TASKS.md` |
| 3 — ELM327 Protocol (M3) | ✅ Complete · ADB bench pending | `COMPLETED_TASKS.md` |
| 4 — OBDLink STN/STPX (M4) | ✅ Complete · ADB bench pending | `COMPLETED_TASKS.md` |
| 5 — Live Data (M5) | ✅ Complete · ADB bench pending | `COMPLETED_TASKS.md` |
| 6 — Persistence (M6) | ✅ Complete | `COMPLETED_TASKS.md` |
| 7 — Demo Mode (M7) | ✅ Complete | `COMPLETED_TASKS.md` |
| 8 — Safety & Audit (M8) | ✅ Complete | `COMPLETED_TASKS.md` |
| 9 — Bidirectional (M9) | ✅ Complete · ADB bench pending | `COMPLETED_TASKS.md` |
| 10 — VIN Resolution (M10) | ✅ Complete | `COMPLETED_TASKS.md` |
| 11 — Personas & Shop (M11) | ✅ Complete | `COMPLETED_TASKS.md` |
| 12 — Local AI (M12) | ✅ Complete · model bundle optional | `COMPLETED_TASKS.md` |
| 13 — F-Droid Prep (M13) | ✅ Complete | `COMPLETED_TASKS.md` |
| 14 — Release Polish | ✅ Complete · GitLab MR pending | `COMPLETED_TASKS.md` |
| 15 — Post-Release Audit | ✅ Complete | `COMPLETED_TASKS.md` |
| Template M19–M29 (upstream) | ✅ Complete | `COMPLETED_TASKS.md` |
