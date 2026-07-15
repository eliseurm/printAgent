# Print Agent — Changelog

## Histórico de versões e alterações

| Campo                    | Valor                      |
| ------------------------ | -------------------------- |
| Projeto                  | Print Agent                |
| Sistema relacionado      | Presente                   |
| Documento                | CHANGELOG.md               |
| Versão atual planejada   | 1.0.0                      |
| Java                     | 17 LTS                     |
| Namespace                | `br.eng.eliseu.printagent` |
| Formato de versionamento | SemVer                     |
| Status                   | Documento inicial          |
| Última atualização       | Julho de 2026              |

---

# 1. Objetivo

Este documento registra as alterações realizadas no Print Agent ao longo de suas versões.

Cada versão publicada deverá possuir uma seção contendo:

* versão;
* data;
* status;
* resumo;
* funcionalidades adicionadas;
* alterações;
* correções;
* limitações;
* alterações incompatíveis;
* instruções de atualização, quando aplicável.

Este documento deverá ser atualizado antes de cada publicação.

---

# 2. Convenção de versionamento

O Print Agent deverá utilizar versionamento semântico:

```text
MAJOR.MINOR.PATCH
```

Exemplos:

```text
1.0.0
1.1.0
1.1.1
2.0.0
```

## 2.1 MAJOR

Incrementado quando existirem alterações incompatíveis.

Exemplos:

* remoção de endpoint;
* alteração incompatível do JSON;
* mudança do envelope padrão;
* nova versão obrigatória da API;
* remoção de suporte a uma plataforma;
* alteração incompatível da configuração.

## 2.2 MINOR

Incrementado quando forem adicionadas funcionalidades compatíveis.

Exemplos:

* novo endpoint;
* novo formato de impressão;
* nova tela;
* novo diagnóstico;
* nova opção de configuração.

## 2.3 PATCH

Incrementado para correções compatíveis.

Exemplos:

* correção de falha;
* melhoria de mensagem;
* ajuste de instalador;
* correção de log;
* melhoria interna sem alteração de contrato.

---

# 3. Categorias utilizadas

Cada versão poderá utilizar as seguintes categorias.

## Adicionado

Novas funcionalidades.

## Alterado

Mudanças compatíveis em comportamento, configuração ou interface.

## Corrigido

Correções de falhas.

## Segurança

Correções relacionadas à segurança.

## Removido

Funcionalidades removidas.

## Obsoleto

Funcionalidades marcadas para remoção futura.

## Limitações conhecidas

Problemas conhecidos que ainda não foram corrigidos.

## Atualização

Instruções específicas para atualizar a versão anterior.

---

# 4. Estado das versões

Valores permitidos:

```text
PLANEJADA
EM_DESENVOLVIMENTO
EM_TESTE
CANDIDATA
PUBLICADA
SUBSTITUIDA
CANCELADA
```

---

# 5. Versão 1.0.0

## Informações

| Campo       | Valor                      |
| ----------- | -------------------------- |
| Versão      | 1.0.0                      |
| Data        | 15/07/2026                 |
| Status      | PLANEJADA                  |
| API         | v1                         |
| Java        | 17 LTS                     |
| Spring Boot | 3.x compatível com Java 17 |
| Sistemas    | Windows e Linux            |
| Arquitetura | amd64 inicialmente         |

---

## Resumo

Primeira versão funcional do Print Agent.

Esta versão deverá disponibilizar um agente local, genérico e multiplataforma para receber trabalhos de impressão por API REST e encaminhá-los às impressoras instaladas no computador do usuário.

O agente será utilizado inicialmente pelo sistema Presente, mas permanecerá desacoplado de suas regras de negócio.

---

## Adicionado

### Núcleo da aplicação

* Projeto Java 17.
* Spring Boot 3.x.
* Build Maven.
* Namespace `br.eng.eliseu.printagent`.
* Configuração externa por `application.yml`.
* Execução como serviço local.
* Escuta exclusiva em loopback.
* Porta padrão `18181`.
* API REST versionada em `/api/v1`.
* Interface Web local.
* Logs estruturados.
* Rotação e retenção de logs.

### API REST

* Endpoint de status.
* Criação de trabalhos de impressão.
* Consulta de trabalhos.
* Cancelamento de trabalhos pendentes.
* Consulta de histórico.
* Limpeza de histórico em memória.
* Listagem de impressoras.
* Atualização do catálogo de impressoras.
* Consulta de impressora.
* Consulta de capacidades.
* Impressão de teste.
* Consulta e atualização de configurações autorizadas.
* Consulta de logs.
* Download de arquivos de log.
* Consulta de atualização.
* Verificação manual de atualização.

### Trabalhos de impressão

