# Third-Party Licenses

> Generated and maintained per release. See pre-release gate in `docs/INITIALIZATION_PROMPT.md` Section 7a.

## Project License

This project is licensed under **GPL-3.0-or-later**. See [`LICENSE`](LICENSE).

## Bundled data

| Asset | Source | License |
|-------|--------|---------|
| `examples/android/app/src/main/assets/diagnostics/dtc_catalog.json` | [OBDex](https://github.com/foerbsnavi/OBDex) generic DTC dataset | [CC0-1.0](https://github.com/foerbsnavi/OBDex/blob/main/LICENSE-DATA) |
| `examples/android/app/src/main/assets/diagnostics/pid_ranges.json` | [OBDex](https://github.com/foerbsnavi/OBDex) Mode 01 PID ranges | [CC0-1.0](https://github.com/foerbsnavi/OBDex/blob/main/LICENSE-DATA) |

Regenerate via `python3 scripts/import-obdex-data.py`. See [`docs/DIAGNOSTIC_DATA.md`](docs/DIAGNOSTIC_DATA.md).

## Dependencies

Run license audits for active stacks:

```bash
# Web (npm)
cd examples/web && npx license-checker --production --summary

# Python (pip)
cd examples/python && uv run pip-licenses --format=markdown

# Rust / Go (optional stacks — MIT stubs; expand when deps are added)
grep 'license' examples/rust/Cargo.toml
head -1 examples/go/go.mod
```

`[AUTO]` CI runs `scripts/check-license-compliance.sh` on each push.

## Attribution

When bundling dependencies in releases (APK, desktop binary, etc.), include
this file or a generated `NOTICE` file in the distribution artifact.

## Incompatible Licenses

`[HUMAN]` must approve any dependency with copyleft licenses (GPL, AGPL) that
may affect distribution. Document exceptions in `DECISION_LOG.md`.
