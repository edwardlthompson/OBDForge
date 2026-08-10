#!/usr/bin/env bash
# Pre-release gate: CI green, zero Critical/High Dependabot alerts, template version present.
# Usage: scripts/pre-release-gate.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

ERRORS=0
VERSION=""

echo "=== Pre-release gate ==="

STACK="multi"
if [ -f .cursor/stack-selection.json ]; then
  STACK="$(python3 -c "import json; print(json.load(open('.cursor/stack-selection.json', encoding='utf-8')).get('stack', 'multi') or 'multi')")"
fi
echo "Using feature-gate stack=${STACK}"

if ! bash scripts/feature-gate.sh --stack "$STACK" --strict --json; then
  echo "FAIL: feature-gate.sh"
  ERRORS=$((ERRORS + 1))
else
  echo "OK   feature-gate.sh passed"
fi

if ! bash scripts/check-security-triage.sh --wait-ci 300 --strict; then
  echo "FAIL: security-triage.sh --strict"
  ERRORS=$((ERRORS + 1))
else
  echo "OK   security-triage.sh --strict passed"
fi

# Template baseline (.template-version) may differ from product semver in child repos.
# Product version comes from Release Please manifest (scripts/product-version.sh).
PRODUCT_VERSION=""
if [ -f .release-please-manifest.json ]; then
  PRODUCT_VERSION="$(bash scripts/product-version.sh 2>/dev/null || true)"
fi

if [ ! -f .template-version ]; then
  echo "MISSING: .template-version"
  ERRORS=$((ERRORS + 1))
else
  TEMPLATE_VERSION="$(tr -d '[:space:]' < .template-version)"
  echo "OK   .template-version = ${TEMPLATE_VERSION}"
  if [ -z "$PRODUCT_VERSION" ]; then
    echo "FAIL: .release-please-manifest.json missing \".\" version"
    ERRORS=$((ERRORS + 1))
    VERSION="$TEMPLATE_VERSION"
  elif [ "$TEMPLATE_VERSION" = "$PRODUCT_VERSION" ]; then
    echo "OK   release-please manifest matches .template-version"
    VERSION="$PRODUCT_VERSION"
  else
    echo "OK   child product version ${PRODUCT_VERSION} (template baseline ${TEMPLATE_VERSION})"
    VERSION="$PRODUCT_VERSION"
  fi
fi

if ! bash scripts/check-license-compliance.sh; then
  echo "FAIL: check-license-compliance.sh"
  ERRORS=$((ERRORS + 1))
else
  echo "OK   check-license-compliance.sh passed"
fi

if [ -f examples/android/metadata/en-US/title.txt ]; then
  if ! bash scripts/verify-fdroid-metadata.sh; then
    echo "FAIL: verify-fdroid-metadata.sh"
    ERRORS=$((ERRORS + 1))
  else
    echo "OK   verify-fdroid-metadata.sh passed"
  fi
fi

if [ -n "$VERSION" ] && [ -f CHANGELOG.md ]; then
  if ! grep -q "^## \\[${VERSION}\\]" CHANGELOG.md; then
    echo "FAIL: CHANGELOG.md missing ## [${VERSION}] section"
    ERRORS=$((ERRORS + 1))
  else
    echo "OK   CHANGELOG.md contains [${VERSION}] section"
  fi
fi

if [ -f CHANGELOG.md ]; then
  if ! bash scripts/check-changelog-unreleased.sh; then
    echo "FAIL: check-changelog-unreleased.sh"
    ERRORS=$((ERRORS + 1))
  else
    echo "OK   check-changelog-unreleased.sh passed"
  fi
fi

echo ""
echo "REMINDER: Before tagging, trigger the Release workflow via workflow_dispatch:"
echo "  GitHub -> Actions -> Release -> Run workflow"
echo "  (.github/workflows/release.yml)"
if [ -n "$VERSION" ]; then
  echo "  Confirm CHANGELOG.md [${VERSION}] section and tag match .template-version"
fi

if [ "$ERRORS" -gt 0 ]; then
  echo "${ERRORS} pre-release gate check(s) failed"
  exit 1
fi

echo "Pre-release gate passed"
