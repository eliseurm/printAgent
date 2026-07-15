# Print Agent — Contratos JSON

## Referência dos objetos de entrada e saída

| Campo               | Valor                       |
| ------------------- | --------------------------- |
| Projeto             | Print Agent                 |
| Sistema relacionado | Presente                    |
| Documento           | JSON.md                     |
| Versão da API       | v1                          |
| Versão do documento | 1.0.0                       |
| Java                | 17 LTS                      |
| Namespace           | `br.eng.eliseu.printagent`  |
| Formato             | JSON UTF-8                  |
| Status              | Aprovado para implementação |
| Última atualização  | Julho de 2026               |

---

# 1. Objetivo

Este documento define os contratos JSON utilizados pela API REST do Print Agent.

São especificados:

* objetos de entrada;
* objetos de saída;
* obrigatoriedade dos campos;
* tipos;
* limites;
* validações;
* enums;
* exemplos completos;
* regras de serialização;
* códigos de erro.

Este documento complementa:

```text
docs/arquitetura/ESPECIFICACAO_PRINT_AGENT.md
```

e:

```text
docs/api/API.md
```

Em caso de divergência entre exemplos informais e este documento, os contratos definidos aqui deverão ser utilizados como referência para os DTOs JSON.

---

# 2. Convenções gerais

## 2.1 Codificação

Todo JSON deverá utilizar:

```text
UTF-8
```

---

## 2.2 Nomenclatura

Propriedades definidas pelo Print Agent deverão utilizar:

```text
camelCase
```

Exemplos:

```text
idTrabalho
tipoConteudo
dataCriacao
configuracoesImpressao
```

---

## 2.3 Idioma das propriedades

As propriedades controladas pelo Print Agent deverão ser escritas em português.

Exemplos:

```text
impressora
trabalho
documento
copias
sucesso
mensagem
erros
```

As propriedades originadas diretamente de drivers ou sistemas operacionais deverão preservar o nome original.

Exemplos:

```text
PageSize
PrintDarkness
PrintSpeed
Orientation
MediaMethod
Resolution
```

---

## 2.4 Propriedades desconhecidas

Propriedades desconhecidas deverão causar rejeição da requisição.

Exemplo inválido:

```json
{
    "trabalho": {
        "copia": 2
    }
}
```

A propriedade correta é:

```json
{
    "trabalho": {
        "copias": 2
    }
}
```

Código de erro:

```text
REQUISICAO_INVALIDA
```

ou:

```text
VALOR_INVALIDO
```

conforme o mecanismo de validação utilizado.

---

## 2.5 Campos obrigatórios

Campos obrigatórios ausentes deverão gerar:

```text
CAMPO_OBRIGATORIO
```

Campos `String` obrigatórios não poderão:

* ser `null`;
* ser vazios;
* conter somente espaços.

---

## 2.6 Datas e horas

Datas e horas deverão utilizar ISO-8601 com deslocamento de fuso horário.

Exemplo:

```text
2026-07-15T18:40:00-03:00
```

Tipo Java recomendado:

```java
OffsetDateTime
```

---

## 2.7 Booleanos

Booleanos deverão utilizar:

```json
true
```

ou:

```json
false
```

Não utilizar:

```json
"sim"
```

```json
"nao"
```

```json
1
```

```json
0
```

---

## 2.8 Objetos e listas vazias

Listas sem elementos deverão ser retornadas como:

```json
[]
```

Objetos sem propriedades deverão ser retornados como:

```json
{}
```

---

## 2.9 Valores nulos

O uso de `null` deverá ser evitado.

Exceções permitidas na API v1:

* `dataInicio`, enquanto o trabalho ainda não começou;
* `dataTermino`, enquanto o trabalho ainda não terminou;
* outros campos temporais cuja ausência represente um estado válido.

Exemplo:

```json
{
    "dataInicio": null,
    "dataTermino": null
}
```

---

## 2.10 Conteúdo de documentos

O conteúdo do documento deverá ser enviado dentro de uma propriedade JSON do tipo `String`.

Não serão utilizados:

* `multipart/form-data`;
* upload separado;
* caminho local;
* URL remota;
* referência a arquivo do computador.

---

# 3. EnvelopeRespostaDTO

## 3.1 Objetivo

Representa a estrutura padrão das respostas da API.

Nome Java:

```text
EnvelopeRespostaDTO<T>
```

Pacote sugerido:

```text
br.eng.eliseu.printagent.dto
```

---

## 3.2 Estrutura

| Propriedade | Tipo JSON       | Tipo Java        | Obrigatório | Descrição                             |
| ----------- | --------------- | ---------------- | ----------: | ------------------------------------- |
| sucesso     | Boolean         | `Boolean`        |         Sim | Indica o resultado geral da operação. |
| mensagem    | String          | `String`         |         Sim | Resumo amigável da resposta.          |
| dados       | Object ou Array | `T`              |         Sim | Conteúdo específico do endpoint.      |
| erros       | Array           | `List<ErroDTO>`  |         Sim | Erros identificados.                  |
| dataHora    | String          | `OffsetDateTime` |         Sim | Momento em que a resposta foi gerada. |

---

## 3.3 Exemplo de sucesso

