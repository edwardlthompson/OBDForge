#!/usr/bin/env bash
# Install signed GitHub Release APK on a connected device (ADB bench).
# Handles signature mismatch by uninstalling the old package once, then retrying.
# Usage: scripts/install-github-release.sh [VERSION]   (default: .template-version)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PKG="dev.foss.obdforge"
VERSION="${1:-$(tr -d '[:space:]' < "$ROOT/.template-version")}"
APK_NAME="OBDForge-${VERSION}.apk"
URL="https://github.com/edwardlthompson/OBDForge/releases/download/v${VERSION}/${APK_NAME}"
TMP="${TMPDIR:-/tmp}/${APK_NAME}"

if ! command -v adb >/dev/null 2>&1; then
  echo "ERROR: adb not found"
  exit 1
fi

if ! adb devices | awk 'NR>1 && $2=="device" {found=1} END {exit !found}'; then
  echo "ERROR: no authorized ADB device"
  adb devices -l
  exit 1
fi

echo "Downloading ${URL} ..."
curl -fsSL -o "$TMP" "$URL"

try_install() {
  adb install -r "$TMP" 2>&1
}

echo "Installing ${APK_NAME} ..."
set +e
OUT="$(try_install)"
CODE=$?
set -e
echo "$OUT"

if [ "$CODE" -eq 0 ]; then
  echo "OK   Installed ${PKG} v${VERSION}"
  adb shell dumpsys package "$PKG" | grep -E "versionName|versionCode" | head -2 || true
  exit 0
fi

if echo "$OUT" | grep -qiE "signatures do not match|UPDATE_INCOMPATIBLE"; then
  echo "WARN signature mismatch — uninstalling ${PKG} (debug or old signing key) and retrying..."
  adb uninstall "$PKG" || true
  adb install "$TMP"
  echo "OK   Installed ${PKG} v${VERSION} after uninstall"
  adb shell dumpsys package "$PKG" | grep -E "versionName|versionCode" | head -2 || true
  exit 0
fi

if echo "$OUT" | grep -qiE "NO_CERTIFICATES|not signed"; then
  echo "ERROR: APK is unsigned — download OBDForge-X.Y.Z.apk from GitHub Releases only"
  exit 1
fi

echo "ERROR: install failed"
exit 1
