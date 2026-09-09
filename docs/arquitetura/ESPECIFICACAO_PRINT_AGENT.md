# Print Agent

# Especificação Técnica

**Documento:** ESPECIFICACAO_PRINT_AGENT.md

| Campo | Valor |
|---------|--------------------------------|
| Projeto | Print Agent |
| Sistema | Presente |
| Documento | ESPECIFICAÇÃO TÉCNICA |
| Tipo | Arquitetura + Especificação |
| Versão | 1.0.0 |
| Status | Aprovado para Implementação |
| Java | 17 LTS |
| Spring Boot | 3.x |
| Build | Maven |
| Namespace | br.eng.eliseu.printagent |
| Data | Julho/2026 |

---

# Histórico de versões

| Versão | Data | Autor | Alterações |
|---------|------------|----------------|--------------------------|
|1.0.0|Julho/2026|Eliseu Rodrigues Menezes / OpenAI|Versão inicial.|

---

# Índice

1. Objetivo

2. Escopo

3. Arquitetura Geral

4. Tecnologias

5. Requisitos Gerais

6. Referências

---

# 1. Objetivo

## 1.1 Finalidade

O Print Agent é um serviço local responsável por receber trabalhos de impressão através de uma API REST executando exclusivamente na máquina do usuário e encaminhá-los ao sistema de impressão do sistema operacional.

O agente foi desenvolvido para ser completamente desacoplado do sistema **Presente**, permitindo sua reutilização por qualquer aplicação que necessite realizar impressões locais.

O agente não possui conhecimento sobre:

- regras de negócio;
- modelos de etiquetas;
- JasperReports;
- patrimônio;
- produtos;
- documentos fiscais;
- qualquer outro domínio da aplicação.

Sua única responsabilidade consiste em receber um documento, encaminhá-lo ao mecanismo de impressão adequado e controlar seu ciclo de vida.

---

## 1.2 Objetivos

São objetivos do Print Agent:

- disponibilizar uma API REST simples;
- abstrair diferenças entre Windows e Linux;
- permitir impressão local sem utilização do navegador;
- permitir impressão silenciosa;
- eliminar dependência de PDF para etiquetas;
- permitir impressão de documentos ZPL;
- permitir impressão de PDF;
- permitir impressão RAW;
- disponibilizar uma interface Web para administração;
- possuir instalação simples;
- possuir atualização simples.

---

## 1.3 Objetivos de Arquitetura

A arquitetura deverá obedecer aos seguintes princípios.

### Simplicidade

O agente deverá conter apenas funcionalidades relacionadas à impressão.

Toda regra de negócio deverá permanecer no sistema Presente.

---

### Baixo Acoplamento

O agente nunca deverá conhecer informações específicas do sistema que o utiliza.

Não poderá existir qualquer classe contendo nomes como:

- Patrimonio
- Produto
- Processo
- Correios
- EtiquetaProduto
- Jasper

---

### Reutilização

O agente deverá ser reutilizável por qualquer sistema.

---

### Independência de Plataforma

O código deverá funcionar em:

- Windows

- Linux

sem necessidade de alteração da aplicação cliente.

---

### Evolução

Novos tipos de documento deverão poder ser adicionados sem alteração da API REST.

---

# 2. Escopo

## 2.1 Funcionalidades contempladas

Esta primeira versão contempla:

- API REST

- Interface Web

- Descoberta automática das impressoras

- Consulta das capacidades da impressora

- Fila de impressão

- Impressão assíncrona

- Consulta de trabalhos

- Cancelamento de trabalhos

- Impressão de ZPL

- Impressão de PDF no Linux

- Impressão RAW

- Logs

- Atualização automática

---

## 2.2 Funcionalidades NÃO contempladas

Esta versão NÃO contempla:

- geração de etiquetas;

- geração de PDF;

- edição de layouts;

- JasperReports;

- criação de etiquetas;

- banco de dados;

- autenticação;

- usuários;

- integração com serviços em nuvem;

- armazenamento permanente da fila.

---

## 2.3 Público-alvo

O agente será utilizado por:

- aplicações Web;

- aplicações Desktop;

- aplicações Mobile que possuam um componente local.

---

## 2.4 Responsabilidades

Compete ao sistema cliente:

- gerar o documento;

- montar o ZPL;

- gerar PDFs;

- escolher a impressora;

- definir configurações de impressão.

Compete ao Print Agent:

- validar a requisição;

- localizar a impressora;

- aplicar configurações;

- enviar o documento;

- controlar a fila;

- informar o resultado.

---

# 3. Arquitetura Geral

## 3.1 Visão Geral

```

+-----------------------+
| Sistema Presente |
+-----------+-----------+
|
| HTTP / JSON
|
v
+-----------------------+
| Print Agent |
| |
| REST API |
| Queue Manager |
| Print Service |
| Printer Manager |
| Web UI |
+-----------+-----------+
|
|
v
Sistema Operacional
|
|
v
Impressora

```

---

## 3.2 Fluxo Geral

1. O usuário solicita a impressão.

2. O sistema Presente gera o documento.

3. O sistema envia um JSON para:

```

POST /api/v1/trabalhos

```

4. O Print Agent valida a requisição.

5. O trabalho recebe um identificador.

6. O trabalho entra na fila.

7. O serviço de impressão consome a fila.

8. O documento é enviado para a impressora.

9. O status do trabalho é atualizado.

---

## 3.3 Camadas

O sistema será dividido em cinco camadas.

### REST API

Responsável por receber requisições.

Não deverá possuir regra de negócio.

---

### Queue Manager

Responsável pelo gerenciamento da fila.

---

### Printer Manager

Responsável pela descoberta das impressoras.

---

### Print Service

Responsável pela comunicação com o sistema operacional.

---

### Web UI

Responsável pela administração do agente.

---

## 3.4 Comunicação

Toda comunicação ocorrerá exclusivamente por HTTP.

Formato:

JSON

Codificação:

UTF-8

---

## 3.5 Porta padrão

```

18181

```

---

## 3.6 Endereço

```

http://localhost:18181

```

Não deverá existir configuração para acesso remoto nesta versão.

---

# 4. Tecnologias

## 4.1 Linguagem

Java 17 LTS

---

## 4.2 Framework

Spring Boot 3.x

---

## 4.3 Build

Maven

---

## 4.4 Interface Web

HTML

CSS

JavaScript

Sem frameworks JavaScript.

---

## 4.5 Configuração

application.yml

---

## 4.6 Linux

CUPS

---

## 4.7 Windows

Java Print Service

---

## 4.8 Namespace

```

br.eng.eliseu.printagent

```

---

## 4.9 Estrutura do projeto

```

printAgent/

docs/

src/

installer/

pom.xml

README.md

```

---

# 5. Requisitos Gerais

## RF-001

O agente deverá iniciar automaticamente junto ao sistema operacional.

---

## RF-002

O agente deverá disponibilizar uma API REST em localhost.

---

## RF-003

A API deverá ser versionada.

Versão inicial:

```

/api/v1

```

---

## RF-004

O endpoint principal deverá ser:

```

POST /api/v1/trabalhos

```

---

## RF-005

Todo trabalho deverá possuir um identificador único.

---

## RF-006

O processamento deverá ocorrer de forma assíncrona.

---

## RF-007

O agente deverá descobrir automaticamente todas as impressoras instaladas.

---

## RF-008

O agente deverá permitir impressão de:

- application/zpl

- application/pdf

- text/plain

---

## RF-009

Todas as respostas da API deverão utilizar um envelope único.

Estrutura:

```json
{
    "sucesso": true,
    "mensagem": "Trabalho recebido.",
    "dados": {},
    "erros": [],
    "dataHora": "2026-07-15T14:10:00-03:00"
}
```

---

## RF-010

A interface Web deverá permitir:

- visualizar impressoras;

- visualizar fila;

- consultar histórico;

- visualizar logs;

- realizar impressão de teste;

- consultar configurações.

---

## RNF-001

O tempo máximo para aceitação de uma requisição deverá ser inferior a 200 ms.

---

## RNF-002

O agente deverá executar em Windows e Linux.

---

## RNF-003

O agente não deverá depender do sistema Presente.

---

## RNF-004

Toda configuração deverá estar centralizada em:

```
application.yml
```

---

## RNF-005

O agente deverá ser capaz de permanecer em execução continuamente sem intervenção do usuário.

---

# 6. Referências

Os próximos documentos desta especificação são:

- API.md
- JSON.md
- WEBUI.md
- INSTALACAO.md
- ROADMAP.md
- CHANGELOG.md

---

# 7. Estrutura do Projeto

## 7.1 Objetivo

O projeto deverá ser organizado em módulos simples e independentes, permitindo manutenção, testes e evolução sem acoplamento entre as camadas.

Todo o código-fonte deverá permanecer sob o namespace:

```
br.eng.eliseu.printagent
```

---

## 7.2 Estrutura de diretórios

```
printAgent/
│
├── docs/
│   ├── arquitetura/
│   ├── api/
│   ├── web/
│   ├── instalacao/
│   ├── roadmap/
│   └── examples/
│
├── installer/
│
├── src/
│   ├── main/
│   │
│   ├── java/
│   │
│   └── resources/
│
├── pom.xml
│
└── README.md
```

---

## 7.3 Estrutura do código

```
src/main/java

br/
 └── eng/
      └── eliseu/
            └── printagent/
```

Todo o código deverá permanecer abaixo deste namespace.

---

## 7.4 Recursos

```
resources/

application.yml

static/

templates/

banner.txt
```

---

## 7.5 Documentação

Toda documentação técnica permanecerá exclusivamente na pasta:

```
docs/
```

Nenhuma documentação deverá ser mantida em código-fonte.

---

# 8. Organização dos Pacotes

## 8.1 Objetivo

A organização dos pacotes deverá seguir responsabilidade única.

Cada pacote deverá possuir uma única responsabilidade claramente definida.

Não será permitido utilizar pacotes genéricos como:

```
util2

misc

helper

commons
```

---

## 8.2 Estrutura

```
br.eng.eliseu.printagent

config

controller

dto

entity

service

queue

printer

history

model

exception

mapper

util

web

job
```

---

## 8.3 config

Responsável por toda configuração da aplicação.

Exemplos:

- Beans

- application.yml

- inicialização

- configuração do Spring

---

## 8.4 controller

Responsável exclusivamente pela API REST.

Nunca deverá conter regra de negócio.

Todo processamento deverá ser delegado aos Services.

---

## 8.5 dto

Contém todos os objetos utilizados na comunicação REST.

Nenhum DTO deverá possuir lógica.

---

## 8.6 service

Contém toda regra operacional do agente.

Exemplos:

PrintService

JobService

PrinterService

HistoryService

---

## 8.7 printer

Responsável pela comunicação com o sistema operacional.

Este pacote abstrairá diferenças entre:

Windows

Linux

No restante do sistema nunca deverá existir:

```
if (windows)

if (linux)
```

Essa decisão elimina dependência da plataforma.

---

## 8.8 queue

Responsável pelo gerenciamento da fila.

Este pacote não conhecerá impressoras.

Sua única responsabilidade consiste em controlar trabalhos.

---

## 8.9 history

Responsável pelo histórico das impressões.

Na versão 1 todo histórico será mantido apenas em memória.

---

## 8.10 exception

Centraliza todas as exceções do sistema.

Todas deverão herdar de:

```
PrintAgentException
```

---

## 8.11 util

Deverá conter apenas classes realmente reutilizáveis.

Não poderá conter regra de negócio.

---

# 9. Arquitetura Interna

## 9.1 Visão Geral

```
REST

↓

Controller

↓

Service

↓

Queue

↓

Printer

↓

Sistema Operacional

↓

Impressora
```

Cada camada possui uma responsabilidade exclusiva.

---

## 9.2 REST

Recebe requisições.

Valida JSON.

Converte DTO.

Encaminha ao Service.

Nunca imprime.

Nunca consulta impressoras.

Nunca manipula filas.

---

## 9.3 Service

Representa o núcleo do sistema.

Toda regra operacional permanecerá aqui.

Exemplo:

- validação

- criação do trabalho

- consulta da fila

- cancelamento

---

## 9.4 Queue

Representa o spool de impressão.

Características:

FIFO

Assíncrono

Thread-safe

Uma única fila.

---

## 9.5 Printer

Responsável pela impressão.

Nunca conhecerá HTTP.

Nunca conhecerá JSON.

Receberá apenas um objeto "Job".

---

## 9.6 Web UI

A interface Web consumirá exatamente a mesma API utilizada pelo sistema Presente.

Não existirão Services exclusivos para a interface.

Toda comunicação ocorrerá pela API REST.

Essa decisão elimina duplicidade de código.

---

## 9.7 Logs

Toda camada poderá registrar logs.

A gravação será centralizada.

Formato:

```
DATA

NÍVEL

CLASSE

MENSAGEM
```

---

## 9.8 Threads

O sistema possuirá inicialmente apenas duas threads principais.

Thread Web

↓

Recebe requisições.

Thread Queue

↓

Processa impressões.

Essa arquitetura reduz significativamente a complexidade.

---

## 9.9 Acoplamento

Não será permitido que:

Controller conheça Printer.

Queue conheça REST.

Printer conheça JSON.

History conheça HTTP.

Cada módulo conhecerá apenas sua camada imediatamente inferior.

---

# 10. Ciclo de Vida da Aplicação

## 10.1 Inicialização

Ao iniciar a aplicação deverá ocorrer:

1.

Carregar application.yml.

2.

Inicializar Spring Boot.

3.

Criar Services.

4.

Inicializar Queue Manager.

5.

Inicializar Printer Manager.

6.

Localizar impressoras.

7.

Inicializar Web UI.

8.

Disponibilizar API REST.

9.

Entrar em modo operacional.

---

## 10.2 Operação

Durante a execução o sistema permanecerá em espera.

Ao receber um trabalho:

Controller

↓

Service

↓

Queue

↓

Printer

↓

Histórico

---

## 10.3 Encerramento

Ao finalizar:

Interromper novas requisições.

Concluir trabalho em execução.

Liberar impressoras.

Finalizar Threads.

Encerrar aplicação.

---

## 10.4 Reinicialização

Na versão 1:

A fila NÃO será recuperada.

O histórico NÃO será recuperado.

As configurações permanecerão preservadas.

---

## 10.5 Atualização

A atualização será realizada substituindo os arquivos da aplicação.

Nenhuma configuração do usuário poderá ser perdida.

O arquivo:

```
application.yml
```

deverá permanecer intacto.

---

## 10.6 Estados da aplicação

```
INICIANDO

OPERACIONAL

PARANDO

FINALIZADO
```

---

## 10.7 Estados de um Trabalho

```
RECEBIDO

VALIDADO

NA_FILA

IMPRIMINDO

CONCLUIDO

ERRO

CANCELADO
```

Esses estados deverão ser utilizados por toda a aplicação.

Nenhum outro estado deverá existir sem atualização desta especificação.

---

# 11. API REST

## 11.1 Objetivo

Toda comunicação entre o sistema Presente e o Print Agent deverá ocorrer exclusivamente através da API REST.

A API será responsável apenas por:

- receber requisições;
- validar dados;
- encaminhar para a camada Service.

A API nunca deverá implementar regra de negócio.

---

# 11.2 Endereço Base

```
http://localhost:18181/api/v1
```

---

# 11.3 Formato

Toda comunicação utilizará:

```
HTTP

JSON

UTF-8
```

---

# 11.4 Envelope de Resposta

Todas as respostas deverão utilizar exatamente a estrutura abaixo.

```json
{
    "sucesso": true,
    "mensagem": "Descrição resumida.",
    "dados": {},
    "erros": [],
    "dataHora": "2026-07-15T16:20:35-03:00"
}
```

---

## sucesso

Tipo

```
Boolean
```

Obrigatório.

---

## mensagem

Tipo

```
String
```

Mensagem amigável.

---

## dados

Tipo

```
Object
```

Resposta específica do endpoint.

---

## erros

Tipo

```
Array
```

Lista de erros.

Nunca utilizar null.

---

## dataHora

Tipo

```
ISO-8601
```

---

# 11.5 Códigos HTTP

| Código | Utilização |
|---------|------------|
|200|Consulta realizada|
|201|Trabalho criado|
|204|Sem conteúdo|
|400|JSON inválido|
|404|Recurso inexistente|
|409|Conflito|
|500|Erro interno|

---

# 12. Endpoints

# 12.1 POST /trabalhos

## Objetivo

Criar um novo trabalho de impressão.

---

## Requisição

```
POST

/api/v1/trabalhos
```

Content-Type

```
application/json
```

---

## Resposta

HTTP

```
201
```

```json
{
    "sucesso": true,
    "mensagem": "Trabalho recebido.",
    "dados": {

        "idTrabalho":"4d98d4f0",

        "status":"RECEBIDO"

    },
    "erros":[],
    "dataHora":"2026-07-15T10:00:00"
}
```

---

## Regras

O endpoint nunca deverá iniciar a impressão.

Ele apenas cria o trabalho.

---

# 12.2 GET /trabalhos

## Objetivo

Listar todos os trabalhos.

---

## Resposta

```json
[
]
```

---

# 12.3 GET /trabalhos/{id}

Consulta um trabalho.

---

## Resposta

```json
{
    "idTrabalho":"",

    "status":"IMPRIMINDO"
}
```

---

# 12.4 DELETE /trabalhos/{id}

Cancela um trabalho.

---

## Regras

Caso o trabalho esteja:

```
CONCLUIDO
```

retornar

```
409
```

---

# 12.5 GET /impressoras

Lista todas as impressoras.

---

## Resposta

```json
[
    {

        "nome":"ElginL42",

        "padrao":true,

        "status":"READY"

    }
]
```

---

# 12.6 GET /impressoras/{nome}

Consulta detalhes.

---

# 12.7 GET /impressoras/{nome}/capacidades

Consulta capacidades.

---

## Exemplo

```json
{
    "nome":"ElginL42",

    "pageSizes":[

        "100x60",

        "100x80"

    ],

    "configuracoes":{

        "PrintDarkness":[

            "0",

            "...",

            "30"

        ]

    }
}
```

---

# 12.8 POST /teste

Executa impressão teste.

---

# 12.9 GET /status

Retorna estado do agente.

---

## Exemplo

```json
{
    "status":"OPERACIONAL",

    "versao":"1.0.0",

    "java":"17"
}
```

---

# 13. Regras Gerais da API

## API-001

Toda API deverá retornar envelope padrão.

---

## API-002

Nenhum endpoint deverá lançar HTML.

---

## API-003

Todas as mensagens deverão ser UTF-8.

---

## API-004

Todos os erros deverão ser documentados.

---

## API-005

Toda resposta deverá possuir:

```
dataHora
```

---

## API-006

Nunca utilizar:

```
null

```

quando puder retornar:

```
[]

ou

{}
```

---

## API-007

A API nunca deverá bloquear aguardando impressão.

---

## API-008

Toda impressão ocorrerá através da fila.

---

# 14. Modelo Geral dos DTOs

Todos os DTOs serão documentados detalhadamente no arquivo

```
docs/api/JSON.md
```

Nenhum DTO deverá possuir comportamento.

DTOs deverão conter apenas atributos.

Não será permitido:

- regra de negócio;

- acesso ao banco;

- acesso ao sistema operacional;

- acesso à fila.

DTO representa exclusivamente transporte de dados.

---

# 15. Arquitetura dos Serviços

## 15.1 Objetivo

A camada Service representa o núcleo do Print Agent.

Toda lógica operacional deverá permanecer nesta camada.

Nenhuma outra camada poderá implementar regras relacionadas ao fluxo de impressão.

---

# 15.2 Relação entre componentes

```
REST

↓

JobService

↓

QueueService

↓

PrintService

↓

PrinterProvider

↓

Sistema Operacional
```

Cada serviço deverá conhecer apenas o serviço imediatamente abaixo.

---

# 15.3 Serviços obrigatórios