* UUID gerado pelo agente.
* Fila FIFO.
* Processamento assíncrono.
* Modo síncrono opcional.
* Histórico em memória.
* Estados:

  * `RECEBIDO`;
  * `VALIDADO`;
  * `NA_FILA`;
  * `IMPRIMINDO`;
  * `CONCLUIDO`;
  * `ERRO`;
  * `CANCELADO`.

### Tipos de documento

* `application/zpl`.
* `application/pdf` no Linux.
* `text/plain`.

### ZPL

* Envio RAW.
* Preservação integral dos comandos.
* Sem rasterização.
* Sem interpretação.
* Sem alteração automática do layout.

### PDF

* Recebimento em Base64.
* Decodificação local.
* Uso de arquivo temporário quando necessário.
* Remoção do arquivo após processamento.
* Encaminhamento ao sistema de impressão.

### RAW

* Envio de texto UTF-8.
* Sem cabeçalhos.
* Sem rodapés.
* Sem comandos adicionais.

### Impressoras

* Descoberta automática.
* Identificação da impressora padrão.
* Consulta de status.
* Consulta de capacidades.
* Preservação dos nomes e propriedades do driver.
* Provider Linux.
* Provider Windows.

### Linux

* Integração com CUPS.
* Uso controlado de:

  * `lp`;
  * `lpstat`;
  * `lpoptions`;
  * `lpinfo`;
  * `cancel`.
* Serviço systemd.
* Pacote `.deb`.
* Diretórios externos para configuração, logs e arquivos temporários.

### Windows

* Integração inicial via Java Print Service.
* Serviço do Windows.
* Instalador `.msi` ou `.exe`.
* Runtime Java 17 empacotado quando possível.
* Diretórios em `Program Files` e `ProgramData`.

### Interface Web

* Dashboard.
* Impressoras.
* Trabalhos.
* Histórico.
* Teste de impressão.
* Configurações.
* Logs.
* Atualizações.
* Sobre.
* Funcionamento sem Internet.
* Interface em português do Brasil.

### Segurança local

* Escuta apenas em `127.0.0.1`.
* CORS com lista explícita.
* Proibição de origem curinga em produção.
* Execução de comandos sem shell.
* Validação de entrada.
* Limites de requisição.
* Proteção contra acesso indevido a arquivos.
* Restrição de logs para evitar exposição de documentos.

### Atualização

* Consulta de manifesto remoto.
* Versionamento semântico.
* Download por HTTPS.
* Validação SHA-256.
* Preservação de configuração.
* Preservação de logs.
* Atualização assistida por sistema operacional.

### Documentação

* `ESPECIFICACAO_PRINT_AGENT.md`.
* `API.md`.
* `JSON.md`.
* `WEBUI.md`.
* `INSTALACAO.md`.
* `ROADMAP.md`.
* `CHANGELOG.md`.
* Estrutura de exemplos JSON, ZPL e PDF.

---

## Alterado

Não aplicável. Esta é a primeira versão.

---

## Corrigido

Não aplicável. Esta é a primeira versão.

---

## Segurança

* Restringido acesso ao loopback.
* Definido CORS explícito.
* Proibida execução via shell com entrada do usuário.
* Definidos limites de tamanho.
* Definido tratamento seguro de arquivos temporários.
* Definida proteção contra path traversal em downloads de log.
* Definida política para não registrar conteúdos completos.

---

## Limitações conhecidas

* Fila não persistente.
* Histórico não persistente.
* Sem autenticação.
* Sem garantia de saída física do documento.
* Sem idempotência automática em `POST /trabalhos`.
* Cancelamento pode não ser possível após envio ao spooler.
* Capacidades podem não estar disponíveis em todos os drivers.
* Impressão PDF no Windows não suportada; planejada para a versão 1.2.0.
* Configurações proprietárias podem não ser aplicáveis no Windows.
* Sem editor visual de etiquetas.
* Sem conversão automática de dimensões para nomes de mídia.
* Sem painel central.
* Sem suporte oficial a ARM.
* Apenas pacote `.deb` planejado inicialmente para Linux.

---

## Atualização

Não aplicável. Esta é a primeira versão.

---

# 6. Versão 1.0.1

## Informações

| Campo  | Valor      |
| ------ | ---------- |
| Versão | 1.0.1      |
| Data   | 15/07/2026 |
| Status | PLANEJADA  |
| Tipo   | Correção   |

---

## Objetivo

Concentrar correções identificadas durante a primeira homologação e implantação.

---

## Itens candidatos

### Corrigido

* Permissões do serviço Linux.
* Descoberta de impressoras sob usuário de serviço.
* Registro do serviço Windows.
* Inicialização automática.
* Tratamento de timeout.
* Mensagens de erro.
* Limpeza de arquivos temporários.
* Regras de CORS.
* Validação de propriedades.
* Logs de Provider.
* Empacotamento do runtime.
* Preservação da configuração em atualização.

