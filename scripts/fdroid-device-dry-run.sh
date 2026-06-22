#!/usr/bin/env bash
# F-Droid device dry-run: metadata gate + signed release APK install + smoke launch + logcat scan.
# Usage: scripts/fdroid-device-dry-run.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

ADB="${ADB:-adb}"
LAUNCHER="dev.foss.obdforge/dev.foss.obdforge.MainActivity"
APK_DIR="$ROOT/examples/android/app/build/outputs/apk/release"
UNSIGNED="$APK_DIR/app-release-unsigned.apk"
SIGNED="$APK_DIR/app-release-adb-smoke.apk"
LOG="/tmp/fdroid-dry-run-logcat-$$.txt"

if ! command -v "$ADB" >/dev/null 2>&1; then
  if [ -x "${LOCALAPPDATA:-}/Android/Sdk/platform-tools/adb.exe" ]; then
    ADB="${LOCALAPPDATA}/Android/Sdk/platform-tools/adb.exe"
  else
    echo "ERROR: adb not found"
    exit 1
  fi
fi

echo "=== F-Droid metadata ==="
bash scripts/verify-fdroid-metadata.sh

DEVICES="$("$ADB" devices | awk 'NR>1 && $2=="device"{print $1}')"
if [ -z "$DEVICES" ]; then
  echo "ERROR: no authorized adb device"
  "$ADB" devices -l
  exit 1
fi
echo "OK   device: $(echo "$DEVICES" | head -1)"

if [ ! -f "$UNSIGNED" ]; then
  export SOURCE_DATE_EPOCH="${SOURCE_DATE_EPOCH:-1700000000}"
  bash scripts/build-release-apk.sh --clean
fi

bash scripts/sign-apk-debug.sh "$UNSIGNED" "$SIGNED"
echo "OK   APK: $SIGNED"

"$ADB" logcat -c || true
"$ADB" install -r "$SIGNED"
"$ADB" shell am start -W -n "$LAUNCHER"

sleep 5
"$ADB" logcat -d > "$LOG" || true

if ! "$ADB" shell dumpsys window | grep -q "dev.foss.obdforge"; then
  echo "FAIL: app not in foreground after launch"
  exit 1
fi

if grep -E 'FATAL EXCEPTION' "$LOG" >/dev/null 2>&1; then
  echo "FAIL: crash signatures in logcat"
  grep -E 'FATAL EXCEPTION' "$LOG" | tail -20
  exit 1
fi

echo "OK   no FATAL EXCEPTION in logcat (saved: $LOG)"
echo "F-Droid device dry-run passed"
