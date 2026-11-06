# Bootstrap Alignment — Gap Analysis

> **Status:** Aligned to upstream **v1.12.0** (2026-11-06)  
> **Local product version:** 1.4.0 (OBDForge)  
> **Upstream template:** [edwardlthompson/agent-project-bootstrap](https://github.com/edwardlthompson/agent-project-bootstrap) **v1.12.0**  
> **Local `.template-version`:** `1.12.0` (template semver; product version remains in Gradle / CHANGELOG / Release Please)

---

## 1. Repo identity

| Aspect | Finding |
|--------|---------|
| Product | OBDForge — FOSS Android OBD-II diagnostics |
| Origin | Child of agent-project-bootstrap (Android stack, pruned) |
| Active stack | **Android only** |
| License | **GPL-3.0-or-later** (do not replace with MIT) |
| Distribution | GitHub Releases + F-Droid path |

## 2. Closed in the 1.12.0 catch-up (2026-11-06)

| Area | Action |
|------|--------|
| Batch commands | Expanded to **52** (47 atomic + 5 super); registries + `check-batch-commands.sh` + print sheet |
| Cursor skills | Added adr, best-of-n, emulator, espresso-android16, local-models, shape-idea, update-deps, upgrade, ux-review |
| Cursor rules | Added agent-mode, brief-replies, local-deps, product-brief, ux-ui (+ refreshed batch-commands) |
| Docs | CI required checks / gaps, Dependabot automerge, merge queue, OpenSSF, sprint smoke, directory rules, preferences, canvas, skills recipes, Android signing, local models, MCP recipes, Winget, help twins |
| Workflows | feedback-notify, issue-pii-nudge, sbom-diff-dependabot, sync-open-prs-build-plan |
| Scripts / lib | Gate scripts + 129 missing `scripts/lib` modules from parent |
| Linguist | Already present from portfolio pass |
| Memory layout | `.agent/AGENT_MEMORY.md` + `.agent/DECISION_LOG.md` |

## 3. Intentionally not mirrored

| Upstream surface | Why skipped / deferred |
|------------------|------------------------|
| Commercial Cursor docs / MCP commercial examples | Child `distribution_tier: foss` |
| Blender icon skill + blender ADR deep tooling | Not an OBDForge product path |
| Full parent `examples/*` inactive stacks | Android-only Golden Path |
| Parent ADR numbering for OBD product ADRs | Keep OBDForge `docs/adr/0002-protocol-plugins` …; template ADRs 0005–0010 added as process ADRs |
| `.clinerules/workflows/` directory | Child keeps single `.clinerules` adapter file |

## 4. Verification

```bash
bash scripts/check-batch-commands.sh
bash scripts/validate-template-index.sh
bash scripts/check-doc-links.sh
bash scripts/verify.sh
```

## 5. Next upgrade

When upstream > `1.12.0`: run `/upgrade` (child plan) or `scripts/simulate-template-upgrade.sh`, then cherry-pick per `docs/UPGRADING_FROM_TEMPLATE.md`.
