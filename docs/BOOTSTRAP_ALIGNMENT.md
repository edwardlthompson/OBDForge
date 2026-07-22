# Bootstrap Alignment — Gap Analysis

> **Status:** Phase 1–4 executed for approved YES items (2026-07-21)  
> **Date:** 2026-07-21  
> **Local app version:** 1.2.8 (OBDForge product)  
> **Upstream template:** [edwardlthompson/agent-project-bootstrap](https://github.com/edwardlthompson/agent-project-bootstrap) **v0.15.1** (latest)  
> **Local `.template-version`:** `0.15.1` (template semver; app version remains in Gradle/CHANGELOG)

---

## 1. Repo identity (orientation)

| Aspect | Finding |
|--------|---------|
| Product | OBDForge — FOSS Android OBD-II diagnostics |
| Origin | Already forked/adapted from agent-project-bootstrap (Android stack, pruned) |
| Active stack | **Android only** (`examples/android/`, `modules/android/MODULE.md`) |
| License | **GPL-3.0-or-later** (not MIT) |
| Distribution | GitHub Releases + F-Droid path; reproducible APK |
| Legacy `.cursorrules` | Absent (good) |
| Agent surface baseline | Largely present from earlier template sync (~0.11–0.13 era) |

This is a **migration / cherry-pick alignment**, not a fresh bootstrap. Preserve all app code, ADRs, product docs, and release tooling.

---

## 2. What already matches the template

### Agent entrypoints & process
- `AGENTS.md`, `docs/START_HERE.md`, `docs/CURSOR_MODES.md`, `docs/FOR_AGENTS.md`, `docs/INITIALIZATION_PROMPT.md`
- Batch commands: 25 slash commands under `.cursor/commands/` + `docs/BATCH_COMMANDS.md` + `docs/help/BATCH_COMMANDS.md` + `batch-commands.mdc`
- Most always-apply rules: `cursor-modes`, `destructive-ops`, `repo-hygiene`, `feature-modules`, `foss-compliance`, `windows-encoding`, `ci-gates`, `security-triage`, etc.
- Memory / board: `AGENT_MEMORY.md`, `DECISION_LOG.md`, `KNOWLEDGE_BASE.md`, `COMPLETED_TASKS.md`, living `BUILD_PLAN.md`
- Status markers: 🔲 / ✅ / ❌ (emoji, not GitHub checkboxes)
- Owner labels: `[AGENT]` `[HUMAN]` `[ADB]` `[AUTO]`
- Security: `SECURITY.md`, `docs/SECURITY_TRIAGE.md`, `docs/THREAT_MODEL.md`, `docs/PRIVACY.md`
- Hygiene tooling: `.gitignore` ignores live `.app-update.json` / `donations.json`; `.editorconfig`, `.cursorignore`, `.pre-commit-config.yaml` (identical to upstream)
- CI family present: `ci`, `security`, `codeql`, `dependency-review`, `scorecard`, `release`, `release-please`, `pages`, `stale`, `weekly-health-check`, `dependabot-automerge`
- Product-specific scripts kept (APK sign/build/install) — correctly local-only vs upstream

### Identical (or effectively synced) files (spot-check vs v0.15.1)
- `docs/START_HERE.md` (identical to upstream — see conflicts)
- `.cursor/rules/destructive-ops.mdc`, `cursor-modes.mdc`
- `.pre-commit-config.yaml`

---

## 3. What is missing (relative to v0.15.1)

### High value — Cursor agent infrastructure (0.14–0.15)
| Item | Upstream | Notes |
|------|----------|-------|
| `.cursor/rules/local-compute.mdc` | ✅ | Prefer This Computer / parallel Task / worktrees |
| `.cursor/commands/cleanup.md` | ✅ | 26th atomic; archive ✅ rows |
| Batch registry updates | ✅ | `/cleanup`; `/build` + `/scope` semantics changed |
| `HUMAN_BACKLOG.md` (+ `.example`) | ✅ | Backlog for failed HUMAN/ADB automation |
| `.cursor/hooks.json` + `hooks/*.py` | ✅ | Destructive-ops + UTF-8 fail-open |
| `.cursor/skills/` (7) | ✅ | Progressive-load companions |
| `.cursor/agents/` (3) | ✅ | verifier, gate-fixer, explorer |
| `.cursor/worktrees.json` + setup scripts | ✅ | Local worktree / best-of-n |
| `.cursor/permissions.json` | ✅ | Auto-review dual layer |
| `.cursor-plugin/` + pack scripts | ✅ | FOSS plugin pack |
| `.cursor/stack-selection.json` | ✅ | distribution_tier / stack |
| `.cursor/mcp.foss.example` | ✅ | Optional MCP |
| `docs/help/CURSOR_FEATURES.md` | ✅ | Feature radar companion |
| Docs: `CURSOR_INTEGRATIONS.md`, `CURSOR_CLI.md`, `FILE_SIZE_GUIDE.md` (if present upstream) | ✅ | Referenced by new AGENTS.md |

### Scripts / gates (upstream-only)
- Parallel / sprint automation: `plan-parallel-dispatch.sh`, `setup-agent-worktrees.sh`, `attempt-build-plan-row.sh`, `build-backlog.sh`, `build-sprint-status.sh`, `check-build-plan-parallel.sh`, `agent-run.py`
- Lib: `run_checks_parallel.py`, `parallel_scope*.py`, `build_sprint.py`, `build_backlog.py`, `human_task_automation.py`, cursor hook/integration checkers
- Cursor: `check-cursor-hooks.sh`, `check-cursor-integrations.sh`, `sync-cursor-features.py`, `cursor-feature-radar.sh`, plugin pack scripts
- Android: `verify-android-insets.sh` (optional)
- Release helper: `merge-release-please-pr.sh`, `setup-automerge-token.*`

### CI
- `.github/workflows/release-please-automerge.yml` (missing locally)

### Optional / low priority for Android-only child
- Inactive module stubs: `modules/{web,python,node}/MODULE.md` — **do not activate**; only add if keeping full template mirror
- Commercial surfaces (`.cursor/rules/commercial-compliance.mdc`, commercial MCP/hooks examples) — FOSS child should keep `distribution_tier: foss` and may skip commercial activation docs
- Web/Python example trees — **do not copy**

---

## 4. Conflicts (migrate carefully)

| Conflict | Local | Upstream / template default | Recommendation |
|----------|-------|-----------------------------|----------------|
| **License language** | GPL-3.0-or-later (`LICENSE`, README, AGENT_MEMORY) | MIT wording in `AGENTS.md`, `foss-compliance.mdc`, UPGRADING “verify MIT” | **Keep GPL.** Patch agent docs to say GPL-3.0-or-later; never replace `LICENSE` with MIT |
| **`.template-version`** | `1.2.8` (app) | Template semver e.g. `0.15.1` | Split tracking: restore template semver in `.template-version`; keep app version in Gradle / CHANGELOG / README badges |
| **`docs/START_HERE.md`** | Identical to template (describes template repo) | Template-centric | **Productize** for OBDForge Reference/child mode; keep bootstrap pointers as secondary |
| **File size budget** | 250 view / 150 logic (`AGENTS.md`) | 300 static data / 150 logic + `FILE_SIZE_GUIDE.md` | Adopt upstream 300/150 **or** keep 250/150 and document decision — prefer adopt with Android Compose note |
| **`BUILD_PLAN.md` shape** | Compact “Active board” + maintenance (product-shaped) | Sequential / Parallel lanes + “Human & device (after automation)” | Keep product content; **reshape structure** to Sequential + Parallel + Human/device groups without losing open HUMAN/ADB rows |
| **Inactive modules on disk** | `modules/{go,lightroom,rust}` + examples | Also has web/python/node | Memory says pruned; leave as optional stubs or prune in a separate `[HUMAN]` hygiene pass — **not** required for alignment |
| **Android CI / release scripts** | Rich local APK/signing pipeline | Generic template CI | Cherry-pick workflow *patterns* only; never blind-overwrite `release.yml` / Android jobs |
| **Live JSON** | Untracked copies may exist locally; gitignored | `.example` only | Keep ignore rules; never commit live `.app-update.json` / `donations.json` |

---

## 5. Recommended stack selection

```text
active_stack: android
distribution_tier: foss
inactive: web, python, node, go, lightroom, rust (do not copy examples)
license: GPL-3.0-or-later
```

Wire `.cursor/stack-selection.json` accordingly when adopting upstream Cursor integrations.

---

## 6. Risk areas

| Risk | Severity | Mitigation |
|------|----------|------------|
| Overwriting Android release/signing CI | High | Diff-only merge; keep OBDForge-specific jobs/scripts |
| Replacing GPL with MIT language or LICENSE | High | Explicit patch; `[HUMAN]` owns LICENSE |
| Blind overwrite of `docs/INITIALIZATION_PROMPT.md` | Medium | Manual merge only |
| Breaking BUILD_PLAN open HUMAN/ADB rows | Medium | Restructure in place; preserve table rows |
| Adopting commercial Cursor tier by mistake | Medium | Set `distribution_tier: foss`; skip commercial activation |
| Template update checker false positives after fixing `.template-version` | Low | Expected once version is corrected to `0.15.1` |
| Hooks failing agent shells on Windows | Medium | Adopt fail-open hooks; smoke-test `agent-run.py check-cursor-hooks` |
| Parallel dispatch scripts unused on solo Android | Low | Bring scripts; use when `/scope` needed — no obligation to parallelize product work |

---

## 7. Prioritized alignment plan (Sequential)

### Phase 1 — Core agent infrastructure `[AGENT]` (low risk)
1. Write this gap analysis ✅ (this file)
2. Fix `.template-version` → track upstream template (`0.15.1`); document app vs template versioning in DECISION_LOG
3. Productize `docs/START_HERE.md` for OBDForge (Reference/child); keep template upgrade pointers
4. Update `AGENTS.md` + `foss-compliance.mdc`: GPL wording; add Cursor FOSS integrations section (adapted)
5. Bring missing rules/commands: `local-compute.mdc`, `cleanup.md`; refresh `batch-commands.mdc` + batch docs
6. Seed `HUMAN_BACKLOG.md` from open HUMAN/ADB BUILD_PLAN rows (+ keep `.example`)
7. Reshape `BUILD_PLAN.md` to Sequential / Parallel / Human & device sections (preserve content)
8. Cherry-pick FOSS Cursor surfaces: hooks, skills, agents, worktrees, permissions, stack-selection, mcp.foss.example, plugin pack (skip commercial activation)

### Phase 2 — Tooling, scripts, CI `[AGENT]` (+ `[HUMAN]` review for workflows)
1. Copy parallel/sprint/cursor scripts listed in §3; adapt `validate-bootstrap.sh` for multi-core checks
2. Refresh `TEMPLATE_INDEX.json` entries for new files (merge, don’t clobber Android-only paths)
3. Diff-merge CI: add `release-please-automerge.yml` only if Release Please already used; do **not** blind-replace Android `ci.yml` / `release.yml`
4. Run `scripts/check-file-encoding.py`, hygiene, and available validate-bootstrap; fix in scope
5. Update `docs/UPGRADING_FROM_TEMPLATE.md` LICENSE row for GPL children

### Phase 3 — Stack modules `[AGENT]`
1. Confirm Android module remains sole active; ensure `modules/android/MODULE.md` still authoritative
2. Do **not** import web/python examples
3. Optional: leave or prune go/lightroom/rust stubs — ask `[HUMAN]`

### Phase 4 — Process & memory hygiene `[AGENT]`
1. Append DECISION_LOG entry for this alignment
2. Milestone update to `AGENT_MEMORY.md` (template baseline → 0.15.1)
3. Short “How agents should work in this repo” in README (pointer to START_HERE) if missing
4. Migration notes section at bottom of this file (fill after execution)

---

## 8. High-risk items needing `[HUMAN]` confirmation before execute

Confirm or reject each:

1. **Fix `.template-version` to `0.15.1`** (app stays 1.2.8 in Gradle/CHANGELOG) — recommended **YES**
2. **Adopt FOSS Cursor integrations** (hooks, skills, agents, worktrees, plugin pack) — recommended **YES** (foss tier only)
3. **Adopt `/build` autonomous HUMAN/ADB automation + `HUMAN_BACKLOG.md`** — recommended **YES**
4. **Reshape `BUILD_PLAN.md`** to Sequential / Parallel / Human & device — recommended **YES** (content preserved)
5. **File size budget:** switch 250→**300** static data lines per upstream — recommended **YES**
6. **Add `release-please-automerge.yml`** — recommended **YES if** Release Please + automerge token already desired; else defer
7. **Prune inactive `modules/{go,lightroom,rust}` + examples** — recommended **DEFER** (separate hygiene PR)
8. **License docs:** keep GPL everywhere agents speak of license — required **YES** (non-negotiable)
9. **Touch Android `ci.yml` / `release.yml`** — recommended **DIFF-ONLY / minimal**; no blind upstream copy

---

## 9. Migration notes (executed)

### Done (YES items 1–5, 8–9)

| Item | Change |
|------|--------|
| Template version | `.template-version` → `0.15.1`; app stays **1.2.8** |
| GPL | `AGENTS.md`, `foss-compliance.mdc`, UPGRADING LICENSE row — GPL-3.0-or-later retained |
| START_HERE | Productized for OBDForge Reference/child mode |
| Cursor FOSS | hooks, skills (7), agents (3), worktrees, permissions, plugin pack, `local-compute.mdc`, `/cleanup`, MCP example |
| `/build` automation | Updated `build.md`/`scope.md`, scripts, seeded `HUMAN_BACKLOG.md` |
| BUILD_PLAN | Reshaped Sequential / Parallel / Human & device; open rows preserved |
| File budget | 300 static / 150 logic across agent docs |
| CI | Android `ci.yml` / `release.yml` **not** overwritten; `.gitignore` Cursor ignores only |

### Deferred (not YES / conditional)

| Item | Status |
|------|--------|
| `release-please-automerge.yml` | ✅ Added 2026-07-22 after F-010 settings enabled |
| Prune go/lightroom/rust modules | Deferred (§8 item 7) |

### Still needs `[HUMAN]` / `[ADB]`

See `BUILD_PLAN.md` → Human & device and `HUMAN_BACKLOG.md` (Release Please permission, app-update URL, F-Droid MR, hardware bench).

### Critique

- **Null/empty:** HUMAN/ADB rows mapped into Human & device + HUMAN_BACKLOG.
- **Timeouts:** Run validate/hooks under Git Bash on Windows if PowerShell path fails.
- **Races:** Release workflows left untouched.
- **Exceptions:** stack-selection `android` + `distribution_tier: foss` drives Cursor integration checks.

---

## 10. Next step

Run local validation (`validate-bootstrap`, encoding, hygiene). Human may later approve deferred items 6–7.
