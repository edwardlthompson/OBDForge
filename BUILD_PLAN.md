# Build Plan

> OBDForge prioritized task board. Product milestones M1–M13: `docs/EXECUTION_PLAN.md`. **Completed sprints:** `COMPLETED_TASKS.md`.

## Owner Label Legend

| Label | Owner | When to use |
|-------|-------|-------------|
| `AGENT` | Cursor Agent | Code, docs, scaffolding, tests, CI config |
| `HUMAN` | Human developer | Approvals, credentials, GitHub settings, product decisions |
| `ADB` | Human (Android) | Android SDK, emulator/device testing, F-Droid submission |
| `AUTO` | CI/scripts/bots | GitHub Actions, Dependabot, pre-commit, update checker |

## Status markers

Use **emoji markers** (not `- [ ]` GitHub checkboxes) so task state reads clearly in Markdown source and Preview.

| Marker | State | Agent action |
|--------|-------|--------------|
| 🔲 | Open | Default for new tasks; work or leave queued |
| ✅ | Done | Replace 🔲 when complete; archive sprint rows to `COMPLETED_TASKS.md` |
| ❌ | Blocked | Replace 🔲 when blocked; add brief reason after the description |

**Task format:** `🔲 [OWNER] Description` · done: `✅ [OWNER] Description` · blocked: `❌ [OWNER] Description — reason`

```bash
grep '\[AGENT\]' BUILD_PLAN.md
grep '\[HUMAN\]' BUILD_PLAN.md
grep '\[ADB\]' BUILD_PLAN.md
grep '\[AUTO\]' BUILD_PLAN.md
```

**Agent rule:** Execute all `[AGENT]` **Sequential** items first, then dispatch **Parallel** agents with isolated file scopes (`docs/PARALLEL_AGENT_SCOPES.md`). Shared schema/types are Sequential-only.

---

## Sprint 0 — Bootstrap & Customization

> Android stack selected; inactive stacks pruned. Most doc scaffold complete after agent bootstrap.

### Sequential

1. ✅ [HUMAN] Create repo from agent-project-bootstrap template (OBDForge)
2. ✅ [AGENT] Run `scripts/init-project.ps1` with `--stack android` (prune inactive stacks)
3. ✅ [AGENT] Bootstrap docs: LICENSE (GPL-3.0), ADRs 0001–0005, AGENT_MEMORY, EXECUTION_PLAN, persona/design specs
4. ✅ [AGENT] Update README, THREAT_MODEL, PRIVACY, RUNBOOK for OBDForge
5. 🔲 [HUMAN] Fill `donations.json` / `examples/android/app/src/main/assets/app-update.json` URLs for your GitHub org
6. 🔲 [HUMAN] Enable Dependabot alerts + private vulnerability reporting (Settings → Code security)
7. 🔲 [AGENT] Run `scripts/setup-github-repo.ps1` (requires `gh` auth with admin)
8. 🔲 [AUTO] Sprint 0 sign-off (all green on `main`):
   - `validate-bootstrap.sh --quick`
   - `feature-gate.sh --stack android`
   - `check-github-ci.sh --wait 300`
   - `check-license-compliance.sh`

### Parallel (safe after Sequential step 3)

| Task | Owner | Isolated scope |
|------|-------|----------------|
| F-Droid metadata stubs | AGENT | `examples/android/metadata/` |
| Gradle namespace rename plan | AGENT | `docs/` only until `[HUMAN]` approves package rename |

---

## Sprint 1 — Core Architecture (M1)

### Sequential

1. ✅ [AGENT] Draft ADR-0001 core architecture
2. 🔲 [HUMAN] Approve ADR-0001 and Sprint 1 scope
3. 🔲 [AGENT] Scaffold Clean Architecture packages under `examples/android/`
4. 🔲 [AGENT] Implement `TransportRegistry` + `ProtocolRegistry` stubs
5. 🔲 [AGENT] Room schema v1 (Session, AuditLog stubs) + migration test
6. 🔲 [AGENT] Wire Golden Path shell (Connect placeholder, About, Settings, theme)
7. 🔲 [AUTO] `./gradlew test assembleDebug` green in CI

### Parallel (safe after Sequential step 4)

| Task | Owner | Isolated scope |
|------|-------|----------------|
| Domain unit tests | AGENT | `domain/**` |
| Compose theme (Garage tokens) | AGENT | `ui/theme/**`, `design-tokens/` |

---

## Sprint 2 — Transports (M2)

### Sequential

1. 🔲 [AGENT] Define `ObdTransport` port + fake transport for tests
2. 🔲 [AGENT] Bluetooth SPP transport
3. 🔲 [AGENT] USB serial transport + permission UX
4. 🔲 [AGENT] WiFi/Ethernet TCP transport
5. 🔲 [AGENT] Transport picker UI + DataStore last-used
6. 🔲 [ADB] Smoke each transport with real adapter hardware

---

## Sprint 3 — ELM327 Protocol (M3)

### Sequential

1. ✅ [AGENT] Draft ADR-0002 protocol plugins
2. 🔲 [HUMAN] Approve ADR-0002
3. 🔲 [AGENT] Implement `DiagnosticProtocol` SPI + `Elm327Protocol`
4. 🔲 [AGENT] Mode 01/03/04/09 + probe logic
5. 🔲 [AGENT] Transcript fixture tests
6. 🔲 [ADB] Bench: read DTC + single PID via ELM327 clone

---

## Sprint 4 — OBDLink STN/STPX (M4)

### Sequential