```json
{
    "sucesso": true,
    "mensagem": "Operação realizada com sucesso.",
    "dados": {},
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

---

## 3.4 Exemplo de sucesso com lista

```json
{
    "sucesso": true,
    "mensagem": "Impressoras consultadas com sucesso.",
    "dados": [
        {
            "nome": "ElginL42",
            "padrao": true,
            "status": "READY"
        }
    ],
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

---

## 3.5 Exemplo de erro

```json
{
    "sucesso": false,
    "mensagem": "Não foi possível concluir a operação.",
    "dados": {},
    "erros": [
        {
            "codigo": "IMPRESSORA_NAO_ENCONTRADA",
            "descricao": "A impressora 'ElginL42' não está instalada.",
            "campo": "impressora.nome"
        }
    ],
    "dataHora": "2026-07-15T18:41:00-03:00"
}
```

---

## 3.6 Regras

* `mensagem` nunca deverá ser vazia.
* `erros` nunca deverá ser `null`.
* `dados` nunca deverá ser `null`.
* `sucesso` deverá ser `false` quando existirem erros que impeçam a operação.
* O campo `dados` poderá conter objeto, lista, string, número ou booleano, conforme o endpoint.
* HTTP `204 No Content` não deverá retornar esse envelope.

---

# 4. ErroDTO

## 4.1 Objetivo

Representa um erro individual retornado pela API.

Nome Java:

```text
ErroDTO
```

---

## 4.2 Estrutura

| Propriedade | Tipo JSON | Tipo Java | Obrigatório | Descrição                                     |
| ----------- | --------- | --------- | ----------: | --------------------------------------------- |
| codigo      | String    | `String`  |         Sim | Código estável do erro.                       |
| descricao   | String    | `String`  |         Sim | Descrição amigável.                           |
| campo       | String    | `String`  |         Sim | Caminho do campo relacionado ou string vazia. |

---

## 4.3 Exemplo

```json
{
    "codigo": "CAMPO_OBRIGATORIO",
    "descricao": "O nome do trabalho deve ser informado.",
    "campo": "trabalho.nome"
}
```

---

## 4.4 Erro sem campo associado

```json
{
    "codigo": "PROVIDER_INDISPONIVEL",
    "descricao": "O sistema de impressão não está disponível.",
    "campo": ""
}
```

---

## 4.5 Convenção do campo

O caminho deverá acompanhar a estrutura JSON.

Exemplos:

```text
impressora.nome
trabalho.nome
trabalho.copias
documento.tipoConteudo
documento.codificacao
documento.conteudo
configuracoesImpressao.PageSize
```

---

# 5. TrabalhoImpressaoRequestDTO

## 5.1 Objetivo

Representa a requisição para criação de um trabalho de impressão.

Endpoint:

```text
POST /api/v1/trabalhos
```

Nome Java:

```text
TrabalhoImpressaoRequestDTO
```

---

## 5.2 Estrutura

| Propriedade            | Tipo JSON | Tipo Java                                            | Obrigatório |
| ---------------------- | --------- | ---------------------------------------------------- | ----------: |
| impressora             | Object    | `ImpressoraDTO`                                      |         Sim |
| configuracoesImpressao | Object    | `Map<String, String>` ou `ConfiguracoesImpressaoDTO` |         Não |
| trabalho               | Object    | `TrabalhoDTO`                                        |         Sim |
| documento              | Object    | `DocumentoDTO`                                       |         Sim |

---

## 5.3 Exemplo completo ZPL

```json
{
    "impressora": {
        "nome": "ElginL42"
    },
    "configuracoesImpressao": {
        "PageSize": "w100h60",
        "PrintDarkness": "20",
        "PrintSpeed": "4",
        "Orientation": "0"
    },
    "trabalho": {
        "nome": "Etiqueta Patrimônio",
        "copias": 2,
        "sincrono": false
    },
    "documento": {
        "tipoConteudo": "application/zpl",
        "codificacao": "utf-8",
        "conteudo": "^XA\n^PW800\n^LL480\n^FO20,20^A0N,30,30^FDPATRIMONIO 12345^FS\n^XZ"
    }
}
```

---

## 5.4 Exemplo sem configurações

```json
{
    "impressora": {
        "nome": "ElginL42"
    },
    "trabalho": {
        "nome": "Etiqueta simples",
        "copias": 1,
        "sincrono": false
    },
    "documento": {
        "tipoConteudo": "application/zpl",
        "codificacao": "utf-8",
        "conteudo": "^XA^FO20,20^FDTESTE^FS^XZ"
    }
}
```

Quando `configuracoesImpressao` for omitido, os padrões atuais da fila deverão ser utilizados.

---

# 6. ImpressoraDTO

## 6.1 Objetivo

Identifica a impressora de destino.

Nome Java:

```text
ImpressoraDTO
```

---

## 6.2 Estrutura

| Propriedade | Tipo JSON | Tipo Java | Obrigatório | Limite             |
| ----------- | --------- | --------- | ----------: | ------------------ |
| nome        | String    | `String`  |         Sim | 1 a 255 caracteres |

---

## 6.3 Exemplo

```json
{
    "nome": "ElginL42"
}
```

---

## 6.4 Validações

O nome:

* não poderá ser vazio;
* não poderá conter somente espaços;
* deverá corresponder exatamente a uma impressora retornada pela API;
* deverá ser tratado como identificador externo;
* não deverá ser normalizado pelo agente.

A comparação deverá respeitar o nome registrado pelo sistema operacional.

---

## 6.5 Exemplo inválido

```json
{
    "nome": ""
}
```

Erro:

```json
{
    "codigo": "CAMPO_OBRIGATORIO",
    "descricao": "O nome da impressora deve ser informado.",
    "campo": "impressora.nome"
}
```

---

# 7. ConfiguracoesImpressaoDTO

## 7.1 Objetivo

Representa as configurações temporárias do trabalho.

Nome Java sugerido:

```text
ConfiguracoesImpressaoDTO
```

O objeto poderá ser implementado internamente como:

```java
Map<String, String>
```

---

## 7.2 Estrutura

O objeto possui propriedades dinâmicas.

Exemplo:

```json
{
    "PageSize": "w100h60",
    "PrintDarkness": "20",
    "PrintSpeed": "4",
    "Orientation": "0"
}
```

---

## 7.3 Tipos

Todas as chaves deverão ser `String`.

Todos os valores deverão ser `String`.

Exemplo inválido:

```json
{
    "PrintDarkness": 20
}
```

Exemplo correto:

```json
{
    "PrintDarkness": "20"
}
```

---

## 7.4 Regras

* As configurações pertencem somente ao trabalho atual.
* Não deverão alterar permanentemente a impressora.
* O agente não deverá traduzir chaves.
* O agente não deverá converter valores.
* O agente não deverá conhecer previamente todas as opções.
* Propriedades específicas de fabricantes deverão ser permitidas.
* A validação semântica deverá ser delegada ao Provider.

---

## 7.5 Limites

| Item                       |  Limite padrão |
| -------------------------- | -------------: |
| Quantidade de propriedades |             50 |
| Tamanho da chave           | 200 caracteres |
| Tamanho do valor           | 200 caracteres |

---

## 7.6 Caracteres proibidos

Chaves e valores não poderão conter:

* quebras de linha;
* retorno de carro;
* caracteres de controle;
* byte nulo.

Chaves não poderão começar com:

```text
-
```

---

## 7.7 Configuração vazia

São aceitos:

```json
{}
```

ou ausência da propriedade `configuracoesImpressao`.

---

## 7.8 Exemplos comuns

### Tamanho de mídia

```json
{
    "PageSize": "w100h60"
}
```

### Etiqueta térmica

```json
{
    "PageSize": "w100h60",
    "PrintDarkness": "20",
    "PrintSpeed": "4",
    "Orientation": "0"
}
```

### PDF A4

```json
{
    "PageSize": "A4",
    "Orientation": "Portrait"
}
```

O cliente deverá utilizar os valores retornados pelo endpoint de capacidades.

---

# 8. TrabalhoDTO

## 8.1 Objetivo

Representa os dados administrativos do trabalho.

Nome Java:

```text
TrabalhoDTO
```

---

## 8.2 Estrutura

| Propriedade | Tipo JSON      | Tipo Java | Obrigatório | Regra              |
| ----------- | -------------- | --------- | ----------: | ------------------ |
| nome        | String         | `String`  |         Sim | 1 a 100 caracteres |
| copias      | Number inteiro | `Integer` |         Sim | 1 a 999            |
| sincrono    | Boolean        | `Boolean` |         Sim | `true` ou `false`  |

---

## 8.3 Exemplo assíncrono

```json
{
    "nome": "Etiqueta Patrimônio",
    "copias": 2,
    "sincrono": false
}
```

---

## 8.4 Exemplo síncrono

```json
{
    "nome": "Teste imediato",
    "copias": 1,
    "sincrono": true
}
```

---

## 8.5 nome

O campo é administrativo.

Poderá ser utilizado em:

* fila;
* histórico;
* logs;
* interface Web;
* diagnóstico.

O nome não deverá alterar o conteúdo impresso.

---

## 8.6 copias

Valor mínimo:

```text
1
```

Valor máximo inicial:

```text
999
```

Exemplo inválido:

```json
{
    "copias": 0
}
```

Erro:

```json
{
    "codigo": "VALOR_INVALIDO",
    "descricao": "A quantidade de cópias deve estar entre 1 e 999.",
    "campo": "trabalho.copias"
}
```

---

## 8.7 sincrono

Quando `false`, a API retorna após criar e enfileirar o trabalho.

Quando `true`, a requisição aguarda:

* conclusão;
* erro;
* cancelamento;
* timeout.

Mesmo no modo síncrono, o trabalho deverá passar pela fila.

---

# 9. DocumentoDTO

## 9.1 Objetivo

Representa o documento a ser enviado ao sistema de impressão.

Nome Java:

```text
DocumentoDTO
```

---

## 9.2 Estrutura

| Propriedade  | Tipo JSON | Tipo Java | Obrigatório |
| ------------ | --------- | --------- | ----------: |
| tipoConteudo | String    | `String`  |         Sim |
| codificacao  | String    | `String`  |         Sim |
| conteudo     | String    | `String`  |         Sim |

---

## 9.3 Tipos suportados

| tipoConteudo      | codificacao |
| ----------------- | ----------- |
| `application/zpl` | `utf-8`     |
| `application/pdf` | `base64`    |
| `text/plain`      | `utf-8`     |

Na versão 1.0.0, `application/pdf` é aceito para processamento pelo Provider
Linux. O Provider Windows deverá rejeitá-lo com
`TIPO_CONTEUDO_NAO_SUPORTADO_PELO_PROVIDER` até a implementação planejada para a
versão 1.2.0.

---

## 9.4 ZPL

```json
{
    "tipoConteudo": "application/zpl",
    "codificacao": "utf-8",
    "conteudo": "^XA^FO20,20^FDTESTE^FS^XZ"
}
```

O agente não deverá:

* validar comandos;
* acrescentar comandos;
* remover comandos;
* rasterizar o conteúdo;
* alterar coordenadas;
* converter o layout.

---

## 9.5 PDF

```json
{
    "tipoConteudo": "application/pdf",
    "codificacao": "base64",
    "conteudo": "JVBERi0xLjQKJ..."
}
```

O conteúdo deverá ser uma representação Base64 válida.

O tamanho deverá ser validado após a decodificação.

---

## 9.6 RAW

```json
{
    "tipoConteudo": "text/plain",
    "codificacao": "utf-8",
    "conteudo": "TESTE PRINT AGENT"
}
```

O agente não deverá acrescentar:

* quebra de linha;
* comando de corte;
* avanço de papel;
* cabeçalho;
* rodapé.

---

## 9.7 Limites padrão

| Tipo             | Limite |
| ---------------- | -----: |
| ZPL              |   2 MB |
| RAW              |   2 MB |
| PDF decodificado |  15 MB |

---

## 9.8 Tipo inválido

```json
{
    "tipoConteudo": "image/png",
    "codificacao": "base64",
    "conteudo": "..."
}
```

Erro:

```text
TIPO_CONTEUDO_NAO_SUPORTADO
```

HTTP:

```text
415 Unsupported Media Type
```

---

## 9.9 Codificação inválida

```json
{
    "tipoConteudo": "application/pdf",
    "codificacao": "utf-8",
    "conteudo": "..."
}
```

Erro:

```text
CODIFICACAO_INVALIDA
```

---

# 10. TrabalhoResponseDTO

## 10.1 Objetivo

Representa a resposta resumida após criação ou cancelamento de um trabalho.

Nome Java:

```text
TrabalhoResponseDTO
```

---

## 10.2 Estrutura

| Propriedade | Tipo JSON   | Tipo Java        | Obrigatório |
| ----------- | ----------- | ---------------- | ----------: |
| idTrabalho  | String UUID | `UUID`           |         Sim |
| status      | String enum | `StatusTrabalho` |         Sim |

---

## 10.3 Exemplo

```json
{
    "idTrabalho": "8fd1b4fb-21db-4e3f-94b2-dfb83b2d3f0d",
    "status": "RECEBIDO"
}
```

---

# 11. TrabalhoConsultaResponseDTO

## 11.1 Objetivo

Representa um trabalho em consultas de fila e histórico.

Nome Java:

```text
TrabalhoConsultaResponseDTO
```

---

## 11.2 Estrutura

| Propriedade  | Tipo JSON               | Tipo Java        | Obrigatório |
| ------------ | ----------------------- | ---------------- | ----------: |
| idTrabalho   | String UUID             | `UUID`           |         Sim |
| nome         | String                  | `String`         |         Sim |
| impressora   | String                  | `String`         |         Sim |
| tipoConteudo | String                  | `String`         |         Sim |
| copias       | Number inteiro          | `Integer`        |         Sim |
| status       | String enum             | `StatusTrabalho` |         Sim |
| dataCriacao  | String ISO-8601         | `OffsetDateTime` |         Sim |
| dataInicio   | String ISO-8601 ou null | `OffsetDateTime` |         Não |
| dataTermino  | String ISO-8601 ou null | `OffsetDateTime` |         Não |
| mensagemErro | String                  | `String`         |         Sim |

---

## 11.3 Exemplo concluído

```json
{
    "idTrabalho": "8fd1b4fb-21db-4e3f-94b2-dfb83b2d3f0d",
    "nome": "Etiqueta Patrimônio",
    "impressora": "ElginL42",
    "tipoConteudo": "application/zpl",
    "copias": 2,
    "status": "CONCLUIDO",
    "dataCriacao": "2026-07-15T18:30:10-03:00",
    "dataInicio": "2026-07-15T18:30:11-03:00",
    "dataTermino": "2026-07-15T18:30:12-03:00",
    "mensagemErro": ""
}
```

---

## 11.4 Exemplo na fila

```json
{
    "idTrabalho": "013becc8-b555-4adf-9237-8d8336c00fa4",
    "nome": "Etiqueta Produto",
    "impressora": "ElginL42",
    "tipoConteudo": "application/zpl",
    "copias": 1,
    "status": "NA_FILA",
    "dataCriacao": "2026-07-15T18:35:00-03:00",
    "dataInicio": null,
    "dataTermino": null,
    "mensagemErro": ""
}
```

---

## 11.5 Exemplo com erro

```json
{
    "idTrabalho": "013becc8-b555-4adf-9237-8d8336c00fa4",
    "nome": "Etiqueta Produto",
    "impressora": "ElginL42",
    "tipoConteudo": "application/zpl",
    "copias": 1,
    "status": "ERRO",
    "dataCriacao": "2026-07-15T18:35:00-03:00",
    "dataInicio": "2026-07-15T18:35:01-03:00",
    "dataTermino": "2026-07-15T18:35:02-03:00",
    "mensagemErro": "O sistema de impressão não aceitou o trabalho."
}
```

---

## 11.6 Conteúdo omitido

Este DTO não deverá conter:

```text
documento.conteudo
```

nem o PDF Base64, ZPL ou RAW original.

---

# 12. ImpressoraResponseDTO

## 12.1 Objetivo

Representa uma impressora instalada.

Nome Java:

```text
ImpressoraResponseDTO
```

---

## 12.2 Estrutura

| Propriedade | Tipo JSON   | Tipo Java                      | Obrigatório |
| ----------- | ----------- | ------------------------------ | ----------: |
| nome        | String      | `String`                       |         Sim |
| padrao      | Boolean     | `Boolean`                      |         Sim |
| status      | String enum | `StatusImpressora` ou `String` |         Sim |

---

## 12.3 Exemplo

```json
{
    "nome": "ElginL42",
    "padrao": true,
    "status": "READY"
}
```

---

## 12.4 Status permitidos

```text
READY
BUSY
OFFLINE
STOPPED
UNKNOWN
```

---

## 12.5 Status desconhecido

Quando o Provider não conseguir interpretar o estado:

```json
{
    "nome": "Impressora Genérica",
    "padrao": false,
    "status": "UNKNOWN"
}
```

---

# 13. CapacidadesImpressoraResponseDTO

## 13.1 Objetivo

Representa as capacidades expostas pelo driver ou sistema operacional.

Nome Java:

```text
CapacidadesImpressoraResponseDTO
```

---

## 13.2 Estrutura

| Propriedade   | Tipo JSON       | Tipo Java                   | Obrigatório |
| ------------- | --------------- | --------------------------- | ----------: |
| nome          | String          | `String`                    |         Sim |
| configuracoes | Object dinâmico | `Map<String, List<String>>` |         Sim |

---

## 13.3 Exemplo

```json
{
    "nome": "ElginL42",
    "configuracoes": {
        "PageSize": [
            "Custom.WIDTHxHEIGHT",
            "w57h45",
            "w75h45",
            "w80h60",
            "w100h80",
            "w100h100",
            "w100h150"
        ],
        "Resolution": [
            "203dpi"
        ],
        "Orientation": [
            "0",
            "1",
            "2",
            "3"
        ],
        "PrintSpeed": [
            "None",
            "2",
            "3",
            "4"
        ],
        "PrintDarkness": [
            "None",
            "0",
            "1",
            "2",
            "3",
            "4",
            "5"
        ]
    }
}
```

---

## 13.4 Capacidades indisponíveis

```json
{
    "nome": "ElginL42",
    "configuracoes": {}
}
```

A ausência de capacidades não significa que a impressora não possa imprimir.

---

# 14. StatusAplicacaoResponseDTO

## 14.1 Objetivo

Representa o estado operacional do agente.

Nome Java:

```text
StatusAplicacaoResponseDTO
```

---

## 14.2 Estrutura

| Propriedade            | Tipo JSON       | Tipo Java         | Obrigatório |
| ---------------------- | --------------- | ----------------- | ----------: |
| status                 | String enum     | `StatusAplicacao` |         Sim |
| versao                 | String          | `String`          |         Sim |
| java                   | String          | `String`          |         Sim |
| sistemaOperacional     | String          | `String`          |         Sim |
| porta                  | Number inteiro  | `Integer`         |         Sim |
| trabalhosNaFila        | Number inteiro  | `Integer`         |         Sim |
| impressorasEncontradas | Number inteiro  | `Integer`         |         Sim |
| iniciadoEm             | String ISO-8601 | `OffsetDateTime`  |         Sim |

---

## 14.3 Exemplo

```json
{
    "status": "OPERACIONAL",
    "versao": "1.0.0",
    "java": "17",
    "sistemaOperacional": "Linux",
    "porta": 18181,
    "trabalhosNaFila": 0,
    "impressorasEncontradas": 5,
    "iniciadoEm": "2026-07-15T08:00:00-03:00"
}
```

---

# 15. TesteImpressaoRequestDTO

## 15.1 Objetivo

Representa uma solicitação de impressão de teste.

Nome Java:

```text
TesteImpressaoRequestDTO
```

---

## 15.2 Estrutura

| Propriedade            | Tipo JSON   | Tipo Java             | Obrigatório |
| ---------------------- | ----------- | --------------------- | ----------: |
| impressora             | Object      | `ImpressoraDTO`       |         Sim |
| tipoTeste              | String enum | `TipoTesteImpressao`  |         Sim |
| configuracoesImpressao | Object      | `Map<String, String>` |         Não |

---

## 15.3 Exemplo

```json
{
    "impressora": {
        "nome": "ElginL42"
    },
    "tipoTeste": "ZPL",
    "configuracoesImpressao": {
        "PageSize": "w100h60",
        "PrintDarkness": "20"
    }
}
```

---

## 15.4 Tipos permitidos

```text
ZPL
RAW
PDF
```

---

# 16. ConfiguracoesAgenteResponseDTO

## 16.1 Objetivo

Representa as configurações editáveis do agente.

Nome Java sugerido:

```text
ConfiguracoesAgenteResponseDTO
```

---

## 16.2 Estrutura completa

```json
{
    "historico": {
        "tamanhoMaximo": 100
    },
    "trabalho": {
        "timeoutSincronoSegundos": 30
    },
    "provider": {
        "timeoutComandoSegundos": 15
    },
    "impressao": {
        "configuracoesDesconhecidas": "IGNORAR"
    },
    "atualizacao": {
        "habilitada": true,
        "verificarAoIniciar": true
    },
    "log": {
        "nivel": "INFO"
    }
}
```

---

## 16.3 historico

```json
{
    "tamanhoMaximo": 100
}
```

| Propriedade   | Tipo    | Regra          |
| ------------- | ------- | -------------- |
| tamanhoMaximo | Integer | Maior que zero |

---

## 16.4 trabalho

```json
{
    "timeoutSincronoSegundos": 30
}
```

| Propriedade             | Tipo    | Regra         |
| ----------------------- | ------- | ------------- |
| timeoutSincronoSegundos | Integer | Entre 1 e 600 |

---

## 16.5 provider

```json
{
    "timeoutComandoSegundos": 15
}
```

| Propriedade            | Tipo    | Regra         |
| ---------------------- | ------- | ------------- |
| timeoutComandoSegundos | Integer | Entre 1 e 300 |

---

## 16.6 impressao

```json
{
    "configuracoesDesconhecidas": "IGNORAR"
}
```

Valores:

```text
IGNORAR
REJEITAR
```

No modo `IGNORAR`, propriedades de impressão não suportadas são desconsideradas e
o trabalho continua quando as propriedades válidas e os padrões do driver forem
suficientes. Se não forem suficientes, o trabalho falha com mensagem clara. No
modo `REJEITAR`, uma propriedade de impressão não suportada impede o envio.

---

## 16.7 atualizacao

```json
{
    "habilitada": true,
    "verificarAoIniciar": true
}
```

---

## 16.8 log

```json
{
    "nivel": "INFO"
}
```

Valores permitidos:

```text
ERROR
WARN
INFO
DEBUG
```

`TRACE` não deverá ser configurável pela interface na versão 1.

---

# 17. ConfiguracoesAgenteRequestDTO

## 17.1 Objetivo

Representa a alteração das configurações editáveis.

Nome Java:

```text
ConfiguracoesAgenteRequestDTO
```

A estrutura é equivalente à resposta de configurações.

---

## 17.2 Exemplo

```json
{
    "historico": {
        "tamanhoMaximo": 200
    },
    "trabalho": {
        "timeoutSincronoSegundos": 45
    },
    "provider": {
        "timeoutComandoSegundos": 20
    },
    "impressao": {
        "configuracoesDesconhecidas": "IGNORAR"
    },
    "atualizacao": {
        "habilitada": true,
        "verificarAoIniciar": true
    },
    "log": {
        "nivel": "INFO"
    }
}
```

---

# 18. ConfiguracoesAtualizadasResponseDTO

## 18.1 Objetivo

Representa o resultado da atualização das configurações.

Nome Java:

```text
ConfiguracoesAtualizadasResponseDTO
```

---

## 18.2 Estrutura

| Propriedade               | Tipo JSON | Tipo Java |
| ------------------------- | --------- | --------- |
| reinicializacaoNecessaria | Boolean   | `Boolean` |

---

## 18.3 Exemplo

```json
{
    "reinicializacaoNecessaria": false
}
```

---

# 19. LogsResponseDTO

## 19.1 Objetivo

Representa a consulta das últimas linhas do log.

Nome Java:

```text
LogsResponseDTO
```

---

## 19.2 Estrutura

| Propriedade | Tipo JSON       | Tipo Java      |
| ----------- | --------------- | -------------- |
| arquivo     | String          | `String`       |
| linhas      | Array de String | `List<String>` |

---

## 19.3 Exemplo

```json
{
    "arquivo": "print-agent.log",
    "linhas": [
        "2026-07-15T18:30:10.100-03:00 INFO [main] Aplicação iniciada.",
        "2026-07-15T18:31:20.100-03:00 ERROR [print-worker] Falha de impressão."
    ]
}
```

---

# 20. ArquivoLogResponseDTO

## 20.1 Objetivo

Representa um arquivo de log disponível.

Nome Java:

```text
ArquivoLogResponseDTO
```

---

## 20.2 Estrutura

| Propriedade  | Tipo JSON       | Tipo Java        |
| ------------ | --------------- | ---------------- |
| nome         | String          | `String`         |
| tamanhoBytes | Number inteiro  | `Long`           |
| modificadoEm | String ISO-8601 | `OffsetDateTime` |

---

## 20.3 Exemplo

```json
{
    "nome": "print-agent.2026-07-14.log.gz",
    "tamanhoBytes": 45200,
    "modificadoEm": "2026-07-15T00:00:00-03:00"
}
```

---

# 21. AtualizacaoResponseDTO

## 21.1 Objetivo

Representa o estado da atualização do agente.

Nome Java:

```text
AtualizacaoResponseDTO
```

---

## 21.2 Estrutura

| Propriedade           | Tipo JSON | Tipo Java |
| --------------------- | --------- | --------- |
| versaoAtual           | String    | `String`  |
| versaoDisponivel      | String    | `String`  |
| atualizacaoDisponivel | Boolean   | `Boolean` |
| notas                 | String    | `String`  |

---

## 21.3 Exemplo sem atualização

```json
{
    "versaoAtual": "1.0.0",
    "versaoDisponivel": "1.0.0",
    "atualizacaoDisponivel": false,
    "notas": ""
}
```

---

## 21.4 Exemplo com atualização

```json
{
    "versaoAtual": "1.0.0",
    "versaoDisponivel": "1.1.0",
    "atualizacaoDisponivel": true,
    "notas": "Correções de impressão no Windows."
}
```

---

# 22. Manifesto de atualização

## 22.1 Objetivo

Define o contrato do manifesto remoto utilizado pelo serviço de atualização.

---

## 22.2 Exemplo

```json
{
    "produto": "print-agent",
    "versao": "1.1.0",
    "canal": "estavel",
    "publicadoEm": "2026-09-01T10:00:00-03:00",
    "notas": "Correções de impressão no Windows.",
    "artefatos": {
        "windows": {
            "url": "https://downloads.exemplo.com/print-agent/1.1.0/print-agent.msi",
            "sha256": "HASH_SHA_256"
        },
        "linux-deb": {
            "url": "https://downloads.exemplo.com/print-agent/1.1.0/print-agent.deb",
            "sha256": "HASH_SHA_256"
        }
    }
}
```

---

## 22.3 ManifestoAtualizacaoDTO

| Propriedade | Tipo            | Obrigatório |
| ----------- | --------------- | ----------: |
| produto     | String          |         Sim |
| versao      | String          |         Sim |
| canal       | String          |         Sim |
| publicadoEm | String ISO-8601 |         Sim |
| notas       | String          |         Sim |
| artefatos   | Object          |         Sim |

---

## 22.4 ArtefatoAtualizacaoDTO

| Propriedade | Tipo   | Obrigatório |
| ----------- | ------ | ----------: |
| url         | String |         Sim |
| sha256      | String |         Sim |

A URL deverá utilizar:

```text
https
```

---

# 23. Enums

## 23.1 StatusTrabalho

```text
RECEBIDO
VALIDADO
NA_FILA
IMPRIMINDO
CONCLUIDO
ERRO
CANCELADO
```

---

## 23.2 StatusAplicacao

```text
INICIANDO
OPERACIONAL
PARANDO
FINALIZADO
```

---

## 23.3 StatusImpressora

```text
READY
BUSY
OFFLINE
STOPPED
UNKNOWN
```

---

## 23.4 TipoTesteImpressao

```text
ZPL
RAW
PDF
```

---

## 23.5 TratamentoConfiguracaoDesconhecida

```text
IGNORAR
REJEITAR
```

---

## 23.6 NivelLog

```text
ERROR
WARN
INFO
DEBUG
```

---

# 24. Códigos de erro

## 24.1 Lista inicial

```text
REQUISICAO_INVALIDA
JSON_INVALIDO
CAMPO_OBRIGATORIO
VALOR_INVALIDO
IDENTIFICADOR_INVALIDO
IMPRESSORA_NAO_ENCONTRADA
IMPRESSORA_INDISPONIVEL
TIPO_CONTEUDO_NAO_SUPORTADO
TIPO_CONTEUDO_NAO_SUPORTADO_PELO_PROVIDER
CODIFICACAO_INVALIDA
CONTEUDO_INVALIDO
TRABALHO_NAO_ENCONTRADO
TRABALHO_NAO_CANCELAVEL
CONFIGURACAO_IMPRESSAO_NAO_SUPORTADA
REQUISICAO_MUITO_GRANDE
METODO_NAO_PERMITIDO
PROVIDER_INDISPONIVEL
AGENTE_EM_ENCERRAMENTO
FALHA_IMPRESSAO
ERRO_INTERNO
```

---

## 24.2 Estabilidade

Códigos já publicados:

* não poderão ser removidos;
* não poderão ser renomeados;
* não poderão mudar de significado dentro da API v1.

---

# 25. Exemplos completos

## 25.1 Impressão ZPL

```json
{
    "impressora": {
        "nome": "ElginL42"
    },
    "configuracoesImpressao": {
        "PageSize": "w100h60",
        "PrintDarkness": "20",
        "PrintSpeed": "4",
        "Orientation": "0"
    },
    "trabalho": {
        "nome": "Etiqueta Patrimônio",
        "copias": 1,
        "sincrono": false
    },
    "documento": {
        "tipoConteudo": "application/zpl",
        "codificacao": "utf-8",
        "conteudo": "^XA\n^PW800\n^LL480\n^FO30,30^A0N,40,40^FDPATRIMONIO^FS\n^FO30,90^A0N,30,30^FD123456^FS\n^XZ"
    }
}
```

---

## 25.2 Impressão PDF

```json
{
    "impressora": {
        "nome": "EPSON-L3250-Series"
    },
    "configuracoesImpressao": {
        "PageSize": "A4",
        "Orientation": "Portrait"
    },
    "trabalho": {
        "nome": "Relatório de conferência",
        "copias": 1,
        "sincrono": false
    },
    "documento": {
        "tipoConteudo": "application/pdf",
        "codificacao": "base64",
        "conteudo": "JVBERi0xLjQKJ..."
    }
}
```

---

## 25.3 Texto RAW

```json
{
    "impressora": {
        "nome": "ElginL42"
    },
    "configuracoesImpressao": {},
    "trabalho": {
        "nome": "Teste de texto",
        "copias": 1,
        "sincrono": false
    },
    "documento": {
        "tipoConteudo": "text/plain",
        "codificacao": "utf-8",
        "conteudo": "TESTE PRINT AGENT"
    }
}
```

---

## 25.4 Erros múltiplos

```json
{
    "sucesso": false,
    "mensagem": "A requisição possui campos inválidos.",
    "dados": {},
    "erros": [
        {
            "codigo": "CAMPO_OBRIGATORIO",
            "descricao": "O nome da impressora deve ser informado.",
            "campo": "impressora.nome"
        },
        {
            "codigo": "VALOR_INVALIDO",
            "descricao": "A quantidade de cópias deve estar entre 1 e 999.",
            "campo": "trabalho.copias"
        },
        {
            "codigo": "CAMPO_OBRIGATORIO",
            "descricao": "O conteúdo do documento deve ser informado.",
            "campo": "documento.conteudo"
        }
    ],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

---

# 26. Anotações Java sugeridas

## 26.1 TrabalhoImpressaoRequestDTO

```java
public record TrabalhoImpressaoRequestDTO(
    @NotNull ImpressoraDTO impressora,
    Map<String, String> configuracoesImpressao,
    @NotNull TrabalhoDTO trabalho,
    @NotNull DocumentoDTO documento
) {
}
```

---

## 26.2 ImpressoraDTO

```java
public record ImpressoraDTO(
    @NotBlank
    @Size(max = 255)
    String nome
) {
}
```

---

## 26.3 TrabalhoDTO

```java
public record TrabalhoDTO(
    @NotBlank
    @Size(max = 100)
    String nome,

    @NotNull
    @Min(1)
    @Max(999)
    Integer copias,

    @NotNull
    Boolean sincrono
) {
}
```

---

## 26.4 DocumentoDTO

```java
public record DocumentoDTO(
    @NotBlank String tipoConteudo,
    @NotBlank String codificacao,
    @NotBlank String conteudo
) {
}
```

As validações semânticas deverão permanecer na camada de serviço.

---

# 27. Regras de serialização Jackson

## 27.1 Campos desconhecidos

A configuração deverá rejeitar propriedades desconhecidas.

Exemplo conceitual:

```yaml
spring:
  jackson:
    deserialization:
      fail-on-unknown-properties: true
```

---

## 27.2 Datas

As datas deverão ser serializadas em ISO-8601.

Não deverão ser serializadas como timestamp numérico.

---

## 27.3 Enums

Enums deverão ser serializados pelo nome exato.

Exemplo:

```json
{
    "status": "NA_FILA"
}
```

---

## 27.4 Propriedades vazias

Strings vazias deverão ser utilizadas somente quando o contrato exigir.

Listas e mapas vazios deverão permanecer visíveis quando fazem parte do envelope.

---

# 28. Compatibilidade

Alterações compatíveis:

* novo campo opcional;
* novo endpoint;
* novo código de erro;
* nova capacidade de driver;
* novo tipo de teste, quando clientes antigos puderem ignorá-lo.

Alterações incompatíveis:

* remover campo;
* renomear campo;
* alterar tipo;
* tornar obrigatório um campo opcional;
* alterar significado;
* mudar estrutura do envelope.

Alterações incompatíveis deverão criar:

```text
/api/v2
```

---

# 29. Critérios de aceite dos contratos JSON

Os contratos serão considerados implementados quando:

1. propriedades desconhecidas forem rejeitadas;
2. campos obrigatórios forem validados;
3. datas forem retornadas em ISO-8601;
4. enums forem serializados conforme documentado;
5. as respostas usarem o envelope padrão;
6. ZPL aceitar UTF-8;
7. PDF aceitar Base64 válido;
8. RAW aceitar UTF-8;
9. configurações de driver preservarem nomes originais;
10. o conteúdo do documento não aparecer nas respostas de consulta;
11. múltiplos erros de validação puderem ser retornados;
12. os exemplos deste documento forem aceitos pela implementação.

---

# 30. Referências

API REST:

```text
docs/api/API.md
```

Especificação principal:

```text
docs/arquitetura/ESPECIFICACAO_PRINT_AGENT.md
```

Exemplos JSON:

```text
docs/examples/json/
```

Exemplos ZPL:

```text
docs/examples/zpl/
```

Exemplos PDF:

```text
docs/examples/pdf/
```

---