A primeira versão deverá conter os seguintes serviços.

| Serviço | Responsabilidade |
|----------|------------------|
|JobService|Gerenciar trabalhos|
|QueueService|Gerenciar fila|
|PrinterService|Consultar impressoras|
|PrintService|Executar impressão|
|HistoryService|Consultar histórico|
|ConfigurationService|Ler configurações|
|WebService|Informações da interface Web|

Nenhum outro serviço deverá ser criado sem necessidade.

---

# 15.4 JobService

## Responsabilidade

Representa o ponto central da aplicação.

Todo trabalho deverá obrigatoriamente passar por este serviço.

---

## Responsabilidades

Criar trabalho.

Consultar trabalho.

Cancelar trabalho.

Consultar estado.

Validar dados.

Inserir na fila.

---

## Não poderá

Conhecer HTTP.

Conhecer CUPS.

Conhecer Windows.

Conhecer ZPL.

Conhecer PDF.

---

# 15.5 QueueService

## Objetivo

Controlar toda fila de impressão.

---

## Características

FIFO.

Thread Safe.

Assíncrona.

Uma única fila.

---

## Operações

Adicionar.

Remover.

Consultar.

Cancelar.

Limpar.

---

## Não poderá

Imprimir.

Consultar impressoras.

Conhecer HTTP.

---

# 15.6 PrintService

## Objetivo

Executar efetivamente uma impressão.

---

## Responsabilidades

Receber Job.

Selecionar Provider.

Enviar documento.

Atualizar estado.

Gerar histórico.

---

## Não poderá

Criar Jobs.

Gerenciar fila.

Consultar REST.

---

# 15.7 PrinterService

## Objetivo

Descobrir impressoras.

---

## Responsabilidades

Listar impressoras.

Consultar impressora.

Consultar capacidades.

Consultar padrão.

Atualizar cache.

---

## Estratégia

As impressoras deverão ser carregadas durante a inicialização.

A atualização ocorrerá somente quando solicitada.

---

# 15.8 HistoryService

Responsável apenas por armazenar o histórico.

Na versão 1:

Memória.

---

# 15.9 ConfigurationService

Centraliza acesso ao

```
application.yml
```

Nenhuma outra classe poderá acessar diretamente o arquivo.

---

# 16. Providers de Impressão

## Objetivo

Eliminar dependência do sistema operacional.

Toda impressão ocorrerá através de Providers.

---

# 16.1 Interface

Todo Provider deverá implementar:

```
PrinterProvider
```

---

# 16.2 LinuxPrinterProvider

Responsável por Linux.

Utilizar:

CUPS

---

# 16.3 WindowsPrinterProvider

Responsável por Windows.

Utilizar:

Java Print Service.

---

# 16.4 Seleção

Durante inicialização.

```
Windows

↓

WindowsProvider
```

```
Linux

↓

LinuxProvider
```

Nunca utilizar

```
if (windows)

```

espalhado pela aplicação.

---

# 17. Modelo dos Trabalhos

## Objetivo

Representar uma impressão.

---

## Estrutura

Todo trabalho possuirá obrigatoriamente:

Id.

Nome.

Documento.

Tipo.

Impressora.

Status.

Data criação.

Data início.

Data término.

Quantidade cópias.

---

# 17.1 Estados

```
RECEBIDO

VALIDADO

NA_FILA

IMPRIMINDO

CONCLUIDO

ERRO

CANCELADO
```

---

# 17.2 Transições

```
RECEBIDO

↓

VALIDADO

↓

NA_FILA

↓

IMPRIMINDO

↓

CONCLUIDO
```

Erro

↓

ERRO

Cancelamento

↓

CANCELADO

Nunca poderá existir:

```
CONCLUIDO

↓

IMPRIMINDO
```

---

# 17.3 Identificador

UUID.

Gerado pelo agente.

Nunca recebido do cliente.

---

# 18. Gerenciamento da Fila

## Objetivo

Representar um spool de impressão.

---

## Características

FIFO.

Memória.

Assíncrona.

---

## Ordem

Primeiro que entra.

Primeiro que sai.

---

## Política

Não haverá prioridades.

Não haverá múltiplas filas.

---

## Reinício

Toda fila será descartada.

---

## Thread

Uma única Thread consumidora.

---

## Processamento

Enquanto existir trabalho.

↓

Consumir.

↓

Imprimir.

↓

Atualizar histórico.

↓

Próximo.

---

# 19. Histórico

## Objetivo

Permitir consulta.

---

## Estrutura

Últimos trabalhos.

Status.

Tempo.

Impressora.

Documento.

---

## Limite

100 registros.

Configurável.

---

## Persistência

Nenhuma.

---

# 20. Configuração

Todas as configurações deverão permanecer em

```
application.yml
```

Nunca utilizar:

Properties.

XML.

Banco.

---

## Estrutura

```yaml
server:
  port: 18181

print:

  history-size: 100

  auto-discover-printers: true

  update-on-startup: true

logging:

  level: INFO
```

---

# 21. Modelo de Dados

## 21.1 Objetivo

O modelo de dados define todas as estruturas utilizadas na comunicação entre o sistema cliente e o Print Agent.

Todos os objetos utilizados pela API REST deverão ser representados através de DTOs (*Data Transfer Objects*).

Os DTOs possuem exclusivamente a responsabilidade de transportar informações entre o cliente e o servidor.

DTOs nunca deverão conter:

- regras de negócio;
- acesso ao sistema operacional;
- acesso à fila de impressão;
- acesso a arquivos;
- acesso à API REST;
- lógica de impressão;
- persistência.

---

## 21.2 Convenções

Todos os DTOs deverão:

- ser serializáveis para JSON;
- utilizar codificação UTF-8;
- utilizar nomenclatura camelCase;
- possuir documentação completa.

---

## 21.3 Organização

Todos os DTOs deverão permanecer no pacote:

```
br.eng.eliseu.printagent.dto
```

Não será permitido criar DTOs fora deste pacote.

---

# 22. DTO TrabalhoImpressaoRequestDTO

## Objetivo

Representa a requisição enviada para criação de um novo trabalho de impressão.

Este DTO será utilizado exclusivamente pelo endpoint:

```
POST /api/v1/trabalhos
```

---

## Estrutura

| Campo | Tipo | Obrigatório |
|---------|---------|------------|
| impressora | ImpressoraDTO | Sim |
| configuracoesImpressao | ConfiguracoesImpressaoDTO | Não |
| trabalho | TrabalhoDTO | Sim |
| documento | DocumentoDTO | Sim |

---

## Exemplo

```json
{
    "impressora": {
        "nome": "ElginL42"
    },

    "configuracoesImpressao": {

        "PageSize": "100x60",

        "PrintDarkness": "20",

        "PrintSpeed": "4"

    },

    "trabalho": {

        "nome": "Etiqueta Patrimônio",

        "copias": 2,

        "sincrono": false

    },

    "documento": {

        "tipoConteudo": "application/zpl",

        "codificacao": "utf-8",

        "conteudo": "^XA...^XZ"

    }

}
```

---

## Regras

O objeto deverá conter obrigatoriamente:

- impressora;
- trabalho;
- documento.

O objeto:

```
configuracoesImpressao
```

é opcional.

Quando omitido, a impressão deverá utilizar a configuração padrão da impressora.

---

# 23. DTO ImpressoraDTO

## Objetivo

Representa a impressora de destino de um trabalho de impressão.

Este DTO possui apenas informações suficientes para identificar uma impressora instalada no sistema operacional.

Nenhuma configuração específica de impressão deverá existir neste objeto.

---

## Estrutura

| Campo | Tipo | Obrigatório | Descrição |
|---------|---------|------------|-----------|
| nome | String | Sim | Nome da impressora instalada. |

---

## Exemplo

```json
{
    "nome": "ElginL42"
}
```

---

## Validações

### nome

Obrigatório.

Não poderá ser vazio.

Não poderá conter apenas espaços.

Deverá corresponder exatamente ao nome retornado pelo endpoint:

```
GET /api/v1/impressoras
```

---

## Regras

O Print Agent deverá localizar a impressora utilizando exclusivamente o atributo:

```
nome
```

Caso a impressora não exista, o trabalho deverá ser rejeitado.

Código sugerido:

```
IMPRESSORA_NAO_ENCONTRADA
```

---

## Responsabilidades

Este DTO possui apenas uma responsabilidade:

identificar a impressora de destino.

---

## Fora do escopo

Este DTO não deverá conter:

- tamanho da mídia;
- orientação;
- velocidade;
- densidade;
- offset;
- quantidade de cópias;
- qualquer parâmetro de impressão.

---

# 24. DTO ConfiguracoesImpressaoDTO

## Objetivo

Representa as configurações específicas de um trabalho de impressão.

As propriedades deste DTO serão encaminhadas diretamente ao Provider responsável pela impressão.

O Print Agent não deverá interpretar, converter ou validar semanticamente estas propriedades.

---

## Estrutura

Este DTO é composto por um mapa de propriedades.

```
Map<String,String>
```

As chaves deverão corresponder exatamente aos nomes disponibilizados pelo sistema operacional ou pelo driver da impressora.

---

## Exemplo

```json
{
    "PageSize": "100x60",

    "PrintDarkness": "20",

    "PrintSpeed": "4",

    "Orientation": "0",

    "MediaMethod": "1"
}
```

---

## Regras

O agente deverá encaminhar todas as propriedades ao Provider.

O agente não deverá remover propriedades desconhecidas.

O agente não deverá traduzir nomes.

O agente não deverá modificar valores.

---

## Objetivo da decisão

Esta decisão torna o Print Agent completamente independente do fabricante da impressora.

Qualquer propriedade suportada pelo driver poderá ser utilizada sem necessidade de atualização do agente.

---

# 25. DTO TrabalhoDTO

## Objetivo

Representa as informações administrativas de um trabalho de impressão.

Estas informações não interferem diretamente no conteúdo impresso.

---

## Estrutura

| Campo | Tipo | Obrigatório |
|---------|---------|------------|
| nome | String | Sim |
| copias | Integer | Sim |
| sincrono | Boolean | Sim |

---

## Exemplo

```json
{
    "nome": "Etiqueta Patrimônio",

    "copias": 2,

    "sincrono": false
}
```

---

## Validações

### nome

Obrigatório.

Máximo:

```
100 caracteres
```

---

### copias

Obrigatório.

Mínimo:

```
1
```

Máximo:

```
999
```

---

### sincrono

Quando:

```
false
```

o endpoint retornará imediatamente após a criação do trabalho.

Quando:

```
true
```

o endpoint aguardará a conclusão da impressão ou erro.

---

# 26. DTO DocumentoDTO

## Objetivo

Representa o documento que será enviado para impressão.

O conteúdo deste objeto será encaminhado ao Provider responsável.

O Print Agent não deverá alterar o documento.

---

## Estrutura

| Campo | Tipo | Obrigatório |
|---------|---------|------------|
| tipoConteudo | String | Sim |
| codificacao | String | Sim |
| conteudo | String | Sim |

---

## Validações

Todos os campos são obrigatórios.

---

## Tipos suportados

```
application/zpl

application/pdf

text/plain
```

Qualquer outro tipo deverá gerar:

```
415 Unsupported Media Type
```

---

## Exemplos

### ZPL

```json
{
    "tipoConteudo": "application/zpl",

    "codificacao": "utf-8",

    "conteudo": "^XA...^XZ"
}
```

---

### PDF

```json
{
    "tipoConteudo": "application/pdf",

    "codificacao": "base64",

    "conteudo": "JVBERi0xLjQK..."
}
```

---

### RAW

```json
{
    "tipoConteudo": "text/plain",

    "codificacao": "utf-8",

    "conteudo": "Texto..."
}
```

---

## Observações

O agente nunca deverá interpretar:

- comandos ZPL;
- conteúdo PDF;
- texto RAW.

Sua responsabilidade limita-se ao encaminhamento para o Provider.

---

# 27. DTO TrabalhoResponseDTO

## Objetivo

Representa o resultado da criação de um novo trabalho.

Este objeto será retornado pelo endpoint:

```
POST /api/v1/trabalhos
```

---

## Estrutura

| Campo | Tipo |
|---------|---------|
| idTrabalho | UUID |
| status | StatusTrabalho |

---

## Exemplo

```json
{
    "idTrabalho": "8fd1b4fb-21db-4e3f-94b2-dfb83b2d3f0d",

    "status": "RECEBIDO"
}
```

---

## Regras

O identificador deverá ser gerado exclusivamente pelo Print Agent.

O cliente nunca poderá informar este valor.

---

# 28. DTO ImpressoraResponseDTO

## Objetivo

Representa uma impressora instalada e disponível no sistema operacional.

Este DTO será utilizado nas respostas dos endpoints relacionados à consulta de impressoras.

---

## Estrutura

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---:|---|
| nome | String | Sim | Nome da impressora registrado no sistema operacional. |
| padrao | Boolean | Sim | Indica se a impressora é a impressora padrão do sistema. |
| status | String | Sim | Estado atual conhecido da impressora. |

---

## Exemplo

```json
{
    "nome": "ElginL42",
    "padrao": true,
    "status": "READY"
}
```

---

## Regras

O atributo `nome` deverá corresponder exatamente ao nome utilizado pelo sistema operacional.

O atributo `padrao` deverá ser:

```text
true
```

quando a impressora estiver configurada como impressora padrão do sistema operacional.

Caso contrário, deverá ser:

```text
false
```

O atributo `status` deverá refletir o estado retornado pelo Provider de impressão.

---

## Valores sugeridos para status

O Provider deverá, sempre que possível, normalizar o status para um dos seguintes valores:

```text
READY

BUSY

OFFLINE

STOPPED

UNKNOWN
```

---

## READY

A impressora está disponível para receber trabalhos.

---

## BUSY

A impressora está ocupada ou possui trabalho em processamento.

---

## OFFLINE

A impressora não está acessível pelo sistema operacional.

---

## STOPPED

A fila da impressora está interrompida, pausada ou desabilitada.

---

## UNKNOWN

O sistema operacional ou o Provider não conseguiu determinar o estado atual.

---

## Observação

O estado retornado pelo sistema operacional pode variar entre Windows e Linux.

O Print Agent deverá normalizar o estado sempre que isso for tecnicamente possível.

Quando não for possível mapear o estado, deverá utilizar:

```text
UNKNOWN
```

---

# 29. DTO CapacidadesImpressoraResponseDTO

## Objetivo

Representa as propriedades, opções e capacidades expostas por uma impressora instalada.

Este DTO deverá permitir que o sistema cliente consulte quais configurações podem ser utilizadas em um trabalho de impressão.

---

## Estrutura

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---:|---|
| nome | String | Sim | Nome da impressora consultada. |
| configuracoes | Map<String, List<String>> | Sim | Opções disponíveis para a impressora. |

---

## Exemplo

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

## Regras

O Print Agent deverá consultar as capacidades diretamente no sistema operacional, no driver ou no mecanismo de impressão disponível.

No Linux, as capacidades poderão ser obtidas por meio do CUPS.

No Windows, deverão ser obtidas através do Java Print Service ou do mecanismo disponível no sistema operacional.

O Print Agent não deverá manter uma lista fixa de capacidades por fabricante ou modelo.

O Print Agent não deverá traduzir os nomes das propriedades.

O Print Agent não deverá modificar os valores retornados pelo driver.

---

## Ausência de capacidades

Caso o sistema operacional não permita consultar capacidades detalhadas, o campo:

```json
"configuracoes"
```

deverá retornar um objeto vazio:

```json
{}
```

A consulta não deverá falhar apenas porque as capacidades não puderam ser determinadas.

---

## Configurações desconhecidas

Propriedades desconhecidas pelo Print Agent deverão ser retornadas normalmente.

Exemplo:

```json
{
    "configuracoes": {
        "VendorSpecificOption": [
            "A",
            "B",
            "C"
        ]
    }
}
```

Essa regra garante que novos modelos de impressora possam ser utilizados sem atualização do agente.

---

# 30. DTO TrabalhoConsultaResponseDTO

## Objetivo

Representa os dados completos de um trabalho já registrado no Print Agent.

Este DTO será utilizado nos endpoints de consulta de trabalhos e histórico.

---

## Estrutura

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---:|---|
| idTrabalho | UUID | Sim | Identificador único gerado pelo Print Agent. |
| nome | String | Sim | Nome administrativo do trabalho. |
| impressora | String | Sim | Nome da impressora de destino. |
| tipoConteudo | String | Sim | Tipo do documento recebido. |
| copias | Integer | Sim | Quantidade de cópias solicitadas. |
| status | StatusTrabalho | Sim | Estado atual ou final do trabalho. |
| dataCriacao | OffsetDateTime | Sim | Data e hora de criação. |
| dataInicio | OffsetDateTime | Não | Data e hora do início do processamento. |
| dataTermino | OffsetDateTime | Não | Data e hora do término. |
| mensagemErro | String | Não | Descrição resumida de eventual erro. |

---

## Exemplo de trabalho concluído

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

## Exemplo de trabalho na fila

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

## Regras

O conteúdo completo do documento não deverá ser devolvido nas consultas.

O campo:

```text
conteudo
```

do `DocumentoDTO` não deverá aparecer neste DTO.

Essa decisão evita:

- exposição desnecessária de dados;
- aumento do tamanho das respostas;
- repetição de conteúdo binário em Base64;
- consumo excessivo de memória e rede.

---

## mensagemErro

Quando o trabalho não estiver no estado:

```text
ERRO
```

o campo deverá retornar uma string vazia:

```json
"mensagemErro": ""
```

Quando o trabalho estiver no estado `ERRO`, o campo deverá conter uma descrição objetiva.

Exemplo:

```json
"mensagemErro": "A impressora 'ElginL42' não está disponível."
```

Detalhes técnicos completos deverão permanecer nos logs.

---

# 31. DTO StatusAplicacaoResponseDTO

## Objetivo

Representa o estado operacional atual do Print Agent.

Este DTO será utilizado pelo endpoint:

```text
GET /api/v1/status
```

---

## Estrutura

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---:|---|
| status | StatusAplicacao | Sim | Estado atual da aplicação. |
| versao | String | Sim | Versão instalada do Print Agent. |
| java | String | Sim | Versão principal do Java em execução. |
| sistemaOperacional | String | Sim | Sistema operacional identificado. |
| porta | Integer | Sim | Porta HTTP utilizada pelo agente. |
| trabalhosNaFila | Integer | Sim | Quantidade de trabalhos aguardando processamento. |
| impressorasEncontradas | Integer | Sim | Quantidade de impressoras detectadas. |
| iniciadoEm | OffsetDateTime | Sim | Data e hora de inicialização. |

---

## Exemplo

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

## Regras

A versão deverá ser obtida das informações de build da aplicação.

Não deverá ser escrita manualmente em diferentes classes.

A porta deverá refletir o valor efetivamente utilizado pelo servidor HTTP.

O número de trabalhos na fila não deverá incluir trabalhos:

```text
CONCLUIDO

ERRO

CANCELADO
```

O número de impressoras deverá refletir o cache atual do `PrinterService`.

---

# 32. DTO ErroDTO

## Objetivo

Representa um erro retornado pela API REST.

O DTO deverá fornecer uma mensagem compreensível para o sistema cliente, sem expor detalhes internos sensíveis ou rastreamentos de pilha.

---

## Estrutura

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---:|---|
| codigo | String | Sim | Código estável e identificável do erro. |
| descricao | String | Sim | Mensagem amigável sobre o erro. |
| campo | String | Não | Nome do campo relacionado ao erro, quando aplicável. |

---

## Exemplo de erro geral

```json
{
    "codigo": "IMPRESSORA_NAO_ENCONTRADA",
    "descricao": "A impressora 'ElginL42' não está instalada.",
    "campo": "impressora.nome"
}
```

---

## Exemplo de erro de validação

```json
{
    "codigo": "CAMPO_OBRIGATORIO",
    "descricao": "O nome do trabalho deve ser informado.",
    "campo": "trabalho.nome"
}
```

