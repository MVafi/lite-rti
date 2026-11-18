#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven is required but was not found on PATH." >&2
  exit 1
fi

mvn -q -DskipTests package
exec java -cp "target/classes:target/dependency/*" nl.literti.LiteRtiApplication "$@"