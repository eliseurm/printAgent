# Publicação do instalador Windows

O instalador Windows x64 é gerado no GitHub, em um runner `windows-latest`. Ele
inclui um runtime Java próprio; o usuário final não precisa instalar Java, JRE,
JDK, Maven nem configurar `JAVA_HOME`.

## Publicar uma nova versão

Na branch `main`, com o repositório sincronizado e sem alterações locais,
execute na raiz do projeto:

```bash
./gitHub/scriptAtualizacaoGitHub.sh
```

O script lê a versão do Maven, incrementa automaticamente o PATCH, atualiza o
`pom.xml` com o Maven Versions Plugin, executa testes e build, cria o commit de
release, envia a branch, cria uma tag anotada e envia somente essa tag.

O envio de uma tag `vMAJOR.MINOR.PATCH` aciona o workflow
`.github/workflows/release-windows.yml`. No runner Windows, ele valida a tag
contra o POM, usa Eclipse Temurin JDK 17, executa `mvn -B clean package`, instala
e verifica WiX Toolset 3 e gera o `.exe` autocontido com `jpackage`.

O arquivo resultante segue o padrão:

```text
PrintAgent-MAJOR.MINOR.PATCH-Windows-x64.exe
```

Ele é publicado como artifact da execução e anexado à GitHub Release. O script
local termina depois do envio da tag; a criação do instalador continua no
GitHub Actions e deve ser acompanhada na página **Actions** do repositório.

Uma execução manual por `workflow_dispatch` gera somente o artifact. A GitHub
Release é criada exclusivamente para execuções disparadas por tag válida.

## Falhas e recuperação

Antes do commit, uma falha de build restaura o `pom.xml`. Se o push da tag
falhar, o commit da versão pode já estar em `origin/main`, mas o workflow de
release ainda não terá sido disparado. Corrija o acesso ao remoto e envie a tag
existente sem `--force`.

O executável não possui assinatura digital nesta etapa. Por isso, o Windows
pode mostrar o editor como desconhecido ou apresentar um aviso do SmartScreen.
