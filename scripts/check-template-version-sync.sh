#!/usr/bin/env bash
# Fail when .template-version drifts from TEMPLATE_INDEX.json.
# Child repos (OBDForge): product semver lives in .release-please-manifest.json;
# .template-version tracks upstream agent-project-bootstrap, not the app version.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if [ ! -f .template-version ] || [ ! -f TEMPLATE_INDEX.json ]; then
  echo "MISSING: .template-version or TEMPLATE_INDEX.json"
  exit 1
fi

VERSION="$(tr -d '[:space:]' < .template-version)"
IDX="$(python3 -c "import json; print(json.load(open('TEMPLATE_INDEX.json', encoding='utf-8'))['template_version'])")"

if [ "$IDX" != "$VERSION" ]; then
  echo "FAIL: TEMPLATE_INDEX template_version ($IDX) != .template-version ($VERSION)"
  echo "Fix: align both to the upstream bootstrap semver (not the app version)"
  exit 1
fi

# Child repo: optional aligned_template_version in stack-selection must match
if [ -f .cursor/stack-selection.json ]; then
  ALIGNED="$(python3 -c "import json;d=json.load(open('.cursor/stack-selection.json', encoding='utf-8'));print((d.get('aligned_template_version') or '').strip())" 2>/dev/null || true)"
  if [ -n "${ALIGNED:-}" ] && [ "$ALIGNED" != "$VERSION" ]; then
    echo "FAIL: stack-selection aligned_template_version ($ALIGNED) != .template-version ($VERSION)"
    exit 1
  fi
fi

# Template maintainer mode: when not a pruned child, also sync Release Please manifest
CHILD="$(python3 -c "import json;d=json.load(open('TEMPLATE_INDEX.json', encoding='utf-8'));print('yes' if d.get('child_repo') else 'no')" 2>/dev/null || echo no)"
PRUNED="$(python3 -c "import json;d=json.load(open('.cursor/stack-selection.json', encoding='utf-8'));print('yes' if d.get('pruned') else 'no')" 2>/dev/null || echo no)"

if [ "$CHILD" = "no" ] && [ "$PRUNED" = "no" ] && [ -f .release-please-manifest.json ]; then
  MANIFEST="$(python3 -c "import json; print(json.load(open('.release-please-manifest.json', encoding='utf-8'))['.'].strip())")"
  if [ "$MANIFEST" != "$VERSION" ]; then
    echo "FAIL: .template-version ($VERSION) != manifest ($MANIFEST)"
    echo "Fix: bash scripts/sync-template-version.sh"
    exit 1
  fi
fi

echo "Template version sync OK ($VERSION)"
if [ "$CHILD" = "yes" ] || [ "$PRUNED" = "yes" ]; then
  if [ -f .release-please-manifest.json ]; then
    PRODUCT="$(python3 -c "import json; print(json.load(open('.release-please-manifest.json', encoding='utf-8'))['.'].strip())")"
    echo "Child product version (Release Please): $PRODUCT"
  fi
fi
