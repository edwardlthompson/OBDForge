# Start Here

> **Read this file first** — whether you are a human or a Cursor agent.

## What is this?

**OBDForge** is a FOSS Android OBD-II diagnostics app (GPL-3.0-or-later), built on [agent-project-bootstrap](https://github.com/edwardlthompson/agent-project-bootstrap) with the Android stack active.

Agents: treat this as a **child / Reference** repo — preserve application code; follow process and tooling from the bootstrap template.

## Repo mode

| Mode | When | Next read |
|------|------|-----------|
| **Reference (this repo)** | Day-to-day OBDForge work | `docs/CURSOR_MODES.md` → `docs/FOR_AGENTS.md` |
| **Bootstrap** | Only if re-running init on a new clone | `docs/CURSOR_MODES.md` → `docs/INITIALIZATION_PROMPT.md` |

Template upgrade guide: [`docs/UPGRADING_FROM_TEMPLATE.md`](UPGRADING_FROM_TEMPLATE.md). Alignment status: [`docs/BOOTSTRAP_ALIGNMENT.md`](BOOTSTRAP_ALIGNMENT.md).

## Cursor modes (Plan / Agent / Debug / Ask)

See [`docs/CURSOR_MODES.md`](CURSOR_MODES.md) — pick the Cursor mode before editing code.

## Agent shortcuts

Type **`/`** in Cursor Agent chat. Start with **[docs/help/BATCH_COMMANDS.md](help/BATCH_COMMANDS.md)** — try `/tour` (first run), `/coach` (what next), `/verify` before merge, or `/build` for BUILD_PLAN sprints.

Other IDEs: read [`docs/help/TOUR.md`](help/TOUR.md) or [`docs/help/COACH.md`](help/COACH.md). Portability map: [`AGENT_PORTABILITY.md`](AGENT_PORTABILITY.md). Glossary: [`help/GLOSSARY.md`](help/GLOSSARY.md).

## OBDForge read order (agents)

1. `README.md` (product overview)
2. `docs/START_HERE.md` (this file)
3. `docs/CURSOR_MODES.md`
4. `docs/BEST_PRACTICES.md` + `docs/FIRST_30_DAYS.md`
5. `docs/FOR_AGENTS.md`
6. `AGENTS.md`
7. `BUILD_PLAN.md` Sequential lane
8. `modules/android/MODULE.md` only
9. `examples/android/` only (Golden Path)
10. `docs/DESIGN_GUIDE.md` / `docs/FEATURE_MODULES.md` when doing UI or Sprint 2+ features
11. Product docs as needed: `docs/spec.md`, `docs/EXECUTION_PLAN.md`, `docs/adr/`, `docs/THREAT_MODEL.md`, `docs/PRIVACY.md`

## Do Not Read Yet

- Inactive `examples/` folders (non-Android)
- `KNOWLEDGE_BASE.md` — reference when debugging (KB entries)
- `docs/MAINTAINING_THE_TEMPLATE.md` (upstream maintainers only)

## BUILD_PLAN Labels

`AGENT` | `HUMAN` | `ADB` | `AUTO` — filter with `grep '\[AGENT\]' BUILD_PLAN.md`

**Status markers:** 🔲 open · ✅ done · ❌ blocked — emoji only (not `- [ ]` checkboxes). See legend in `BUILD_PLAN.md`.

## Security

Enable Dependabot alerts on GitHub. Weekly CVE triage: `docs/SECURITY_TRIAGE.md`. Vulnerability reporting: `SECURITY.md`.

## Agent prompt (OBDForge)

Read @docs/START_HERE.md, @docs/CURSOR_MODES.md, @docs/FOR_AGENTS.md, and @TEMPLATE_INDEX.json. Pick Cursor mode per CURSOR_MODES. Active stack: **Android only**. License: **GPL-3.0-or-later**. Do not copy inactive `examples/` wholesale. Follow `BUILD_PLAN.md` Sequential before Parallel.
