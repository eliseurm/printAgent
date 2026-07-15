# Print Agent — API REST

## Documento de referência da API

| Campo               | Valor                           |
| ------------------- | ------------------------------- |
| Projeto             | Print Agent                     |
| Sistema relacionado | Presente                        |
| Documento           | API.md                          |
| Versão da API       | v1                              |
| Versão do documento | 1.0.0                           |
| Java                | 17 LTS                          |
| Namespace           | `br.eng.eliseu.printagent`      |
| Endereço padrão     | `http://localhost:18181`        |
| URL base            | `http://localhost:18181/api/v1` |
| Formato             | JSON UTF-8                      |
| Status              | Aprovado para implementação     |
| Última atualização  | Julho de 2026                   |

---

# 1. Objetivo

Este documento especifica os endpoints REST disponibilizados pelo Print Agent.

A API permite que uma aplicação executada no navegador envie trabalhos de impressão para uma impressora instalada no computador do usuário.

O Print Agent é executado localmente e atua como intermediário entre:

```text
Aplicação Web
      |
      | HTTP/JSON
      v
Print Agent
      |
      | CUPS ou Java Print Service
      v
Impressora local
```

A API não possui regras de negócio relacionadas ao sistema Presente.

---

# 2. Endereço da API

Endereço padrão:

```text
http://localhost:18181/api/v1
```

Exemplo de endpoint completo:

```text
http://localhost:18181/api/v1/trabalhos
```

A porta poderá ser alterada pelo arquivo externo `application.yml`.

O frontend não deverá assumir que o agente está disponível em outro computador.

---

# 3. Formato das requisições

As requisições com corpo deverão utilizar:

```http
Content-Type: application/json
Accept: application/json
```

Codificação:

```text
UTF-8
```

Exemplo:

```http
POST /api/v1/trabalhos HTTP/1.1
Host: localhost:18181
Content-Type: application/json
Accept: application/json
```

---

# 4. Envelope padrão de resposta

Todas as respostas com corpo deverão utilizar o seguinte envelope:

