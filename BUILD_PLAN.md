# Build Plan

> OBDForge prioritized task board. Milestones M1–M13: `docs/EXECUTION_PLAN.md`. **Finished work:** [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md) (Sprints 0–19).

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

> **v1.2.1** on main. Last audit: 2026-06-24 (`CODE_REVIEW.md` ephemeral).

### Open work

| # | Owner | Task | Notes |
|---|-------|------|-------|
| 1 | AGENT | Live-data charting / time-series dashboard | ❌ Blocked — hardware bench (`docs/ADB_BENCH_RESULTS.md`) |
| 2 | HUMAN | Fill `app-update.json` release-check URL | Donations done (Venmo) |
| 3 | ADB | F-Droid fdroiddata MR | Draft ready · device smoke PASS · GitLab submit pending |
| 4 | HUMAN | Shop operator review | Optional real-world workflow feedback |
| 5 | HUMAN | Post-release monitoring | Issues, adapter notes → `KNOWLEDGE_BASE.md` |
| 6 | HUMAN | Configure GitHub signing secrets | `OBDFORGE_KEYSTORE_BASE64` + passwords for CI signed APK upload |

### Blocked — hardware bench

No OBD adapter or bench ECU on hand. Pull when hardware is available.

| Area | Task |
|------|------|
| Transports | BLE GATT, Classic SPP, USB, Wi‑Fi/Ethernet smoke with real adapter |
| Protocol | ELM327 DTC + PID read; STN vs ELM latency |
| Live data | 10+ PID stability (5 min); unblocks charting (#1) |
| Bidirectional | Staged bench ECU test (authorized vehicles only) |

Log when bench arrives: `docs/ADB_BENCH_RESULTS.md`

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

- ✅ `pre-release-gate.sh` + reproducible APK verify (v1.0.0, v1.1.0, v1.2.0, v1.2.1)

---

## Archive

Sprints **0–19** complete. Sprint detail: [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md). Release notes: [`CHANGELOG.md`](CHANGELOG.md).
