#!/usr/bin/env bash
set -euo pipefail

# Este script delega el uild completo a Docker Compose para mantener una sola ruta de build.
docker compose build --pull

echo "All Docker images built successfully."
