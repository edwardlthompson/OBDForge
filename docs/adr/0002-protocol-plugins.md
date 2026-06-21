# ADR-0002: Diagnostic Protocol Plugins

- **Status:** Accepted
- **Date:** 2026-06-21
- **Deciders:** OBDForge team

## Context

OBDForge must support generic ELM327 clones, OBDLink STN/STPX adapters, and bus-specific behavior (ISO 9141, ISO 14230 KWP, ISO 15765 CAN, SAE J1939). Adapter capabilities vary widely; hard-coding protocol logic in UI or transport layers does not scale.

## Decision

Introduce a **`DiagnosticProtocol` SPI** (Service Provider Interface) registered in **`ProtocolRegistry`**.

### SPI contract (conceptual)

```kotlin
interface DiagnosticProtocol {
    val id: ProtocolId
    suspend fun probe(transport: ObdTransport): ProbeResult
    suspend fun readPid(mode: ObdMode, pid: Int): PidResponse
    suspend fun readDtcs(): DtcList
    suspend fun clearDtcs(): Result<Unit>
    // Extended: UDS ReadDataByIdentifier, STN-specific commands
}
```

### Plugin implementations

| Plugin | Adapter target | Notes |
|--------|----------------|-------|
| `Elm327Protocol` | Generic clones | AT command subset; slow-path fallback |
| `StnProtocol` | OBDLink STN11xx/22xx | Native ST commands, faster streaming |
| `StpxProtocol` | OBDLink STPX | Extended headers, manufacturer modes |
| `UdsHelpers` | ISO-TP on CAN | Shared by VIN F190 path and bidirectional controls |
| `J1939Protocol` | Heavy-duty CAN | Mode not applicable; J1939 PGN reads |

### Registration and selection

1. Transport connects → adapter identification string parsed.
2. `ProtocolRegistry` ranks plugins by probe success and capability flags.
3. Selected plugin stored on session; user may override in Semi-pro/Racing persona (with warning).

### Testing

- Each plugin has pure unit tests with recorded adapter transcripts (fixtures).
- No plugin may import Compose or Android framework except via thin adapter shims in `data/`.

## Consequences

- New adapter families add a plugin + tests without changing use cases.
- Shared ISO-TP framing lives in one module consumed by UDS and OBD modes.
- Plugin mis-probe must fail closed (no silent wrong-bus commands).

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| Single mega ELM327 class | STN/STPX optimizations impossible; untestable |
| Transport-level AT commands | Duplicated across BT/USB/WiFi |
| Runtime downloadable plugins | F-Droid policy complexity; supply-chain risk |
