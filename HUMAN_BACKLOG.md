# Human Backlog

> Items automation attempted during autonomous `/build` but could not complete. BUILD_PLAN rows stay open until a human finishes them.

| Deferred | Sprint | Owner | Task | Reason |
|----------|--------|-------|------|--------|
| 2026-08-21 | post-v1.3.0 | ADB | Bump `versionCode` above 25 | v1.3.0 APK still uses code 25 (same as 1.2.13) |
| 2026-07-21 | post-v1.2.8 | HUMAN | Fill `app-update.json` release-check URL | Product URL decision |
| 2026-07-21 | post-v1.2.8 | HUMAN | Shop operator review (optional) | Needs human shop workflow |
| 2026-07-21 | post-v1.2.8 | HUMAN | Post-release monitoring | Ongoing human triage |
| 2026-07-21 | post-v1.2.8 | ADB | F-Droid fdroiddata MR | GitLab submit pending |
| 2026-07-21 | post-v1.2.8 | ADB | Bench connect / flash USB-C on device | No adapter/bench ECU on hand |

## Cleared by automation (2026-07-22)

| Task | Resolution |
|------|------------|
| Release Please Actions PR permission (F-010) | Actions write + create/approve PRs; `allow_auto_merge`; `AUTOMERGE_TOKEN` |
| Dependabot auto-merge | Same `allow_auto_merge` + automerge workflows |
| Branch protection check names | Required checks → `Feature Gate`, `Repo Hygiene`, `Analyze (java-kotlin)`, `Gitleaks Secret Scan`, `Trivy FS Scan` — verified; PR #16 auto-merged to **v1.2.10** |
