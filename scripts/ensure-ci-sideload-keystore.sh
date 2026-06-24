#!/usr/bin/env bash
# Create or reuse a stable CI sideload keystore (cached in GitHub Actions).
# Outputs export lines for OBDFORGE_KEYSTORE_* when release secrets are unset.
# Usage: eval "$(bash scripts/ensure-ci-sideload-keystore.sh)"
set -euo pipefail

KS="${OBDFORGE_CI_SIDELOAD_KEYSTORE:-/tmp/obdforge-sideload.keystore}"
ALIAS="${OBDFORGE_CI_SIDELOAD_ALIAS:-obdforge-sideload}"
PASS="${OBDFORGE_CI_SIDELOAD_PASSWORD:-obdforge-sideload-ci}"

if [ ! -f "$KS" ]; then
  keytool -genkeypair \
    -keystore "$KS" \
    -alias "$ALIAS" \
    -keyalg RSA \
    -keysize 4096 \
    -validity 10000 \
    -storepass "$PASS" \
    -keypass "$PASS" \
    -dname "CN=OBDForge Sideload CI, OU=Mobile, O=OBDForge, C=US"
  echo "Created CI sideload keystore: $KS" >&2
else
  echo "Reusing CI sideload keystore: $KS" >&2
fi

printf 'export OBDFORGE_KEYSTORE_PATH=%q\n' "$KS"
printf 'export OBDFORGE_KEYSTORE_PASSWORD=%q\n' "$PASS"
printf 'export OBDFORGE_KEY_ALIAS=%q\n' "$ALIAS"
printf 'export OBDFORGE_KEY_PASSWORD=%q\n' "$PASS"
