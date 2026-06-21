# ADR-0003: Safety Interlocks and Audit Log

- **Status:** Accepted
- **Date:** 2026-06-21
- **Deciders:** OBDForge team

## Context

Bidirectional OBD/UDS commands (actuator tests, coding, clear adaptations) can damage vehicles or create liability if misused. OBDForge targets DIY through shop personas with different risk tolerance.

## Decision

### Safety interlocks (mandatory before ECU writes)

1. **Vehicle stationary** — speed PID ≈ 0 (or user attestation in demo mode).
2. **Parking brake / gear interlock** — where PIDs available; otherwise explicit checklist.
3. **Adapter connected and protocol probed** — no writes on failed probe.
4. **Expert mode unlock** — time-limited PIN or biometric + `[HUMAN]` documented default-off.
5. **Confirm dialog** — shows command summary, affected ECU, persona-specific warnings.
6. **Rate limiting** — max N write commands per session without re-confirmation.

### Persona defaults

| Persona | Bidirectional writes | Expert mode |
|---------|---------------------|-------------|
| DIY | Blocked | Off |
| Semi-pro | Read-only + limited tests | Opt-in |
| Shop | Full with interlocks | On after unlock |
| Racing | Full with interlocks | On after unlock |

### Audit log

- **Room table `audit_log`:** timestamp, persona, protocol id, command hash, result, user note (optional).
- Local-only; export via share sheet as JSON/CSV on user request.
- Never upload automatically; VIN redacted in exports unless user opts in.
- Retention: user-configurable (30/90/365 days); default 90 days.

## Consequences

- Use cases for writes call `SafetyGate.evaluate()` before transport I/O.
- UI cannot bypass gate — ViewModels route through `SafetyGateUseCase`.
- F-Droid anti-feature text documents vehicle interaction risk.

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| No interlocks (Torque-like) | Liability and ADR-0004 shop trust requirements |
| Cloud audit | Violates local-first privacy |
| Reversible undo for ECU writes | Not technically feasible on most ECUs |