### Alterado

* Melhorias pequenas na interface.
* Ajustes de textos.
* Ajustes nos instaladores.
* Ajustes de documentação.

---

## Limitações conhecidas

As limitações da versão 1.0.0 permanecerão, salvo correção expressamente registrada.

---

# 6.1 Versão 1.0.2

## Informações

| Campo  | Valor               |
| ------ | ------------------- |
| Versão | 1.0.2               |
| Data   | 15/07/2026          |
| Status | EM_DESENVOLVIMENTO  |
| Tipo   | Evolução compatível |

## Adicionado

* Painel administrativo local em `/printAgent`.
* Resumo do status, versão, sistema operacional, fila e impressoras.
* Consulta visual das impressoras e seus estados.
* Consulta visual dos trabalhos atuais e do histórico.
* Visualização das últimas linhas do log.
* Atualização periódica do status quando a página está visível.
* Interface responsiva, offline e sem dependências externas.

---

# 7. Versão 1.1.0

## Informações

| Campo  | Valor               |
| ------ | ------------------- |
| Versão | 1.1.0               |
| Data   |                     |
| Status | PLANEJADA           |
| Tipo   | Evolução compatível |

---

## Objetivo

Melhorar diagnóstico e suporte técnico.

---

## Itens planejados

### Adicionado

* Diagnóstico detalhado do ambiente.
* Teste de acesso ao CUPS.
* Teste do spooler do Windows.
* Teste de permissão da impressora.
* Teste do diretório temporário.
* Exportação de relatório técnico.
* Indicadores de operação degradada.
* Melhor visualização de logs.
* Informações adicionais no endpoint de status.

### Alterado

* Mensagens de diagnóstico mais objetivas.
* Dashboard com alertas de ambiente.
* Melhor detalhamento de erros de Provider.

---

## Compatibilidade

A API v1 deverá permanecer compatível.

---

# 8. Versão 1.2.0

## Informações

| Campo  | Valor               |
| ------ | ------------------- |
| Versão | 1.2.0               |
| Data   |                     |
| Status | PLANEJADA           |
| Tipo   | Evolução compatível |

---

## Objetivo

Aprimorar compatibilidade de impressão e drivers.

---

## Itens candidatos

### Adicionado

* Impressão PDF no Windows, condicionada à homologação dos mecanismos disponíveis.
* Melhor mapeamento de atributos no Windows.
* Suporte ampliado a cópias.
* Cancelamento no spooler quando suportado.
* Cache de capacidades.
* Atualização periódica do catálogo.
* Novos testes internos.
* Diagnóstico de impressoras offline.
* Melhor normalização de status.

### Alterado

* Estratégia de capacidades.
* Tratamento de configurações não suportadas.
* Mensagens de incompatibilidade.

---

# 9. Versão 1.3.0

## Informações

| Campo  | Valor               |
| ------ | ------------------- |
| Versão | 1.3.0               |
| Data   |                     |
| Status | EM_ANALISE          |
| Tipo   | Evolução compatível |

---

## Objetivo

Adicionar novos formatos quando houver necessidade real.

---

## Itens em análise

```text
image/png
image/jpeg
application/epl
application/cpcl
application/escpos
```

---

## Observação

Nenhum formato será implementado apenas por antecipação.

Cada formato deverá possuir:

* caso real;
* estratégia;
* Provider compatível;
* testes;
* documentação;
* critérios de aceite.

---

# 10. Versão 1.4.0

## Informações

| Campo  | Valor               |
| ------ | ------------------- |
| Versão | 1.4.0               |
| Data   |                     |
| Status | ADIADA              |
| Tipo   | Evolução compatível |

---

## Objetivo

Adicionar perfis de impressão reutilizáveis.

---

## Itens em análise

* Nome lógico.
* Impressora física.
* Configurações padrão.
* Tipo de conteúdo.
* Consulta por perfil.
* Uso opcional no trabalho.

---

## Motivo do adiamento

A versão 1 utilizará diretamente o nome real da impressora e configurações por trabalho.

Perfis somente deverão ser adicionados quando houver necessidade comprovada.

---

# 11. Versão 1.5.0

## Informações

| Campo  | Valor               |
| ------ | ------------------- |
| Versão | 1.5.0               |
| Data   |                     |
| Status | ADIADA              |
| Tipo   | Evolução compatível |

---

## Objetivo

Adicionar persistência opcional.

---

## Itens em análise

* Recuperação da fila.
* Histórico persistente.
* Persistência de metadados.
* Retenção controlada.
* Recuperação após reinicialização.

---

## Fora do escopo inicial

* Armazenamento indefinido de documentos.
* Banco remoto.
* Sincronização com nuvem.

---

# 12. Versão 2.0.0

## Informações

