#!/usr/bin/env bash
# Validate F-Droid/Fastlane metadata scaffold (AGENT gate; APK hashes remain ADB).
# Usage: scripts/verify-fdroid-metadata.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

META="$ROOT/examples/android/metadata/en-US"
GRADLE="$ROOT/examples/android/app/build.gradle.kts"
RELEASE_SCRIPT="$ROOT/scripts/build-release-apk.sh"
ERRORS=0

fail() {
  echo "FAIL: $1"
  ERRORS=$((ERRORS + 1))
}

ok() {
  echo "OK   $1"
}

if [ ! -d "$META" ]; then
  fail "missing $META"
else
  ok "metadata directory present"
fi

for f in title.txt short_description.txt full_description.txt anti_features.txt; do
  if [ ! -s "$META/$f" ]; then
    fail "missing or empty $META/$f"
  else
    ok "$f present"
  fi
done

if grep -qE 'Replace this text|Golden Path Android is a minimal' "$META/full_description.txt"; then
  fail "full_description.txt still contains template placeholder text"
else
  ok "full_description.txt customized for OBDForge"
fi

SHORT_LEN="$(wc -c < "$META/short_description.txt" | tr -d ' ')"
if [ "$SHORT_LEN" -gt 80 ]; then
  fail "short_description.txt exceeds 80 chars ($SHORT_LEN)"
else
  ok "short_description.txt within 80 char limit"
fi

if [ ! -f "$GRADLE" ]; then
  fail "missing $GRADLE"
else
  VERSION_CODE="$(grep -E 'versionCode\s*=' "$GRADLE" | head -1 | sed -E 's/.*=\s*([0-9]+).*/\1/')"
  if [ -z "${VERSION_CODE:-}" ]; then
    fail "could not parse versionCode from build.gradle.kts"
  elif [ ! -s "$META/changelogs/${VERSION_CODE}.txt" ]; then
    fail "missing changelog $META/changelogs/${VERSION_CODE}.txt"
  else
    ok "changelog for versionCode ${VERSION_CODE}"
  fi
fi

if [ ! -d "$META/images" ]; then
  fail "missing $META/images/ (add README + assets before submit)"
else
  ok "images directory present"
fi

if [ -f "$ROOT/LICENSE" ] && grep -q "GNU GENERAL PUBLIC LICENSE" "$ROOT/LICENSE"; then
  ok "root LICENSE present (GPL-3.0)"
else
  fail "missing or unexpected root LICENSE (expected GPL-3.0)"
fi

if [ -f "$ROOT/examples/android/metadata/fdroiddata-handoff.yml" ]; then
  ok "fdroiddata handoff draft present"
else
  fail "missing examples/android/metadata/fdroiddata-handoff.yml"
fi

if [ -f "$RELEASE_SCRIPT" ] && grep -q 'SOURCE_DATE_EPOCH' "$RELEASE_SCRIPT"; then
  ok "build-release-apk.sh pins SOURCE_DATE_EPOCH"
else
  fail "missing scripts/build-release-apk.sh with SOURCE_DATE_EPOCH"
fi

if [ -d "$ROOT/examples/android/fastlane/metadata/android/en-US" ]; then
  ok "fastlane metadata mirror present"
fi

echo ""
echo "SKIP [ADB] reproducible APK hash verification — run scripts/verify-reproducible-apk.sh or CI android-release"
echo "SKIP [ADB] device install smoke — run scripts/fdroid-device-dry-run.sh"

if [ "$ERRORS" -gt 0 ]; then
  echo "${ERRORS} F-Droid metadata check(s) failed"
  exit 1
fi

echo "F-Droid metadata scaffold verified"
