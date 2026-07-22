# Decision Log

> Chronological register of major technical trade-offs, accepted architectures, and rejected alternatives.
> **Treat past entries as immutable history; append only.**

## Format

```markdown
### YYYY-MM-DD — [Title]
- **Status:** Accepted | Rejected | Superseded
- **Context:** ...
- **Decision:** ...
- **Alternatives considered:** ...
- **Consequences:** ...

```

## Entries

_Seed template ADR: `docs/adr/0000-template-baseline.md`. Child repos use `docs/adr/0001-core-architecture.md`._

### 2026-07-22 — Hide docs/chore from Release Please releasable units
- **Status:** Accepted
- **Context:** Post-release README/memory sync (`docs:`) opened Release Please PR #18 for v1.2.12 (docs-only patch loop after v1.2.11)
- **Decision:** Mark `docs` and `chore` as `"hidden": true` in `release-please-config.json` changelog-sections; close PR #18 without merge
- **Alternatives considered:** Keep merging docs-only patches (rejected); remove docs/chore from sections entirely (also fine; hidden keeps types known)
- **Consequences:** Only feat/fix/perf/revert open release PRs; badge/memory hygiene commits no longer cut versions

### 2026-07-22 — Branch protection uses Actions job check names
- **Status:** Accepted
- **Context:** Required contexts `CI` / `Security Scan` / GHAS `CodeQL` never went green on PRs; auto-merge stayed BLOCKED
- **Decision:** Human set required checks to `Feature Gate`, `Repo Hygiene`, `Analyze (java-kotlin)`, `Gitleaks Secret Scan`, `Trivy FS Scan` (GitHub Actions app)
- **Alternatives considered:** Keep admin-merge only (rejected)
- **Consequences:** Release Please PR #16 auto-merged to v1.2.10 without `--admin`; APK uploaded via Release workflow

### 2026-07-22 — Enable Release Please / Dependabot auto-merge (F-010)
- **Status:** Accepted
- **Context:** Human enabled Actions create/approve PRs and repo `allow_auto_merge`
- **Decision:** Set `AUTOMERGE_TOKEN` secret; add `release-please-automerge.yml`; point Release Please + Dependabot automerge at `AUTOMERGE_TOKEN || GITHUB_TOKEN`; clear F-010 from BUILD_PLAN / HUMAN_BACKLOG
- **Alternatives considered:** Keep manual merge of Release Please PRs (rejected once settings available)
- **Consequences:** Release Please PRs queue auto-merge when checks pass; merge push should trigger CI via PAT

### 2026-07-22 — Pin Kotlin 2.4.0 until CodeQL supports 2.4.10+
- **Status:** Accepted
- **Context:** Dependabot PR #14 bumped Kotlin to 2.4.10; CodeQL failed with extractor ceiling “supports versions below 2.4.10”
- **Decision:** Merge AGP 9.3.0 + usb-serial 3.11.0; keep Kotlin at 2.4.0; ignore Kotlin `>=2.4.10` in Dependabot (KB-019). Merged Actions group PR #13 (setup-node/go/stale v7)
- **Alternatives considered:** Wait for CodeQL (blocks other bumps); disable CodeQL on PRs (rejected)
- **Consequences:** Revisit Kotlin bump when CodeQL release notes include 2.4.10+; automerge still needs repo `enablePullRequestAutoMerge` `[HUMAN]`

