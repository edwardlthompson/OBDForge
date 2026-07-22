#!/usr/bin/env bash
# Print OBDForge product semver from Release Please manifest (not .template-version).
# .template-version tracks upstream agent-project-bootstrap; app version is "." in the manifest.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
python3 -c "import json; print(json.load(open('.release-please-manifest.json', encoding='utf-8'))['.'].strip())"
