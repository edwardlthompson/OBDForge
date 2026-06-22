# Threat Model

> OBDForge Android diagnostics — STRIDE + MASVS. Link mitigations to `BUILD_PLAN.md` and ADRs.

## Scope

| Item | Value |
|------|-------|
| Project | OBDForge |
| Stack | Android (Kotlin, Compose, Room) — `examples/android/` |
| Methodology | STRIDE; OWASP MASVS L1; vehicle safety overlay (ADR-0003) |

## Trust Boundaries

```text
[User] --> [OBDForge App] --> [Local Room / DataStore]
                |
                v
         [OBD Adapter] --> [Vehicle ECU / CAN bus]
                |
    (Untrusted: clone ELM firmware, malicious WiFi AP,
     USB gadget, barcode payload, prompt injection to local AI)
```

| Boundary | Trust level | Notes |
|----------|-------------|-------|
| App process | Trusted (FOSS build) | Reproducible APK; no proprietary SDKs |
| OBD adapter | **Untrusted** | Treat responses as hostile input |
| Vehicle ECU | Sensitive | Writes can cause damage — SafetyGate required |
| Local AI model | Trusted if bundled | Prompt injection from DTC text — sanitize |
| Network (update check) | Semi-trusted | GitHub Releases only; TLS; no PII |

## OBD Adapter Trust

- **Validate all adapter I/O:** length limits, timeout, charset, no command injection via VIN/barcode fields echoed to AT commands.
- **Never auto-execute** adapter-init strings from QR codes or NFC.
- **WiFi adapters:** warn on cleartext TCP; prefer user-confirmed SSID; no credential storage in logs.
- **USB:** VID/PID allowlist; reject unexpected composite interfaces.
- **Clone ELM327:** probe before extended STN commands; fail closed on ambiguous responses.

## STRIDE Summary

| Threat | Example | Mitigation | Owner |
|--------|---------|------------|-------|
| Spoofing | Rogue WiFi OBD AP | User confirmation, cert pinning N/A (local TCP); SSID display | AGENT |
| Tampering | Malicious adapter frames | Parse defensively; checksum where applicable | AGENT |
| Repudiation | Denied ECU write | Local audit log (ADR-0003) | AGENT |
| Information disclosure | VIN in logcat | Redact VIN; no PII in logs — `docs/PRIVACY.md` | AGENT |
| Denial of service | Adapter flood | Read timeouts, circuit breaker, cancel coroutines | AGENT |
| Elevation of privilege | Bypass expert mode | SafetyGate in use case layer; not UI-only | AGENT |

## Top Abuse Cases

1. **Malicious adapter → arbitrary AT/UDS commands** — SafetyGate + persona policy; expert mode required for writes.
2. **VIN exfiltration via export malware** — user-initiated export only; no background upload.
3. **Prompt injection via DTC description → local AI** — system prompt hardening; no tool execution from AI output.
4. **Supply-chain: tampered APK** — reproducible builds, F-Droid signature, GitHub release checksums.
5. **Bidirectional misuse on moving vehicle** — speed PID interlock + racing persona still requires stationary for writes.
6. **Barcode VIN swap attack** — ECU-first chain; conflict warning when barcode ≠ ECU.

## Security Tasks

| Task | BUILD_PLAN | Status |
|------|------------|--------|
| SafetyGate + audit | Sprint 8 | ✅ |
| Adapter input validation | Sprint 2–3 | 🔲 |
| VIN privacy | Sprint 10 + PRIVACY.md | 🔲 |
| Reproducible APK | Sprint 13 | 🔲 |
| Weekly CVE triage | Ongoing | 🔲 |

## Review Cadence

- `[HUMAN]` Review at each milestone boundary (M1–M13)
- `[AGENT]` Update when transport/protocol/data flows change (reference ADR)
- `[ADB]` Annual bench retest with known-good and known-bad adapters