### 2026-07-21 — Align child repo to agent-project-bootstrap v0.15.1
- **Status:** Accepted
- **Context:** OBDForge already used bootstrap process files but lagged Cursor FOSS integrations (hooks, skills, parallel `/build`) and mis-tracked app semver in `.template-version`
- **Decision:** Cherry-pick FOSS Cursor surfaces + parallel/sprint scripts from upstream v0.15.1; productize `docs/START_HERE.md`; keep **GPL-3.0-or-later** in agent docs; set `.template-version` to `0.15.1`; reshape `BUILD_PLAN.md` with Sequential / Parallel / Human & device; seed `HUMAN_BACKLOG.md`; adopt 300/150 file budgets. Defer `release-please-automerge.yml` and pruning inactive go/lightroom/rust modules
- **Alternatives considered:** Fresh bootstrap overwrite (rejected: destroys product history); adopt MIT LICENSE from template (rejected: product is GPL); activate commercial Cursor tier (rejected: F-Droid FOSS)
- **Consequences:** Agents use local-compute + `/cleanup` + HUMAN backlog automation; Android CI/release workflows untouched beyond additive ignores; see `docs/BOOTSTRAP_ALIGNMENT.md`

### 2026-07-10 — Stage A flash: USB-C host only + AI assist policy
- **Status:** Accepted
- **Context:** Refined flash requirements plan — USB-C-first hardware, local AI must not crack crypto
- **Decision:** `FlashTransportPolicy` allows only `UsbSerial` + `Simulated` (Wi‑Fi/Ethernet/BT blocked). Ship `docs/FLASH_HARDWARE.md` (EX/SX + OTG). `FlashSecurityAssist` may match installed plugins / explain NRCs; key generation and crypto cracking are rejected. Stack: `IsoTpLink`, `UdsProgrammingSession`, `FlashTransferEngine` behind `WriteOperation.EcuFlash`
- **Alternatives considered:** Allow Wi‑Fi flash when USB unavailable (rejected for Stage A integrity); LLM invents `27` keys (rejected)
- **Consequences:** Supersedes earlier “USB/Wi‑Fi” wording for flash transport; MX remains diagnostics/coding only; real-vehicle bench still `[HUMAN]`

### 2026-07-10 — Stage A ECU flash scaffold (USB/Wi‑Fi only; no MX)
- **Status:** Superseded (transport narrowed by entry above)
- **Context:** User approved implementing the ECU flash requirements plan (Stage A)
- **Decision:** Scaffold ISO-TP framing, demo `34`/`36`/`37` transfer engine, `WriteOperation.EcuFlash`, USB/Wi‑Fi/Ethernet/Simulated-only flash transport policy (Bluetooth/MX blocked), user-supplied `SecurityAccessPlugin` SPI with no OEM keys in APK, and one demo ECU profile. Local AI must not invent seed/keys. Real-vehicle bench remains `[HUMAN]`
- **Alternatives considered:** Keep flash permanently out of scope (superseded for Stage A scaffold only); allow Bluetooth flash (rejected: drop/brick risk)
- **Consequences:** Coding path still rejects raw `34`/`36`/`37`; flash uses separate gated service; F-Droid default remains diagnostics-first until a flash product flavor ships

### 2026-07-10 — ECU coding is DID-only; flash out of scope on MX
- **Status:** Superseded (in part by Stage A scaffold above)
- **Context:** Personas advertise ECU writes/coding; users asked for coding/flashing with OBDLink MX
- **Decision:** Implement gated UDS DID read (`22`) / write (`2E`) for Shop/Racing with expert unlock; MX remains diagnostics/coding only — not a flash transport
- **Alternatives considered:** Full reflash toolchain on MX (rejected); raw unrestricted UDS (rejected: ADR-0003 safety)
- **Consequences:** Coding UI has no binary picker; flash requires USB-C Stage A path

### 2026-06-21 — Release APK signing outside Gradle
- **Status:** Accepted
- **Context:** F-Droid requires reproducible unsigned APK builds; sideload users want a properly signed release artifact without manual `jarsigner` steps each time
- **Decision:** Post-build signing via `scripts/sign-release-apk.sh` with env-var keystore contract; optional CI upload when GitHub secrets are configured; unsigned APK remains the reproducible default
- **Alternatives considered:** Gradle `signingConfigs` in repo (rejected: secrets in CI only, harder to keep unsigned reproducible path clean); debug-only signing (rejected: not suitable for sideload distribution)
- **Consequences:** Maintainers run `generate-release-keystore` once locally; CI signed APK is opt-in via secrets

