
#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

export CODEX_HOME="$HOME/.codex-printagent"

mkdir -p "$CODEX_HOME"
chmod 700 "$CODEX_HOME"

cd "$SCRIPT_DIR"

exec codex "$@"
