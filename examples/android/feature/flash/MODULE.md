# Feature: flash (Stage A scaffold)

Vertical-slice marker for ECU programming. Domain logic lives in the app module under
`domain/flash` and `data/flash` until a full product flavor lands.

## Scope (Stage A)

- USB-C host / Simulated transport only (`FlashTransportPolicy`)
- `IsoTpLink` + `UdsProgrammingSession` + `FlashTransferEngine`
- `SecurityAccessPlugin` SPI + `FlashSecurityAssist` (match/explain only — no key cracking)
- Demo profile `demo-isotp-v1`; real vehicle = `[HUMAN]` bench

## Out of scope

- Bluetooth / MX flash
- OEM keys in APK
- Map editor (Stage D)
- Multi-make claims
