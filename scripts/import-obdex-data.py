#!/usr/bin/env python3
"""Import CC0 OBDex DTC + PID range data into Android assets.

Source: https://github.com/foerbsnavi/OBDex (data: CC0-1.0)
"""
from __future__ import annotations

import gzip
import json
import re
import sys
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "examples" / "android" / "app" / "src" / "main" / "assets" / "diagnostics"

GENERIC_URL = "https://foerbsnavi.github.io/OBDex/generic.json"
PIDS_URL = "https://foerbsnavi.github.io/OBDex/pids/mode01.min.json"


def fetch_json(url: str) -> object:
    with urllib.request.urlopen(url, timeout=120) as response:
        return json.load(response)


def severity(entry: dict) -> str:
    difficulty = (entry.get("repair") or {}).get("difficulty", "unknown")
    return {
        "easy": "low",
        "medium": "medium",
        "hard": "high",
        "expert": "high",
    }.get(difficulty, "unknown")


def compact_dtc(entry: dict) -> dict:
    title = (entry.get("title") or {}).get("en") or entry["code"]
    description = (entry.get("description") or {}).get("en") or ""
    description = re.sub(r"\s+", " ", description).strip()
    if len(description) > 480:
        description = description[:477] + "..."
    return {
        "c": entry["code"],
        "t": title,
        "s": description,
        "g": entry.get("category") or "unknown",
        "v": severity(entry),
    }


def compact_pid_ranges(pids: list) -> list:
    out: list[dict] = []
    for pid_entry in pids:
        rng = pid_entry.get("range")
        if not isinstance(rng, list) or len(rng) < 2:
            continue
        pid_str = str(pid_entry.get("pid", ""))
        if not pid_str:
            continue
        pid = int(pid_str, 16)
        unit = pid_entry.get("unit")
        if isinstance(unit, dict):
            unit = unit.get("en", "")
        name = (pid_entry.get("name") or {}).get("en") or ""
        out.append(
            {
                "pid": pid,
                "min": rng[0],
                "max": rng[1],
                "unit": unit or "",
                "name": name,
            }
        )
    return out


def main() -> int:
    OUT.mkdir(parents=True, exist_ok=True)
    print(f"Fetching {GENERIC_URL} ...")
    generic = fetch_json(GENERIC_URL)
    dtc_compact = [compact_dtc(entry) for entry in generic]
    dtc_path = OUT / "dtc_catalog.json.gz"
    dtc_bytes = json.dumps(dtc_compact, separators=(",", ":")).encode("utf-8")
    dtc_path.write_bytes(gzip.compress(dtc_bytes, compresslevel=9))
    print(
        f"Wrote {len(dtc_compact)} DTCs -> {dtc_path} "
        f"({dtc_path.stat().st_size // 1024} KiB gzip, {len(dtc_bytes) // 1024} KiB raw)",
    )

    print(f"Fetching {PIDS_URL} ...")
    pids = fetch_json(PIDS_URL)
    ranges = compact_pid_ranges(pids)
    pid_path = OUT / "pid_ranges.json"
    pid_path.write_text(json.dumps(ranges, separators=(",", ":")), encoding="utf-8")
    print(f"Wrote {len(ranges)} PID ranges -> {pid_path} ({pid_path.stat().st_size // 1024} KiB)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
