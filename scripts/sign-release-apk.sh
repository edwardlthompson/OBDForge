#!/usr/bin/env bash
# Sign a release APK with the OBDForge release keystore (post-build; keeps unsigned APK reproducible).
# Usage: scripts/sign-release-apk.sh [INPUT_APK] [OUTPUT_APK]
# Env: OBDFORGE_KEYSTORE_PATH, OBDFORGE_KEYSTORE_PASSWORD, OBDFORGE_KEY_ALIAS, OBDFORGE_KEY_PASSWORD
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
INPUT="${1:-$ROOT/examples/android/app/build/outputs/apk/release/app-release-unsigned.apk}"
OUTPUT="${2:-}"

if [ -z "$OUTPUT" ]; then
  dir="$(dirname "$INPUT")"
  OUTPUT="${dir}/app-release-signed.apk"
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

KS="${OBDFORGE_KEYSTORE_PATH:-}"
KS_PASS="${OBDFORGE_KEYSTORE_PASSWORD:-}"
ALIAS="${OBDFORGE_KEY_ALIAS:-obdforge}"
KEY_PASS="${OBDFORGE_KEY_PASSWORD:-$KS_PASS}"

if [ -z "$KS" ] || [ -z "$KS_PASS" ]; then
  echo "ERROR: set OBDFORGE_KEYSTORE_PATH and OBDFORGE_KEYSTORE_PASSWORD"
  echo "       or create ~/.obdforge/signing.env"
  exit 1
fi

if [ ! -f "$INPUT" ]; then
  echo "ERROR: input APK not found: $INPUT"
  exit 1
fi

if [ ! -f "$KS" ]; then
  echo "ERROR: keystore not found: $KS"
  exit 1
fi

APKSIGNER=""
if [ -n "${ANDROID_HOME:-}" ] && [ -d "${ANDROID_HOME}/build-tools" ]; then
  BT="$(find "${ANDROID_HOME}/build-tools" -maxdepth 1 -mindepth 1 -type d | sort -V | tail -1)"
  APKSIGNER="${BT}/apksigner"
fi
if [ -z "$APKSIGNER" ] || [ ! -x "$APKSIGNER" ]; then
  if [ -n "${LOCALAPPDATA:-}" ] && [ -d "${LOCALAPPDATA}/Android/Sdk/build-tools" ]; then
    BT="$(find "${LOCALAPPDATA}/Android/Sdk/build-tools" -maxdepth 1 -mindepth 1 -type d | sort -V | tail -1)"
    APKSIGNER="${BT}/apksigner"
    [ -x "${APKSIGNER}.bat" ] && APKSIGNER="${APKSIGNER}.bat"
  fi
fi
if [ -z "$APKSIGNER" ] || ! command -v "$APKSIGNER" >/dev/null 2>&1 && [ ! -x "$APKSIGNER" ]; then
  if command -v apksigner >/dev/null 2>&1; then
    APKSIGNER="apksigner"
  else
    echo "ERROR: apksigner not found"
    exit 1
  fi
fi

"$APKSIGNER" sign \
  --ks "$KS" \
  --ks-key-alias "$ALIAS" \
  --ks-pass "pass:${KS_PASS}" \
  --key-pass "pass:${KEY_PASS}" \
  --out "$OUTPUT" \
  "$INPUT"

"$APKSIGNER" verify --verbose "$OUTPUT"
echo "Signed APK: $OUTPUT"
