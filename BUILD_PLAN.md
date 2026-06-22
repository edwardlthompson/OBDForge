# Build Plan

> OBDForge prioritized task board. Milestones M1–M13: `docs/EXECUTION_PLAN.md`. **Finished work:** [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) (Sprints 0–17, v1.1.0 shipped).

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

> **v1.1.0** released 2026-06-22. Next cut when Sprint 17 optional items or hardware bench land.

### Sprint 17 — Diagnostic data parity (optional follow-ups)

1. ✅ [AGENT] Import OBDex CC0 DTC catalog (~9.5k codes) + 95 Mode 01 PID ranges
2. ✅ [AGENT] PID 0x00 bitmap discovery — filter live-data PIDs to ECU-supported subset
3. ✅ [AGENT] CSV session export (AndrOBD-style flat export alongside JSON)
4. ✅ [AGENT] Wal33D manufacturer DTC import ([MIT, ~9.4k entries](https://github.com/Wal33D/dtc-database)) — VIN-aware overlay
5. 🔲 [AGENT] Live-data charting / time-series dashboard — defer until hardware bench validates polling

Detail: `docs/DIAGNOSTIC_DATA.md`

### Blocked — hardware bench

| Area | Task | Blocker |
|------|------|---------|
| Transports | Smoke BLE GATT, Classic SPP, USB, Wi‑Fi/Ethernet with real adapter | No OBD adapter on bench — `docs/ADB_BENCH_RESULTS.md` |
| Protocol | ELM327 DTC + PID read; STN vs ELM latency | Same |
| Live data | 10+ PID stability (5 min) | Same |
| Bidirectional | Staged bench ECU test (authorized vehicles only) | No bench ECU |

Pull when hardware is available.

---

## Human backlog

| Task | Notes |
|------|-------|
| Fill `app-update.json` URL | Donations ✅ (Venmo); release-check URL still pending |
| F-Droid fdroiddata MR | Draft at `packaging/fdroid/dev.foss.obdforge.yml` — GitLab submit |
| ~~Approve bundled LLM size~~ | ✅ Gemma 3 1B IT — optional download; see `docs/LOCAL_AI.md` |
| Shop operator review | Optional real-world workflow feedback |
| ~~Package rename approval~~ | ✅ `dev.foss.goldenpath` → `dev.foss.obdforge` namespace (v1.1.0) |
| Post-release monitoring | Issues, adapter compatibility reports |
| ~~Approve v1.1.0 tag~~ | ✅ Released 2026-06-22 |

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

- ✅ `pre-release-gate.sh` + reproducible APK verify (v1.0.0, v1.1.0)

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
| 12 — Local AI (M12) | ✅ Complete · Gemma 3 1B optional download | `COMPLETED_TASKS.md` |
| 13 — F-Droid Prep (M13) | ✅ Complete | `COMPLETED_TASKS.md` |
| 14 — Release Polish | ✅ Complete · GitLab MR pending | `COMPLETED_TASKS.md` |
| 15 — Post-Release Audit | ✅ Complete | `COMPLETED_TASKS.md` |
| 16 — v1.1.0 release | ✅ Complete | `CHANGELOG.md` [1.1.0] |
| 17 — Diagnostic data | ✅ Core complete · Wal33D/charting optional | `docs/DIAGNOSTIC_DATA.md` |
| Template M19–M29 (upstream) | ✅ Complete | `COMPLETED_TASKS.md` |
