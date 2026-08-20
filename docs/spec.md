# Product Specification

> OBDForge child spec. Canonical milestones: [`EXECUTION_PLAN.md`](EXECUTION_PLAN.md). Feature slices: `docs/features/{name}.md`.
> Status markers: 🔲 open · ✅ done · ❌ blocked.

## Overview

**Product:** OBDForge  
**Purpose:** FOSS Android OBD-II diagnostics (GPL-3.0-or-later) with multi-transport adapters, local AI, and F-Droid / GitHub Releases.  
**Users:** DIY / shop / racing technicians and agents maintaining the Android app.

## Functional Requirements & User Stories

| ID | Story | Acceptance |
|----|-------|------------|
| FR-1 | As a maintainer I run `scripts/init-project.sh` so the child repo is customized | Manifest, adapters, and checklist exist; unused stacks prune when asked |
| FR-2 | As an agent I read `AGENTS.md` first so I follow architecture and test-first rules | Adapters for Cursor, Claude Code, and Copilot stay in sync |
| FR-3 | As a reviewer I get CI + security on every PR without opting in | `ci.yml`, `security.yml`, Dependabot, issue/PR templates present |

## Non-Functional Constraints

- MIT default (Apache-2.0 selectable at init for child repos)
- No proprietary SDKs on the FOSS production path
- Opt-in telemetry only; never enabled by default
- File budgets: 300 lines static data, 150 lines pure logic
- Preflight fails clearly when `git` or Python is missing

## Architecture & Data Flow

```mermaid
flowchart LR
  Template[GitHub Template] --> Clone[Child clone]
  Clone --> Pre[Preflight hooks]
  Pre --> Init[init-project]
  Init --> Post[Post hooks]
  Post --> Agents[AGENTS.md adapters]
  Post --> Check[PROJECT_CHECKLIST.md]
  Post --> Manifest[bootstrap.config.json]
```

## Test-first rule

Every feature in `docs/plan.md` / BUILD_PLAN must list tests, or state why automation is not feasible and name the fallback command.
