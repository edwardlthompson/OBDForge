# Adapter compatibility matrix

Known-working and explicitly **untested** OBD adapter combinations for OBDForge.
Status reflects automated CI + documented benches (`docs/ADB_BENCH_RESULTS.md`, `docs/FLASH_HARDWARE.md`). Physical adapter rows remain untested until an `[ADB]` phone-connected session is logged.

Legend: **OK** = exercised in demo/CI or documented as supported path · **Untested** = code path exists, no logged hardware pass · **Blocked** = policy or hardware forbids.

## Matrix

| Chip / family | Transport | Firmware / probe | Live data / DTC | DID coding | ECU flash | Status |
|---------------|-----------|------------------|-----------------|------------|-----------|--------|
| Simulated (canned) | Simulated | N/A (fixtures) | OK (CI + demo) | OK (demo) | OK (demo ISO-TP only) | OK |
| ELM327 clone | Bluetooth SPP | Generic AT / `ATSP0` | Untested | Untested | Blocked (BT) | Untested |
| ELM327 clone | USB serial | Generic AT | Untested | Untested | Untested | Untested |
| ELM327 clone | Wi‑Fi / Ethernet TCP | Generic AT | Untested | Untested | Blocked (policy) | Untested |
| OBDLink STN11xx/22xx | Bluetooth Classic SPP | STN / STPX | Untested (MX intended) | Untested | Blocked (BT) | Untested |
| OBDLink MX / MX+ | Bluetooth Classic SPP | STN | Untested | Untested | Blocked — diagnostics only | Untested |
| OBDLink MX / MX+ | BLE GATT | STN | Untested | Untested | Blocked | Untested |
| OBDLink EX | USB-C OTG host | STN | Untested | Untested | Untested (preferred flash path) | Untested |
| OBDLink SX | USB-C OTG host | STN | Untested | Untested | Untested | Untested |
| Generic STN USB | USB serial | STN | Untested | Untested | Untested | Untested |
| Wi‑Fi STN AP | Wi‑Fi TCP | STN | Untested | Untested | Blocked (policy) | Untested |
| J2534 / DoIP | — | OEM | — | — | — | Untested (out of v1 scope) |

## Notes

- **Bus selection** is adapter firmware (`ATSP0` auto). OBDForge does not claim every OBD-II vehicle.
- **OBDLink MX** must use Classic SPP for diagnostics; close the vendor OBDLink app before connecting (KB-016).
- **Flash** is USB host (`UsbSerial`) or Simulated only — see `FlashTransportPolicy` and `docs/FLASH_HARDWARE.md`.
- Update this table when `[ADB]` bench results land in `docs/ADB_BENCH_RESULTS.md`.