| Campo  | Valor                 |
| ------ | --------------------- |
| Versão | 2.0.0                 |
| Data   |                       |
| Status | EM_ANALISE            |
| Tipo   | Evolução incompatível |

---

## Possíveis itens

* API v2.
* Autenticação local.
* HTTPS local.
* Múltiplas filas.
* Prioridades.
* Painel central.
* Múltiplos agentes.
* Descoberta de agentes.
* Gestão de frota.
* Políticas corporativas.

---

## Observação

Nenhum item da versão 2 está automaticamente aprovado.

A criação da versão 2 dependerá de requisitos reais e revisão completa da arquitetura.

---

# 13. Modelo para novas versões

Copiar o modelo abaixo.

```markdown
# X. Versão X.Y.Z

## Informações

| Campo | Valor |
|---|---|
| Versão | X.Y.Z |
| Data | DD/MM/AAAA |
| Status | PLANEJADA |
| API | v1 |

## Resumo

Descrição resumida.

## Adicionado

- Item.

## Alterado

- Item.

## Corrigido

- Item.

## Segurança

- Item.

## Removido

- Item.

## Obsoleto

- Item.

## Limitações conhecidas

- Item.

## Atualização

- Instrução.
```

---

# 14. Regras de preenchimento

## 14.1 Data

Versões publicadas deverão possuir data no formato:

```text
DD/MM/AAAA
```

---

## 14.2 Status

Versão publicada:

```text
PUBLICADA
```

Versão em desenvolvimento:

```text
EM_DESENVOLVIMENTO
```

---

## 14.3 Itens vazios

Categorias sem alterações poderão ser omitidas.

---

## 14.4 Clareza

Cada item deverá ser objetivo.

Exemplo adequado:

```text
Corrigida a descoberta de impressoras quando o serviço Linux é executado pelo usuário printagent.
```

Exemplo inadequado:

```text
Melhorias gerais.
```

---

## 14.5 Compatibilidade

Alterações incompatíveis deverão ser destacadas.

Exemplo:

```text
ALTERAÇÃO INCOMPATÍVEL:
A propriedade "nomeImpressora" foi substituída por "impressora.nome".
```

---

## 14.6 Referência a problemas

Quando existir sistema de issues, o item poderá incluir referência.

Exemplo:

```text
Corrigido timeout no CUPS. (#123)
```

---

# 15. Processo de publicação

Antes de publicar uma versão:

1. atualizar a versão no `pom.xml`;
2. atualizar este arquivo;
3. atualizar o `ROADMAP.md`;
4. atualizar documentação afetada;
5. executar testes;
6. gerar instaladores;
7. validar hashes;
8. publicar manifesto;
9. instalar em ambiente limpo;
10. testar atualização da versão anterior;
11. marcar a versão como `PUBLICADA`;
12. criar tag no repositório.

---

# 16. Tags do repositório

Formato sugerido:

```text
print-agent-v1.0.0
```

Exemplos:

```text
print-agent-v1.0.0
print-agent-v1.0.1
print-agent-v1.1.0
```

---

# 17. Branches

Estratégia simples sugerida:

```text
main
develop
feature/*
fix/*
release/*
```

A adoção exata poderá seguir o padrão já utilizado no repositório Presente.

---

# 18. Manifesto de atualização

O manifesto deverá refletir uma versão publicada.

Exemplo:

```json
{
    "produto": "print-agent",
    "versao": "1.0.0",
    "canal": "estavel",
    "publicadoEm": "2026-07-15T10:00:00-03:00",
    "notas": "Primeira versão funcional.",
    "artefatos": {
        "windows": {
            "url": "https://downloads.exemplo.com/print-agent/1.0.0/print-agent.msi",
            "sha256": "HASH"
        },
        "linux-deb": {
            "url": "https://downloads.exemplo.com/print-agent/1.0.0/print-agent_1.0.0_amd64.deb",
            "sha256": "HASH"
        }
    }
}
```

A versão do manifesto deverá existir neste Changelog.

---

# 19. Compatibilidade documental

Quando uma versão alterar contratos, os seguintes documentos deverão ser revisados:

```text
docs/arquitetura/ESPECIFICACAO_PRINT_AGENT.md
docs/api/API.md
docs/api/JSON.md
docs/web/WEBUI.md
docs/instalacao/INSTALACAO.md
docs/roadmap/ROADMAP.md
docs/roadmap/CHANGELOG.md
```

---

# 20. Referências

Roadmap:

```text
docs/roadmap/ROADMAP.md
```

Especificação:

```text
docs/arquitetura/ESPECIFICACAO_PRINT_AGENT.md
```

API:

```text
docs/api/API.md
```

Contratos JSON:

```text
docs/api/JSON.md
```

Instalação:

```text
docs/instalacao/INSTALACAO.md
```

---
