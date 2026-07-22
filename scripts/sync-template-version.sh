#!/usr/bin/env bash
# Template maintainer: sync .template-version + TEMPLATE_INDEX from Release Please manifest.
# Child repos (TEMPLATE_INDEX.child_repo or stack-selection.pruned): do NOT overwrite
# .template-version (tracks upstream bootstrap). Only refresh product notes if needed.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if [ ! -f .release-please-manifest.json ]; then
  echo "MISSING: .release-please-manifest.json"
  exit 1
fi

PRODUCT="$(python3 -c "import json; print(json.load(open('.release-please-manifest.json', encoding='utf-8'))['.'].strip())")"

if [ -z "$PRODUCT" ]; then
  echo "FAIL: empty version in manifest"
  exit 1
fi

CHILD="$(python3 -c "import json;from pathlib import Path;p=Path('TEMPLATE_INDEX.json');print('yes' if p.exists() and json.loads(p.read_text(encoding='utf-8')).get('child_repo') else 'no')")"
PRUNED="$(python3 -c "import json;from pathlib import Path;p=Path('.cursor/stack-selection.json');print('yes' if p.exists() and json.loads(p.read_text(encoding='utf-8')).get('pruned') else 'no')" 2>/dev/null || echo no)"

if [ "$CHILD" = "yes" ] || [ "$PRUNED" = "yes" ]; then
  TEMPLATE="$(tr -d '[:space:]' < .template-version)"
  echo "Child repo mode: leaving .template-version=$TEMPLATE (upstream bootstrap)"
  echo "Product version (Release Please): $PRODUCT"
  echo "Skip syncing template semver from app manifest."
  exit 0
fi

echo "$PRODUCT" > .template-version

export SYNC_TEMPLATE_VERSION="${PRODUCT}"
python3 <<'PY'
import json
import os
import re
from pathlib import Path

version = os.environ["SYNC_TEMPLATE_VERSION"]
idx = Path("TEMPLATE_INDEX.json")
data = json.loads(idx.read_text(encoding="utf-8"))
data["template_version"] = version
idx.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")

readme = Path("README.md")
text = readme.read_text(encoding="utf-8")
text = re.sub(
    r"!\[Template\]\(https://img\.shields\.io/badge/template-[\d.]+",
    f"![Template](https://img.shields.io/badge/template-{version}",
    text,
)
text = re.sub(
    r"Current template version: \*\*[\d.]+\*\*",
    f"Current template version: **{version}**",
    text,
)
readme.write_text(text, encoding="utf-8")

mem = Path("AGENT_MEMORY.md")
mt = mem.read_text(encoding="utf-8")
mt = re.sub(
    r"(\| Multi-stack template[^\|]+\| )[\d.]+( \| Template maintainer)",
    lambda m: f"{m.group(1)}{version}{m.group(2)}",
    mt,
)
mt = re.sub(
    r"(\*\*Template version:\*\* `)[\d.]+(`)",
    lambda m: f"{m.group(1)}{version}{m.group(2)}",
    mt,
)
mem.write_text(mt, encoding="utf-8")
PY

echo "Synced template version to ${PRODUCT} (.template-version, TEMPLATE_INDEX.json, README.md, AGENT_MEMORY.md)"
