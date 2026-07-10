# Flash hardware recommendations

> **USB-C host only** for programming sessions. OBDLink **MX / Bluetooth is diagnostics and DID coding only** — never use it for ECU flash.

Stage A scaffolding uses Simulated transport in CI/demo. Real flash requires the kit below and a `[HUMAN]` non-customer vehicle bench before any public claim.

## Android device (required)

| Requirement | Spec |
|-------------|------|
| USB role | **USB host / OTG** (not charge-only) |
| Port | **USB-C** preferred |
| OS | Android **8.0+** (OBDForge API 26+); test on API 31+ for USB permission UX |
| Power | Phone/tablet that can **power the adapter** or use a powered hub; keep screen awake during flash |
| Storage | Enough free space for firmware file + audit log |
| Battery | Device >50% or plugged in; vehicle 12V supported with tender (below) |

**Recommended Android class:** recent Pixel / Samsung flagship or midrange with documented USB-C OTG host mode. Avoid cheap tablets that only support USB gadget/charge.

## OBD adapters (ranked)

### Tier 1 — preferred (USB, STN-class, Android-capable)

| Adapter | Interface | Notes |
|---------|-----------|--------|
| **OBDLink EX** | USB | STN; works with Android via **USB-C OTG**; best first choice for OBDForge USB path |
| **OBDLink SX** | USB | STN; Android via OTG; solid wired diagnostics; same OTG caveat |

Both need a **USB-C OTG data cable/adapter** (host mode). A plain USB-A→C charge cable will not work.

### Tier 2 — acceptable but not desirable

| Setup | Why demoted |
|-------|-------------|
| USB-A OBD adapter (EX/SX) + **USB-C OTG hub/dongle** | Extra failure point; hub power issues mid-transfer |
| OBDLink MX / MX+ Bluetooth | Fine for live data/coding; **forbidden for flash** (drop risk) |
| Generic ELM327 USB clones | Unreliable ISO-TP / timing; do not recommend for programming |
| Wi‑Fi / Ethernet STN | Prefer USB for transfer integrity; **not allowed** by `FlashTransportPolicy` |

### Tier 3 — pro / future (out of FOSS v1 unless separately scoped)

| Interface | Notes |
|-----------|--------|
| J2534 Pass-Thru + Android bridge | Shop-grade; often Windows-centric; not in current Android stack |
| DoIP (Ethernet) | Modern OEM flash path; would need new transport plugin |

## Cables and power (mandatory kit)

| Item | Role |
|------|------|
| **USB-C OTG data** cable/adapter (host) | Phone ↔ USB OBD adapter |
| Short, quality USB cable | Prefer direct USB-C–to–adapter if a native-C adapter exists; else C-OTG + adapter’s USB-A |
| **12V battery tender / maintainer** | Hold vehicle voltage during long writes |
| Ignition-on procedure card | User must not cycle key mid-transfer |

**Explicitly not recommended for flash:** Bluetooth-only MX; random Amazon “ELM327 USB-C” no-name sticks; charge-only OTG dongles.

## Safety / product notes

- Shop/Racing + expert unlock + brick-risk attestation required
- Default F-Droid build stays diagnostics-first until a separate `flash` product flavor ships
- Local AI may explain NRCs / match **installed** security plugins — never invent `27` keys

## Human bench checklist

1. USB adapter on phone via USB-C OTG (simulation off for real ECU)
2. Battery tender connected; voltage floor OK
3. Shop/Racing + expert unlock + brick-risk attestation
4. Demo profile `demo-isotp-v1` first, then **one** real ECU family under `[HUMAN]` approval
5. User-supplied `SecurityAccessPlugin` or manual key — no OEM keys in APK
6. Export audit log (file SHA-256 + command hashes only); update `docs/ADB_BENCH_RESULTS.md`

**Status:** Real-vehicle flash bench remains **`[HUMAN]` blocked** until the checklist above is completed on a non-customer vehicle.
