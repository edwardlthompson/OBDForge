# Human Backlog

> Items automation attempted during autonomous `/build` but could not complete. BUILD_PLAN rows stay open until a human finishes them.

| Deferred | Sprint | Owner | Task | Reason |
|----------|--------|-------|------|--------|
| 2026-07-21 | post-v1.2.8 | HUMAN | Fill `app-update.json` release-check URL | Product URL decision |
| 2026-07-21 | post-v1.2.8 | HUMAN | Shop operator review (optional) | Needs human shop workflow |
| 2026-07-21 | post-v1.2.8 | HUMAN | Post-release monitoring | Ongoing human triage |
| 2026-07-21 | post-v1.2.8 | ADB | F-Droid fdroiddata MR | GitLab submit pending |
| 2026-07-21 | post-v1.2.8 | ADB | Bench connect / flash USB-C on device | No adapter/bench ECU on hand |

## Cleared by automation (2026-07-22)

| Task | Resolution |
|------|------------|
| Release Please Actions PR permission (F-010) | Repo: Actions write + create/approve PRs; `allow_auto_merge`; `AUTOMERGE_TOKEN`; `release-please-automerge.yml` |
| Dependabot auto-merge | Same `allow_auto_merge` + `dependabot-automerge.yml` uses `AUTOMERGE_TOKEN` |
