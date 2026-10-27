#!/usr/bin/env bash
# Sobe infra (Postgres, Kafka, RustFS) + os 5 microservices e espera readiness.
set -uo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOGDIR="$ROOT/scripts/logs"
mkdir -p "$LOGDIR"

JH="$(/usr/libexec/java_home -v 21 2>/dev/null || true)"
if [ -z "$JH" ]; then echo "ERRO: JDK 21 nao encontrado (/usr/libexec/java_home -v 21)"; exit 1; fi
export JAVA_HOME="$JH"
echo "JAVA_HOME=$JAVA_HOME"

wait_tcp() { # nome host port max
  local name=$1 host=$2 port=$3 max=${4:-60} i=0
  printf "  %s (%s:%s) " "$name" "$host" "$port"
  while ! nc -z "$host" "$port" >/dev/null 2>&1; do
    i=$((i+1)); [ "$i" -ge "$max" ] && { echo "TIMEOUT"; return 1; }
    printf "."; sleep 1
  done
  echo "OK"
}

wait_http() { # nome port max
  local name=$1 port=$2 max=${3:-90} i=0 code=""
  printf "  %s (porta %s) " "$name" "$port"
  while :; do
    code=$(curl -s -m 2 -o /dev/null -w '%{http_code}' "http://localhost:$port/" 2>/dev/null || true)
    [ -n "$code" ] && [ "$code" != "000" ] && { echo "OK (HTTP $code)"; return 0; }
    i=$((i+1)); [ "$i" -ge "$max" ] && { echo "TIMEOUT (veja $LOGDIR/$name.log)"; return 1; }
    printf "."; sleep 2
  done
}

start_svc() { # nome modulo porta
  local name=$1 mod=$2 port=$3 code=""
  code=$(curl -s -m 2 -o /dev/null -w '%{http_code}' "http://localhost:$port/" 2>/dev/null || true)
  if [ -n "$code" ] && [ "$code" != "000" ]; then echo "  $name ja no ar (porta $port) — pulando"; return 0; fi
  echo "  subindo $name ..."
  ( cd "$ROOT/$mod" && JAVA_HOME="$JH" nohup ./mvnw spring-boot:run > "$LOGDIR/$name.log" 2>&1 & )
}

echo "== 1/3 infra (docker) =="
docker compose -f "$ROOT/servicos/database/docker-compose.yml" up -d
docker compose -f "$ROOT/servicos/broker/docker-compose.yml"   up -d
docker compose -f "$ROOT/servicos/bucket/docker-compose.yml"   up -d
wait_tcp postgres localhost 5555 60
wait_tcp kafka    localhost 9092 60
wait_tcp rustfs   localhost 9000 60

echo "== 2/3 subindo os 5 microservices (logs em scripts/logs/) =="
start_svc clientes    microservice-clientes    8082
start_svc produtos    microservice-produtos    8084
start_svc faturamento microservice-faturamento 8085
start_svc logistica   microservice-logistica   8086
start_svc pedidos     microservice-pedidos     8083

echo "== 3/3 aguardando readiness (ate ~3min; 1a vez compila) =="
wait_http clientes    8082 120
wait_http produtos    8084 120
wait_http faturamento 8085 120
wait_http logistica   8086 120
wait_http pedidos     8083 120

echo
echo "Tudo no ar. Logs:  tail -f scripts/logs/<servico>.log"
echo "E2E:  cd e2e && PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1 npm install && npm test"