---

## Regras

O atributo `codigo` deverá ser estável.

Um mesmo tipo de erro não poderá retornar códigos diferentes em endpoints distintos.

O atributo `descricao` poderá conter informações específicas da requisição.

O atributo `campo` deverá utilizar o caminho completo da propriedade no JSON.

Exemplos:

```text
impressora.nome

trabalho.copias

documento.tipoConteudo

configuracoesImpressao.PageSize
```

Quando o erro não estiver associado a um campo específico, `campo` deverá retornar uma string vazia.

---

## Códigos iniciais de erro

| Código | Utilização |
|---|---|
| REQUISICAO_INVALIDA | Estrutura geral da requisição inválida. |
| JSON_INVALIDO | Corpo da requisição não pôde ser interpretado. |
| CAMPO_OBRIGATORIO | Campo obrigatório ausente ou vazio. |
| VALOR_INVALIDO | Valor fora das regras permitidas. |
| IMPRESSORA_NAO_ENCONTRADA | Impressora informada não foi localizada. |
| IMPRESSORA_INDISPONIVEL | Impressora localizada, mas indisponível. |
| TIPO_CONTEUDO_NAO_SUPORTADO | Tipo de documento não suportado. |
| CODIFICACAO_INVALIDA | Codificação incompatível com o documento. |
| CONTEUDO_INVALIDO | Conteúdo vazio, malformado ou não decodificável. |
| TRABALHO_NAO_ENCONTRADO | Identificador de trabalho inexistente. |
| TRABALHO_NAO_CANCELAVEL | Estado atual não permite cancelamento. |
| FALHA_IMPRESSAO | Falha ao enviar o documento à impressora. |
| ERRO_INTERNO | Erro não previsto na aplicação. |

---

# 33. DTO EnvelopeRespostaDTO

## Objetivo

Representa a estrutura padrão utilizada em todas as respostas da API REST.

Todo endpoint deverá retornar este envelope, exceto respostas HTTP que, por definição, não possuam corpo.

---

## Estrutura

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---:|---|
| sucesso | Boolean | Sim | Indica se a operação foi concluída conforme esperado. |
| mensagem | String | Sim | Resumo amigável do resultado. |
| dados | Object | Sim | Conteúdo específico do endpoint. |
| erros | List<ErroDTO> | Sim | Lista de erros identificados. |
| dataHora | OffsetDateTime | Sim | Data e hora da geração da resposta. |

---

## Exemplo de sucesso

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

---

## Exemplo de erro

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

---

## Regras

O campo `sucesso` deverá ser `true` apenas quando a operação principal do endpoint for aceita ou concluída.

Em respostas de erro:

```json
"sucesso": false
```

O campo `mensagem` nunca deverá ser vazio.

O campo `erros` nunca deverá ser `null`.

Quando não existirem erros:

```json
"erros": []
```

O campo `dados` nunca deverá ser `null`.

Quando não existirem dados:

```json
"dados": {}
```

O campo `dataHora` deverá utilizar ISO-8601 com deslocamento de fuso horário.

Exemplo:

```text
2026-07-15T18:41:00-03:00
```

---

## Respostas sem corpo

Endpoints que utilizarem HTTP `204 No Content` não deverão retornar o envelope.

A utilização de `204` deverá ser restrita a operações cujo contrato não exija qualquer conteúdo de resposta.

---

# 34. Enum StatusTrabalho

## Objetivo

Representa o ciclo de vida de um trabalho de impressão.

---

## Valores

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

## RECEBIDO

O Print Agent recebeu a requisição e criou o identificador do trabalho.

---

## VALIDADO

Os campos obrigatórios, a impressora, o tipo de conteúdo e as regras básicas foram validados.

---

## NA_FILA

O trabalho foi adicionado à fila e aguarda processamento.

---

## IMPRIMINDO

O trabalho foi retirado da fila e está sendo encaminhado ao sistema de impressão.

---

## CONCLUIDO

O documento foi aceito pelo sistema de impressão ou pela fila do sistema operacional sem erro imediato.

O estado `CONCLUIDO` não significa necessariamente que o papel ou a etiqueta saiu fisicamente da impressora.

---

## ERRO

O processamento não pôde ser concluído.

O erro deverá ser registrado no histórico e nos logs.

---

## CANCELADO

O trabalho foi cancelado antes de sua conclusão.

---

## Transições permitidas

```text
RECEBIDO -> VALIDADO

VALIDADO -> NA_FILA

NA_FILA -> IMPRIMINDO

IMPRIMINDO -> CONCLUIDO
```

Transições para erro:

```text
RECEBIDO -> ERRO

VALIDADO -> ERRO

NA_FILA -> ERRO

IMPRIMINDO -> ERRO
```

Transições para cancelamento:

```text
RECEBIDO -> CANCELADO

VALIDADO -> CANCELADO

NA_FILA -> CANCELADO
```

---

## Transições proibidas

Não serão permitidas transições a partir dos estados finais:

```text
CONCLUIDO

ERRO

CANCELADO
```

Exemplo proibido:

```text
CONCLUIDO -> IMPRIMINDO
```

---

# 35. Enum StatusAplicacao

## Objetivo

Representa o estado geral do Print Agent.

---

## Valores

```text
INICIANDO

OPERACIONAL

PARANDO

FINALIZADO
```

---

## INICIANDO

A aplicação está carregando configurações, criando componentes e descobrindo impressoras.

---

## OPERACIONAL

A API e a fila estão disponíveis para receber trabalhos.

---

## PARANDO

A aplicação iniciou o processo de encerramento e não deverá aceitar novos trabalhos.

---

## FINALIZADO

Todos os recursos foram liberados e a aplicação foi encerrada.

---

# 36. Regras Gerais dos DTOs

## DTO-001

Todos os DTOs deverão permanecer abaixo do pacote:

```text
br.eng.eliseu.printagent.dto
```

---

## DTO-002

Os DTOs não deverão acessar:

- serviços;
- sistema operacional;
- impressoras;
- fila;
- arquivos;
- configuração;
- rede.

---

## DTO-003

Os DTOs não deverão conter regras de negócio.

---

## DTO-004

As validações declarativas de entrada poderão utilizar Bean Validation.

Exemplos:

```java
@NotNull
@NotBlank
@Min
@Max
@Size
```

---

## DTO-005

Mensagens detalhadas de validação não deverão ser definidas diretamente em múltiplos DTOs quando puderem ser centralizadas.

---

## DTO-006

DTOs de entrada e saída não deverão compartilhar campos desnecessários apenas para reduzir a quantidade de classes.

---

## DTO-007

O conteúdo de documentos PDF em Base64 não deverá ser escrito integralmente nos logs.

---

## DTO-008

O conteúdo ZPL e RAW não deverá ser registrado integralmente nos logs em nível `INFO`.

---

## DTO-009

Propriedades desconhecidas no JSON deverão, por padrão, causar erro de validação.

Essa decisão evita que erros de digitação sejam ignorados silenciosamente.

Exemplo:

```json
{
    "trabalho": {
        "copia": 2
    }
}
```

O campo correto é:

```json
"copias"
```

A requisição deverá ser rejeitada.

---

## DTO-010

Campos obrigatórios ausentes deverão gerar erro com o código:

```text
CAMPO_OBRIGATORIO
```

---

# 37. Convenções de Nomenclatura Java

## Classes de entrada

Deverão terminar com:

```text
RequestDTO
```

Exemplo:

```text
TrabalhoImpressaoRequestDTO
```

---

## Classes de saída

Deverão terminar com:

```text
ResponseDTO
```

Exemplos:

```text
TrabalhoResponseDTO

TrabalhoConsultaResponseDTO

ImpressoraResponseDTO

StatusAplicacaoResponseDTO
```

---

## Objetos internos de transporte

Poderão terminar apenas com:

```text
DTO
```

Exemplos:

```text
DocumentoDTO

TrabalhoDTO

ImpressoraDTO

ConfiguracoesImpressaoDTO

ErroDTO
```

---

## Enums

Deverão possuir nomes de domínio claros.

Exemplos:

```text
StatusTrabalho

StatusAplicacao
```

Não utilizar sufixo:

```text
Enum
```

Exemplo proibido:

```text
StatusTrabalhoEnum
```

---

# 38. Convenções JSON

## Propriedades em português

As propriedades definidas pelo Print Agent deverão utilizar português e camelCase.

Exemplos:

```text
idTrabalho

tipoConteudo

dataCriacao

mensagemErro

configuracoesImpressao
```

---

## Propriedades externas

Propriedades originadas diretamente de drivers ou sistemas de impressão deverão preservar sua nomenclatura original.

Exemplos:

```text
PageSize

PrintDarkness

PrintSpeed

Orientation

MediaMethod
```

---

## Nomes proibidos

Não utilizar propriedades definidas pelo Print Agent em:

```text
snake_case

PascalCase

kebab-case
```

Exemplos proibidos:

```text
id_trabalho

IdTrabalho

id-trabalho
```

---

## Datas e horários

Datas e horários deverão utilizar ISO-8601.

Exemplo:

```text
2026-07-15T18:41:00-03:00
```

---

## Booleanos

Valores booleanos deverão utilizar:

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

# 39. Compatibilidade e Versionamento dos Contratos

## Regra geral

A API inicial deverá utilizar:

```text
/api/v1
```

---

## Alterações compatíveis

São consideradas compatíveis:

- adição de endpoint;
- adição de campo opcional;
- adição de novo código de erro;
- adição de nova capacidade de impressora;
- adição de novo status sem alteração dos fluxos existentes, desde que documentada.

---

## Alterações incompatíveis

São consideradas incompatíveis:

- remoção de campo;
- alteração de nome de campo;
- alteração de tipo;
- transformação de campo opcional em obrigatório;
- alteração de significado de campo existente;
- alteração do envelope padrão;
- remoção de endpoint.

Alterações incompatíveis deverão gerar uma nova versão da API.

Exemplo:

```text
/api/v2
```

---

## Estabilidade dos códigos de erro

Códigos de erro publicados não poderão ter seu significado alterado.

Novos códigos poderão ser adicionados sem alteração da versão da API.

---

## Estabilidade dos enums

Valores de enum existentes não deverão ser removidos ou renomeados dentro da API v1.

---

# 40. Exemplo Completo de Criação de Trabalho

## Requisição

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

## Resposta de sucesso

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

---

## Resposta para impressora inexistente

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

---

## Resposta para tipo não suportado

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
            "descricao": "O tipo 'image/png' não é suportado na versão 1.",
            "campo": "documento.tipoConteudo"
        }
    ],
    "dataHora": "2026-07-15T18:42:00-03:00"
}
```

---

# 41. Processamento de Trabalhos de Impressão

## 41.1 Objetivo

Este capítulo define o fluxo interno utilizado pelo Print Agent desde o recebimento de uma requisição até o encaminhamento do documento ao sistema de impressão do sistema operacional.

Todo trabalho deverá seguir o mesmo fluxo, independentemente do tipo de documento ou da impressora selecionada.

---

## 41.2 Fluxo principal

O processamento deverá ocorrer na seguinte ordem:

```text
Requisição recebida
        |
        v
Validação estrutural
        |
        v
Validação da impressora
        |
        v
Validação do documento
        |
        v
Criação do trabalho
        |
        v
Inclusão na fila
        |
        v
Consumo pelo processador
        |
        v
Seleção do Provider
        |
        v
Envio ao sistema operacional
        |
        v
Atualização do status
        |
        v
Registro no histórico
```

---

## 41.3 Recebimento

Ao receber uma requisição em:

```text
POST /api/v1/trabalhos
```

o Controller deverá:

1. desserializar o JSON;
2. executar validações declarativas;
3. encaminhar o DTO ao `JobService`;
4. retornar o envelope produzido pela camada de serviço.

O Controller não deverá:

* consultar impressoras diretamente;
* criar identificadores;
* inserir trabalhos na fila;
* executar impressão;
* manipular conteúdo ZPL, PDF ou RAW.

---

## 41.4 Validação estrutural

A validação estrutural deverá verificar:

* existência do objeto `impressora`;
* existência de `impressora.nome`;
* existência do objeto `trabalho`;
* existência de `trabalho.nome`;
* validade de `trabalho.copias`;
* existência do objeto `documento`;
* existência de `documento.tipoConteudo`;
* existência de `documento.codificacao`;
* existência de `documento.conteudo`.

Falhas nesta etapa deverão impedir a criação do trabalho.

---

## 41.5 Validação da impressora

O `PrinterService` deverá verificar se a impressora informada está disponível no catálogo atual de impressoras.

A identificação deverá ocorrer pelo nome retornado pelo sistema operacional.

Exemplo:

```text
ElginL42
```

Caso a impressora não seja encontrada, a requisição deverá ser rejeitada com:

```text
IMPRESSORA_NAO_ENCONTRADA
```

Caso a impressora exista, mas esteja indisponível, o comportamento deverá ser configurável.

Na versão 1, o trabalho deverá ser rejeitado com:

```text
IMPRESSORA_INDISPONIVEL
```

---

## 41.6 Validação do documento

O tipo informado deverá ser comparado com os tipos suportados na versão 1:

```text
application/zpl

application/pdf

text/plain
```

A codificação deverá ser compatível com o tipo de conteúdo.

| Tipo            | Codificação permitida |
| --------------- | --------------------- |
| application/zpl | utf-8                 |
| application/pdf | base64                |
| text/plain      | utf-8                 |

O conteúdo não poderá ser vazio.

Para PDF, o conteúdo deverá ser decodificável de Base64.

O agente não deverá validar a estrutura interna do PDF.

O agente não deverá validar comandos ZPL.

O agente não deverá alterar o texto RAW.

---

## 41.7 Criação do trabalho

Após as validações iniciais, o `JobService` deverá criar uma representação interna do trabalho.

O trabalho deverá receber:

* UUID;
* status inicial;
* data e hora de criação;
* impressora de destino;
* configurações de impressão;
* documento;
* quantidade de cópias;
* nome administrativo.

O primeiro status deverá ser:

```text
RECEBIDO
```

Após a validação completa:

```text
VALIDADO
```

Depois da inclusão na fila:

```text
NA_FILA
```

---

## 41.8 Inclusão na fila

O trabalho somente poderá ser incluído na fila após conclusão de todas as validações.

O `QueueService` deverá utilizar uma estrutura FIFO e thread-safe.

Exemplo de implementação aceitável:

```java
BlockingQueue<TrabalhoImpressao>
```

A especificação não obriga o uso de uma implementação específica, desde que:

* preserve a ordem FIFO;
* seja segura para acesso concorrente;
* permita bloqueio eficiente da thread consumidora;
* permita consulta da quantidade de trabalhos;
* permita cancelamento de trabalhos ainda não iniciados.

---

## 41.9 Processamento assíncrono

A fila deverá possuir uma thread consumidora dedicada.

A thread consumidora deverá:

1. aguardar um trabalho;
2. retirar o próximo trabalho;
3. atualizar o status para `IMPRIMINDO`;
4. encaminhar o trabalho ao `PrintService`;
5. registrar o resultado;
6. continuar aguardando o próximo trabalho.

A thread não deverá utilizar espera ativa.

É proibido implementar ciclos contínuos como:

```java
while (true) {
    if (!fila.isEmpty()) {
        // processa
    }
}
```

Deverá ser utilizado bloqueio apropriado.

---

## 41.10 Trabalho síncrono

Quando:

```json
"sincrono": true
```

a requisição poderá aguardar o término do processamento.

O modo síncrono não elimina a fila.

O trabalho deverá seguir o mesmo fluxo:

```text
Recebido -> Validado -> Na fila -> Imprimindo -> Estado final
```

A diferença consiste apenas no fato de que a requisição HTTP aguardará o estado final ou o tempo limite configurado.

O tempo limite deverá ser configurável.

Valor padrão sugerido:

```yaml
print-agent:
  trabalho:
    timeout-sincrono-segundos: 30
```

Caso o tempo limite seja atingido, o trabalho continuará na fila.

A resposta deverá informar que o processamento permanece em andamento.

---

# 42. Estratégias de Impressão por Tipo de Documento

## 42.1 Objetivo

Cada tipo de documento deverá possuir uma estratégia própria de preparação e envio.

A seleção deverá ocorrer pelo atributo:

```text
documento.tipoConteudo
```

---

## 42.2 Interface de estratégia

As estratégias deverão implementar uma abstração comum.

Exemplo conceitual:

```java
public interface EstrategiaImpressao {

    boolean suporta(String tipoConteudo);

    ResultadoImpressao imprimir(
        TrabalhoImpressao trabalho,
        PrinterProvider provider
    );
}
```

O nome exato dos métodos poderá variar, mas a separação de responsabilidades deverá ser preservada.

---

## 42.3 Estratégia ZPL

A estratégia ZPL deverá ser utilizada quando:

```text
tipoConteudo = application/zpl
```

O conteúdo deverá ser convertido para bytes utilizando a codificação informada.

Na versão 1, a codificação suportada será:

```text
utf-8
```

O conteúdo deverá ser enviado em modo RAW para a fila da impressora.

O agente não deverá:

* rasterizar o ZPL;
* gerar PDF;
* interpretar comandos;
* alterar coordenadas;
* substituir caracteres;
* adicionar comandos `^XA` ou `^XZ`;
* corrigir comandos inválidos.

O sistema cliente é responsável por gerar ZPL completo e válido, inclusive
qualquer normalização de espaços e quebras de linha. O agente deve preservar
integralmente os bytes UTF-8 recebidos. Para `application/zpl`, o mapa
`configuracoesImpressao` é ignorado também no CUPS: nenhuma opção de tamanho,
escala, orientação ou ajuste do driver deve ser aplicada ao trabalho ZPL.

---

## 42.4 Estratégia PDF

A estratégia PDF deverá ser utilizada quando:

```text
tipoConteudo = application/pdf
```

O conteúdo deverá ser recebido em Base64 e decodificado para bytes.

A impressão deverá utilizar os recursos disponíveis no sistema operacional.

No Linux, o PDF poderá ser encaminhado ao CUPS.

Na versão 1.0.0, a estratégia PDF será suportada somente pelo Provider Linux.
O suporte no Windows está planejado para a versão 1.2.0.

O agente não deverá:

* alterar o tamanho do PDF;
* rotacionar o PDF;
* ajustar margens;
* modificar escala;
* converter páginas;
* aplicar configurações não solicitadas.

Configurações de mídia e orientação poderão ser encaminhadas ao Provider quando presentes em `configuracoesImpressao`.

---

## 42.5 Estratégia RAW

A estratégia RAW deverá ser utilizada quando:

```text
tipoConteudo = text/plain
```

O conteúdo deverá ser convertido para bytes utilizando UTF-8.

O agente deverá encaminhar os bytes sem formatação adicional.

O modo RAW poderá ser utilizado para:

* texto simples;
* comandos específicos de impressoras;
* linguagens não classificadas separadamente;
* testes técnicos.

O agente não deverá acrescentar:

* quebras de linha;
* cabeçalhos;
* rodapés;
* comandos de alimentação;
* comandos de corte.

---

## 42.6 Tipos desconhecidos

Caso nenhuma estratégia suporte o tipo informado, a requisição deverá ser rejeitada com:

```text
TIPO_CONTEUDO_NAO_SUPORTADO
```

e HTTP:

```text
415 Unsupported Media Type
```

---

## 42.7 Registro das estratégias

As estratégias deverão ser descobertas por injeção de dependência.

Não deverá existir uma sequência extensa de condicionais como:

```java
if (tipo.equals("application/zpl")) {
    // ...
} else if (tipo.equals("application/pdf")) {
    // ...
} else if (tipo.equals("text/plain")) {
    // ...
}
```

O `PrintService` deverá selecionar a estratégia compatível entre as implementações registradas.

---

# 43. Provider de Impressão

## 43.1 Objetivo

O Provider abstrai a comunicação com o sistema operacional.

O restante da aplicação não deverá conhecer detalhes específicos do CUPS, Windows, comandos de terminal ou bibliotecas de impressão.

---

## 43.2 Contrato mínimo

O Provider deverá oferecer operações equivalentes a:

```text
listar impressoras

