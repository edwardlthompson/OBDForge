# Diagnostic data — comparison and imports

## Pipeline comparison (open source OBD-II apps)

| Capability | OBDForge | [AndrOBD](https://github.com/fr3ts0n/AndrOBD) | [OBDex](https://github.com/foerbsnavi/OBDex) (data) |
|------------|----------|-----------------------------------------------|------------------------------------------------------|
| Transport | BT BLE+Classic, USB, TCP, simulated | BT, USB, Wi‑Fi | N/A (data only) |
| Protocol plugins | ELM327, STN, STPX | ELM327 + extensions | N/A |
| DTC read/clear | Mode 03/04 | Yes | N/A |
| DTC explanations | Asset catalog + optional on-device LLM | Built-in lookup | 9,533 generic codes (CC0) |
| Live PIDs | 11 hardcoded + PID 0x00 discovery | Large PID set + charts | 119 Mode 01 definitions |
| PID range checks | OBDex ranges + heuristics | Dashboard alarms (varies) | `range` per PID |
| Freeze frames | Stored in sessions | UI + export | N/A |
| CSV/session export | JSON + CSV sessions | CSV + plugins | N/A |
| Plugin ecosystem | No | MQTT, GPS, sensors | N/A |
| Manufacturer DTCs | Not yet | Partial via plugins | Generic only in OBDex |

### What was off in OBDForge (addressed)

1. **DTC catalog** — only 3 hardcoded codes vs thousands in peers → imported **OBDex generic** (9,533 codes, CC0).
2. **PID normal ranges** — 6 hand-tuned values vs standard tables → imported **95 OBDex Mode 01 ranges**.
3. **No import pipeline** — peers ship bundled DBs → added `scripts/import-obdex-data.py`.
4. **Addressed in Sprint 17:** PID 0x00 capability discovery, CSV session export.
5. **Still open vs AndrOBD:** charting plugins, manufacturer-specific DTC DB ([Wal33D/dtc-database](https://github.com/Wal33D/dtc-database), MIT, ~12k unique codes).

## Imported assets

| File | Source | License |
|------|--------|---------|
| `assets/diagnostics/dtc_catalog.json` | [OBDex generic.json](https://foerbsnavi.github.io/OBDex/generic.json) | CC0-1.0 |
| `assets/diagnostics/pid_ranges.json` | [OBDex mode01.min.json](https://foerbsnavi.github.io/OBDex/pids/mode01.min.json) | CC0-1.0 |

Regenerate:

```bash
python3 scripts/import-obdex-data.py
```

Loaded at app start in `ObdForgeCompositionRoot.create()`.
