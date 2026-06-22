#!/usr/bin/env python3
"""Import Wal33D manufacturer DTC overlay into compact Android assets.

Source: https://github.com/Wal33D/dtc-database (MIT)
Extracts manufacturer-specific rows (is_generic=0) from data/dtc_codes.db.
"""
from __future__ import annotations

import gzip
import json
import re
import sqlite3
import sys
import tempfile
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "examples" / "android" / "app" / "src" / "main" / "assets" / "diagnostics"
DB_URL = "https://raw.githubusercontent.com/Wal33D/dtc-database/main/data/dtc_codes.db"
MAX_SUMMARY = 480


def fetch_db() -> Path:
    tmp = Path(tempfile.gettempdir()) / "wal33d_dtc_codes.db"
    print(f"Downloading {DB_URL} ...")
    req = urllib.request.Request(DB_URL, headers={"User-Agent": "OBDForge-import-wal33d/1.0"})
    with urllib.request.urlopen(req, timeout=300) as response:
        tmp.write_bytes(response.read())
    print(f"Downloaded {tmp.stat().st_size // (1024 * 1024)} MiB")
    return tmp


def compact_summary(text: str) -> str:
    cleaned = re.sub(r"\s+", " ", text).strip()
    if len(cleaned) > MAX_SUMMARY:
        return cleaned[: MAX_SUMMARY - 3] + "..."
    return cleaned


def type_category(code_type: str) -> str:
    return {
        "P": "powertrain",
        "B": "body",
        "C": "chassis",
        "U": "network",
    }.get(code_type.upper(), "unknown")


def main() -> int:
    OUT.mkdir(parents=True, exist_ok=True)
    db_path = fetch_db()
    conn = sqlite3.connect(db_path)
    cursor = conn.execute(
        """
        SELECT code, manufacturer, description, type
        FROM dtc_definitions
        WHERE is_generic = 0 AND locale = 'en'
        ORDER BY manufacturer, code
        """
    )
    compact: list[dict] = []
    manufacturers: set[str] = set()
    for code, manufacturer, description, code_type in cursor:
        mfr = str(manufacturer).upper().strip()
        manufacturers.add(mfr)
        compact.append(
            {
                "c": str(code).upper(),
                "m": mfr,
                "t": compact_summary(str(description)),
                "s": compact_summary(str(description)),
                "g": type_category(str(code_type)),
                "v": "unknown",
            }
        )
    conn.close()

    out_path = OUT / "dtc_manufacturer_overlay.json.gz"
    raw = json.dumps(compact, separators=(",", ":")).encode("utf-8")
    out_path.write_bytes(gzip.compress(raw, compresslevel=9))
    gzip_kb = out_path.stat().st_size // 1024
    raw_kb = len(raw) // 1024
    print(
        f"Wrote {len(compact)} manufacturer entries "
        f"({len(manufacturers)} makes) -> {out_path} "
        f"({gzip_kb} KiB gzip, {raw_kb} KiB raw)",
    )
    if gzip_kb > 500:
        print(f"WARN: gzip size {gzip_kb} KiB exceeds 500 KiB repo gate", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