localizar impressora

consultar impressora padrão

consultar capacidades

enviar documento

cancelar trabalho, quando suportado

consultar disponibilidade
```

---

## 43.3 Modelo conceitual

Exemplo:

```java
public interface PrinterProvider {

    List<Impressora> listarImpressoras();

    Optional<Impressora> localizar(String nome);

    Optional<Impressora> obterPadrao();

    CapacidadesImpressora consultarCapacidades(String nome);

    ResultadoImpressao imprimir(SolicitacaoImpressao solicitacao);

    boolean estaDisponivel(String nome);
}
```

Os nomes são referenciais.

A implementação poderá adaptar o contrato, desde que preserve as responsabilidades.

---

## 43.4 Seleção do Provider

A seleção deverá ocorrer durante a inicialização da aplicação.

Para Linux:

```text
LinuxPrinterProvider
```

Para Windows:

```text
WindowsPrinterProvider
```

Sistemas operacionais não suportados deverão impedir a inicialização operacional do agente.

O status da aplicação deverá permanecer fora de:

```text
OPERACIONAL
```

e um erro claro deverá ser registrado.

---

## 43.5 Detecção do sistema operacional

A detecção deverá ser centralizada.

Não será permitido espalhar verificações como:

```java
System.getProperty("os.name")
```

por diversas classes.

Deverá existir um componente responsável pela identificação da plataforma.

Exemplo:

```text
SistemaOperacionalService
```

ou:

```text
PlataformaDetector
```

---

# 44. Integração com Linux e CUPS

## 44.1 Objetivo

No Linux, o Print Agent deverá utilizar o CUPS como sistema de impressão.

---

## 44.2 Pré-requisitos

O ambiente Linux deverá possuir:

* CUPS instalado;
* serviço CUPS em execução;
* impressora cadastrada;
* permissões para o usuário do Print Agent;
* comandos necessários disponíveis no sistema.

---

## 44.3 Comandos permitidos

A implementação poderá utilizar comandos do CUPS, incluindo:

```text
lp

lpstat

lpoptions

lpinfo

cancel
```

A utilização deverá ser encapsulada exclusivamente no `LinuxPrinterProvider`.

Nenhuma outra camada poderá executar esses comandos.

---

## 44.4 Listagem de impressoras

A listagem poderá utilizar:

```bash
lpstat -p -d
```

ou mecanismo equivalente.

O resultado deverá ser convertido para objetos internos.

A aplicação não deverá expor diretamente a saída textual do comando.

---

## 44.5 Consulta de capacidades

A consulta poderá utilizar:

```bash
lpoptions -p NOME_DA_IMPRESSORA -l
```

O Provider deverá transformar as opções retornadas em:

```text
Map<String, List<String>>
```

Exemplo de origem:

```text
PageSize/Media Size: *w100h80 w100h100 w100h150
```

Exemplo convertido:

```json
{
    "PageSize": [
        "w100h80",
        "w100h100",
        "w100h150"
    ]
}
```

O caractere `*`, utilizado para indicar o valor padrão, não deverá fazer parte do valor retornado.

A informação sobre o valor padrão poderá ser ignorada na versão 1.

---

## 44.6 Impressão RAW no CUPS

Para ZPL e RAW, o Provider deverá encaminhar os bytes sem conversão.

Uma implementação possível é utilizar:

```bash
lp -d NOME_DA_IMPRESSORA -o raw ARQUIVO_TEMPORARIO
```

ou enviar os dados pela API disponível do CUPS.

Caso sejam utilizados arquivos temporários, deverão ser:

* criados em diretório seguro;
* removidos após o envio;
* nomeados com identificador aleatório;
* inacessíveis a outros usuários quando possível.

---

## 44.7 Impressão PDF no CUPS

Para PDF, o Provider poderá utilizar:

```bash
lp -d NOME_DA_IMPRESSORA ARQUIVO.pdf
```

Configurações recebidas deverão ser encaminhadas como opções.

Exemplo conceitual:

```bash
lp \
  -d ElginL42 \
  -o PageSize=w100h60 \
  -o Orientation=0 \
  arquivo.pdf
```

O Provider deverá construir argumentos como itens separados.

Não deverá concatenar um comando de shell completo.

Essa regra evita problemas de injeção de comandos.

---

## 44.8 Segurança na execução de comandos

Deverá ser utilizado mecanismo equivalente a:

```java
ProcessBuilder
```

Cada argumento deverá ser informado separadamente.

Exemplo aceitável:

```java
new ProcessBuilder(
    "lp",
    "-d",
    nomeImpressora,
    "-o",
    "PageSize=w100h60",
    caminhoArquivo
);
```

Exemplo proibido:

```java
Runtime.getRuntime().exec(
    "lp -d " + nomeImpressora + " " + caminhoArquivo
);
```

Entradas do usuário nunca deverão ser interpretadas por um shell.

---

## 44.9 Resultado do comando

O Provider deverá capturar:

* código de saída;
* saída padrão;
* saída de erro;
* tempo de execução.

Código de saída diferente de zero deverá ser tratado como falha.

---

## 44.10 Timeout

Todo comando externo deverá possuir timeout.

Valor padrão sugerido:

```yaml
print-agent:
  provider:
    timeout-comando-segundos: 15
```

Ao exceder o timeout:

* o processo deverá ser finalizado;
* o trabalho deverá ir para `ERRO`;
* o código deverá ser `FALHA_IMPRESSAO`;
* o evento deverá ser registrado nos logs.

---

# 45. Integração com Windows

## 45.1 Objetivo

No Windows, o Print Agent deverá utilizar os serviços de impressão disponíveis na plataforma Java ou outro mecanismo nativo encapsulado no `WindowsPrinterProvider`.

---

## 45.2 Java Print Service

A implementação inicial deverá priorizar:

```text
javax.print
```

ou APIs equivalentes disponíveis no Java 17.

Principais responsabilidades:

* listar `PrintService`;
* localizar impressora por nome;
* consultar impressora padrão;
* criar `DocPrintJob`;
* enviar bytes;
* receber eventos de impressão quando disponíveis.

---

## 45.3 Localização da impressora

A impressora deverá ser localizada pelo nome retornado por:

```java
PrintService.getName()
```

O nome deverá corresponder ao informado no JSON.

---

## 45.4 Impressão ZPL e RAW

Para ZPL e RAW, o Provider deverá criar um documento baseado em bytes.

Exemplo conceitual de `DocFlavor`:

```java
DocFlavor.BYTE_ARRAY.AUTOSENSE
```

ou outro formato compatível com a impressora.

O Provider deverá evitar conversão de texto pelo sistema operacional quando o objetivo for impressão RAW.

---

## 45.5 Impressão PDF

A impressão PDF pelo Provider Windows não faz parte da versão 1.0.0 e está
planejada para a versão 1.2.0. Até essa versão, uma solicitação PDF destinada ao
Provider Windows deverá falhar com mensagem clara, sem tentar enviar o documento
ao spooler.

Código:

```text
TIPO_CONTEUDO_NAO_SUPORTADO_PELO_PROVIDER
```

Esse código deverá ser acrescentado à lista de erros da API.

---

## 45.6 Configurações de impressão

Configurações conhecidas pelo Java Print Service poderão ser convertidas para atributos de impressão.

Configurações externas e específicas de drivers poderão não estar disponíveis por meio do Java Print Service.

Na versão 1:

* o agente deverá aplicar as propriedades suportadas;
* propriedades não suportadas deverão ser registradas;
* o trabalho não deverá falhar exclusivamente pela existência de uma propriedade desconhecida, salvo quando configurado para modo estrito.

---

## 45.7 Modo estrito

O comportamento deverá ser configurável.

Exemplo:

```yaml
print-agent:
  impressao:
    configuracoes-desconhecidas: IGNORAR
```

Valores permitidos:

```text
IGNORAR

REJEITAR
```

Valor padrão:

```text
IGNORAR
```

---

# 46. Configurações do Trabalho de Impressão

## 46.1 Objetivo

As configurações recebidas em:

```text
configuracoesImpressao
```

pertencem exclusivamente ao trabalho atual.

Elas não deverão alterar permanentemente a configuração padrão da impressora.

---

## 46.2 Aplicação temporária

O Provider deverá aplicar as configurações apenas ao trabalho enviado.

Não deverá executar comandos equivalentes a alterar opções padrão globais, salvo quando tecnicamente inevitável e devidamente revertido.

No Linux, deve-se preferir:

```bash
lp -o NomeOpcao=Valor
```

em vez de:

```bash
lpoptions -p Impressora -o NomeOpcao=Valor
```

O segundo comando altera opções padrão e não deverá ser utilizado no fluxo normal de impressão.

---

## 46.3 Propriedades genéricas

Exemplos de propriedades que poderão ser enviadas:

```text
PageSize

Orientation

Resolution

PrintSpeed

PrintDarkness

MediaMethod

PaperType

MirrorImage

NegativeImage
```

A lista não é fechada.

---

## 46.4 Validação sintática

As chaves não poderão:

* ser vazias;
* conter caracteres de controle;
* começar com hífen;
* conter quebras de linha;
* conter caracteres de shell destinados a composição de comandos.

Os valores não poderão:

* conter caracteres de controle;
* conter quebras de linha;
* exceder o tamanho máximo configurado.

Tamanho máximo sugerido por chave ou valor:

```text
200 caracteres
```

---

## 46.5 Validação semântica

O Print Agent não deverá interpretar o significado das propriedades.

A validação semântica poderá ser delegada ao Provider ou ao sistema operacional.

---

## 46.6 Propriedades não suportadas

Quando uma propriedade não for suportada:

* em modo `IGNORAR`, a propriedade deverá ser desconsiderada e registrada em
  log, e o processamento deverá continuar sempre que as propriedades válidas e
  os padrões do driver forem suficientes para concluir a impressão;
* em modo `IGNORAR`, o trabalho deverá falhar com mensagem clara quando, após
  desconsiderar as propriedades não suportadas, não restarem informações
  suficientes para o Provider concluir a impressão;
* em modo `REJEITAR`, o trabalho deverá falhar antes do envio.

Código sugerido:

```text
CONFIGURACAO_IMPRESSAO_NAO_SUPORTADA
```

---

## 46.7 Cópias

A quantidade de cópias pertence ao objeto:

```text
trabalho.copias
```

e não deverá ser duplicada dentro de `configuracoesImpressao`.

O Provider deverá decidir entre:

* solicitar múltiplas cópias ao spooler;
* enviar o documento repetidas vezes.

Deverá ser priorizado o suporte nativo do spooler.

Para documentos ZPL, caso o Provider ou a impressora não suporte cópias nativas, o conteúdo poderá ser enviado a quantidade de vezes solicitada.

---

## 46.8 Tamanho de etiqueta

O Print Agent não deverá converter automaticamente dimensões como:

```text
100x60
```

para nomes de mídia específicos como:

```text
w100h60
```

O sistema cliente deverá utilizar o valor retornado pelo endpoint de capacidades da impressora.

Essa regra evita suposições incorretas entre drivers.

---

## 46.9 Orientação

O agente não deverá presumir o significado de valores como:

```text
0

1

2

3
```

O valor deverá ser encaminhado conforme recebido.

---

## 46.10 Configurações ausentes

Quando `configuracoesImpressao` não for enviado ou estiver vazio:

```json
{}
```

o Provider deverá utilizar os padrões atuais da fila da impressora.

---

# 47. Interface Web

## 47.1 Objetivo

O Print Agent deverá disponibilizar uma interface Web local para consulta, configuração básica, diagnóstico e acompanhamento das impressões.

A interface deverá ser acessível pelo endereço:

```text
http://localhost:18181
```

A interface Web deverá consumir a mesma API REST documentada para integrações externas.

Não deverão existir regras de negócio exclusivas na interface.

---

## 47.2 Tecnologias

A interface deverá utilizar:

* HTML;
* CSS;
* JavaScript;
* recursos estáticos servidos pelo Spring Boot.

Na versão 1, não deverá ser utilizado framework JavaScript de aplicação, como:

* Angular;
* React;
* Vue;
* Svelte.

A interface deverá permanecer simples, leve e de fácil manutenção.

Bibliotecas pequenas poderão ser utilizadas quando agregarem valor claro, desde que sejam empacotadas com a aplicação e não dependam de CDN.

---

## 47.3 Funcionamento local

A interface deverá funcionar sem conexão com a Internet.

Todos os arquivos necessários deverão estar incluídos na aplicação.

É proibida a dependência obrigatória de:

* serviços externos;
* fontes hospedadas na Internet;
* bibliotecas carregadas por CDN;
* APIs de terceiros;
* autenticação em nuvem.

---

## 47.4 Idioma

A versão 1 deverá apresentar todos os textos em português do Brasil.

Exemplos:

```text
Impressoras

Trabalhos

Histórico

Configurações

Logs

Impressão de teste
```

A internacionalização não faz parte do escopo da versão 1.

---

## 47.5 Estrutura de navegação

A interface deverá possuir as seguintes áreas:

```text
Dashboard

Impressoras

Trabalhos

Histórico

Teste de impressão

Configurações

Logs

Sobre
```

A navegação deverá permanecer disponível em todas as telas.

---

# 48. Dashboard

## 48.1 Objetivo

O Dashboard deverá apresentar uma visão resumida do estado do Print Agent.

A tela deverá permitir que o usuário identifique rapidamente se o agente está operacional e se existem problemas de impressão.

---

## 48.2 Informações obrigatórias

O Dashboard deverá exibir:

* estado atual do agente;
* versão instalada;
* sistema operacional;
* versão do Java;
* horário de inicialização;
* porta HTTP;
* quantidade de impressoras encontradas;
* quantidade de trabalhos na fila;
* quantidade de trabalhos concluídos no histórico;
* quantidade de trabalhos com erro;
* impressora padrão do sistema.

---

## 48.3 Exemplo visual

```text
+-------------------------------------------------------+
| Print Agent 1.0.0                         OPERACIONAL |
+-------------------------------------------------------+
| Impressoras encontradas: 5                            |
| Impressora padrão: ElginL42                           |
| Trabalhos na fila: 0                                  |
| Concluídos: 18                                        |
| Erros: 1                                              |
| Iniciado em: 15/07/2026 08:00                         |
+-------------------------------------------------------+
```

---

## 48.4 Estado da aplicação

O estado deverá ser apresentado com texto claro.

Valores possíveis:

```text
INICIANDO

OPERACIONAL

PARANDO

FINALIZADO
```

A interface poderá utilizar indicadores visuais, mas não deverá depender somente de cores.

Exemplo aceitável:

```text
● OPERACIONAL
```

Exemplo inadequado:

```text
●
```

sem descrição textual.

---

## 48.5 Atualização dos dados

Os dados do Dashboard deverão ser atualizados periodicamente.

Intervalo padrão sugerido:

```text
5 segundos
```

O intervalo deverá ser suficientemente longo para não gerar carga desnecessária.

A atualização deverá utilizar:

```text
GET /api/v1/status
```

---

## 48.6 Falha de comunicação

Caso a interface não consiga acessar a API local, deverá apresentar:

```text
Não foi possível comunicar com o Print Agent.
```

A tela não deverá permanecer carregando indefinidamente.

---

# 49. Tela de Impressoras

## 49.1 Objetivo

A tela de Impressoras deverá listar todas as impressoras encontradas pelo sistema operacional.

---

## 49.2 Informações da lista

Para cada impressora, deverão ser exibidos:

* nome;
* status;
* indicação de impressora padrão;
* botão para consultar capacidades;
* botão para realizar impressão de teste.

---

## 49.3 Exemplo

| Nome               | Status  | Padrão | Ações                |
| ------------------ | ------- | -----: | -------------------- |
| ElginL42           | READY   |    Sim | Capacidades / Testar |
| EPSON-L3250-Series | OFFLINE |    Não | Capacidades / Testar |
| ML-2160-Series     | READY   |    Não | Capacidades / Testar |

---

## 49.4 Atualização da lista

A tela deverá oferecer uma ação:

```text
Atualizar impressoras
```

Essa ação deverá solicitar uma nova descoberta ao `PrinterService`.

O endpoint necessário deverá ser definido em `API.md`.

A atualização não deverá exigir reinicialização do agente.

---

## 49.5 Consulta de capacidades

Ao selecionar:

```text
Capacidades
```

a interface deverá consultar:

```text
GET /api/v1/impressoras/{nome}/capacidades
```

As capacidades deverão ser exibidas em uma tabela.

Exemplo:

| Propriedade | Valores                     |
| ----------- | --------------------------- |
| PageSize    | w100h80, w100h100, w100h150 |
| Resolution  | 203dpi                      |
| Orientation | 0, 1, 2, 3                  |
| PrintSpeed  | 2, 3, 4                     |

---

## 49.6 Capacidades indisponíveis

Caso o Provider não consiga consultar as capacidades, a interface deverá informar:

```text
O sistema operacional não disponibilizou as capacidades desta impressora.
```

Isso não deverá ser apresentado como falha geral do agente.

---

## 49.7 Impressoras indisponíveis

Impressoras com status:

```text
OFFLINE

STOPPED

UNKNOWN
```

deverão continuar visíveis.

A interface não deverá ocultar impressoras problemáticas.

---

# 50. Tela de Trabalhos

## 50.1 Objetivo

A tela de Trabalhos deverá apresentar os trabalhos atuais, incluindo:

* trabalhos recebidos;
* trabalhos validados;
* trabalhos na fila;
* trabalho em impressão.

---

## 50.2 Informações obrigatórias

A lista deverá apresentar:

| Campo      | Descrição                                       |
| ---------- | ----------------------------------------------- |
| ID         | Identificador reduzido ou completo do trabalho. |
| Nome       | Nome administrativo do trabalho.                |
| Impressora | Impressora de destino.                          |
| Tipo       | Tipo do documento.                              |
| Cópias     | Quantidade solicitada.                          |
| Status     | Estado atual.                                   |
| Criado em  | Data e hora de criação.                         |
| Ações      | Consulta e cancelamento.                        |

---

## 50.3 Ordenação

A lista deverá ser ordenada por data de criação.

O trabalho mais recente deverá aparecer primeiro.

A fila interna continuará sendo FIFO, independentemente da ordenação visual.

---

## 50.4 Atualização

A tela deverá atualizar os dados periodicamente.

Intervalo sugerido:

```text
3 segundos
```

A atualização deverá ocorrer apenas enquanto a tela estiver aberta.

---

## 50.5 Consulta de detalhes

Ao selecionar um trabalho, a interface deverá exibir:

* UUID completo;
* nome;
* impressora;
* tipo de conteúdo;
* quantidade de cópias;
* status;
* data de criação;
* data de início;
* data de término;
* mensagem de erro;
* configurações de impressão, quando disponíveis.

O conteúdo completo do documento não deverá ser exibido.

---

## 50.6 Cancelamento

A ação de cancelamento deverá estar disponível somente para trabalhos nos estados:

```text
RECEBIDO

VALIDADO

NA_FILA
```

A interface deverá solicitar confirmação:

```text
Deseja cancelar este trabalho de impressão?
```

Após a confirmação, deverá chamar:

```text
DELETE /api/v1/trabalhos/{id}
```

---

## 50.7 Trabalho não cancelável

Quando o trabalho já estiver:

```text
IMPRIMINDO

CONCLUIDO

ERRO

CANCELADO
```

o botão de cancelamento deverá permanecer indisponível.

Caso o servidor rejeite o cancelamento, a mensagem retornada pela API deverá ser exibida.

---

# 51. Tela de Histórico

## 51.1 Objetivo

A tela de Histórico deverá apresentar os trabalhos concluídos, cancelados ou encerrados com erro.

---

## 51.2 Estados exibidos

Deverão ser exibidos trabalhos nos estados:

```text
CONCLUIDO

