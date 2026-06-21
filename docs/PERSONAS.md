# Persona Matrix

> Concise capability matrix for OBDForge persona modes. See ADR-0004 for workflow details.

## Persona overview

| Persona | Target user | Default theme | Expert mode |
|---------|-------------|---------------|-------------|
| DIY | Vehicle owner | Material You (system) | Off |
| Semi-pro | Enthusiast | Garage Classic | Opt-in |
| Shop | Technician | Garage Pro (high contrast) | After unlock |
| Racing | Track crew | Garage Night | After unlock |

## Feature access

| Feature | DIY | Semi-pro | Shop | Racing |
|---------|-----|----------|------|--------|
| Read DTCs | ✅ | ✅ | ✅ | ✅ |
| Clear DTCs | ✅ (confirm) | ✅ | ✅ (interlock) | ✅ (interlock) |
| Live PID dashboard | Basic set | Custom layouts | Multi-pane | High-rate |
| Data logging | — | ✅ | ✅ | ✅ (lap markers) |
| Bidirectional tests | — | Limited | ✅ | ✅ |
| ECU writes / coding | — | — | ✅ (interlock) | ✅ (interlock) |
| Work orders | — | — | ✅ | — |
| Audit log export | — | — | ✅ | Optional |
| VIN resolve (ECU-first) | ✅ | ✅ | ✅ | ✅ |
| Local AI assistant | Plain language | Technical | Shop briefs | Minimal UI |
| Demo mode | ✅ | ✅ | ✅ | ✅ |

## Safety and interlocks

| Interlock | DIY | Semi-pro | Shop | Racing |
|-----------|-----|----------|------|--------|
| Stationary vehicle check | Clear DTCs | Writes/tests | All writes | All writes |
| Expert mode PIN | — | Optional | Required | Required |
| Confirm destructive action | Clear DTCs | Tests | All writes | All writes |
| Session audit entry | — | Writes | All writes | All writes |

## UI density

| Surface | DIY | Semi-pro | Shop | Racing |
|---------|-----|----------|------|--------|
| Navigation items | 4–5 | 6–8 | 10+ | 5 (focused) |
| PID cards | Large | Medium | Compact grid | Strip chart |
| Settings depth | Minimal | Standard | Full | Racing-only |
| Help / AI prominence | High | Medium | Low | Low |

## Transport and protocol

| Capability | DIY | Semi-pro | Shop | Racing |
|------------|-----|----------|------|--------|
| BT / USB / WiFi / Ethernet | ✅ | ✅ | ✅ | ✅ |
| Adapter auto-detect | ✅ | ✅ | ✅ | ✅ |
| Manual protocol override | — | ✅ | ✅ | ✅ |
| OBDLink STN/STPX fast path | Auto | Auto | Auto | Forced prefer |

## Switching personas

- Change in **Settings → Persona**; requires idle session (no active logging).
- Switching from Shop/Racing to DIY prompts audit log retention notice.
- Demo mode uses DIY defaults unless overridden in developer settings.