1. 🔲 [AGENT] `StnProtocol` + capability detection
2. 🔲 [AGENT] `StpxProtocol` + fast streaming path
3. 🔲 [AGENT] Fallback to ELM327 when probe fails
4. 🔲 [ADB] Bench: compare STN vs ELM latency on OBDLink adapter

---

## Sprint 5 — Live Data (M5)

### Sequential

1. 🔲 [AGENT] PID catalog + parser (Mode 01)
2. 🔲 [AGENT] Streaming loop with backpressure
3. 🔲 [AGENT] Persona-aware dashboard layouts (DIY vs Racing density)
4. 🔲 [ADB] 10+ PID stability test (5 min bench)

### Parallel (safe after Sequential step 2)

| Task | Owner | Isolated scope |
|------|-------|----------------|
| PID formatters + tests | AGENT | `feature/livedata/logic/` |
| Dashboard composables | AGENT | `ui/livedata/` |

---

## Sprint 6 — Persistence (M6)

### Sequential

1. 🔲 [AGENT] Room: sessions, DTC snapshots, freeze frames
2. 🔲 [AGENT] Session export JSON
3. 🔲 [AGENT] Schema migration tests
4. 🔲 [AGENT] Session history UI

---

## Sprint 7 — Demo Mode (M7)

### Sequential

1. 🔲 [AGENT] Mock transport + mock protocol fixtures
2. 🔲 [AGENT] Settings toggle + persistent banner
3. 🔲 [AGENT] CI test: full connect → DTC → PID without hardware
4. 🔲 [HUMAN] Approve demo data realism (no misleading brand names)

---

## Sprint 8 — Safety & Audit (M8)

### Sequential

1. ✅ [AGENT] Draft ADR-0003 safety interlocks
2. 🔲 [HUMAN] Approve interlock copy and expert mode policy
3. 🔲 [AGENT] Implement `SafetyGate` + expert unlock
4. 🔲 [AGENT] Audit log Room table + export
5. 🔲 [AGENT] Unit tests: blocked/allowed write paths

---

## Sprint 9 — Bidirectional Controls (M9)

### Sequential

1. 🔲 [AGENT] UDS write / actuator test use cases behind SafetyGate
2. 🔲 [AGENT] PersonaPolicy blocks DIY writes
3. 🔲 [ADB] Staged bench ECU test only (no public road vehicles)
4. 🔲 [HUMAN] Sign off liability disclaimer in app + F-Droid description

---

## Sprint 10 — VIN Resolution (M10)

### Sequential

1. ✅ [AGENT] Draft ADR-0005 + `docs/features/vin-resolution.md`
2. 🔲 [AGENT] Implement `VinResolver` ECU-first chain
3. 🔲 [AGENT] Barcode scan (CameraX + ML Kit) + manual entry
4. 🔲 [AGENT] Vehicle profile Room entity + provenance UI
5. 🔲 [ADB] Bench Mode 09 + barcode fallback smoke

---

## Sprint 11 — Personas & Shop (M11)

### Sequential

1. ✅ [AGENT] Draft ADR-0004 + `docs/PERSONAS.md`
2. 🔲 [AGENT] Persona switcher + filtered navigation
3. 🔲 [AGENT] Shop: customer, work order, intake → close-out flow
4. 🔲 [HUMAN] Review shop workflow with real shop operator (if available)

---

## Sprint 12 — Local AI (M12)

### Sequential

1. 🔲 [AGENT] MediaPipe LLM integration (on-device DTC explain)
2. 🔲 [AGENT] TFLite optional classifier hooks
3. 🔲 [AGENT] Airplane-mode AI smoke test
4. 🔲 [HUMAN] Approve bundled model size for F-Droid (~MB budget)

---

## Sprint 13 — F-Droid Prep (M13)

### Sequential

1. 🔲 [AGENT] `SOURCE_DATE_EPOCH` in release scripts + CI
2. 🔲 [AUTO] `verify-reproducible-apk.sh` green
3. 🔲 [AGENT] Complete `examples/android/metadata/` (summary, anti-features)
4. 🔲 [AUTO] `verify-fdroid-metadata.sh` green
5. 🔲 [ADB] Release APK install + cold start smoke
6. 🔲 [ADB] F-Droid dry-run (`fdroid lint` or fdroiddata MR draft)

---

## Sprint 14 — Release Polish

### Sequential

1. 🔲 [AGENT] CHANGELOG + Release Please version bump
2. 🔲 [AUTO] `pre-release-gate.sh` + SBOM on release
3. 🔲 [HUMAN] Approve v1.0.0 tag
4. 🔲 [ADB] F-Droid submission merge request
5. 🔲 [HUMAN] Post-release monitoring (issues, adapter reports)

---

## Ongoing Maintenance

### Weekly

- 🔲 [AUTO] `check-security-triage.sh --wait-ci 300`
- 🔲 [AGENT] Apply Dependabot bumps; triage Scorecard SARIF
- 🔲 [AUTO] CI + Feature Gate green on `main`

### Monthly

- 🔲 [AUTO] `check-template-updates.sh` (upstream bootstrap)
- 🔲 [AUTO] `check-license-compliance.sh` + SBOM on latest release
- 🔲 [AGENT] Review adapter compatibility reports; update KNOWLEDGE_BASE

### Pre-release

- 🔲 [AUTO] `pre-release-gate.sh` + reproducible APK verify
- 🔲 [HUMAN] Approve release tag when product-ready

---

## Archived Sprints

| Sprint | Status | Archive |
|--------|--------|---------|
| Template maintainer M19–M29 | Complete (upstream) | `COMPLETED_TASKS.md` |