ERRO

CANCELADO
```

---

## 51.3 Informações

A lista deverá apresentar:

* identificador;
* nome;
* impressora;
* tipo do documento;
* cópias;
* estado final;
* data de criação;
* data de término;
* duração do processamento;
* mensagem resumida de erro.

---

## 51.4 Limite

O histórico deverá respeitar o limite configurado no agente.

Valor padrão:

```text
100 registros
```

Quando o limite for excedido, o registro mais antigo deverá ser removido.

---

## 51.5 Persistência

O histórico da versão 1 será mantido apenas em memória.

Após reinicialização do agente, a tela deverá iniciar vazia.

A interface poderá informar:

```text
O histórico é limpo quando o Print Agent é reiniciado.
```

---

## 51.6 Filtros

A tela deverá permitir filtragem local por:

* nome;
* impressora;
* status;
* tipo de conteúdo.

Não será necessária paginação no servidor na versão 1.

---

## 51.7 Limpeza manual

A interface poderá oferecer:

```text
Limpar histórico
```

A ação deverá exigir confirmação.

A limpeza não deverá apagar arquivos de log.

---

# 52. Tela de Teste de Impressão

## 52.1 Objetivo

A tela de Teste de Impressão deverá permitir verificar se o Print Agent consegue enviar dados para uma impressora selecionada.

---

## 52.2 Tipos de teste

A versão 1 deverá oferecer:

```text
Teste ZPL

Teste de texto RAW

Teste PDF
```

O teste PDF poderá ser disponibilizado apenas quando houver um arquivo de teste incluído na aplicação.

---

## 52.3 Seleção da impressora

O usuário deverá selecionar uma impressora entre as impressoras encontradas.

A impressora padrão poderá aparecer previamente selecionada.

---

## 52.4 Teste ZPL

O teste ZPL deverá utilizar conteúdo interno e conhecido.

Exemplo:

```zpl
^XA
^PW800
^LL480
^FO20,20
^A0N,30,30
^FDTESTE PRINT AGENT^FS
^FO20,70
^A0N,25,25
^FDImpressora ZPL operacional^FS
^XZ
```

O layout do teste poderá ser ajustado, mas deverá permanecer simples.

---

## 52.5 Teste RAW

O teste RAW poderá utilizar:

```text
TESTE PRINT AGENT
```

O agente não deverá presumir que todas as impressoras imprimirão texto RAW corretamente.

---

## 52.6 Configurações de teste

A tela deverá permitir informar configurações opcionais no formato de chave e valor.

Exemplo:

| Propriedade   | Valor   |
| ------------- | ------- |
| PageSize      | w100h60 |
| PrintDarkness | 20      |
| PrintSpeed    | 4       |

As propriedades deverão ser enviadas como:

```json
{
    "PageSize": "w100h60",
    "PrintDarkness": "20",
    "PrintSpeed": "4"
}
```

---

## 52.7 Resultado

Após o envio, a interface deverá apresentar:

* identificador do trabalho;
* status;
* mensagem retornada;
* link ou ação para consultar o trabalho.

---

## 52.8 Aviso

A tela deverá exibir:

```text
O Print Agent confirma o envio ao sistema de impressão. Ele não consegue garantir que o papel ou a etiqueta saiu fisicamente da impressora.
```

---

# 53. Tela de Configurações

## 53.1 Objetivo

A tela de Configurações deverá permitir consultar e alterar apenas configurações operacionais autorizadas.

---

## 53.2 Configurações editáveis

A versão 1 poderá permitir alteração de:

* tamanho máximo do histórico;
* intervalo de atualização da lista de impressoras;
* timeout de comandos externos;
* timeout de trabalhos síncronos;
* comportamento para configurações desconhecidas;
* verificação automática de atualizações;
* nível de log.

---

## 53.3 Configurações não editáveis

A interface não deverá permitir alteração de:

* bind de rede;
* endereço de escuta;
* namespace Java;
* caminhos internos;
* comandos do sistema operacional;
* versão da API.

A porta poderá permanecer configurável apenas pelo arquivo de configuração.

Alterar a porta pela interface criaria risco de perda de comunicação com o sistema cliente.

---

## 53.4 Armazenamento

As configurações editáveis deverão ser armazenadas em arquivo externo à aplicação.

A configuração não deverá ser gravada dentro do JAR.

O formato deverá permanecer compatível com YAML.

---

## 53.5 Aplicação das alterações

Cada configuração deverá informar se exige reinicialização.

Exemplo:

| Configuração               |       Reinicialização |
| -------------------------- | --------------------: |
| Tamanho do histórico       |                   Não |
| Nível de log               | Não, quando suportado |
| Timeout de comandos        |                   Não |
| Porta HTTP                 |                   Sim |
| Verificação de atualização |                   Não |

---

## 53.6 Validação

A interface deverá validar:

* campos obrigatórios;
* limites numéricos;
* valores permitidos;
* formatos.

A validação do servidor continuará obrigatória.

A validação no navegador não substitui a validação da API.

---

## 53.7 Restauração

A tela poderá oferecer:

```text
Restaurar valores padrão
```

A ação deverá exigir confirmação e informar quais propriedades serão alteradas.

---

# 54. Tela de Logs

## 54.1 Objetivo

A tela de Logs deverá auxiliar no diagnóstico de problemas sem exigir acesso manual aos arquivos do sistema.

---

## 54.2 Conteúdo

A tela deverá permitir consultar as últimas linhas do log atual.

Quantidade padrão sugerida:

```text
500 linhas
```

---

## 54.3 Filtros

A interface deverá permitir filtrar por nível:

```text
ERROR

WARN

INFO

DEBUG
```

Também deverá permitir busca textual.

---

## 54.4 Atualização

A tela poderá oferecer:

```text
Atualizar
```

e:

```text
Atualização automática
```

A atualização automática deverá vir desativada por padrão.

---

## 54.5 Download

A interface deverá permitir baixar o arquivo de log atual.

Quando existirem arquivos rotacionados, eles poderão ser listados para download.

---

## 54.6 Conteúdo protegido

A interface e os arquivos de log não deverão exibir integralmente:

* PDFs em Base64;
* conteúdo completo de documentos;
* grandes blocos ZPL;
* dados binários;
* rastreamentos de pilha em respostas comuns da API.

Rastreamentos de pilha poderão permanecer no arquivo de log técnico.

---

## 54.7 Limpeza

A interface não deverá permitir apagar logs na versão 1.

Essa decisão evita remoção acidental de informações de diagnóstico.

---

# 55. Tela Sobre

## 55.1 Objetivo

A tela Sobre deverá apresentar informações técnicas e institucionais do agente.

---

## 55.2 Informações obrigatórias

A tela deverá exibir:

* nome do produto;
* versão;
* data do build;
* Java utilizado;
* Spring Boot;
* sistema operacional;
* namespace;
* porta;
* localização do arquivo de configuração;
* localização dos logs;
* endereço da documentação local, quando disponível.

---

## 55.3 Exemplo

```text
Print Agent

Versão: 1.0.0
Java: 17
Namespace: br.eng.eliseu.printagent
Porta: 18181
Sistema operacional: Linux
```

---

# 56. Requisitos de Usabilidade

## UI-001

A interface deverá ser utilizável em resoluções a partir de:

```text
1024 x 768
```

---

## UI-002

A interface deverá se adaptar a telas menores, embora o uso principal seja em computadores.

---

## UI-003

Todas as ações deverão possuir descrição textual.

---

## UI-004

Mensagens de erro deverão explicar:

* o que ocorreu;
* qual item causou o problema;
* qual ação o usuário poderá tentar.

---

## UI-005

A interface não deverá exibir exceções Java diretamente.

---

## UI-006

Operações destrutivas ou irreversíveis deverão solicitar confirmação.

Exemplos:

* cancelar trabalho;
* limpar histórico;
* restaurar configurações.

---

## UI-007

Botões indisponíveis deverão permanecer desabilitados, não ocultos, quando isso ajudar o usuário a compreender o fluxo.

---

## UI-008

A interface não deverá abrir novas janelas desnecessariamente.

---

## UI-009

O navegador não deverá armazenar o conteúdo dos documentos enviados para impressão.

---

## UI-010

A interface deverá apresentar feedback visual durante operações demoradas.

Exemplos:

```text
Atualizando impressoras...

Enviando teste...

Salvando configurações...
```

---

# 57. Requisitos de Acessibilidade

## 57.1 Navegação

As principais ações deverão ser acessíveis por teclado.

---

## 57.2 Rótulos

Campos de formulário deverão possuir rótulos associados.

---

## 57.3 Contraste

Textos e elementos de interface deverão possuir contraste suficiente.

---

## 57.4 Estados

Estados não deverão ser indicados somente por cor.

Exemplo correto:

```text
ERRO
```

acompanhado de destaque visual.

---

## 57.5 Mensagens

Mensagens importantes deverão permanecer visíveis por tempo suficiente para leitura.

Erros críticos não deverão desaparecer automaticamente.

---

# 58. Comunicação da Web UI com a API

## 58.1 Endereço

A interface deverá utilizar caminhos relativos.

Exemplo:

```text
/api/v1/status
```

Não deverá fixar:

```text
http://localhost:18181
```

no código JavaScript.

Essa decisão permite que a interface continue funcionando caso a porta seja alterada.

---

## 58.2 Formato

As requisições deverão utilizar:

```text
Content-Type: application/json
```

quando possuírem corpo JSON.

---

## 58.3 Tratamento do envelope

A interface deverá interpretar:

```json
{
    "sucesso": true,
    "mensagem": "",
    "dados": {},
    "erros": [],
    "dataHora": ""
}
```

Quando:

```json
"sucesso": false
```

os itens de `erros` deverão ser exibidos de forma legível.

---

## 58.4 Timeout

As chamadas da interface deverão possuir timeout.

Valor sugerido:

```text
15 segundos
```

---

## 58.5 Repetição de requisições

Consultas automáticas poderão ser repetidas.

Operações de escrita não deverão ser reenviadas automaticamente sem confirmação.

Exemplo:

```text
POST /trabalhos
```

não poderá ser repetido automaticamente, pois isso poderá gerar impressão duplicada.

---

## 58.6 Estado da interface

A interface poderá manter estado temporário apenas em memória do navegador.

Não deverá depender de banco de dados local.

---

## 58.7 Cache

Endpoints de estado, impressoras, trabalhos e logs não deverão ser armazenados em cache pelo navegador.

Os recursos estáticos poderão utilizar cache controlado por versão.

---

# 59. Interface Web e Acesso Local

## 59.1 Restrição de origem

A interface será servida pelo próprio Print Agent.

Origem esperada:

```text
http://localhost:18181
```

---

## 59.2 Acesso por outras aplicações Web

O frontend do sistema Presente estará hospedado em domínio diferente e precisará acessar a API local.

O Print Agent deverá possuir configuração de CORS.

A configuração deverá permitir explicitamente as origens autorizadas.

Exemplo:

```yaml
print-agent:
  cors:
    origens-permitidas:
      - "https://presente.exemplo.com.br"
```

---

## 59.3 Desenvolvimento local

A configuração poderá permitir origens de desenvolvimento.

Exemplo:

```yaml
print-agent:
  cors:
    origens-permitidas:
      - "http://localhost:4200"
      - "https://presente.exemplo.com.br"
```

---

## 59.4 Origem curinga

Não deverá ser utilizado:

```text
*
```

em ambientes de produção.

Embora a API não utilize autenticação, permitir qualquer origem possibilitaria que sites não autorizados tentassem enviar impressões ao agente local.

---

## 59.5 Métodos permitidos

O CORS deverá autorizar apenas os métodos necessários:

```text
GET

POST

DELETE

OPTIONS
```

Caso seja criado endpoint de atualização parcial, `PUT` ou `PATCH` poderá ser adicionado de forma explícita.

---

## 59.6 Cabeçalhos

Deverão ser permitidos apenas os cabeçalhos necessários.

Exemplo:

```text
Content-Type

Accept
```

---

## 59.7 Acesso pela rede

O servidor HTTP deverá escutar apenas na interface de loopback.

Configuração esperada:

```yaml
server:
  address: 127.0.0.1
  port: 18181
```

A versão 1 não deverá aceitar conexões por endereços da rede local.

---

## 59.8 IPv6

Quando houver suporte, o agente poderá aceitar:

```text
::1
```

A implementação deverá continuar restrita ao loopback.

---

# 60. Requisitos da Web UI

## WEB-001

A interface deverá ser servida pelo próprio Spring Boot.

---

## WEB-002

A interface deverá funcionar sem Internet.

---

## WEB-003

A interface deverá utilizar a mesma API REST disponibilizada ao sistema cliente.

---

## WEB-004

Nenhuma regra de negócio deverá existir exclusivamente no JavaScript.

---

## WEB-005

A interface deverá funcionar nas versões atuais dos navegadores:

* Google Chrome;
* Microsoft Edge;
* Firefox.

---

## WEB-006

A interface deverá exibir claramente o nome e a versão do agente.

---

## WEB-007

A interface deverá tratar falhas de comunicação sem travar a navegação.

---

## WEB-008

A interface não deverá enviar trabalhos automaticamente ao carregar uma página.

---

## WEB-009

Toda impressão de teste deverá exigir ação explícita do usuário.

---

## WEB-010

A interface deverá utilizar caminhos relativos para acessar a API.

---

## WEB-011

A interface deverá impedir múltiplos cliques consecutivos durante o envio de uma operação.

---

## WEB-012

O conteúdo de um trabalho não deverá ser exibido nas telas de fila ou histórico.

---

## WEB-013

A interface não deverá armazenar ZPL, PDF ou RAW em `localStorage`.

---

## WEB-014

Configurações sensíveis à inicialização deverão informar que exigem reinicialização.

---

## WEB-015

A interface deverá permitir copiar mensagens técnicas e identificadores de trabalhos para facilitar o suporte.

---

# 61. Instalação e Distribuição

## 61.1 Objetivo

O Print Agent deverá possuir processo de instalação simples, previsível e compatível com Windows e Linux.

A instalação deverá preparar o ambiente para que o agente:

* inicie automaticamente com o sistema operacional;
* execute sem intervenção do usuário;
* escute exclusivamente em endereço local;
* mantenha configurações fora do pacote da aplicação;
* preserve configurações durante atualizações;
* grave logs em diretório apropriado;
* possa ser removido de forma segura.

---

## 61.2 Formatos de distribuição

A versão 1 deverá possuir os seguintes formatos de distribuição:

| Sistema operacional           | Formato principal           |
| ----------------------------- | --------------------------- |
| Windows                       | Instalador `.msi` ou `.exe` |
| Linux Debian/Ubuntu/Mint      | Pacote `.deb`               |
| Desenvolvimento e diagnóstico | Arquivo `.jar` executável   |

O arquivo JAR não deverá ser considerado o formato principal para usuários finais.

---

## 61.3 Artefatos de build

O processo Maven deverá gerar, no mínimo:

```text
print-agent-1.0.0.jar
```

Os instaladores deverão incorporar ou utilizar esse artefato.

A versão do nome do arquivo deverá corresponder à versão do projeto definida no `pom.xml`.

---

## 61.4 Java

O Print Agent deverá utilizar Java 17.

A instalação poderá seguir uma das seguintes estratégias:

### Java empacotado

O instalador inclui um runtime Java compatível com a aplicação.

Essa deverá ser a estratégia preferencial para distribuição aos usuários finais.

Vantagens:

* elimina dependência de Java previamente instalado;
* reduz conflitos de versão;
* simplifica suporte;
* permite controlar o runtime utilizado.

### Java do sistema

O agente utiliza uma instalação Java 17 já existente.

Essa estratégia poderá ser mantida para desenvolvimento ou instalações administradas.

Quando utilizada, o instalador deverá validar:

```text
java.version >= 17
```

Versões inferiores deverão impedir a instalação ou inicialização.

---

## 61.5 Runtime reduzido

Quando tecnicamente viável, o projeto poderá utilizar:

```text
jlink
```

ou mecanismo equivalente para gerar um runtime reduzido.

O runtime deverá conter todos os módulos necessários à aplicação, incluindo:

* HTTP;
* logging;
* Java Print Service;
* manipulação de arquivos;
* processos externos;
* segurança e criptografia necessárias ao Spring Boot.

A utilização de runtime reduzido não deverá alterar o comportamento da aplicação.

---

# 62. Estrutura de Instalação

## 62.1 Princípio geral

Arquivos executáveis, configurações e logs deverão permanecer separados.

A aplicação não deverá gravar configurações dentro do diretório do JAR quando isso impedir atualizações seguras.

---

## 62.2 Estrutura sugerida no Windows

```text
C:\Program Files\Print Agent\
├── app\
│   ├── print-agent.jar
│   └── runtime\
├── config\
│   └── application.yml
├── logs\
├── docs\
└── uninstall\
```

Quando as permissões do Windows impedirem escrita em `Program Files`, configurações e logs deverão ser armazenados em:

```text
C:\ProgramData\PrintAgent\
```

Estrutura recomendada:

```text
C:\Program Files\Print Agent\
├── app\
└── runtime\

C:\ProgramData\PrintAgent\
├── config\
│   └── application.yml
└── logs\
```

---

## 62.3 Estrutura sugerida no Linux

Executável:

```text
/opt/print-agent/
```

Configuração:

```text
/etc/print-agent/application.yml
```

Logs:

```text
/var/log/print-agent/
```

Arquivo de serviço:

```text
/etc/systemd/system/print-agent.service
```

Estrutura:

```text
/opt/print-agent/
├── print-agent.jar
└── runtime/

/etc/print-agent/
└── application.yml

/var/log/print-agent/
```

---

## 62.4 Permissões

Os arquivos da aplicação deverão ser somente leitura para usuários comuns.

O arquivo de configuração deverá ser gravável apenas pelo usuário administrativo ou pelo serviço autorizado.

Os logs deverão ser graváveis pelo usuário que executa o Print Agent.

---

## 62.5 Usuário de serviço no Linux

A instalação Linux deverá criar, preferencialmente, um usuário de sistema dedicado:

```text
printagent
```

Esse usuário deverá:

* não possuir login interativo;
* possuir acesso somente aos diretórios necessários;
* possuir permissão para utilizar o CUPS;
* possuir permissão para gravar logs;
* não possuir privilégios administrativos.

Exemplo conceitual:

```bash
useradd \
  --system \
  --no-create-home \
  --shell /usr/sbin/nologin \
  printagent
```

A implementação do instalador poderá variar conforme a distribuição.

---

## 62.6 Impressoras do usuário

Em determinados ambientes, impressoras podem estar disponíveis somente para a sessão do usuário conectado.

O instalador e a documentação deverão considerar essa diferença.

Caso a execução como serviço de sistema impeça acesso às impressoras do usuário, o agente poderá ser executado como serviço do usuário.

A estratégia deverá ser validada durante os testes de instalação em cada sistema operacional.

---

# 63. Instalação no Windows

## 63.1 Objetivo

O instalador Windows deverá permitir instalação guiada com o mínimo de decisões.

---

## 63.2 Etapas do instalador

O instalador deverá:

1. verificar compatibilidade do sistema;
2. instalar a aplicação;
3. instalar o runtime Java, quando empacotado;
4. criar o arquivo de configuração inicial;
5. criar diretório de logs;
6. registrar o agente para inicialização automática;
7. iniciar o serviço;
8. verificar se a porta local respondeu;
9. disponibilizar atalho para a interface Web;
10. registrar o desinstalador.

---

## 63.3 Nome do serviço

Nome interno sugerido:

```text
PrintAgent
```

Nome de exibição:

```text
Print Agent
```

Descrição:

```text
Agente local responsável por receber e encaminhar trabalhos de impressão.
```

---

## 63.4 Inicialização

O serviço deverá utilizar inicialização automática.

Após falha inesperada, o Windows deverá tentar reiniciar o serviço.

Política sugerida:

```text
Primeira falha: reiniciar o serviço

