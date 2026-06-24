#!/usr/bin/env bash
# Sign an unsigned release APK for sideload install (phones reject unsigned APKs).
# Prefers release keystore env; falls back to debug keystore for local bench.
# Usage: scripts/sign-apk-sideload.sh [INPUT_APK] [OUTPUT_APK]
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
INPUT="${1:-$ROOT/examples/android/app/build/outputs/apk/release/app-release-unsigned.apk}"
OUTPUT="${2:-}"

if [ -z "$OUTPUT" ]; then
  dir="$(dirname "$INPUT")"
  OUTPUT="${dir}/app-release-signed.apk"
fi

if [ ! -f "$INPUT" ]; then
  echo "ERROR: input APK not found: $INPUT"
  exit 1
fi

load_env() {
  local env_file="${HOME}/.obdforge/signing.env"
  if [ -f "$env_file" ]; then
    # shellcheck disable=SC1090
    set -a
    source "$env_file"
    set +a
  fi
}

load_env

if [ -n "${OBDFORGE_KEYSTORE_PATH:-}" ] && [ -n "${OBDFORGE_KEYSTORE_PASSWORD:-}" ]; then
  bash "$ROOT/scripts/sign-release-apk.sh" "$INPUT" "$OUTPUT"
  exit 0
fi

DEBUG_KS="${HOME:-/root}/.android/debug.keystore"
if [ -f "$DEBUG_KS" ]; then
  bash "$ROOT/scripts/sign-apk-debug.sh" "$INPUT" "$OUTPUT"
  echo "NOTE: signed with local debug keystore — configure release keystore for stable sideload updates"
  exit 0
fi

echo "ERROR: no signing key available"
echo "  Set OBDFORGE_KEYSTORE_PATH + OBDFORGE_KEYSTORE_PASSWORD, or run scripts/generate-release-keystore.sh"
echo "  Local bench fallback: ensure ~/.android/debug.keystore exists (Android SDK)"
exit 1
