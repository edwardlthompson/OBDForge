#!/usr/bin/env bash
# Build reproducible release APK with pinned SOURCE_DATE_EPOCH (F-Droid parity).
# Usage: scripts/build-release-apk.sh [--clean] [--sign]
# Env: SOURCE_DATE_EPOCH (default 1700000000); OBDFORGE_FLAVOR=full|foss (default full)
#      signing env for --sign (see sign-release-apk.sh)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=lib/android-flavor.sh
. "$ROOT/scripts/lib/android-flavor.sh"
ANDROID="$ROOT/examples/android"
SOURCE_DATE_EPOCH="${SOURCE_DATE_EPOCH:-1700000000}"
CLEAN=""
SIGN=false

while [ $# -gt 0 ]; do
  case "$1" in
    --clean) CLEAN="clean "; shift ;;
    --sign) SIGN=true; shift ;;
    -h|--help)
      echo "Usage: scripts/build-release-apk.sh [--clean] [--sign]"
      echo "Env: SOURCE_DATE_EPOCH (default 1700000000); OBDFORGE_FLAVOR=full|foss (default full)"
      exit 0
      ;;
    *) echo "Unknown option: $1" >&2; exit 1 ;;
  esac
done

if [ ! -f "$ANDROID/gradlew" ]; then
  echo "ERROR: missing $ANDROID/gradlew"
  exit 1
fi

export SOURCE_DATE_EPOCH
cd "$ANDROID"
chmod +x gradlew

TASK="$(android_assemble_release_task)"
echo "Building ${OBDFORGE_FLAVOR} release APK (SOURCE_DATE_EPOCH=${SOURCE_DATE_EPOCH}, task=${TASK})..."
./gradlew ${CLEAN}${TASK} --no-daemon

APK_DIR="$(android_release_apk_dir "$ANDROID")"
APK="$(android_find_release_apk "$APK_DIR")"
if [ -z "$APK" ]; then
  echo "FAIL: no release APK found in $APK_DIR"
  exit 1
fi

echo "OK   Release APK: $APK"

if [ "$SIGN" = true ]; then
  bash "$ROOT/scripts/sign-release-apk.sh" "$APK"
fi