Segunda falha: reiniciar o serviço

Falhas seguintes: reiniciar após intervalo maior
```

---

## 63.5 Execução como serviço

A aplicação Spring Boot não deverá depender de:

* área de trabalho;
* janela gráfica;
* interação com console;
* sessão aberta no navegador.

A interface Web será acessada separadamente pelo navegador.

---

## 63.6 Atalho

O instalador poderá criar um atalho denominado:

```text
Abrir Print Agent
```

O atalho deverá abrir:

```text
http://localhost:18181
```

O atalho não deverá iniciar uma segunda instância da aplicação.

---

## 63.7 Firewall

Como o agente escutará apenas em loopback, o instalador não deverá criar regra de entrada pública no firewall.

Não deverá abrir a porta `18181` para a rede local.

---

## 63.8 Desinstalação

A desinstalação deverá:

1. interromper o serviço;
2. remover o registro do serviço;
3. remover executáveis;
4. remover atalhos;
5. preservar ou remover configuração e logs conforme escolha explícita.

A opção recomendada deverá preservar:

```text
configuração e logs
```

para facilitar reinstalação e suporte.

---

# 64. Instalação no Linux

## 64.1 Distribuições-alvo

A primeira versão deverá priorizar distribuições baseadas em Debian, incluindo:

* Debian;
* Ubuntu;
* Linux Mint.

O pacote principal será:

```text
.deb
```

---

## 64.2 Dependências

Quando o Java estiver empacotado, as dependências mínimas deverão incluir:

* CUPS;
* comandos de impressão do CUPS;
* systemd, quando utilizado;
* bibliotecas básicas do sistema.

Pacotes sugeridos:

```text
cups

cups-client
```

O nome dos pacotes poderá variar conforme a distribuição.

---

## 64.3 Validação do CUPS

Durante a instalação ou inicialização, o agente deverá verificar se o CUPS está disponível.

Exemplos de verificação:

```bash
systemctl status cups
```

```bash
lpstat -r
```

A instalação não deverá configurar automaticamente impressoras.

A configuração da impressora continua sendo responsabilidade do sistema operacional ou do administrador.

---

## 64.4 Serviço systemd

O pacote deverá instalar uma unidade equivalente a:

```ini
[Unit]
Description=Print Agent
After=network.target cups.service
Wants=cups.service

[Service]
Type=simple
User=printagent
Group=printagent
WorkingDirectory=/opt/print-agent
ExecStart=/opt/print-agent/runtime/bin/java \
  -jar /opt/print-agent/print-agent.jar \
  --spring.config.additional-location=file:/etc/print-agent/application.yml
Restart=on-failure
RestartSec=5
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

O conteúdo final poderá variar, mas deverá preservar:

* execução com usuário sem privilégios;
* configuração externa;
* reinicialização após falhas;
* dependência do CUPS;
* diretório de trabalho definido.

---

## 64.5 Ativação

Após instalação:

```bash
sudo systemctl daemon-reload
sudo systemctl enable print-agent
sudo systemctl start print-agent
```

O pacote deverá automatizar essas etapas quando permitido.

---

## 64.6 Consulta do serviço

Comandos documentados:

```bash
systemctl status print-agent
```

```bash
journalctl -u print-agent
```

Os logs próprios continuarão disponíveis em:

```text
/var/log/print-agent/
```

---

## 64.7 Remoção

A remoção deverá interromper e desabilitar o serviço.

Exemplo:

```bash
sudo systemctl stop print-agent
sudo systemctl disable print-agent
```

O pacote deverá remover:

* serviço;
* aplicação;
* runtime empacotado.

A configuração e os logs poderão ser preservados.

---

# 65. Execução em Modo Desenvolvimento

## 65.1 Objetivo

O modo de desenvolvimento deverá permitir execução direta pelo Maven ou JAR.

---

## 65.2 Maven

Comando sugerido:

```bash
mvn spring-boot:run
```

---

## 65.3 JAR

```bash
java -jar target/print-agent-1.0.0.jar
```

---

## 65.4 Perfil de desenvolvimento

Deverá existir um perfil Spring:

```text
dev
```

Exemplo:

```bash
java \
  -jar target/print-agent-1.0.0.jar \
  --spring.profiles.active=dev
```

---

## 65.5 Porta de desenvolvimento

A porta padrão continuará sendo:

```text
18181
```

Quando houver conflito, poderá ser substituída:

```bash
java \
  -jar target/print-agent-1.0.0.jar \
  --server.port=18182
```

---

## 65.6 Origem Angular local

O perfil de desenvolvimento poderá permitir:

```text
http://localhost:4200
```

na configuração de CORS.

Essa origem não deverá ser habilitada automaticamente no perfil de produção.

---

## 65.7 Modo console

Durante o desenvolvimento, a aplicação poderá manter logs no console.

No modo serviço, os logs deverão ser gravados conforme a configuração de produção.

---

# 66. Configuração Externa

## 66.1 Objetivo

As configurações deverão permanecer fora do JAR para permitir alteração e preservação durante atualizações.

---

## 66.2 Arquivo principal

Nome:

```text
application.yml
```

---

## 66.3 Ordem de carregamento

A aplicação deverá respeitar a precedência padrão do Spring Boot e permitir arquivo externo.

Prioridade esperada:

1. argumentos de linha de comando;
2. variáveis de ambiente;
3. arquivo externo;
4. configuração empacotada;
5. valores padrão do código.

---

## 66.4 Configuração mínima

Exemplo:

```yaml
server:
  address: 127.0.0.1
  port: 18181

spring:
  application:
    name: print-agent

print-agent:
  historico:
    tamanho-maximo: 100

  trabalho:
    timeout-sincrono-segundos: 30

  provider:
    timeout-comando-segundos: 15

  impressao:
    configuracoes-desconhecidas: IGNORAR

  impressoras:
    atualizar-ao-iniciar: true

  cors:
    origens-permitidas:
      - "https://presente.exemplo.com.br"

  atualizacao:
    habilitada: true
    verificar-ao-iniciar: true

logging:
  level:
    root: INFO
    br.eng.eliseu.printagent: INFO
```

---

## 66.5 Validação na inicialização

A aplicação deverá validar a configuração antes de entrar no estado:

```text
OPERACIONAL
```

Erros de configuração deverão:

* impedir inicialização operacional;
* gerar mensagem clara;
* indicar propriedade e valor problemático;
* ser registrados no log.

---

## 66.6 Valores padrão

Valores padrão deverão existir para propriedades não críticas.

Propriedades obrigatórias sem valor padrão deverão impedir a inicialização.

---

## 66.7 Configurações desconhecidas

Configurações desconhecidas no arquivo YAML poderão ser ignoradas pelo Spring Boot.

Entretanto, propriedades próprias do Print Agent deverão ser documentadas e validadas por classes de configuração tipadas.

Exemplo:

```java
@ConfigurationProperties(prefix = "print-agent")
```

---

## 66.8 Variáveis de ambiente

As propriedades deverão poder ser substituídas por variáveis de ambiente.

Exemplo conceitual:

```text
PRINT_AGENT_HISTORICO_TAMANHO_MAXIMO
```

A nomenclatura seguirá as regras do Spring Boot.

---

# 67. Atualização do Print Agent

## 67.1 Objetivo

O Print Agent deverá possuir mecanismo simples de verificação e instalação de novas versões.

A atualização não deverá exigir alteração manual dos arquivos de configuração.

---

## 67.2 Escopo da versão 1

A versão 1 deverá possuir:

* consulta de versão disponível;
* comparação de versões;
* informação ao usuário;
* download do instalador ou pacote;
* instalação controlada.

A atualização silenciosa completa poderá depender do sistema operacional e das permissões disponíveis.

---

## 67.3 Fonte de atualização

A aplicação deverá consultar um endereço HTTPS configurável.

Exemplo:

```yaml
print-agent:
  atualizacao:
    url-manifesto: "https://downloads.exemplo.com/print-agent/manifesto.json"
```

---

## 67.4 Manifesto

Exemplo:

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
            "sha256": "HASH"
        },
        "linux-deb": {
            "url": "https://downloads.exemplo.com/print-agent/1.1.0/print-agent.deb",
            "sha256": "HASH"
        }
    }
}
```

---

## 67.5 Comparação de versões

A comparação deverá seguir versionamento semântico:

```text
MAJOR.MINOR.PATCH
```

Exemplos:

```text
1.0.0

1.1.0

2.0.0
```

---

## 67.6 Canais

A versão 1 poderá suportar apenas:

```text
estavel
```

A arquitetura poderá ser preparada para:

```text
beta

desenvolvimento
```

sem exigir implementação inicial.

---

## 67.7 Verificação

A verificação poderá ocorrer:

* ao iniciar;
* em intervalo periódico;
* por ação manual na interface.

Frequência padrão sugerida:

```text
uma vez a cada 24 horas
```

---

## 67.8 Falha de atualização

Falhas na consulta de atualização não deverão impedir o funcionamento da impressão.

A aplicação deverá:

* registrar aviso;
* manter a versão atual;
* tentar novamente no próximo intervalo.

---

## 67.9 Validação do artefato

Todo artefato baixado deverá ter seu hash SHA-256 validado.

A aplicação não deverá executar ou instalar um arquivo cujo hash não corresponda ao manifesto.

---

## 67.10 HTTPS

A consulta e o download deverão utilizar HTTPS.

Redirecionamentos para HTTP não deverão ser aceitos.

---

## 67.11 Instalação

No Windows, o atualizador poderá iniciar o instalador correspondente.

No Linux, a aplicação não deverá executar instalação privilegiada sem consentimento.

Ela poderá:

* baixar o pacote;
* informar o caminho;
* apresentar o comando necessário.

Exemplo:

```bash
sudo apt install ./print-agent_1.1.0_amd64.deb
```

---

## 67.12 Reinicialização

Quando a atualização for aplicada, o agente deverá ser reiniciado.

O processo deverá preservar:

* `application.yml`;
* arquivos de log;
* configurações do usuário.

A fila em memória poderá ser perdida.

Antes de atualizar, a interface deverá informar:

```text
Os trabalhos ainda não concluídos serão interrompidos durante a atualização.
```

---

## 67.13 Atualização automática sem interface

Quando configurada para instalação automática, a aplicação deverá:

1. verificar se a fila está vazia;
2. impedir novos trabalhos;
3. concluir o trabalho atual;
4. instalar a atualização;
5. reiniciar;
6. validar o novo estado operacional.

Esse comportamento poderá ser postergado caso o instalador da versão 1 não suporte atualização totalmente automática.

---

# 68. Compatibilidade de Atualização

## 68.1 Configurações

Novas versões deverão aceitar configurações antigas sempre que possível.

---

## 68.2 Novas propriedades

Novas propriedades deverão possuir valores padrão.

---

## 68.3 Propriedades removidas

Propriedades não deverão ser removidas sem alteração de versão principal.

---

## 68.4 Migração

Caso uma versão exija migração de configuração, deverá:

* criar backup;
* migrar automaticamente;
* registrar as mudanças;
* manter possibilidade de restauração.

---

## 68.5 Backup de configuração

Antes de modificar o arquivo externo, deverá ser criada uma cópia:

```text
application.yml.bak
```

ou arquivo equivalente com data e hora.

---

# 69. Inicialização Automática

## 69.1 Windows

A inicialização deverá ocorrer por serviço do Windows.

Não deverá depender da pasta:

```text
Inicializar
```

do usuário.

---

## 69.2 Linux

A inicialização deverá ocorrer por unidade systemd ou mecanismo equivalente.

---

## 69.3 Ordem

O agente deverá iniciar após os serviços necessários de impressão.

No Linux:

```text
cups.service
```

---

## 69.4 Falhas temporárias

Se o CUPS ou o sistema de impressão ainda não estiver disponível, o agente poderá:

* iniciar em estado degradado;
* repetir descoberta após intervalo;
* permanecer acessível para diagnóstico.

O estado poderá continuar:

```text
INICIANDO
```

ou ser representado por informação operacional degradada na resposta de status.

Não deverá encerrar imediatamente por uma falha temporária do spooler.

---

## 69.5 Recuperação

Após o sistema de impressão tornar-se disponível, o agente deverá atualizar o catálogo de impressoras sem exigir reinicialização manual.

---

# 70. Encerramento Seguro

## 70.1 Novas requisições

Quando o encerramento iniciar, o agente deverá parar de aceitar novos trabalhos.

Resposta sugerida:

```text
503 Service Unavailable
```

Código:

```text
AGENTE_EM_ENCERRAMENTO
```

---

## 70.2 Trabalho atual

O agente deverá tentar concluir o trabalho em execução.

Tempo máximo configurável:

```yaml
print-agent:
  encerramento:
    timeout-segundos: 30
```

---

## 70.3 Trabalhos na fila

Como não existe persistência na versão 1, os trabalhos restantes poderão ser descartados após o timeout.

O descarte deverá ser registrado em log.

---

## 70.4 Recursos

O encerramento deverá:

* interromper executor da fila;
* finalizar processos externos;
* remover arquivos temporários;
* liberar recursos;
* descarregar buffers de log.

---

## 70.5 Status

Durante o encerramento:

```text
PARANDO
```

Ao concluir:

```text
FINALIZADO
```

---

# 71. Diagnóstico Pós-instalação

## 71.1 Objetivo

Após a instalação, deverá ser possível verificar rapidamente se o agente está operacional.

---

## 71.2 Verificações

O diagnóstico deverá confirmar:

* serviço em execução;
* porta local acessível;
* configuração válida;
* Provider carregado;
* sistema de impressão acessível;
* impressoras encontradas;
* diretório de logs gravável;
* diretório temporário gravável.

---

## 71.3 Endpoint

O endpoint:

```text
GET /api/v1/status
```

deverá fornecer informações suficientes para diagnóstico básico.

---

## 71.4 Interface

A tela inicial deverá destacar problemas encontrados.

Exemplos:

```text
Nenhuma impressora foi localizada.
```

```text
O serviço CUPS não está disponível.
```

```text
O diretório de logs não permite gravação.
```

---

## 71.5 Código de saída

Quando executada em modo de diagnóstico por linha de comando, a aplicação poderá retornar:

```text
0 = ambiente operacional

1 = erro de configuração

2 = sistema operacional não suportado

3 = sistema de impressão indisponível

4 = erro de acesso a diretórios
```

O modo de diagnóstico em linha de comando é recomendável, mas poderá ser implementado após o núcleo da versão 1.

---

# 72. Requisitos de Instalação e Atualização

## INST-001

O agente deverá possuir instalador para Windows.

---

## INST-002

O agente deverá possuir pacote `.deb` para distribuições Debian, Ubuntu e Linux Mint.

---

## INST-003

O instalador deverá configurar inicialização automática.

---

## INST-004

O instalador não deverá abrir a porta do agente para a rede local.

---

## INST-005

A configuração deverá permanecer fora do JAR.

---

## INST-006

Atualizações não deverão apagar a configuração existente.

---

## INST-007

Logs não deverão ser apagados automaticamente durante atualização.

---

## INST-008

O runtime Java 17 deverá ser empacotado sempre que possível.

---

## INST-009

A instalação Linux deverá utilizar usuário sem privilégios quando compatível com o acesso às impressoras.

---

## INST-010

O serviço deverá reiniciar automaticamente após falha inesperada.

---

## INST-011

A aplicação deverá validar o ambiente antes de entrar em estado operacional.

---

## INST-012

O instalador Windows deverá criar atalho para a interface Web.

---

## INST-013

A desinstalação deverá permitir preservar configuração e logs.

---

## INST-014

A atualização deverá validar o hash do artefato.

---

## INST-015

A consulta de atualização deverá utilizar HTTPS.

---

## INST-016

Falhas no serviço de atualização não deverão impedir impressões.

---

## INST-017

O serviço não deverá exigir uma janela ou sessão gráfica.

---

## INST-018

Arquivos temporários deverão ser removidos após utilização.

---

## INST-019

A aplicação deverá permitir execução direta por JAR para desenvolvimento e diagnóstico.

---

## INST-020

A documentação de instalação deverá informar os pré-requisitos específicos de cada sistema operacional.

---

# 73. Segurança Local

## 73.1 Objetivo

O Print Agent deverá operar exclusivamente como serviço local.

A segurança da versão 1 será baseada em:

* escuta apenas em interface de loopback;
* controle explícito de origens CORS;
* validação rigorosa das requisições;
* execução de comandos sem shell;
* limitação de tamanhos;
* proteção contra impressão involuntária ou maliciosa;
* execução com o menor nível de privilégio possível.

A versão 1 não utilizará autenticação por usuário, senha ou token.

---

## 73.2 Endereço de escuta

O servidor HTTP deverá escutar exclusivamente em:

```text
127.0.0.1
```

Quando houver suporte a IPv6, poderá também escutar em:

```text
::1
```

Configuração esperada:

```yaml
server:
  address: 127.0.0.1
  port: 18181
```

O agente não deverá escutar por padrão em:

```text
0.0.0.0
```

ou em endereços da rede local.

---

## 73.3 Acesso remoto

A versão 1 não deverá oferecer configuração de acesso remoto pela interface Web.

Qualquer alteração manual que exponha o agente à rede será considerada fora do escopo suportado.

---

## 73.4 CORS

O acesso por aplicações Web hospedadas em outros domínios dependerá de configuração explícita de CORS.

Exemplo:

```yaml
print-agent:
  cors:
    origens-permitidas:
      - "https://presente.exemplo.com.br"
      - "http://localhost:4200"
```

A origem curinga:

```text
*
```

não deverá ser permitida em produção.

---

## 73.5 Validação da origem

A aplicação deverá validar o cabeçalho:

```text
Origin
```

quando presente.

Origens não autorizadas deverão receber resposta HTTP apropriada e não deverão criar trabalhos de impressão.

---

## 73.6 Requisições sem Origin

Clientes locais que não utilizem navegador, como:

* `curl`;
* integração Java;
* ferramentas de diagnóstico;

poderão não enviar o cabeçalho `Origin`.

A ausência de `Origin` não deverá causar rejeição automática, pois CORS é uma proteção aplicada pelo navegador.

A API continuará restrita ao loopback.

---

## 73.7 Métodos HTTP

Somente os métodos necessários deverão ser disponibilizados.

Versão 1:

```text
GET

POST

DELETE

OPTIONS
```

Métodos não suportados deverão retornar:

```text
405 Method Not Allowed
```

---

## 73.8 Cabeçalhos de segurança

As respostas da interface Web deverão incluir, quando aplicável:

```text
X-Content-Type-Options: nosniff
```

```text
X-Frame-Options: DENY
```

```text
Referrer-Policy: no-referrer
```

A interface não deverá ser carregada dentro de `iframe`.

---

## 73.9 Content Security Policy

A interface Web deverá utilizar uma política de conteúdo restritiva.

Exemplo conceitual:

```text
Content-Security-Policy:
default-src 'self';
script-src 'self';
style-src 'self';
connect-src 'self';
img-src 'self' data:;
frame-ancestors 'none'
```

Scripts inline deverão ser evitados.

---

# 74. Validação e Limites de Requisição

## 74.1 Objetivo

A API deverá impor limites para evitar:

* consumo excessivo de memória;
* travamento da aplicação;
* arquivos muito grandes;
* envio acidental de conteúdo incorreto;
* abuso por aplicações locais não autorizadas.

---

## 74.2 Tamanho máximo da requisição

O tamanho máximo do corpo HTTP deverá ser configurável.

Valor padrão sugerido:

```text
20 MB
```

Exemplo:

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 20MB
      max-request-size: 20MB

print-agent:
  limites:
    requisicao-maxima-bytes: 20971520
```

Embora o endpoint utilize JSON, a aplicação deverá possuir limite global de requisição.

---

## 74.3 Tamanho máximo por tipo

Valores sugeridos:

