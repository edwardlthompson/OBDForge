# Privacy Policy

> OBDForge — local-first Android diagnostics. No mandatory cloud. GPL-3.0-or-later.

## Summary

OBDForge stores vehicle and session data **on your device**. We do not operate a backend that collects diagnostics. Optional update checks contact GitHub Releases only. Local AI runs entirely on-device.

## Data We Collect (on device)

| Data | Purpose | Storage | Retention |
|------|---------|---------|-----------|
| VIN / vehicle profile | Session identity, shop work orders | Room (local) | Until user deletes |
| DTCs, freeze frames, PID logs | Diagnostics history | Room (local) | User-configurable |
| Safety audit log | ECU write accountability | Room (local) | Default 90 days |
| App settings (theme, persona, expert mode) | Preferences | DataStore | Until app uninstall |
| Adapter identifiers (hashed) | Last-used transport | DataStore | Until app uninstall |

## Data We Do Not Collect

- No analytics or tracking SDKs
- No sale of personal data
- No mandatory account or cloud sync
- No upload of VIN/DTCs to OBDForge servers (none exist)
- No cloud AI inference — MediaPipe LLM runs locally

## VIN Privacy

- VIN is **personally identifiable** in many jurisdictions (links owner to vehicle).
- ECU-first resolution preferred; provenance shown in UI (ADR-0005).
- VIN excluded from logcat, crash reports, and default audit exports.
- User may opt in to include VIN in manual export files.
- Barcode scan processed on-device; no image upload.

## App Update Checks

- Source: GitHub Releases API or configured manifest URL
- Transmitted: app version, artifact format (`apk`) — **no PII**
- Stored locally: `last_checked`, `installed_artifact_format`, check interval
- F-Droid builds: informational update only (store listing)

## Local AI

- DTC text and selected PIDs may be passed to on-device model for explanations.
- No prompts sent to third-party cloud services.
- User can disable AI features in Settings.

## Permissions

| Permission | Use |
|------------|-----|
| Bluetooth | OBD adapter connection |
| USB host | Wired adapters |
| Camera | VIN barcode scan (optional) |
| Network | WiFi/Ethernet adapters, update check |

## User Rights (GDPR / CCPA)

- **Access:** Export session JSON / audit CSV from app
- **Deletion:** Clear app data or uninstall
- **Opt-out:** No telemetry to opt out of; disable update check and AI separately
- **Portability:** Export features in Shop persona

## F-Droid Reproducible Builds

- Builds are reproducible per F-Droid policy (`SOURCE_DATE_EPOCH`, pinned deps).
- Verify locally: `bash scripts/verify-reproducible-apk.sh`
- Anti-features declared in metadata: Network (adapter/update only), no Ads, no Tracking

## DPIA Checklist (`[HUMAN]`)

If distributing in EU with shop features processing customer VIN:

- 🔲 Document processing purpose (vehicle repair records)
- 🔲 Assess necessity of VIN retention period
- 🔲 Identify risks (device theft → data exposure) and mitigations (OS disk encryption)
- 🔲 Record in `DECISION_LOG.md`

## Contact

Privacy inquiries: see maintainers in `.github/CODEOWNERS` or `SECURITY.md`.
