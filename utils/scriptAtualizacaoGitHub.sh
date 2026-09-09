#!/usr/bin/env bash
set -euo pipefail

# Configuracao do repositorio usado para publicar a release.
readonly REMOTE="origin"
readonly EXPECTED_BRANCH="main"

POM_UPDATED=false
COMMIT_CREATED=false
TAG_CREATED=false
NOVA_VERSAO=""
NOVA_TAG=""

erro() {
    printf 'ERRO: %s\n' "$*" >&2
    exit 1
}

restore_pom_on_error() {
    local exit_code=$?
    if (( exit_code != 0 )) && [[ "$POM_UPDATED" == true ]] && [[ "$COMMIT_CREATED" == false ]]; then
        printf '\nFalha antes do commit. Restaurando pom.xml...\n' >&2
        git restore --staged --worktree -- pom.xml 2>/dev/null || \
            printf 'AVISO: nao foi possivel restaurar pom.xml automaticamente.\n' >&2
    fi
    if (( exit_code != 0 )) && [[ "$COMMIT_CREATED" == true ]] && [[ "$TAG_CREATED" == false ]]; then
        printf 'AVISO: o commit Release %s existe localmente, mas a tag nao foi criada.\n' "$NOVA_VERSAO" >&2
    fi
    exit "$exit_code"
}
trap restore_pom_on_error EXIT

printf '%s\n' '========================================='
printf '%s\n' 'Print Agent - Publicacao GitHub'
printf '%s\n\n' '========================================='
printf '%s\n' 'Verificando repositorio...'

git rev-parse --is-inside-work-tree >/dev/null 2>&1 || erro 'o diretorio atual nao pertence a um repositorio Git.'
REPO_ROOT="$(git rev-parse --show-toplevel)"
cd "$REPO_ROOT"
[[ -f pom.xml ]] || erro "pom.xml nao encontrado na raiz do repositorio: $REPO_ROOT"

BRANCH="$(git branch --show-current)"
[[ -n "$BRANCH" ]] || erro 'HEAD destacado; faca checkout da branch principal antes de publicar.'
[[ "$BRANCH" == "$EXPECTED_BRANCH" ]] || erro "a publicacao deve ocorrer na branch '$EXPECTED_BRANCH'; branch atual: '$BRANCH'."

if [[ -n "$(git status --porcelain)" ]]; then
    erro $'existem alteracoes locais nao commitadas.\nFaca commit ou stash antes de gerar uma nova versao.'
fi

git remote get-url "$REMOTE" >/dev/null 2>&1 || erro "remote '$REMOTE' nao configurado."
printf 'Atualizando referencias de %s...\n' "$REMOTE"
git fetch "$REMOTE"

git show-ref --verify --quiet "refs/remotes/$REMOTE/$EXPECTED_BRANCH" || \
    erro "a branch remota '$REMOTE/$EXPECTED_BRANCH' nao existe."

read -r ATRAS A_FRENTE < <(git rev-list --left-right --count "$BRANCH...$REMOTE/$EXPECTED_BRANCH")
if (( ATRAS > 0 )); then
    erro "a branch local esta $ATRAS commit(s) atras de '$REMOTE/$EXPECTED_BRANCH'. Atualize-a antes de publicar."
fi
if (( A_FRENTE > 0 )); then
    erro "a branch local possui $A_FRENTE commit(s) ainda nao enviado(s). Sincronize-a antes de publicar."
fi

VERSAO_ATUAL="$(mvn -Dstyle.color=never help:evaluate -Dexpression=project.version -q -DforceStdout | tr -d '\r')"
[[ "$VERSAO_ATUAL" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)$ ]] || \
    erro "a versao Maven '$VERSAO_ATUAL' nao segue o formato MAJOR.MINOR.PATCH."

MAJOR="${BASH_REMATCH[1]}"
MINOR="${BASH_REMATCH[2]}"
PATCH="${BASH_REMATCH[3]}"
NOVA_VERSAO="$MAJOR.$MINOR.$((10#$PATCH + 1))"
NOVA_TAG="v$NOVA_VERSAO"

printf '\nBranch.............: %s\n' "$BRANCH"
printf 'Versao atual.......: %s\n' "$VERSAO_ATUAL"
printf 'Nova versao........: %s\n' "$NOVA_VERSAO"
printf 'Tag................: %s\n\n' "$NOVA_TAG"

git show-ref --verify --quiet "refs/tags/$NOVA_TAG" && erro "a tag '$NOVA_TAG' ja existe localmente."
set +e
git ls-remote --exit-code --tags "$REMOTE" "refs/tags/$NOVA_TAG" >/dev/null
LS_REMOTE_STATUS=$?
set -e
if (( LS_REMOTE_STATUS == 0 )); then
    erro "a tag '$NOVA_TAG' ja existe no remote '$REMOTE'."
fi
if (( LS_REMOTE_STATUS != 2 )); then
    erro "nao foi possivel verificar a tag '$NOVA_TAG' no remote '$REMOTE'."
fi

printf 'Atualizando versao...\n%s -> %s\n' "$VERSAO_ATUAL" "$NOVA_VERSAO"
mvn -B versions:set -DnewVersion="$NOVA_VERSAO" -DgenerateBackupPoms=false
POM_UPDATED=true

VERSAO_CONFIRMADA="$(mvn -Dstyle.color=never help:evaluate -Dexpression=project.version -q -DforceStdout | tr -d '\r')"
[[ "$VERSAO_CONFIRMADA" == "$NOVA_VERSAO" ]] || \
    erro "o pom.xml informa '$VERSAO_CONFIRMADA' depois da atualizacao; esperado: '$NOVA_VERSAO'."

printf '\nExecutando testes e build...\n'
mvn clean package
printf 'OK\n\nCriando commit...\n'

git add -- pom.xml
git diff --cached --quiet && erro 'a atualizacao nao produziu alteracao versionavel no pom.xml.'
git commit -m "Release $NOVA_VERSAO"
COMMIT_CREATED=true
printf 'Release %s\n\nEnviando branch...\n' "$NOVA_VERSAO"
git push "$REMOTE" "$BRANCH"
printf 'OK\n\nCriando tag...\n'

git tag -a "$NOVA_TAG" -m "Print Agent $NOVA_VERSAO"
TAG_CREATED=true
printf '%s\n\nEnviando tag...\n' "$NOVA_TAG"
if ! git push "$REMOTE" "$NOVA_TAG"; then
    printf 'ERRO: falha ao enviar a tag %s.\n' "$NOVA_TAG" >&2
    printf 'O commit da nova versao pode ja estar no remoto.\n' >&2
    printf 'A Release ainda nao foi disparada; envie essa tag sem usar --force.\n' >&2
    exit 1
fi

trap - EXIT
printf '%s\n' 'OK'
printf '\n%s\n' '========================================='
printf 'Versao %s enviada com sucesso.\n' "$NOVA_VERSAO"
printf '%s\n\n' '========================================='
printf 'O GitHub Actions foi acionado pela tag:\n\n%s\n\n' "$NOVA_TAG"
printf '%s\n' 'Apos a conclusao do workflow, o instalador'
printf '%s\n' 'Windows estara disponivel na GitHub Release.'
