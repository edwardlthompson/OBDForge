# Feature: VIN Resolution

> ECU-first VIN resolution per ADR-0005. Checklist: 🔲 open · ✅ done · ❌ blocked

## Acceptance criteria

- 🔲 **ECU-first chain:** Mode 09 PID 02 → UDS F190 → KWP OEM map → J1939 PGN → barcode → manual
- 🔲 **Provenance:** UI shows source badge (ECU / barcode / manual) on vehicle profile
- 🔲 **Validation:** 17-char ISO 3779 normalize + check digit verify
- 🔲 **Privacy:** VIN not in logs; Room encrypt-at-rest optional future; export redaction default on
- 🔲 **Offline:** Barcode + manual work without adapter; ECU paths require active session
- 🔲 **Shop:** Resolved VIN links to work order and session history
- 🔲 **i18n:** keys under `vin.*` in `strings.xml`

## Smoke scenario

1. _Given_ adapter connected to bench ECU with known VIN
2. _When_ user opens Shop intake and taps **Resolve VIN**
3. _Then_ ECU VIN appears within 10s with **ECU** badge; work order pre-filled

**Barcode fallback:**

1. _Given_ adapter disconnected
2. _When_ user scans VIN barcode
3. _Then_ validated VIN saved with **Barcode** badge

## Container map

| Layer | Path |
|-------|------|
| Domain | `domain/vin/ResolveVinUseCase.kt`, `VinValidator.kt` |
| Data | `data/vin/VinResolver.kt`, `EcuVinDataSource.kt`, `BarcodeVinDataSource.kt` |
| Protocol | `protocol/*/Mode09VinReader.kt`, `UdsF190Reader.kt`, `J1939VinReader.kt` |
| UI | `ui/vin/VinResolveScreen.kt`, `VinBadge.kt` |
| DB | `data/db/VehicleProfileEntity.kt` |
| Tests | `test/.../vin/VinValidatorTest.kt`, transcript fixtures |

## Resolution algorithm

```text
ResolveVin(session):
  if session.transport.connected:
    for step in [Mode09, UdsF190, KwpVin, J1939Vin]:
      vin = step.tryRead(session)
      if vin.valid(): return Result(vin, source=step)
  if cameraPermission:
    vin = barcodeScanner.scan()
    if vin.valid(): return Result(vin, BARCODE)
  return PromptManualEntry()
```

## Error behavior

| Condition | Behavior |
|-----------|----------|
| ECU returns partial VIN | Retry once; show hex debug in Semi-pro+ only |
| Mode 09 unsupported | Skip to UDS F190 without error toast |
| Check digit fail | Block save; show validation message |
| Duplicate VIN different source | Prefer ECU; log conflict in audit (no auto overwrite) |

## Definition of Done

- Unit tests: validator, chain order, mock ECU fixtures
- `[ADB]` bench: Mode 09 success on ≥1 vehicle
- Shop workflow links VIN to `VehicleProfileEntity`
- Documented in `docs/PRIVACY.md` (VIN as PII)

## Notes

- Camera: CameraX + ML Kit barcode (on-device, FOSS-compatible deps only)
- Manual entry: disable autocorrect; uppercase transform on blur
- Racing persona: minimal UI — VIN resolve from connect screen overflow menu
