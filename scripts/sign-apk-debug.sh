#!/usr/bin/env bash
# Sign an unsigned release APK with the local debug keystore for ADB device smoke tests.
# Usage: scripts/sign-apk-debug.sh INPUT_APK [OUTPUT_APK]
set -euo pipefail

INPUT="${1:-}"
OUTPUT="${2:-}"

if [ -z "$INPUT" ] || [ ! -f "$INPUT" ]; then
  echo "Usage: scripts/sign-apk-debug.sh INPUT_APK [OUTPUT_APK]"
  exit 1
fi

if [ -z "$OUTPUT" ]; then
  dir="$(dirname "$INPUT")"
  OUTPUT="${dir}/app-release-adb-smoke.apk"
fi

KS="${HOME:-/root}/.android/debug.keystore"
if [ ! -f "$KS" ]; then
  echo "ERROR: debug keystore not found at $KS"
  exit 1
fi

if [ -n "${LOCALAPPDATA:-}" ] && [ -d "${LOCALAPPDATA}/Android/Sdk/build-tools" ]; then
  BT="$(find "${LOCALAPPDATA}/Android/Sdk/build-tools" -maxdepth 1 -mindepth 1 -type d | sort -V | tail -1)"
else
  BT="$(find "${ANDROID_HOME:-/opt/android-sdk}/build-tools" -maxdepth 1 -mindepth 1 -type d 2>/dev/null | sort -V | tail -1 || true)"
fi

APKSIGNER="${BT}/apksigner"
[ -x "${APKSIGNER}.bat" ] && APKSIGNER="${APKSIGNER}.bat"
if [ ! -x "$APKSIGNER" ] && ! command -v apksigner >/dev/null 2>&1; then
  echo "ERROR: apksigner not found"
  exit 1
fi

if command -v apksigner >/dev/null 2>&1; then
  apksigner sign --ks "$KS" --ks-pass pass:android --key-pass pass:android --out "$OUTPUT" "$INPUT"
else
  "$APKSIGNER" sign --ks "$KS" --ks-pass pass:android --key-pass pass:android --out "$OUTPUT" "$INPUT"
fi

echo "Signed APK: $OUTPUT"