| Tipo            |            Limite padrão |
| --------------- | -----------------------: |
| application/zpl |                     2 MB |
| text/plain      |                     2 MB |
| application/pdf | 15 MB após decodificação |

Os limites deverão ser configuráveis.

---

## 74.4 Base64

O tamanho do PDF deverá ser validado após a decodificação Base64.

A aplicação não deverá confiar apenas no tamanho da string recebida.

---

## 74.5 Quantidade de configurações

O mapa:

```text
configuracoesImpressao
```

deverá possuir quantidade máxima configurável.

Valor sugerido:

```text
50 propriedades
```

---

## 74.6 Tamanho de chave e valor

Cada chave ou valor deverá possuir, por padrão, no máximo:

```text
200 caracteres
```

---

## 74.7 Quantidade de cópias

O limite inicial deverá permanecer:

```text
1 a 999
```

O valor máximo poderá ser reduzido por configuração administrativa.

---

## 74.8 Nome do trabalho

O nome deverá possuir:

```text
1 a 100 caracteres
```

Caracteres de controle não deverão ser aceitos.

---

## 74.9 Identificadores

Identificadores de trabalho informados em URLs deverão ser UUIDs válidos.

Exemplo:

```text
8fd1b4fb-21db-4e3f-94b2-dfb83b2d3f0d
```

UUIDs inválidos deverão resultar em:

```text
400 Bad Request
```

---

# 75. Segurança na Execução de Comandos

## 75.1 Objetivo

No Linux, parte da integração com o CUPS poderá utilizar comandos externos.

Essa execução deverá ser rigorosamente isolada da entrada do usuário.

---

## 75.2 Proibição de shell

A aplicação não deverá executar comandos através de:

```text
sh -c
```

```text
bash -c
```

```text
cmd.exe /c
```

ou equivalentes, salvo necessidade técnica extraordinária documentada.

---

## 75.3 ProcessBuilder

A implementação deverá preferir:

```java
ProcessBuilder
```

com argumentos separados.

---

## 75.4 Nome da impressora

O nome da impressora não deverá ser concatenado em uma string de comando.

Exemplo correto:

```java
new ProcessBuilder(
    "lp",
    "-d",
    nomeImpressora,
    caminhoArquivo
);
```

---

## 75.5 Configurações

Cada configuração deverá ser adicionada como argumento independente.

Exemplo:

```java
argumentos.add("-o");
argumentos.add(chave + "=" + valor);
```

Antes disso, chave e valor deverão passar pela validação sintática definida nesta especificação.

---

## 75.6 Comandos permitidos

A aplicação deverá manter uma lista fechada de executáveis autorizados.

Exemplo:

```text
lp

lpstat

lpoptions

lpinfo

cancel
```

O cliente não poderá informar qual comando será executado.

---

## 75.7 Caminhos

Arquivos temporários deverão possuir caminhos gerados pela aplicação.

O cliente não poderá fornecer caminho de arquivo local para impressão.

---

# 76. Arquivos Temporários

## 76.1 Objetivo

Documentos poderão precisar ser materializados em disco antes do envio ao sistema operacional.

---

## 76.2 Diretório

O agente deverá utilizar um diretório temporário controlado.

Exemplo Linux:

```text
/var/tmp/print-agent/
```

Exemplo Windows:

```text
C:\ProgramData\PrintAgent\temp\
```

---

## 76.3 Nome

Os arquivos deverão utilizar identificadores aleatórios.

Exemplo:

```text
8fd1b4fb-21db-4e3f-94b2-dfb83b2d3f0d.pdf
```

---

## 76.4 Extensões

Extensões permitidas:

```text
.zpl

.pdf

.txt

.raw
```

A extensão será definida pelo agente a partir do tipo de conteúdo.

---

## 76.5 Permissões

Arquivos temporários deverão ser acessíveis apenas pelo usuário do serviço, quando o sistema operacional permitir.

---

## 76.6 Exclusão

Os arquivos deverão ser removidos:

* após envio ao spooler;
* após erro;
* durante encerramento;
* durante limpeza de inicialização.

---

## 76.7 Limpeza na inicialização

Ao iniciar, o agente deverá excluir arquivos temporários antigos deixados por encerramento inesperado.

A limpeza deverá considerar apenas arquivos criados pelo próprio Print Agent.

---

## 76.8 Conteúdo dos arquivos

O agente não deverá manter cópias permanentes dos documentos impressos.

---

# 77. Sistema de Logs

## 77.1 Objetivo

Os logs deverão permitir:

* diagnóstico de falhas;
* rastreamento de trabalhos;
* identificação de impressoras;
* análise de configuração;
* suporte técnico;
* acompanhamento da inicialização e encerramento.

---

## 77.2 Tecnologia

O projeto deverá utilizar a infraestrutura padrão de logs do Spring Boot.

A implementação poderá utilizar:

```text
SLF4J
```

com:

```text
Logback
```

---

## 77.3 Formato

Formato sugerido:

```text
2026-07-15T18:41:00.123-03:00 INFO  [print-worker] JobService - Trabalho criado: id=...
```

Cada linha deverá incluir:

* data e hora;
* fuso horário;
* nível;
* thread;
* classe ou componente;
* mensagem.

---

## 77.4 Níveis

### ERROR

Falhas que impediram uma operação.

Exemplos:

* erro de impressão;
* Provider indisponível;
* configuração inválida;
* falha ao iniciar serviço;
* erro não tratado.

### WARN

Situações anormais que não impediram totalmente o funcionamento.

Exemplos:

* propriedade de impressão ignorada;
* impressora offline;
* falha ao verificar atualização;
* limpeza incompleta de arquivo temporário.

### INFO

Eventos operacionais relevantes.

Exemplos:

* inicialização;
* encerramento;
* trabalho recebido;
* trabalho concluído;
* impressoras encontradas;
* atualização disponível.

### DEBUG

Detalhes técnicos para diagnóstico.

Exemplos:

* argumentos enviados ao Provider;
* transições internas;
* capacidades retornadas;
* tempos de execução.

### TRACE

Não deverá ser habilitado em produção por padrão.

---

## 77.5 Identificador de correlação

Todos os logs relacionados a um trabalho deverão incluir:

```text
idTrabalho
```

Quando disponível, poderão também incluir:

```text
idRequisicao
```

caso esse campo venha a ser adotado no contrato final.

---

## 77.6 Conteúdo proibido

Os logs não deverão registrar integralmente:

* PDF em Base64;
* documento PDF decodificado;
* ZPL completo;
* RAW completo;
* arquivos binários;
* credenciais;
* conteúdo sensível do documento.

---

## 77.7 Conteúdo permitido

Poderão ser registrados:

* tamanho em bytes;
* tipo de conteúdo;
* nome do trabalho;
* nome da impressora;
* quantidade de cópias;
* hash do documento, quando útil;
* quantidade de configurações;
* resultado do Provider.

---

## 77.8 Resumo do conteúdo

Em nível `DEBUG`, o agente poderá registrar um trecho limitado de ZPL ou RAW.

Limite sugerido:

```text
200 caracteres
```

Caracteres de controle deverão ser escapados.

Essa funcionalidade deverá vir desabilitada por padrão.

---

# 78. Rotação e Retenção de Logs

## 78.1 Rotação

Os logs deverão possuir rotação diária ou por tamanho.

Configuração sugerida:

```text
rotação diária
```

com limite adicional por arquivo.

---

## 78.2 Tamanho máximo

Valor sugerido por arquivo:

```text
20 MB
```

---

## 78.3 Retenção

Valor padrão sugerido:

```text
30 dias
```

---

## 78.4 Tamanho total

O sistema deverá limitar o tamanho total dos logs.

Valor padrão sugerido:

```text
500 MB
```

---

## 78.5 Compressão

Arquivos rotacionados poderão ser comprimidos em:

```text
.gz
```

---

## 78.6 Configuração

Exemplo:

```yaml
logging:
  file:
    name: /var/log/print-agent/print-agent.log

  logback:
    rollingpolicy:
      max-file-size: 20MB
      max-history: 30
      total-size-cap: 500MB
```

A configuração exata poderá variar conforme a versão do Spring Boot.

---

# 79. Tratamento de Exceções

## 79.1 Objetivo

Todas as exceções deverão ser tratadas de forma centralizada.

Controllers não deverão possuir blocos repetitivos de tratamento de erro.

---

## 79.2 Exceção base

Exceções operacionais deverão herdar de:

```text
PrintAgentException
```

---

## 79.3 Especializações sugeridas

```text
ImpressoraNaoEncontradaException

ImpressoraIndisponivelException

TrabalhoNaoEncontradoException

TrabalhoNaoCancelavelException

TipoConteudoNaoSuportadoException

ConteudoInvalidoException

ConfiguracaoImpressaoException

ProviderException

FilaImpressaoException
```

---

## 79.4 Tratador global

Deverá existir componente equivalente a:

```text
@RestControllerAdvice
```

responsável por converter exceções em:

```text
EnvelopeRespostaDTO
```

---

## 79.5 Erros de validação

Falhas de Bean Validation deverão ser convertidas para uma lista de `ErroDTO`.

