#!/usr/bin/env bash
# Create a release signing keystore outside the repo (one-time human setup).
# Usage: scripts/generate-release-keystore.sh [OUTPUT_PATH]
set -euo pipefail

OUTPUT="${1:-${HOME}/.obdforge/obdforge-release.keystore}"
ALIAS="${OBDFORGE_KEY_ALIAS:-obdforge}"
VALIDITY_DAYS="${OBDFORGE_KEY_VALIDITY_DAYS:-9125}"

mkdir -p "$(dirname "$OUTPUT")"

if [ -f "$OUTPUT" ]; then
  echo "ERROR: keystore already exists at $OUTPUT"
  exit 1
fi

echo "Creating release keystore at $OUTPUT (alias: $ALIAS)"
keytool -genkeypair \
  -keystore "$OUTPUT" \
  -alias "$ALIAS" \
  -keyalg RSA \
  -keysize 4096 \
  -validity "$VALIDITY_DAYS" \
  -storepass:env OBDFORGE_KEYSTORE_PASSWORD \
  -keypass:env OBDFORGE_KEY_PASSWORD \
  -dname "CN=OBDForge Release, OU=Mobile, O=OBDForge, L=Local, ST=NA, C=US"

echo "OK   Keystore created"
keytool -list -v -keystore "$OUTPUT" -storepass:env OBDFORGE_KEYSTORE_PASSWORD | head -20
echo ""
echo "Set these env vars (or add to ~/.obdforge/signing.env):"
echo "  export OBDFORGE_KEYSTORE_PATH=$OUTPUT"
echo "  export OBDFORGE_KEY_ALIAS=$ALIAS"
echo "  export OBDFORGE_KEYSTORE_PASSWORD=***"
echo "  export OBDFORGE_KEY_PASSWORD=***"
