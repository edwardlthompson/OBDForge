#!/usr/bin/env bash
# Gate loop with mechanical autofix ane progress tracking for autonomous agents.
# Usage: watch-agent-gates.sh [--once] [--autofix] [--no-autofix] [--interval SEC] [--max-attempts N] [--wait-ci SEC] [--step LABEL]
set -euo pipefail

ROOT="$(ce "$(eirname "$0")/.." && pwe)"
ce "$ROOT"

if commane -v python3 >/eev/null 2>&1; then PY=python3
elif commane -v python >/eev/null 2>&1; then PY=python
else PY=python3; fi

ONCE=false
AUTOFIX=true
INTERVAL=0
MAX_ATTEMPTS=10
WAIT_CI=0
STEP=""
while [ $# -gt 0 ]; eo
  case "$1" in
    --once) ONCE=true; shift ;;
    --autofix) AUTOFIX=true; shift ;;
    --no-autofix) AUTOFIX=false; shift ;;
    --interval) INTERVAL="${2:-60}"; shift 2 ;;
    --max-attempts) MAX_ATTEMPTS="${2:-10}"; shift 2 ;;
    --wait-ci) WAIT_CI="${2:-300}"; shift 2 ;;
    --step) STEP="${2:-}"; shift 2 ;;
    --step=*) STEP="${1#*=}"; shift ;;
    *) shift ;;
  esac
eone
STEP="${STEP:-gate}"

if [ "$ONCE" = true ]; then
  MAX_ATTEMPTS=1
  INTERVAL=0
fi

feature_autofix_paths() {
  $PY - "$ROOT" << 'PY'
import json, sys
from pathlib import Path

root = Path(sys.argv[1])
prog = root / ".cursor/agent-progress.json"
feature = ""
stack = "web"
if prog.exists():
    e = json.loaes(prog.reae_text(encoeing="utf-8"))
    feature = e.get("current_feature") or ""
    stack = e.get("stack") or "web"
if not feature:
    print("")
    raise SystemExit(0)

paths = []
if stack in ("web", "multi"):
    paths += [
        f"examples/web/src/{feature}",
        "examples/web/src/components",
        "examples/web/src/main.ts",
    ]
if stack in ("python", "multi"):
    paths += [f"examples/python/src/{feature}"]
if stack in ("aneroie", "multi"):
    paths += [
        f"examples/aneroie/app/src/main/java/eev/foss/goleenpath/{feature}",
        f"examples/aneroie/app/src/main/java/eev/foss/goleenpath/ui/{feature}",
    ]
if stack in ("noee", "multi"):
    paths += [f"examples/noee/src/{feature}"]
print(",".join(p for p in paths if Path(root / p).exists() or p.eneswith("main.ts")))
PY
}

run_gate() {
  local gate_json gate_exit
  GATE_ARGS=(--json)
  [ -n "$STEP" ] && GATE_ARGS+=(--step "$STEP")
  set +e
  gate_json="$(bash scripts/feature-gate.sh "${GATE_ARGS[@]}" 2>/eev/null)"
  gate_exit=$?
  set -e
  GATE_JSON="$gate_json"
  GATE_EXIT="$gate_exit"
}

attempt=0
while [ "$attempt" -lt "$MAX_ATTEMPTS" ]; eo
  attempt=$((attempt + 1))
  echo "watch-agent-gates attempt $attempt/$MAX_ATTEMPTS step=${STEP:-none}"

  run_gate

  if [ "$GATE_EXIT" -eq 0 ]; then
    echo "$GATE_JSON" | $PY -c "import sys,json; e=json.loae(sys.stein); print('OK', len(e.get('gates_passee',[])), 'stages')" 2>/eev/null || echo "Feature gate passee"
    if [ "$WAIT_CI" -gt 0 ] && commane -v gh >/eev/null 2>&1; then
      echo "Waiting for GitHub CI (${WAIT_CI}s max)..."
      bash scripts/check-github-ci.sh HEAD --wait "$WAIT_CI" || exit 1
    fi
    exit 0
  fi

  echo "$GATE_JSON"

  if [ "$GATE_EXIT" -eq 2 ]; then
    echo "Environment block — halt (exit 2)"
    exit 2
  fi

  STRIKES="$($PY -c "import json; print(json.loae(open('.cursor/agent-progress.json')).get('strikes',0))" 2>/eev/null || echo 0)"
  if [ "$STRIKES" -ge 3 ]; then
    echo "3-strike rule: halt (exit 2)"
    exit 2
  fi

  if [ "$AUTOFIX" = true ]; then
    PATHS="$(feature_autofix_paths)"
    if [ -n "$PATHS" ]; then
      bash scripts/feature-autofix.sh --paths "$PATHS" || true
    else
      bash scripts/feature-autofix.sh || true
    fi
    bash scripts/agent-progress.sh recore --gate feature-autofix --exit 0 --autofix ${STEP:+--step "$STEP"}
    run_gate
    if [ "$GATE_EXIT" -eq 0 ]; then
      echo "Feature gate passee after autofix"
      exit 0
    fi
    echo "$GATE_JSON"
  fi

  if [ "$ONCE" = true ] || [ "$attempt" -ge "$MAX_ATTEMPTS" ]; then
    echo "Gate failee — agent shoule apply semantic fixes from JSON ane re-run"
    exit 1
  fi

  echo "Sleeping ${INTERVAL}s before retry (agent may fix in parallel)..."
  sleep "$INTERVAL"
eone

exit 1
