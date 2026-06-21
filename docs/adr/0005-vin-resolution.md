# ADR-0005: ECU-First VIN Resolution

- **Status:** Accepted
- **Date:** 2026-06-21
- **Deciders:** OBDForge team

## Context

Vehicle identity drives shop workflow, AI context, and parts lookup. VIN from windshield/barcode is error-prone; ECU-reported VIN is authoritative when available. Multiple buses and protocols expose VIN differently.

## Decision

Implement **`VinResolver`** with an **ECU-first resolution chain**, falling back to barcode/manual entry.

### Resolution order

| Step | Source | Method |
|------|--------|--------|
| 1 | OBD Mode 09 PID 02 | Standard legislated VIN |
| 2 | UDS ReadDataByIdentifier | DID `F190` (ISO-TP) |
| 3 | KWP2000 | OEM-specific VIN request where mapped |
| 4 | SAE J1939 | PGN field for commercial vehicles |
| 5 | Barcode / QR scan | CameraX + ML Kit barcode (on-device) |
| 6 | Manual entry | Validated 17-char ISO 3779 check digit |

### Validation

- Normalize: uppercase, strip spaces, reject invalid charset (I/O/Q).
- Verify check digit (position 9) when 17 characters present.
- Cache successful ECU VIN in Room keyed by session + adapter id hash (not raw MAC).

### Privacy

- VIN stored locally only; excluded from crash logs and default AI cloud context (N/A — on-device AI).
- Export and audit redaction per `docs/PRIVACY.md`.

## Consequences

- `ResolveVinUseCase` orchestrates chain; UI shows provenance badge (ECU vs barcode vs manual).
- Barcode fallback does not skip ECU attempt when adapter connected.
- Feature spec: `docs/features/vin-resolution.md`.

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| Barcode-first | ECU mismatch common in replaced modules |
| NHTSA API lookup only | Requires network; wrong for non-US |
| VIN optional everywhere | Breaks shop work order integrity |
