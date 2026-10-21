#!/usr/bin/env bash
set -euo pipefail


if ! command -v mvn >/dev/null 2>&1; then
  echo "ERROR: Maven is required for host-side verification." >&2
  exit 1
fi

mvn -B clean verify
