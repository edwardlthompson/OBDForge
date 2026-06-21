# ADR-0001: Core Application Architecture

- **Status:** Accepted
- **Date:** 2026-06-21
- **Deciders:** OBDForge team

## Context

OBDForge is a long-lived Android diagnostics product with multiple transports, protocol plugins, safety interlocks, and on-device AI. The Golden Path must stay testable without hardware and comply with F-Droid FOSS constraints.

## Decision

**Selected pattern:** Clean Architecture (layered, dependency rule inward)

### Layers

| Layer | Responsibility | Examples |
|-------|----------------|----------|
| **Domain** | Entities, use cases, repository interfaces | `ObdSession`, `ReadPidUseCase`, `Transport` port |
| **Data** | Room DAOs, adapter I/O, protocol implementations | `SessionRepository`, `Elm327Protocol`, `AuditLogDao` |
| **UI** | Compose screens, ViewModels | `LiveDataScreen`, `ConnectViewModel` |

### Core registries

- **`TransportRegistry`** — registers BT/USB/WiFi/Ethernet transport implementations; selects by user preference and hardware availability.
- **`ProtocolRegistry`** — maps detected adapter family + vehicle bus to a `DiagnosticProtocol` implementation (ELM327, STN, STPX, UDS helpers).

### Persistence

- **Room** for sessions, DTC snapshots, freeze frames, safety audit log, and cached VIN decode metadata.
- **DataStore** for user preferences (theme, persona, expert mode, update interval).

### Demo mode

- Deterministic **mock transport + mock protocol** wired through the same use-case layer.
- Enables CI unit tests, UI previews, and F-Droid smoke without OBD hardware.
- Demo data fixtures live under `examples/android/.../demo/`; never bypass safety interlocks in demo — simulate confirmations instead.

## Consequences

- Golden Path features respect layer boundaries; ViewModels call use cases only.
- Protocol and transport additions are registry plugins, not UI conditionals.
- Changing this ADR requires a new ADR and `[HUMAN]` approval.

## Alternatives Considered

| Pattern | Rejected because |
|---------|------------------|
| MVVM-only (no use cases) | Protocol/transport logic would leak into ViewModels |
| Monolith Activity | Untestable; violates file-size limits |
| Hexagonal-only naming | Clean Architecture maps cleanly to Android module layout already used in template |
