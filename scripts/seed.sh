#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${REWARDS_URL:-http://localhost:8082}"

curl -fsS -X POST "${BASE_URL}/api/v1/rewards/catalog" \
  -H 'Content-Type: application/json' \
  -d '{"code":"FREE_BET_10","description":"Ten euro promotional reward","pointsCost":100,"expiresAt":null}' >/dev/null

curl -fsS -X POST "${BASE_URL}/api/v1/rewards/catalog" \
  -H 'Content-Type: application/json' \
  -d '{"code":"LOYALTY_CREDIT_25","description":"Twenty five euro loyalty reward","pointsCost":250,"expiresAt":null}' >/dev/null

echo "Sample rewards created successfully."
