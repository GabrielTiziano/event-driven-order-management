#!/usr/bin/env bash
# Derruba os 5 microservices (por porta). Com --infra, derruba tambem o docker.
set -uo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

echo "== derrubando os 5 microservices =="
for port in 8082 8083 8084 8085 8086; do
  pids=$(lsof -ti tcp:"$port" 2>/dev/null || true)
  if [ -n "$pids" ]; then echo "  porta $port -> kill $pids"; kill $pids 2>/dev/null || true; fi
done

if [ "${1:-}" = "--infra" ]; then
  echo "== derrubando infra (docker) =="
  docker compose -f "$ROOT/servicos/bucket/docker-compose.yml"   down
  docker compose -f "$ROOT/servicos/broker/docker-compose.yml"   down
  docker compose -f "$ROOT/servicos/database/docker-compose.yml" down
fi
echo "Pronto."
