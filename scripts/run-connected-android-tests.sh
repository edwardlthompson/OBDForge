#!/usr/bin/env bash
# Foss + full instrumented tests. Invoked as a single emulator-runner script line.
set -u
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/examples/android"
chmod +x gradlew
STATUS=0
./gradlew connectedFossDebugAndroidTest connectedFullDebugAndroidTest --no-daemon || STATUS=$?
python3 - <<'PY'
import xml.etree.ElementTree as ET
from pathlib import Path
root = Path("app/build/outputs/androidTest-results")
print("Instrumented results:")
if not root.exists():
    print("(no androidTest-results dir)")
for xml in root.rglob("*.xml"):
    try:
        suite = ET.parse(xml).getroot()
    except ET.ParseError:
        continue
    for case in suite.iter("testcase"):
        err = case.find("failure")
        if err is None:
            err = case.find("error")
        if err is None:
            continue
        msg = (err.get("message") or err.text or "").strip().splitlines()[:8]
        print(f"FAIL {case.get('classname')}#{case.get('name')}")
        print("\n".join(msg))
PY
exit "$STATUS"
