#!/usr/bin/env bash
set -euo pipefail

docker compose down -v --remove-orphans
rm -rf data logs
