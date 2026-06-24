#!/usr/bin/env bash
# Fail fast when GitHub release signing secrets are not configured.
# Stable OBDFORGE_KEYSTORE_BASE64 is required so sideload updates keep the same signature.
set -euo pipefail

if [ -z "${OBDFORGE_KEYSTORE_BASE64:-}" ]; then
  echo "ERROR: OBDFORGE_KEYSTORE_BASE64 secret is not set"
  echo "  Run scripts/generate-release-keystore.sh once, then:"
  echo "  gh secret set OBDFORGE_KEYSTORE_BASE64 --body \"\$(base64 -w0 ~/.obdforge/obdforge-release.keystore)\""
  echo "  gh secret set OBDFORGE_KEYSTORE_PASSWORD --body '...'"
  echo "  gh secret set OBDFORGE_KEY_ALIAS --body obdforge"
  echo "  gh secret set OBDFORGE_KEY_PASSWORD --body '...'"
  exit 1
fi

for var in OBDFORGE_KEYSTORE_PASSWORD OBDFORGE_KEY_ALIAS OBDFORGE_KEY_PASSWORD; do
  if [ -z "${!var:-}" ]; then
    echo "ERROR: GitHub secret $var is not set"
    exit 1
  fi
done

echo "OK   Release signing secrets present"
