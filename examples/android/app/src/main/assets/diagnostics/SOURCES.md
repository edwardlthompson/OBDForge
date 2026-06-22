# Diagnostic data sources

## OBDex (imported)

| Asset | Source | License |
|-------|--------|---------|
| `dtc_catalog.json.gz` | [OBDex generic.json](https://foerbsnavi.github.io/OBDex/generic.json) | [CC0-1.0](https://github.com/foerbsnavi/OBDex/blob/main/LICENSE-DATA) |
| `dtc_manufacturer_overlay.json.gz` | [Wal33D dtc-database](https://github.com/Wal33D/dtc-database) | [MIT](https://github.com/Wal33D/dtc-database/blob/main/LICENSE) |
| `pid_ranges.json` | [OBDex mode01 PIDs](https://foerbsnavi.github.io/OBDex/pids/mode01.min.json) | [CC0-1.0](https://github.com/foerbsnavi/OBDex/blob/main/LICENSE-DATA) |

Regenerate:

```bash
python3 scripts/import-obdex-data.py
python3 scripts/import-wal33d-data.py
```

Compact JSON (gzip-shipped as `dtc_catalog.json.gz`; Android Gradle decompresses to `dtc_catalog.json` at build time) keeps English title, summary, category, and severity hint per code (~9.5k generic SAE J2012 entries).
