#!/usr/bin/env python3
"""Fetch Gemma 3 1B IT INT4 MediaPipe model for optional on-device DTC explanations.

Source: https://huggingface.co/litert-community/Gemma3-1B-IT (Gemma license)
Output: examples/android/app/src/main/assets/ai/llm_model.task (gitignored, ~530 MB)
"""
from __future__ import annotations

import sys
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT_DIR = ROOT / "examples" / "android" / "app" / "src" / "main" / "assets" / "ai"
OUT_FILE = OUT_DIR / "llm_model.task"
URL = (
    "https://huggingface.co/litert-community/Gemma3-1B-IT/"
    "resolve/main/gemma3-1b-it-int4.task"
)


def main() -> int:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    print(f"Downloading {URL} ...")
    req = urllib.request.Request(URL, headers={"User-Agent": "OBDForge-fetch-llm-model/1.0"})
    with urllib.request.urlopen(req, timeout=600) as response:
        data = response.read()
    OUT_FILE.write_bytes(data)
    print(f"Wrote {OUT_FILE} ({len(data) // (1024 * 1024)} MiB)")
    print("Rebuild the app to bundle the model, or use in-app download on device.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