```json
{
    "sucesso": true,
    "mensagem": "Operação realizada com sucesso.",
    "dados": {},
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

## 4.1 Propriedades

| Propriedade | Tipo            | Obrigatório | Descrição                                               |
| ----------- | --------------- | ----------: | ------------------------------------------------------- |
| sucesso     | Boolean         |         Sim | Indica se a operação principal foi aceita ou concluída. |
| mensagem    | String          |         Sim | Mensagem resumida e amigável.                           |
| dados       | Object ou Array |         Sim | Conteúdo específico do endpoint.                        |
| erros       | Array           |         Sim | Lista de erros identificados.                           |
| dataHora    | String ISO-8601 |         Sim | Data e hora da resposta com fuso horário.               |

## 4.2 Resposta de sucesso

```json
{
    "sucesso": true,
    "mensagem": "Trabalho recebido com sucesso.",
    "dados": {
        "idTrabalho": "8fd1b4fb-21db-4e3f-94b2-dfb83b2d3f0d",
        "status": "RECEBIDO"
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

## 4.3 Resposta de erro

```json
{
    "sucesso": false,
    "mensagem": "Não foi possível criar o trabalho de impressão.",
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

## 4.4 Regras do envelope

* `mensagem` nunca poderá ser vazia.
* `erros` nunca poderá ser `null`.
* Quando não existirem erros, retornar `[]`.
* `dados` nunca poderá ser `null`.
* Quando não existirem dados, retornar `{}`.
* Respostas HTTP `204 No Content` não deverão possuir corpo.

---

# 5. Códigos HTTP

| Código | Nome                   | Utilização                                      |
| -----: | ---------------------- | ----------------------------------------------- |
|    200 | OK                     | Consulta ou operação concluída.                 |
|    201 | Created                | Trabalho de impressão criado.                   |
|    204 | No Content             | Operação concluída sem corpo de resposta.       |
|    400 | Bad Request            | JSON inválido, campo ausente ou valor inválido. |
|    404 | Not Found              | Impressora, trabalho ou recurso não encontrado. |
|    405 | Method Not Allowed     | Método HTTP não suportado.                      |
|    409 | Conflict               | Estado atual impede a operação.                 |
|    413 | Payload Too Large      | Requisição ou documento excede o limite.        |
|    415 | Unsupported Media Type | Tipo de documento não suportado.                |
|    422 | Unprocessable Entity   | Configuração incompatível ou não suportada.     |
|    500 | Internal Server Error  | Erro inesperado.                                |
|    503 | Service Unavailable    | Provider indisponível ou agente encerrando.     |

---

# 6. Estados de trabalho

Valores possíveis:

```text
RECEBIDO
VALIDADO
NA_FILA
IMPRIMINDO
CONCLUIDO
ERRO
CANCELADO
```

Fluxo principal:

```text
RECEBIDO
    |
    v
VALIDADO
    |
    v
NA_FILA
    |
    v
IMPRIMINDO
    |
    v
CONCLUIDO
```

Estados finais:

```text
CONCLUIDO
ERRO
CANCELADO
```

O estado `CONCLUIDO` significa que o sistema de impressão aceitou o trabalho sem erro imediato.

Ele não garante que o papel ou a etiqueta tenha saído fisicamente da impressora.

---

# 7. Resumo dos endpoints

| Método | Endpoint                          | Descrição                           |
| ------ | --------------------------------- | ----------------------------------- |
| GET    | `/status`                         | Consulta o estado do agente.        |
| POST   | `/trabalhos`                      | Cria um trabalho de impressão.      |
| GET    | `/trabalhos`                      | Lista os trabalhos atuais.          |
| GET    | `/trabalhos/{id}`                 | Consulta um trabalho.               |
| DELETE | `/trabalhos/{id}`                 | Cancela um trabalho.                |
| GET    | `/historico`                      | Lista trabalhos finalizados.        |
| DELETE | `/historico`                      | Limpa o histórico em memória.       |
| GET    | `/impressoras`                    | Lista as impressoras disponíveis.   |
| POST   | `/impressoras/atualizar`          | Atualiza o catálogo de impressoras. |
| GET    | `/impressoras/{nome}`             | Consulta uma impressora.            |
| GET    | `/impressoras/{nome}/capacidades` | Consulta opções do driver.          |
| POST   | `/testes/impressao`               | Cria uma impressão de teste.        |
| GET    | `/configuracoes`                  | Consulta configurações editáveis.   |
| PUT    | `/configuracoes`                  | Atualiza configurações editáveis.   |
| GET    | `/logs`                           | Consulta as últimas linhas do log.  |
| GET    | `/logs/arquivos`                  | Lista arquivos de log.              |
| GET    | `/logs/arquivos/{nome}`           | Baixa um arquivo de log.            |
| GET    | `/atualizacoes`                   | Consulta atualização disponível.    |
| POST   | `/atualizacoes/verificar`         | Força a verificação de atualização. |

---

# 8. Status do agente

## 8.1 Consultar status

```http
GET /api/v1/status
```

Retorna o estado atual do Print Agent.

### Resposta de sucesso

HTTP:

```text
200 OK
```

```json
{
    "sucesso": true,
    "mensagem": "Status consultado com sucesso.",
    "dados": {
        "status": "OPERACIONAL",
        "versao": "1.0.0",
        "java": "17",
        "sistemaOperacional": "Linux",
        "porta": 18181,
        "trabalhosNaFila": 0,
        "impressorasEncontradas": 5,
        "iniciadoEm": "2026-07-15T08:00:00-03:00"
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

### Possíveis estados

```text
INICIANDO
OPERACIONAL
PARANDO
FINALIZADO
```

### Exemplo com cURL

```bash
curl \
  -X GET \
  -H "Accept: application/json" \
  http://localhost:18181/api/v1/status
```

---

# 9. Trabalhos de impressão

## 9.1 Criar trabalho

```http
POST /api/v1/trabalhos
```

Cria um novo trabalho de impressão.

O trabalho deverá ser processado pela fila interna.

O Controller não deverá imprimir diretamente.

## 9.1.1 Requisição ZPL

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

## 9.1.2 Requisição PDF

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

## 9.1.3 Requisição RAW

```json
{
    "impressora": {
        "nome": "ElginL42"
    },
    "configuracoesImpressao": {},
    "trabalho": {
        "nome": "Teste RAW",
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

## 9.1.4 Resposta assíncrona

HTTP:

```text
201 Created
```

```json
{
    "sucesso": true,
    "mensagem": "Trabalho recebido com sucesso.",
    "dados": {
        "idTrabalho": "8fd1b4fb-21db-4e3f-94b2-dfb83b2d3f0d",
        "status": "RECEBIDO"
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

## 9.1.5 Modo síncrono

Quando:

```json
"sincrono": true
```

a requisição aguardará até:

* conclusão;
* erro;
* cancelamento;
* timeout configurado.

O trabalho continuará passando pela fila.

### Resposta síncrona concluída

```json
{
    "sucesso": true,
    "mensagem": "Trabalho concluído com sucesso.",
    "dados": {
        "idTrabalho": "8fd1b4fb-21db-4e3f-94b2-dfb83b2d3f0d",
        "status": "CONCLUIDO"
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:02-03:00"
}
```

### Timeout síncrono

Quando o timeout for atingido, o trabalho continuará na fila.

HTTP:

```text
200 OK
```

```json
{
    "sucesso": true,
    "mensagem": "O trabalho continua em processamento.",
    "dados": {
        "idTrabalho": "8fd1b4fb-21db-4e3f-94b2-dfb83b2d3f0d",
        "status": "NA_FILA"
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:30-03:00"
}
```

## 9.1.6 Validações

| Campo                  | Regra                                   |
| ---------------------- | --------------------------------------- |
| impressora.nome        | Obrigatório e deve existir no catálogo. |
| trabalho.nome          | Obrigatório, entre 1 e 100 caracteres.  |
| trabalho.copias        | Entre 1 e 999.                          |
| trabalho.sincrono      | Obrigatório.                            |
| documento.tipoConteudo | Deve ser suportado.                     |
| documento.codificacao  | Deve ser compatível com o tipo.         |
| documento.conteudo     | Obrigatório e não vazio.                |
| configuracoesImpressao | Opcional.                               |

## 9.1.7 Tipos suportados

| Tipo              | Codificação |
| ----------------- | ----------- |
| `application/zpl` | `utf-8`     |
| `application/pdf` | `base64`    |
| `text/plain`      | `utf-8`     |

Na versão 1.0.0, `application/pdf` é suportado somente pelo Provider Linux. No
Windows, a API deverá responder com
`TIPO_CONTEUDO_NAO_SUPORTADO_PELO_PROVIDER`. O suporte no Windows está planejado
para a versão 1.2.0.

## 9.1.8 Erro: impressora não encontrada

HTTP:

```text
404 Not Found
```

```json
{
    "sucesso": false,
    "mensagem": "Não foi possível criar o trabalho de impressão.",
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

## 9.1.9 Erro: tipo não suportado

HTTP:

```text
415 Unsupported Media Type
```

```json
{
    "sucesso": false,
    "mensagem": "O tipo de conteúdo informado não é suportado.",
    "dados": {},
    "erros": [
        {
            "codigo": "TIPO_CONTEUDO_NAO_SUPORTADO",
            "descricao": "O tipo 'image/png' não é suportado na API v1.",
            "campo": "documento.tipoConteudo"
        }
    ],
    "dataHora": "2026-07-15T18:42:00-03:00"
}
```

## 9.1.10 Exemplo com cURL

```bash
curl \
  -X POST \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  --data-binary @imprimirEtiqueta.json \
  http://localhost:18181/api/v1/trabalhos
```

---

## 9.2 Listar trabalhos atuais

```http
GET /api/v1/trabalhos
```

Retorna os trabalhos não removidos da fila operacional.

Parâmetros opcionais:

| Parâmetro  | Tipo    | Descrição                       |
| ---------- | ------- | ------------------------------- |
| status     | String  | Filtra pelo status.             |
| impressora | String  | Filtra pelo nome da impressora. |
| limite     | Integer | Limita a quantidade retornada.  |

Exemplo:

```text
GET /api/v1/trabalhos?status=NA_FILA&limite=20
```

### Resposta

```json
{
    "sucesso": true,
    "mensagem": "Trabalhos consultados com sucesso.",
    "dados": [
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
    ],
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

Embora a especificação geral prefira evitar `null`, datas ainda inexistentes poderão ser `null`.

Essa exceção deverá ser mantida de forma consistente para datas opcionais.

### Exemplo com cURL

```bash
curl \
  -X GET \
  -H "Accept: application/json" \
  "http://localhost:18181/api/v1/trabalhos?status=NA_FILA"
```

---

## 9.3 Consultar trabalho

```http
GET /api/v1/trabalhos/{id}
```

Exemplo:

```text
GET /api/v1/trabalhos/8fd1b4fb-21db-4e3f-94b2-dfb83b2d3f0d
```

### Resposta

```json
{
    "sucesso": true,
    "mensagem": "Trabalho consultado com sucesso.",
    "dados": {
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
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

### Trabalho não encontrado

HTTP:

```text
404 Not Found
```

```json
{
    "sucesso": false,
    "mensagem": "Trabalho não encontrado.",
    "dados": {},
    "erros": [
        {
            "codigo": "TRABALHO_NAO_ENCONTRADO",
            "descricao": "Não existe trabalho com o identificador informado.",
            "campo": "id"
        }
    ],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

---

## 9.4 Cancelar trabalho

```http
DELETE /api/v1/trabalhos/{id}
```

O cancelamento será permitido apenas nos estados:

```text
RECEBIDO
VALIDADO
NA_FILA
```

### Resposta

HTTP:

```text
200 OK
```

```json
{
    "sucesso": true,
    "mensagem": "Trabalho cancelado com sucesso.",
    "dados": {
        "idTrabalho": "8fd1b4fb-21db-4e3f-94b2-dfb83b2d3f0d",
        "status": "CANCELADO"
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

### Trabalho não cancelável

HTTP:

```text
409 Conflict
```

```json
{
    "sucesso": false,
    "mensagem": "O trabalho não pode ser cancelado.",
    "dados": {},
    "erros": [
        {
            "codigo": "TRABALHO_NAO_CANCELAVEL",
            "descricao": "O trabalho está no estado 'IMPRIMINDO'.",
            "campo": "id"
        }
    ],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

---

# 10. Histórico

## 10.1 Listar histórico

```http
GET /api/v1/historico
```

Retorna trabalhos nos estados finais:

```text
CONCLUIDO
ERRO
CANCELADO
```

Parâmetros opcionais:

| Parâmetro    | Tipo    | Descrição                 |
| ------------ | ------- | ------------------------- |
| status       | String  | Filtra pelo estado final. |
| impressora   | String  | Filtra pela impressora.   |
| tipoConteudo | String  | Filtra pelo tipo.         |
| limite       | Integer | Limita os resultados.     |

### Resposta

```json
{
    "sucesso": true,
    "mensagem": "Histórico consultado com sucesso.",
    "dados": [
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
    ],
    "erros": [],
    "dataHora": "2026-07-15T18:45:00-03:00"
}
```

---

## 10.2 Limpar histórico

```http
DELETE /api/v1/historico
```

Remove os registros mantidos em memória.

Não remove arquivos de log.

### Resposta

```json
{
    "sucesso": true,
    "mensagem": "Histórico limpo com sucesso.",
    "dados": {},
    "erros": [],
    "dataHora": "2026-07-15T18:45:00-03:00"
}
```

---

# 11. Impressoras

## 11.1 Listar impressoras

```http
GET /api/v1/impressoras
```

### Resposta

```json
{
    "sucesso": true,
    "mensagem": "Impressoras consultadas com sucesso.",
    "dados": [
        {
            "nome": "ElginL42",
            "padrao": true,
            "status": "READY"
        },
        {
            "nome": "EPSON-L3250-Series",
            "padrao": false,
            "status": "OFFLINE"
        }
    ],
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

Valores normalizados de status:

```text
READY
BUSY
OFFLINE
STOPPED
UNKNOWN
```

### Exemplo com cURL

```bash
curl \
  -X GET \
  -H "Accept: application/json" \
  http://localhost:18181/api/v1/impressoras
```

---

## 11.2 Atualizar catálogo de impressoras

```http
POST /api/v1/impressoras/atualizar
```

Solicita nova descoberta no sistema operacional.

### Resposta

```json
{
    "sucesso": true,
    "mensagem": "Catálogo de impressoras atualizado com sucesso.",
    "dados": {
        "quantidade": 5
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

### Provider indisponível

HTTP:

```text
503 Service Unavailable
```

```json
{
    "sucesso": false,
    "mensagem": "O sistema de impressão não está disponível.",
    "dados": {},
    "erros": [
        {
            "codigo": "PROVIDER_INDISPONIVEL",
            "descricao": "Não foi possível acessar o sistema de impressão.",
            "campo": ""
        }
    ],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

---

## 11.3 Consultar impressora

```http
GET /api/v1/impressoras/{nome}
```

O nome deverá ser codificado corretamente na URL.

Exemplo:

```text
GET /api/v1/impressoras/EPSON-L3250-Series
```

### Resposta

```json
{
    "sucesso": true,
    "mensagem": "Impressora consultada com sucesso.",
    "dados": {
        "nome": "ElginL42",
        "padrao": true,
        "status": "READY"
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

---

## 11.4 Consultar capacidades

```http
GET /api/v1/impressoras/{nome}/capacidades
```

### Resposta

```json
{
    "sucesso": true,
    "mensagem": "Capacidades consultadas com sucesso.",
    "dados": {
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
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

Quando as capacidades não estiverem disponíveis:

```json
{
    "sucesso": true,
    "mensagem": "A impressora foi localizada, mas suas capacidades não foram disponibilizadas.",
    "dados": {
        "nome": "ElginL42",
        "configuracoes": {}
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

---

# 12. Teste de impressão

## 12.1 Criar teste

```http
POST /api/v1/testes/impressao
```

Cria um trabalho real de impressão utilizando um conteúdo de teste interno.

## 12.1.1 Requisição

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

Valores de `tipoTeste`:

```text
ZPL
RAW
PDF
```

## 12.1.2 Resposta

```json
{
    "sucesso": true,
    "mensagem": "Teste de impressão criado com sucesso.",
    "dados": {
        "idTrabalho": "1cefaee0-79a9-4a48-8467-1d77cd4ed843",
        "status": "RECEBIDO"
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

O teste deverá seguir a mesma fila utilizada pelos demais trabalhos.

---

# 13. Configurações do agente

## 13.1 Consultar configurações

```http
GET /api/v1/configuracoes
```

Retorna somente propriedades autorizadas para consulta e edição.

### Resposta

```json
{
    "sucesso": true,
    "mensagem": "Configurações consultadas com sucesso.",
    "dados": {
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
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

---

## 13.2 Atualizar configurações

```http
PUT /api/v1/configuracoes
```

Atualiza apenas propriedades permitidas.

### Requisição

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

### Resposta

```json
{
    "sucesso": true,
    "mensagem": "Configurações atualizadas com sucesso.",
    "dados": {
        "reinicializacaoNecessaria": false
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

### Valor inválido

```json
{
    "sucesso": false,
    "mensagem": "Existem configurações inválidas.",
    "dados": {},
    "erros": [
        {
            "codigo": "VALOR_INVALIDO",
            "descricao": "O tamanho máximo do histórico deve ser maior que zero.",
            "campo": "historico.tamanhoMaximo"
        }
    ],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

---

# 14. Logs

## 14.1 Consultar últimas linhas

```http
GET /api/v1/logs
```

Parâmetros:

| Parâmetro | Tipo    | Padrão | Descrição                    |
| --------- | ------- | -----: | ---------------------------- |
| linhas    | Integer |    500 | Quantidade máxima de linhas. |
| nivel     | String  |  vazio | Filtra pelo nível.           |
| busca     | String  |  vazio | Filtra pelo texto.           |

Exemplo:

```text
GET /api/v1/logs?linhas=200&nivel=ERROR
```

### Resposta

```json
{
    "sucesso": true,
    "mensagem": "Logs consultados com sucesso.",
    "dados": {
        "arquivo": "print-agent.log",
        "linhas": [
            "2026-07-15T18:30:10.100-03:00 INFO [main] Aplicacao iniciada.",
            "2026-07-15T18:31:20.100-03:00 ERROR [print-worker] Falha de impressão."
        ]
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

---

## 14.2 Listar arquivos

```http
GET /api/v1/logs/arquivos
```

### Resposta

```json
{
    "sucesso": true,
    "mensagem": "Arquivos de log consultados com sucesso.",
    "dados": [
        {
            "nome": "print-agent.log",
            "tamanhoBytes": 143220,
            "modificadoEm": "2026-07-15T18:39:00-03:00"
        },
        {
            "nome": "print-agent.2026-07-14.log.gz",
            "tamanhoBytes": 45200,
            "modificadoEm": "2026-07-15T00:00:00-03:00"
        }
    ],
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

---

## 14.3 Baixar arquivo

```http
GET /api/v1/logs/arquivos/{nome}
```

A resposta será binária.

Exemplo de cabeçalhos:

```http
Content-Type: application/octet-stream
Content-Disposition: attachment; filename="print-agent.log"
```

O nome deverá ser validado para impedir acesso a arquivos fora do diretório de logs.

Entradas como:

```text
../application.yml
```

deverão ser rejeitadas.

---

# 15. Atualizações

## 15.1 Consultar estado da atualização

```http
GET /api/v1/atualizacoes
```

### Resposta sem atualização

```json
{
    "sucesso": true,
    "mensagem": "O Print Agent está atualizado.",
    "dados": {
        "versaoAtual": "1.0.0",
        "versaoDisponivel": "1.0.0",
        "atualizacaoDisponivel": false,
        "notas": ""
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

### Resposta com atualização

```json
{
    "sucesso": true,
    "mensagem": "Existe uma nova versão disponível.",
    "dados": {
        "versaoAtual": "1.0.0",
        "versaoDisponivel": "1.1.0",
        "atualizacaoDisponivel": true,
        "notas": "Correções de impressão no Windows."
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

---

## 15.2 Verificar atualização

```http
POST /api/v1/atualizacoes/verificar
```

Força nova consulta ao manifesto remoto.

### Resposta

```json
{
    "sucesso": true,
    "mensagem": "Verificação de atualização concluída.",
    "dados": {
        "versaoAtual": "1.0.0",
        "versaoDisponivel": "1.1.0",
        "atualizacaoDisponivel": true,
        "notas": "Correções de impressão no Windows."
    },
    "erros": [],
    "dataHora": "2026-07-15T18:40:00-03:00"
}
```

Falhas na verificação não deverão impedir o funcionamento da impressão.

---

# 16. CORS

O Print Agent deverá permitir somente origens configuradas.

Exemplo:

```yaml
print-agent:
  cors:
    origens-permitidas:
      - "https://presente.exemplo.com.br"
      - "http://localhost:4200"
```

Origem curinga não deverá ser utilizada em produção:

```text
*
```

Métodos permitidos:

```text
GET
POST
PUT
DELETE
OPTIONS
```

Cabeçalhos permitidos:

```text
Content-Type
Accept
```

---

# 17. Limites

Valores iniciais sugeridos:

| Item                       |         Limite |
| -------------------------- | -------------: |
| Requisição HTTP            |          20 MB |
| ZPL                        |           2 MB |
| RAW                        |           2 MB |
| PDF decodificado           |          15 MB |
| Configurações por trabalho |             50 |
| Chave de configuração      | 200 caracteres |
| Valor de configuração      | 200 caracteres |
| Nome do trabalho           | 100 caracteres |
| Cópias                     |            999 |
| Histórico                  |  100 registros |

Requisições acima do limite deverão retornar:

```text
413 Payload Too Large
```

Código:

```text
REQUISICAO_MUITO_GRANDE
```

---

# 18. Códigos de erro

| Código                                    | HTTP padrão | Descrição                          |
| ----------------------------------------- | ----------: | ---------------------------------- |
| REQUISICAO_INVALIDA                       |         400 | Estrutura geral inválida.          |
| JSON_INVALIDO                             |         400 | JSON não pôde ser interpretado.    |
| CAMPO_OBRIGATORIO                         |         400 | Campo obrigatório ausente.         |
| VALOR_INVALIDO                            |         400 | Valor fora das regras.             |
| IDENTIFICADOR_INVALIDO                    |         400 | UUID ou identificador inválido.    |
| IMPRESSORA_NAO_ENCONTRADA                 |         404 | Impressora não localizada.         |
| IMPRESSORA_INDISPONIVEL                   |         409 | Impressora indisponível.           |
| TIPO_CONTEUDO_NAO_SUPORTADO               |         415 | Tipo não suportado.                |
| TIPO_CONTEUDO_NAO_SUPORTADO_PELO_PROVIDER |         415 | Provider não suporta o tipo.       |
| CODIFICACAO_INVALIDA                      |         400 | Codificação incompatível.          |
| CONTEUDO_INVALIDO                         |         400 | Conteúdo vazio ou inválido.        |
| TRABALHO_NAO_ENCONTRADO                   |         404 | Trabalho inexistente.              |
| TRABALHO_NAO_CANCELAVEL                   |         409 | Estado não permite cancelamento.   |
| CONFIGURACAO_IMPRESSAO_NAO_SUPORTADA      |         422 | Configuração não suportada.        |
| REQUISICAO_MUITO_GRANDE                   |         413 | Limite excedido.                   |
| METODO_NAO_PERMITIDO                      |         405 | Método HTTP não permitido.         |
| PROVIDER_INDISPONIVEL                     |         503 | Sistema de impressão indisponível. |
| AGENTE_EM_ENCERRAMENTO                    |         503 | Agente não aceita novos trabalhos. |
| FALHA_IMPRESSAO                           |         500 | Falha durante envio ao spooler.    |
| ERRO_INTERNO                              |         500 | Erro inesperado.                   |

---

# 19. Regras de idempotência

O endpoint:

```text
POST /api/v1/trabalhos
```

não será idempotente na versão 1.

Cada requisição válida cria um novo trabalho.

O sistema cliente deverá impedir:

* duplo clique;
* reenvio automático;
* repetição após timeout sem consulta prévia;
* duplicidade por falha de rede.

A API não deverá repetir automaticamente uma impressão após erro.

---

# 20. Regras de cache

Os seguintes endpoints não deverão ser armazenados em cache:

```text
/status
/trabalhos
/historico
/impressoras
/configuracoes
/logs
/atualizacoes
```

Cabeçalho sugerido:

```http
Cache-Control: no-store
```

Recursos estáticos da interface Web poderão utilizar cache baseado em versão.

---

# 21. OpenAPI

A versão 1 poderá disponibilizar documentação OpenAPI gerada automaticamente, desde que isso não altere os contratos deste documento.

Endereços sugeridos:

```text
/api-docs
/swagger-ui
```

A geração de OpenAPI é opcional.

Este arquivo permanece como referência normativa principal.

---

# 22. Exemplos JavaScript

## 22.1 Listar impressoras

```javascript
async function listarImpressoras() {
    const resposta = await fetch(
        "http://localhost:18181/api/v1/impressoras",
        {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        }
    );

    const corpo = await resposta.json();

    if (!resposta.ok || !corpo.sucesso) {
        throw new Error(corpo.mensagem);
    }

    return corpo.dados;
}
```

---

## 22.2 Criar trabalho ZPL

```javascript
async function imprimirEtiqueta(zpl) {
    const requisicao = {
        impressora: {
            nome: "ElginL42"
        },
        configuracoesImpressao: {
            PageSize: "w100h60",
            PrintDarkness: "20",
            PrintSpeed: "4"
        },
        trabalho: {
            nome: "Etiqueta Patrimônio",
            copias: 1,
            sincrono: false
        },
        documento: {
            tipoConteudo: "application/zpl",
            codificacao: "utf-8",
            conteudo: zpl
        }
    };

    const resposta = await fetch(
        "http://localhost:18181/api/v1/trabalhos",
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(requisicao)
        }
    );

    const corpo = await resposta.json();

    if (!resposta.ok || !corpo.sucesso) {
        const detalhes = corpo.erros
            ?.map(erro => erro.descricao)
            .join("; ");

        throw new Error(detalhes || corpo.mensagem);
    }

    return corpo.dados;
}
```

---

## 22.3 Consultar trabalho

```javascript
async function consultarTrabalho(idTrabalho) {
    const resposta = await fetch(
        `http://localhost:18181/api/v1/trabalhos/${idTrabalho}`,
        {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        }
    );

    const corpo = await resposta.json();

    if (!resposta.ok || !corpo.sucesso) {
        throw new Error(corpo.mensagem);
    }

    return corpo.dados;
}
```

---

# 23. Exemplo Angular

## Serviço simplificado

```typescript
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

interface EnvelopeResposta<T> {
    sucesso: boolean;
    mensagem: string;
    dados: T;
    erros: ErroApi[];
    dataHora: string;
}

interface ErroApi {
    codigo: string;
    descricao: string;
    campo: string;
}

interface TrabalhoCriado {
    idTrabalho: string;
    status: string;
}

@Injectable({
    providedIn: 'root'
})
export class PrintAgentService {

    private readonly urlBase =
        'http://localhost:18181/api/v1';

    constructor(
        private readonly http: HttpClient
    ) {
    }

    imprimir(
        requisicao: unknown
    ): Observable<EnvelopeResposta<TrabalhoCriado>> {
        return this.http.post<EnvelopeResposta<TrabalhoCriado>>(
            `${this.urlBase}/trabalhos`,
            requisicao
        );
    }
}
```

O frontend deverá tratar separadamente:

* falha de conexão;
* agente não instalado;
* agente parado;
* erro HTTP;
* envelope com `sucesso = false`.

---

# 24. Falha de conexão com o agente

Quando o navegador não conseguir acessar:

```text
http://localhost:18181
```

a aplicação cliente deverá apresentar uma mensagem semelhante a:

```text
Não foi possível conectar ao Print Agent.

Verifique se o agente está instalado e em execução neste computador.
```

A aplicação cliente não deverá interpretar essa falha como erro da impressora.

---

# 25. Versionamento

A API inicial será:

```text
/api/v1
```

Alterações incompatíveis deverão criar:

```text
/api/v2
```

São alterações incompatíveis:

* remoção de campo;
* alteração de nome;
* alteração de tipo;
* transformação de campo opcional em obrigatório;
* alteração do envelope;
* alteração do significado de um campo;
* remoção de endpoint.

Novos campos opcionais e novos endpoints poderão ser adicionados à API v1.

---

# 26. Critérios de aceite da API

A API será considerada pronta quando:

1. `/status` responder corretamente;
2. impressoras puderem ser listadas;
3. capacidades puderem ser consultadas;
4. trabalhos ZPL puderem ser criados;
5. trabalhos PDF puderem ser criados no Provider Linux;
6. trabalhos RAW puderem ser criados;
7. trabalhos puderem ser consultados;
8. trabalhos pendentes puderem ser cancelados;
9. o histórico puder ser consultado;
10. erros utilizarem envelope padrão;
11. CORS respeitar as origens configuradas;
12. o agente não aceitar conexões de rede externa;
13. os limites de requisição forem aplicados;
14. nenhuma impressão ocorrer diretamente no Controller;
15. os exemplos deste documento forem compatíveis com a implementação.

---

# 27. Referências

Documento principal:

```text
docs/arquitetura/ESPECIFICACAO_PRINT_AGENT.md
```

Contratos JSON detalhados:

```text
docs/api/JSON.md
```

Interface Web:

```text
docs/web/WEBUI.md
```

Instalação:

```text
docs/instalacao/INSTALACAO.md
```

Exemplos:

```text
docs/examples/json/
docs/examples/zpl/
docs/examples/pdf/
```

---
