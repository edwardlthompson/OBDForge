#!/usr/bin/env bash
# Count open Critical/High Dependabot alerts (paginated).
# Usage: scripts/count-critical-high-dependabot.sh
# Exit 0 prints count to stdout; exit 1 on API/auth error.
set -euo pipefail

if ! command -v gh >/dev/null 2>&1; then
  echo "ERROR: gh CLI required" >&2
  exit 1
fi

REPO="${GITHUB_REPO:-$(gh repo view --json nameWithOwner -q .nameWithOwner 2>/dev/null || true)}"
if [ -z "$REPO" ]; then
  echo "ERROR: gh auth required" >&2
  exit 1
fi

if command -v python3 >/dev/null 2>&1; then PY=python3
elif command -v python >/dev/null 2>&1; then PY=python
else PY=python3; fi

COUNT="$("$PY" - "$REPO" << 'PY'
import json, subprocess, sys

repo = sys.argv[1]
total = 0
for severity in ("critical", "high"):
    proc = subprocess.run(
        [
            "gh",
            "api",
            "--paginate",
            f"repos/{repo}/dependabot/alerts?state=open&severity={severity}&per_page=100",
        ],
        capture_output=True,
        text=True,
    )
    if proc.returncode != 0:
        print((proc.stderr or proc.stdout or "error").strip(), file=sys.stderr)
        raise SystemExit(1)
    raw = (proc.stdout or "").strip()
    if not raw:
        continue
    # --paginate may concatenate JSON arrays; normalize to one list
    try:
        alerts = json.loads(raw)
    except json.JSONDecodeError:
        alerts = []
        for chunk in raw.replace("][", "]\n[").splitlines():
            chunk = chunk.strip()
            if chunk:
                alerts.extend(json.loads(chunk))
    if isinstance(alerts, dict):
        alerts = [alerts]
    total += len(alerts)
print(total)
PY
)"

echo "$COUNT"