### 2026-06-20 — Repo-wide checklist status markers
- **Status:** Accepted
- **Context:** BUILD_PLAN and scattered checklists used mixed ⬜ / `- [ ]` / ✅ formats; inconsistent in Markdown Preview vs source
- **Decision:** Standardize on 🔲 open · ✅ done · ❌ blocked emoji markers repo-wide; document in `BUILD_PLAN.md` legend and agent read order
- **Alternatives considered:** GitHub `- [ ]` task lists (rejected: poor Preview readability and agent parsing); keep ⬜ white square (rejected: visually similar to ✅ in some fonts)
- **Consequences:** All new checklist rows use emoji; `agent-progress.sh` accepts legacy ⬜ for child repos during transition

### 2026-06-18 — Release automation hardening (M29)
- **Status:** Accepted
- **Context:** v0.11.0 release lacked SBOM assets (GITHUB_TOKEN cannot chain `release` → `release.yml`); Release Please skipped `extra-files`; `health-check.yml` registered as path name caused 0-job push failures
- **Decision:** `release-please.yml` runs `sync-template-version.sh` on release PR branches and dispatches `release.yml` on `release_created`; rename workflow to `weekly-health-check.yml`; fix sync script for Windows Git Bash
- **Alternatives considered:** PAT with workflow scope for release chaining (rejected: secrets management); manual SBOM backfill only (rejected: repeated human step each release)
- **Consequences:** Release Please needs `actions: write`; future releases should ship SBOM assets without manual dispatch

### 2026-06-17 — Batch instruction templates (M27)
- **Status:** Accepted
- **Context:** Agents and child-repo owners needed repeatable shortcuts for bootstrap, verify, build, ship, and maintenance workflows without re-pasting long prompts
- **Decision:** Ship 25 slash commands in `.cursor/commands/` (20 atomic + 5 super), bare-word expansion via `batch-commands.mdc`, human cheat sheet at `docs/help/BATCH_COMMANDS.md`, registry at `docs/BATCH_COMMANDS.md`; `/push` and `/ship` grant explicit push approval
- **Alternatives considered:** `beforeSubmitPrompt` hook for bare words (rejected: Cursor API cannot rewrite prompts); single mega-doc for humans and agents (rejected: overwhelms first-time users)
- **Consequences:** `alwaysApply` rule adds ~25 lines per session; `check-batch-commands.sh` prevents registry drift; child repos cherry-pick via `UPGRADING_FROM_TEMPLATE.md`

### 2026-06-13 — @lhci/cli npm overrides for transitive CVEs
- **Status:** Accepted
- **Context:** Lighthouse CI (`@lhci/cli`) bundles transitive dependencies (`tmp`, `uuid`) with known CVEs; no patched `@lhci/cli` release available at triage time
- **Decision:** Add npm `overrides` in `examples/web/package.json` forcing `tmp >= 0.2.6` and `uuid >= 11.1.1`; document in KB-007
- **Alternatives considered:** Dismiss Dependabot alert (rejected: hides real risk); remove Lighthouse CI job (rejected: loses performance gate)
- **Consequences:** Lockfile must be regenerated after override changes; overrides should be removed when `@lhci/cli` ships fixed dependencies

### 2026-06-13 — Ship all optional ecosystem modules (M3)
- **Status:** Accepted
- **Context:** Sprint M3 asked whether to ship Lightroom, Rust, and Go optional modules in the template maintainer repo
- **Decision:** Ship all three with Golden Path stubs, MODULE.md guides, and path-gated CI jobs (`lightroom`, `rust`, `go`) that skip when child repos remove the directories
- **Alternatives considered:** Lightroom-only (rejected: Rust/Go stubs are low-cost and popular); defer all optional modules (rejected: COMPLETED_TASKS M3 work already landed)
- **Consequences:** Template CI runs more jobs on `main`; child repos can delete unused `examples/` folders to skip jobs via `hashFiles` guards
