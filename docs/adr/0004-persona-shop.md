# ADR-0004: Persona Modes and Shop Workflow

- **Status:** Accepted
- **Date:** 2026-06-21
- **Deciders:** OBDForge team

## Context

Users range from casual DIY owners to shop technicians and track-side racing crews. One UI density and permission model does not fit all; safety and workflow differ by persona.

## Decision

Implement **four persona modes** persisted in DataStore; switchable from Settings with cooldown warning if session active.

### Personas

| Persona | Primary user | UI density | Key capabilities |
|---------|--------------|------------|------------------|
| **DIY** | Owner | Simplified | DTC read/clear, basic live PIDs, AI explain |
| **Semi-pro** | Enthusiast | Standard | Custom dashboards, logging, limited tests |
| **Shop** | Technician | Dense | Customer/vehicle records, work orders, audit export |
| **Racing** | Track crew | Telemetry-first | High-rate PIDs, lap/session markers, minimal friction |

### Shop workflow

1. **Intake** — VIN resolve (ADR-0005) → vehicle profile in Room.
2. **Connect** — adapter pick from `TransportRegistry`; last-used per bay optional.
3. **Inspect** — DTC + readiness + freeze frame capture to session.
4. **Work order** — notes, parts, labor timer (local); link session IDs.
5. **Close-out** — audit log slice export; clear DTCs only with Shop interlocks (ADR-0003).

### AI assistant behavior

- DIY: plain-language DTC explanations, no raw hex dumps by default.
- Shop: technical summaries + suggested test plans; cites PID labels.
- Racing: latency-aware; suppress non-critical dialogs during recording.

## Consequences

- Navigation graph adapts visible destinations per persona (single NavHost, filtered routes).
- Feature flags centralized in `PersonaPolicy` — not scattered `if` in composables.
- Shop multi-vehicle data stays on-device unless user exports.

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| Separate apps per persona | F-Droid maintenance burden |
| Role-based auth server | Violates offline/local-first |
| Fixed UI for all | Shop and Racing needs unmet |