Exemplo:

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
            "descricao": "A quantidade de cópias deve ser maior ou igual a 1.",
            "campo": "trabalho.copias"
        }
    ],
    "dataHora": "2026-07-15T18:41:00-03:00"
}
```

---

## 79.6 Erro interno

Exceções não previstas deverão retornar:

```text
500 Internal Server Error
```

Código:

```text
ERRO_INTERNO
```

A resposta não deverá expor:

* nome da classe;
* caminho de arquivo;
* comando executado;
* rastreamento de pilha;
* detalhes internos do sistema operacional.

---

## 79.7 Rastreamento

O erro técnico completo deverá ser registrado no log com o identificador do trabalho ou da requisição.

---

# 80. Mapeamento de Erros HTTP

| Situação                                   | HTTP | Código                               |
| ------------------------------------------ | ---: | ------------------------------------ |
| JSON inválido                              |  400 | JSON_INVALIDO                        |
| Campo obrigatório ausente                  |  400 | CAMPO_OBRIGATORIO                    |
| Valor inválido                             |  400 | VALOR_INVALIDO                       |
| UUID inválido                              |  400 | IDENTIFICADOR_INVALIDO               |
| Impressora não encontrada                  |  404 | IMPRESSORA_NAO_ENCONTRADA            |
| Trabalho não encontrado                    |  404 | TRABALHO_NAO_ENCONTRADO              |
| Trabalho não cancelável                    |  409 | TRABALHO_NAO_CANCELAVEL              |
| Impressora indisponível                    |  409 | IMPRESSORA_INDISPONIVEL              |
| Tipo não suportado                         |  415 | TIPO_CONTEUDO_NAO_SUPORTADO          |
| Configuração não suportada em modo estrito |  422 | CONFIGURACAO_IMPRESSAO_NAO_SUPORTADA |
| Requisição muito grande                    |  413 | REQUISICAO_MUITO_GRANDE              |
| Método não permitido                       |  405 | METODO_NAO_PERMITIDO                 |
| Agente encerrando                          |  503 | AGENTE_EM_ENCERRAMENTO               |
| Provider indisponível                      |  503 | PROVIDER_INDISPONIVEL                |
| Erro inesperado                            |  500 | ERRO_INTERNO                         |

---

# 81. Resiliência

## 81.1 Falha de um trabalho

A falha de um trabalho não deverá interromper a thread consumidora da fila.

Após registrar o erro, o processador deverá continuar com o próximo trabalho.

---

## 81.2 Falha do Provider

Quando o Provider estiver temporariamente indisponível:

* o trabalho atual deverá ir para `ERRO`;
* a aplicação deverá permanecer ativa;
* o catálogo de impressoras poderá ser atualizado;
* novas requisições poderão ser rejeitadas enquanto o Provider permanecer indisponível.

---

## 81.3 Falha do CUPS

No Linux, a indisponibilidade do CUPS não deverá derrubar a interface Web nem a API de status.

A aplicação deverá informar o problema.

---

## 81.4 Falha na descoberta

Caso a descoberta falhe:

* o cache anterior poderá ser mantido;
* um aviso deverá ser registrado;
* o estado operacional deverá refletir a degradação.

---

## 81.5 Repetição automática

A versão 1 não deverá repetir automaticamente uma impressão que falhou após o envio ao spooler.

Essa decisão evita duplicidade física de etiquetas ou documentos.

---

## 81.6 Reenvio manual

O reenvio deverá ser realizado pelo sistema cliente ou por ação explícita do usuário.

---

## 81.7 Duplicidade de requisição

A versão 1 não garante idempotência automática do endpoint:

```text
POST /api/v1/trabalhos
```

Cada requisição válida deverá criar um novo trabalho.

O sistema cliente deverá impedir envio duplicado por múltiplos cliques ou repetição indevida.

---

# 82. Requisitos Não Funcionais

## 82.1 Desempenho

### RNF-DES-001

O agente deverá aceitar uma requisição assíncrona válida em até:

```text
200 ms
```

em condições normais, desconsiderando o tempo de impressão.

### RNF-DES-002

A listagem de trabalhos em memória deverá responder em até:

```text
500 ms
```

para o limite padrão de histórico.

### RNF-DES-003

A listagem de impressoras em cache deverá responder em até:

```text
500 ms
```

### RNF-DES-004

Consultas de capacidades poderão demorar mais, pois dependem do sistema operacional.

O timeout deverá respeitar a configuração do Provider.

---

## 82.2 Uso de recursos

### RNF-REC-001

O agente deverá permanecer adequado à execução contínua em computadores de usuário.

### RNF-REC-002

O consumo de memória em repouso deverá ser monitorado durante testes.

Meta inicial:

```text
até 250 MB
```

considerando Spring Boot e runtime Java.

### RNF-REC-003

O agente não deverá manter em memória documentos concluídos por tempo indefinido.

### RNF-REC-004

O histórico não deverá armazenar o conteúdo dos documentos.

---

## 82.3 Disponibilidade

### RNF-DISP-001

O serviço deverá iniciar automaticamente com o sistema operacional.

### RNF-DISP-002

O serviço deverá reiniciar após falha inesperada.

### RNF-DISP-003

Falhas de atualização não deverão impedir impressão.

### RNF-DISP-004

Falhas da interface Web não deverão interromper a fila.

---

## 82.4 Compatibilidade

### RNF-COMP-001

A versão 1 deverá suportar Java 17.

### RNF-COMP-002

A versão 1 deverá suportar Windows 10 ou superior.

### RNF-COMP-003

A versão 1 deverá priorizar Debian, Ubuntu e Linux Mint atuais compatíveis com Java 17 e CUPS.

### RNF-COMP-004

Impressoras deverão estar previamente instaladas no sistema operacional.

---

## 82.5 Manutenibilidade

### RNF-MAN-001

O código deverá permanecer sob:

```text
br.eng.eliseu.printagent
```

### RNF-MAN-002

A lógica específica de sistema operacional deverá permanecer nos Providers.

### RNF-MAN-003

Controllers não deverão conter regras de negócio.

### RNF-MAN-004

Estratégias de conteúdo deverão ser independentes.

### RNF-MAN-005

Configurações deverão utilizar classes tipadas com `@ConfigurationProperties`.

### RNF-MAN-006

Não deverão existir valores operacionais relevantes espalhados como constantes mágicas.

---

## 82.6 Observabilidade

### RNF-OBS-001

Todo trabalho deverá possuir UUID.

### RNF-OBS-002

Toda falha deverá ser registrada com contexto suficiente.

### RNF-OBS-003

Logs deverão possuir rotação e retenção.

### RNF-OBS-004

A API deverá disponibilizar o estado operacional.

---

## 82.7 Segurança

### RNF-SEG-001

O agente deverá escutar somente em loopback.

### RNF-SEG-002

CORS deverá utilizar lista explícita de origens.

### RNF-SEG-003

Entradas do usuário não deverão ser concatenadas em comandos de shell.

### RNF-SEG-004

Conteúdos de documentos não deverão ser expostos em logs.

### RNF-SEG-005

O agente deverá executar com o menor privilégio possível.

### RNF-SEG-006

O agente deverá impor limite de requisição.

---

## 82.8 Usabilidade

### RNF-USA-001

A interface deverá funcionar sem Internet.

### RNF-USA-002

Mensagens deverão estar em português do Brasil.

### RNF-USA-003

Erros não deverão exibir detalhes internos da aplicação.

### RNF-USA-004

O estado do agente deverá estar visível no Dashboard.

---

# 83. Requisitos de Qualidade de Código

## COD-001

O projeto deverá utilizar Java 17.

---

## COD-002

O build deverá ser realizado com Maven.

---

## COD-003

O projeto deverá possuir compilação reproduzível.

---

## COD-004

Não deverão existir dependências sem finalidade documentada.

---

## COD-005

Dependências deverão possuir versões controladas pelo `pom.xml` ou gerenciamento de dependências do Spring Boot.

---

## COD-006

Métodos deverão possuir responsabilidade única.

---

## COD-007

Classes não deverão misturar:

* REST;
* fila;
* sistema operacional;
* preparação de documentos;
* configuração.

---

## COD-008

Exceções não deverão ser ignoradas silenciosamente.

Exemplo proibido:

```java
try {
    executar();
} catch (Exception e) {
}
```

---

## COD-009

`System.out.println` não deverá ser utilizado para logs.

---

## COD-010

Dados mutáveis compartilhados deverão possuir proteção adequada para concorrência.

---

## COD-011

O estado dos trabalhos deverá ser alterado somente por componente responsável pelo ciclo de vida.

---

## COD-012

Enums definidos no contrato não deverão ser substituídos por strings internas espalhadas.

---

## COD-013

O código deverá evitar comentários que apenas repitam a implementação.

Comentários deverão explicar decisões não óbvias.

---

## COD-014

Nomes de classes e métodos deverão refletir o domínio técnico do agente.

---

## COD-015

Nenhuma classe deverá depender diretamente do sistema Presente.

---

# 84. Requisitos de Testabilidade

## TEST-001

Services deverão depender de interfaces quando houver integração externa.

---

## TEST-002

Providers deverão poder ser substituídos por implementações simuladas.

---

## TEST-003

A fila deverá poder ser testada sem impressora física.

---

## TEST-004

As estratégias ZPL, PDF e RAW deverão poder ser testadas com Provider simulado.

---

## TEST-005

O tratamento de exceções REST deverá possuir testes para os principais códigos HTTP.

---

## TEST-006

A validação dos DTOs deverá possuir testes automatizados.

---

## TEST-007

Transições inválidas de status deverão ser rejeitadas por testes.

---

## TEST-008

O projeto deverá possuir teste para garantir que o endpoint de criação não imprima diretamente fora da fila.

---

## TEST-009

Os testes não deverão depender da impressora Elgin específica.

---

## TEST-010

Testes de integração com impressoras reais deverão ser separados dos testes automatizados comuns.

---

# 85. Critérios de Aceite

## 85.1 Objetivo

Os critérios de aceite definem as condições mínimas para considerar a versão 1.0.0 do Print Agent pronta para uso.

A implementação somente deverá ser considerada concluída quando todos os critérios obrigatórios deste capítulo forem atendidos.

---

## 85.2 Inicialização

### CA-INI-001

A aplicação deverá iniciar utilizando Java 17.

### CA-INI-002

O agente deverá disponibilizar a interface Web em:

```text
http://localhost:18181
```

### CA-INI-003

A API deverá responder em:

```text
http://localhost:18181/api/v1/status
```

### CA-INI-004

O agente deverá entrar no estado:

```text
OPERACIONAL
```

quando a configuração e o Provider estiverem disponíveis.

### CA-INI-005

Erros de configuração deverão impedir a entrada no estado operacional e deverão ser registrados claramente.

---

## 85.3 Segurança local

### CA-SEG-001

A aplicação deverá escutar somente em endereço de loopback.

### CA-SEG-002

Não deverá ser possível acessar o agente por outro computador da rede utilizando o IP da máquina.

### CA-SEG-003

Origens Web não autorizadas deverão ser rejeitadas pela configuração de CORS.

### CA-SEG-004

Nenhuma requisição poderá determinar qual comando do sistema operacional será executado.

### CA-SEG-005

Nenhuma entrada do usuário poderá ser concatenada em comando de shell.

---

## 85.4 Descoberta de impressoras

### CA-IMP-001

O agente deverá listar as impressoras instaladas no sistema operacional.

### CA-IMP-002

A impressora padrão deverá ser identificada quando essa informação estiver disponível.

### CA-IMP-003

A atualização manual da lista deverá funcionar sem reiniciar a aplicação.

### CA-IMP-004

Impressoras indisponíveis deverão permanecer visíveis com status adequado.

### CA-IMP-005

A consulta de capacidades deverá retornar as opções disponibilizadas pelo sistema operacional ou um objeto vazio quando indisponíveis.

---

## 85.5 Criação de trabalhos

### CA-TRA-001

O endpoint:

```text
POST /api/v1/trabalhos
```

deverá aceitar uma requisição válida.

### CA-TRA-002

Cada trabalho deverá receber um UUID gerado pelo agente.

### CA-TRA-003

O trabalho deverá seguir as transições:

```text
RECEBIDO
```

```text
VALIDADO
```

```text
NA_FILA
```

```text
IMPRIMINDO
```

e um estado final.

### CA-TRA-004

O endpoint assíncrono deverá retornar sem aguardar a impressão física.

### CA-TRA-005

O envelope de resposta deverá seguir o contrato documentado.

---

## 85.6 Validação

### CA-VAL-001

Requisições sem impressora deverão ser rejeitadas.

### CA-VAL-002

Requisições com impressora inexistente deverão retornar:

```text
IMPRESSORA_NAO_ENCONTRADA
```

### CA-VAL-003

Requisições sem documento deverão ser rejeitadas.

### CA-VAL-004

Tipos de conteúdo não suportados deverão retornar HTTP `415`.

### CA-VAL-005

PDF inválido em Base64 deverá ser rejeitado.

### CA-VAL-006

Quantidade de cópias inferior a `1` ou superior ao limite deverá ser rejeitada.

### CA-VAL-007

Propriedades desconhecidas no JSON deverão ser rejeitadas.

---

## 85.7 Fila

### CA-FILA-001

A fila deverá preservar a ordem FIFO.

### CA-FILA-002

O processamento deverá ocorrer fora da thread da requisição HTTP.

### CA-FILA-003

A thread consumidora não deverá utilizar espera ativa.

### CA-FILA-004

A falha de um trabalho não deverá interromper o processamento dos próximos.

### CA-FILA-005

Trabalhos ainda não iniciados deverão poder ser cancelados.

### CA-FILA-006

Trabalhos concluídos não poderão ser cancelados.

---

## 85.8 Impressão ZPL

### CA-ZPL-001

O agente deverá receber conteúdo:

```text
application/zpl
```

### CA-ZPL-002

O conteúdo deverá ser enviado em modo RAW.

### CA-ZPL-003

O agente não deverá modificar os comandos ZPL.

### CA-ZPL-004

Uma etiqueta ZPL válida deverá ser enviada para uma impressora compatível.

### CA-ZPL-005

A quantidade de cópias deverá ser respeitada.

---

## 85.9 Impressão PDF

### CA-PDF-001

O agente deverá aceitar PDF codificado em Base64.

### CA-PDF-002

O conteúdo deverá ser decodificado sem alteração.

### CA-PDF-003

O PDF deverá ser enviado ao sistema de impressão disponível.

### CA-PDF-004

Falha de suporte do Provider deverá gerar mensagem clara.

### CA-PDF-005

O PDF não deverá ser mantido permanentemente após o envio.

---

## 85.10 Impressão RAW

### CA-RAW-001

O agente deverá aceitar:

```text
text/plain
```

### CA-RAW-002

O conteúdo deverá ser enviado sem cabeçalhos, rodapés ou comandos adicionais.

### CA-RAW-003

A codificação utilizada deverá ser UTF-8.

---

## 85.11 Configurações de impressão

### CA-CONF-001

As configurações deverão pertencer somente ao trabalho atual.

### CA-CONF-002

O agente não deverá alterar permanentemente as opções padrão da impressora.

### CA-CONF-003

As chaves deverão preservar a nomenclatura original do driver.

### CA-CONF-004

Em modo `IGNORAR`, propriedades não suportadas deverão ser desconsideradas e não
impedirão a impressão quando as propriedades válidas e os padrões do driver forem
suficientes. Se não forem suficientes, o trabalho deverá falhar com mensagem
clara.

### CA-CONF-005

Em modo `REJEITAR`, propriedades não suportadas deverão impedir o trabalho.

---

## 85.12 Histórico

### CA-HIST-001

Trabalhos finalizados deverão ser adicionados ao histórico.

### CA-HIST-002

O histórico deverá respeitar o limite configurado.

### CA-HIST-003

O histórico não deverá armazenar o conteúdo completo do documento.

### CA-HIST-004

O histórico deverá ser limpo após reinicialização.

---

## 85.13 Logs

### CA-LOG-001

A inicialização deverá ser registrada.

### CA-LOG-002

Cada trabalho deverá ser identificado pelo UUID nos logs.

### CA-LOG-003

Falhas de impressão deverão registrar detalhes técnicos.

### CA-LOG-004

O conteúdo completo de PDF, ZPL ou RAW não deverá aparecer em nível `INFO`.

### CA-LOG-005

A rotação e retenção deverão funcionar conforme configuração.

---

## 85.14 Interface Web

### CA-WEB-001

O Dashboard deverá mostrar o estado do agente.

### CA-WEB-002

A tela de Impressoras deverá listar as impressoras instaladas.

### CA-WEB-003

A tela de Trabalhos deverá exibir a fila atual.

### CA-WEB-004

A tela de Histórico deverá exibir os trabalhos finalizados.

### CA-WEB-005

A tela de Teste deverá criar um trabalho real de impressão.

### CA-WEB-006

A interface deverá funcionar sem acesso à Internet.

### CA-WEB-007

A interface deverá exibir erros da API em português.

---

## 85.15 Instalação

### CA-INST-001

O agente deverá possuir pacote instalável para Windows.

### CA-INST-002

O agente deverá possuir pacote `.deb`.

### CA-INST-003

A instalação deverá configurar inicialização automática.

### CA-INST-004

A atualização não deverá apagar o arquivo de configuração.

### CA-INST-005

A desinstalação deverá permitir preservar configuração e logs.

---

# 86. Cenários Mínimos de Homologação

## 86.1 Linux com impressora ZPL USB

Ambiente mínimo:

* Linux Mint ou Ubuntu;
* CUPS ativo;
* impressora térmica USB instalada;
* fila visível no CUPS;
* Print Agent em execução.

Procedimento:

1. listar impressoras;
2. localizar a fila térmica;
3. consultar capacidades;
4. enviar ZPL de teste;
5. consultar o trabalho;
6. confirmar estado final;
7. verificar logs.

Resultado esperado:

* trabalho aceito;
* trabalho processado;
* conteúdo enviado sem rasterização;
* etiqueta impressa.

---

## 86.2 Windows com impressora ZPL USB

Procedimento equivalente ao cenário Linux.

Resultado esperado:

* impressora localizada pelo Java Print Service;
* bytes encaminhados em modo compatível;
* trabalho concluído ou erro técnico claro quando o driver não suportar RAW.

---

## 86.3 PDF no Linux

Procedimento:

1. utilizar ambiente Linux com CUPS;
2. selecionar impressora compatível;
3. enviar PDF Base64;
4. informar configurações opcionais;
5. consultar estado.

Resultado esperado:

* PDF encaminhado;
* arquivo temporário removido;
* trabalho registrado no histórico.

---

## 86.4 Impressora inexistente

Enviar:

```json
{
    "impressora": {
        "nome": "IMPRESSORA_INEXISTENTE"
    }
}
```

Resultado esperado:

```text
404 Not Found
```

```text
IMPRESSORA_NAO_ENCONTRADA
```

Nenhum trabalho deverá entrar na fila.

---

## 86.5 Conteúdo inválido

Enviar PDF com Base64 inválido.

Resultado esperado:

```text
400 Bad Request
```

```text
CONTEUDO_INVALIDO
```

---

## 86.6 Cancelamento

Criar múltiplos trabalhos para permitir que um permaneça na fila.

Cancelar um trabalho em:

```text
NA_FILA
```

Resultado esperado:

```text
CANCELADO
```

O trabalho não deverá ser impresso.

---

## 86.7 Reinicialização

Criar trabalhos pendentes e reiniciar o agente.

Resultado esperado:

* fila anterior não recuperada;
* histórico anterior não recuperado;
* configuração preservada;
* logs preservados.

---

## 86.8 CORS

Testar uma origem autorizada e uma não autorizada.

Resultado esperado:

* origem autorizada consegue chamar a API;
* origem não autorizada é bloqueada pelo navegador;
* nenhuma origem curinga é utilizada.

---

# 87. Limitações Conhecidas da Versão 1

## 87.1 Confirmação física

O Print Agent não consegue garantir que o documento saiu fisicamente da impressora.

O estado:

```text
CONCLUIDO
```

significa que o trabalho foi aceito pelo sistema de impressão sem erro imediato.

---

## 87.2 Persistência

A fila e o histórico são mantidos somente em memória.

Uma reinicialização remove essas informações.

---

## 87.3 Idempotência

O endpoint de criação não possui proteção automática contra repetição da mesma requisição.

Cada chamada válida cria um novo trabalho.

---

## 87.4 Impressão PDF no Windows

Não suportada na versão 1.0.0. A implementação está planejada para a versão
1.2.0 e dependerá de homologação com os mecanismos de impressão disponíveis.

---

## 87.5 Capacidades

Nem todos os drivers disponibilizam capacidades detalhadas.

---

## 87.6 Configurações proprietárias

Algumas configurações específicas de fabricante podem não estar acessíveis pelo Java Print Service.

---

## 87.7 Cancelamento após envio

Após o trabalho ser enviado ao spooler do sistema operacional, o cancelamento poderá não ser possível.

---

## 87.8 Sem autenticação

A versão 1 não possui autenticação.

A proteção depende da escuta local e da lista de origens permitidas.

---

## 87.9 Sem editor de etiquetas

O Print Agent não monta, edita ou visualiza layouts ZPL.

---

## 87.10 Sem conversão automática de mídia

O agente não converte dimensões como:

```text
100x60
```

para identificadores específicos de driver.

---

# 88. Decisões Arquiteturais Consolidadas

## DA-001 — Java 17

O projeto utilizará Java 17 LTS.

A evolução para versões posteriores será avaliada futuramente.

---

## DA-002 — Spring Boot 3.x

O agente será construído com Spring Boot compatível com Java 17.

---

## DA-003 — Maven

O gerenciamento de build e dependências utilizará Maven.

---

## DA-004 — Namespace

Todo o código ficará abaixo de:

```text
br.eng.eliseu.printagent
```

---

## DA-005 — Projeto independente

O agente ficará em:

```text
presente/printAgent
```

mas possuirá ciclo de build e distribuição independente.

---

## DA-006 — API local

A API deverá escutar somente em loopback.

---

## DA-007 — Sem autenticação

A versão 1 não utilizará token, login ou senha.

---

## DA-008 — Fila em memória

A versão 1 terá fila FIFO não persistente.

---

## DA-009 — Histórico em memória

O histórico não utilizará banco de dados.

---

## DA-010 — Endpoint baseado em trabalhos

A criação utilizará:

```text
POST /api/v1/trabalhos
```

---

## DA-011 — Envelope padrão

Todas as respostas com corpo utilizarão envelope padronizado.

---

## DA-012 — Propriedades em português

Propriedades controladas pelo Print Agent serão escritas em português.

---

## DA-013 — Propriedades de driver preservadas

Propriedades externas como:

```text
PageSize

PrintDarkness

PrintSpeed
```

manterão seus nomes originais.

---

## DA-014 — Configuração por trabalho

Configurações de impressão pertencem ao trabalho, não à impressora.

---

## DA-015 — Providers por sistema operacional

Linux e Windows possuirão Providers independentes.

---

## DA-016 — Estratégias por conteúdo

ZPL, PDF e RAW possuirão estratégias independentes.

---

## DA-017 — ZPL sem interpretação

O agente não validará nem modificará comandos ZPL.

---

## DA-018 — Interface Web simples

A interface utilizará HTML, CSS e JavaScript sem framework de aplicação na versão 1.

---

## DA-019 — Configuração externa

O arquivo `application.yml` ficará fora do JAR nas instalações.

---

## DA-020 — Runtime empacotado

Os instaladores deverão incluir Java 17 sempre que possível.

---

## DA-021 — Sem dependência do Presente

Nenhuma classe deverá conter regras ou tipos específicos do sistema Presente.

---

## DA-022 — Sem banco de dados

A versão 1 não utilizará SQLite, PostgreSQL ou outro banco.

---

## DA-023 — Sem plugins

A versão 1 não terá sistema de plugins.

---

## DA-024 — Sem perfis de impressora

A versão 1 utilizará os nomes reais das impressoras instaladas.

---

## DA-025 — Atualização controlada

O agente deverá verificar novas versões e preservar configurações.

---

# 89. Estrutura Final Esperada do Projeto

```text
printAgent/
├── docs/
│   ├── arquitetura/
│   │   └── ESPECIFICACAO_PRINT_AGENT.md
│   ├── api/
│   │   ├── API.md
│   │   └── JSON.md
│   ├── web/
│   │   └── WEBUI.md
│   ├── instalacao/
│   │   └── INSTALACAO.md
│   ├── roadmap/
│   │   ├── ROADMAP.md
│   │   └── CHANGELOG.md
│   └── examples/
│       ├── json/
│       ├── pdf/
│       └── zpl/
├── installer/
│   ├── linux/
│   └── windows/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/
│   │   │       └── eng/
│   │   │           └── eliseu/
│   │   │               └── printagent/
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       ├── application.yml
│   │       └── banner.txt
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

---

# 90. Ordem Recomendada de Implementação

## Fase 1 — Fundação

1. criar projeto Maven;
2. configurar Java 17;
3. configurar Spring Boot;
4. criar namespace;
5. criar endpoint de status;
6. criar envelope padrão;
7. configurar logs;
8. configurar loopback e CORS.

---

## Fase 2 — Modelo e fila

1. criar DTOs;
2. criar modelo interno de trabalho;
3. criar enums;
4. implementar transições;
5. implementar fila FIFO;
6. implementar histórico em memória;
7. implementar cancelamento.

---

## Fase 3 — Impressoras

1. criar `PrinterProvider`;
2. criar detector de sistema operacional;
3. implementar Provider Linux;
4. implementar Provider Windows;
5. listar impressoras;
6. consultar padrão;
7. consultar capacidades.

---

## Fase 4 — Estratégias

1. implementar ZPL;
2. implementar RAW;
3. implementar PDF;
4. aplicar configurações por trabalho;
5. implementar arquivos temporários;
6. tratar timeout e erros.

---

## Fase 5 — API completa

1. criar trabalho;
2. listar trabalhos;
3. consultar trabalho;
4. cancelar trabalho;
5. listar impressoras;
6. consultar capacidades;
7. atualizar catálogo;
8. imprimir teste;
9. consultar histórico;
10. consultar logs e configurações.

---

## Fase 6 — Interface Web

1. Dashboard;
2. Impressoras;
3. Trabalhos;
4. Histórico;
5. Teste;
6. Configurações;
7. Logs;
8. Sobre.

---

## Fase 7 — Distribuição

1. configuração externa;
2. serviço systemd;
3. pacote `.deb`;
4. serviço Windows;
5. instalador Windows;
6. runtime Java empacotado;
7. atualização;
8. documentação de instalação.

---

## Fase 8 — Homologação

1. testes unitários;
2. testes de integração;
3. teste Linux;
4. teste Windows;
5. teste Elgin L42 Pro Full;
6. teste de atualização;
7. teste de reinstalação;
8. validação dos critérios de aceite.

---

# 91. Documentos Complementares

Esta especificação define a visão arquitetural e os requisitos gerais.

Os detalhes operacionais deverão ser mantidos nos seguintes documentos.

## API REST

```text
docs/api/API.md
```

Conteúdo:

* endpoints completos;
* parâmetros;
* respostas;
* códigos HTTP;
* exemplos com `curl`.

---

## Contratos JSON

```text
docs/api/JSON.md
```

Conteúdo:

* DTOs;
* propriedades;
* tipos;
* obrigatoriedade;
* exemplos;
* validações.

---

## Interface Web

```text
docs/web/WEBUI.md
```

Conteúdo:

* telas;
* componentes;
* navegação;
* comportamento;
* mensagens;
* exemplos visuais.

---

## Instalação

```text
docs/instalacao/INSTALACAO.md
```

Conteúdo:

* Windows;
* Linux;
* CUPS;
* serviços;
* configuração;
* atualização;
* desinstalação;
* diagnóstico.

---

## Roadmap

```text
docs/roadmap/ROADMAP.md
```

Conteúdo:

* funcionalidades futuras;
* itens fora da versão 1;
* critérios para novas versões.

---

## Histórico de alterações

```text
docs/roadmap/CHANGELOG.md
```

Conteúdo:

* versões;
* correções;
* evoluções;
* alterações incompatíveis.

---

## Exemplos

```text
docs/examples/
```

Conteúdo:

* requisições JSON;
* etiquetas ZPL;
* arquivos PDF;
* comandos de teste.

---

# 92. Controle de Alterações da Especificação

## 92.1 Regra

Toda alteração arquitetural deverá atualizar:

* versão do documento;
* histórico de versões;
* seção afetada;
* documentos complementares relacionados.

---

## 92.2 Correções editoriais

Correções de ortografia ou formatação que não alterem comportamento poderão incrementar apenas o campo de revisão documental.

---

## 92.3 Alterações compatíveis

Adições compatíveis poderão incrementar a versão secundária.

Exemplo:

```text
1.0.0 -> 1.1.0
```

---

## 92.4 Alterações incompatíveis

Mudanças incompatíveis deverão incrementar a versão principal.

Exemplo:

```text
1.0.0 -> 2.0.0
```

---

## 92.5 Status do documento

Estados permitidos:

```text
RASCUNHO

EM REVISAO

APROVADO PARA IMPLEMENTACAO

SUBSTITUIDO
```

A versão atual deverá permanecer:

```text
APROVADO PARA IMPLEMENTACAO
```

após a revisão final e correção das inconsistências identificadas.

---

# 93. Conclusão

O Print Agent será um serviço local, multiplataforma e desacoplado, responsável por receber trabalhos de impressão via API REST e encaminhá-los ao sistema de impressão do computador do usuário.

Sua arquitetura deverá permanecer simples:

```text
Sistema cliente
       |
       v
API REST local
       |
       v
Fila FIFO
       |
       v
Estratégia de conteúdo
       |
       v
Provider do sistema operacional
       |
       v
Spooler
       |
       v
Impressora
```

A versão 1 deverá atender especialmente ao cenário em que:

* o backend está hospedado na nuvem;
* o frontend Web é executado no computador do usuário;
* a impressora está conectada localmente por USB;
* o sistema precisa imprimir sem exibir a janela de impressão do navegador;
* o conteúdo ZPL é gerado pela aplicação cliente.

O agente não deverá conhecer layouts, relatórios ou regras de negócio.

Sua responsabilidade termina após o encaminhamento seguro e rastreável do trabalho ao sistema de impressão local.

---

# 94. Aprovação

| Campo                   | Valor                       |
| ----------------------- | --------------------------- |
| Projeto                 | Print Agent                 |
| Versão da especificação | 1.0.0                       |
| Java                    | 17 LTS                      |
| Namespace               | br.eng.eliseu.printagent    |
| Status                  | Aprovado para implementação |
| Data                    | Julho de 2026               |

---
