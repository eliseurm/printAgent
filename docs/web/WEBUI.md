# Print Agent — Interface Web

## Especificação da Web UI

| Campo               | Valor                       |
| ------------------- | --------------------------- |
| Projeto             | Print Agent                 |
| Sistema relacionado | Presente                    |
| Documento           | WEBUI.md                    |
| Versão da interface | 1.0.2                       |
| Versão da API       | v1                          |
| Java                | 17 LTS                      |
| Namespace           | `br.eng.eliseu.printagent`  |
| Endereço padrão     | `http://localhost:18181`    |
| Idioma              | Português do Brasil         |
| Status              | Aprovado para implementação |
| Última atualização  | Julho de 2026               |

---

# 1. Objetivo

Este documento define a interface Web administrativa do Print Agent.

A interface deverá permitir:

* consultar o estado do agente;
* visualizar impressoras instaladas;
* consultar capacidades de impressão;
* acompanhar a fila;
* consultar o histórico;
* cancelar trabalhos pendentes;
* executar impressões de teste;
* consultar e alterar configurações autorizadas;
* visualizar logs;
* verificar atualizações;
* consultar informações da instalação.

A interface não deverá possuir regras de negócio próprias.

Toda operação deverá utilizar a API REST definida em:

```text
docs/api/API.md
```

---

# 2. Endereço

A partir da versão 1.0.2, a interface deverá estar disponível em:

```text
http://localhost:18181/printAgent
```

A rota principal deverá abrir o Dashboard.

Exemplo:

```text
GET /printAgent
```

A interface deverá utilizar caminhos relativos para comunicação com a API.

Exemplo correto:

```javascript
fetch('/api/v1/status');
```

Exemplo proibido:

```javascript
fetch('http://localhost:18181/api/v1/status');
```

Essa regra permite que a interface continue funcionando quando a porta for alterada.

---

# 3. Tecnologias

A versão 1 deverá utilizar:

* HTML5;
* CSS3;
* JavaScript moderno;
* recursos estáticos servidos pelo Spring Boot.

Não deverá utilizar framework JavaScript de aplicação.

Exemplos fora do escopo:

* Angular;
* React;
* Vue;
* Svelte;
* Electron.

Bibliotecas auxiliares pequenas poderão ser utilizadas quando:

* forem realmente necessárias;
* estiverem empacotadas com a aplicação;
* não dependerem de CDN;
* não introduzirem build frontend complexo;
* tiverem licença compatível.

---

# 4. Estrutura de arquivos

Estrutura sugerida:

```text
src/main/resources/
├── static/
│   ├── css/
│   │   ├── app.css
│   │   ├── layout.css
│   │   ├── components.css
│   │   └── responsive.css
│   ├── js/
│   │   ├── app.js
│   │   ├── api.js
│   │   ├── dashboard.js
│   │   ├── impressoras.js
│   │   ├── trabalhos.js
│   │   ├── historico.js
│   │   ├── testes.js
│   │   ├── configuracoes.js
│   │   ├── logs.js
│   │   └── atualizacoes.js
│   ├── img/
│   └── index.html
└── templates/
```

A interface poderá ser implementada como aplicação de página única simples ou como páginas separadas.

A solução deverá priorizar simplicidade.

---

# 5. Princípios da interface

## 5.1 Simplicidade

A interface deverá permitir consulta rápida.

Não deverá possuir animações, assistentes ou componentes complexos sem necessidade.

---

## 5.2 Clareza

Todos os estados deverão ser apresentados por texto.

Exemplo:

```text
OPERACIONAL
```

Não deverá existir indicação somente por cor.

---

## 5.3 Funcionamento offline

A interface deverá funcionar sem acesso à Internet.

Não deverão existir dependências obrigatórias de:

* fontes externas;
* CDNs;
* APIs públicas;
* serviços de analytics;
* bibliotecas hospedadas remotamente.

---

## 5.4 Baixo consumo

A interface deverá possuir poucos recursos estáticos e baixo consumo de CPU.

Atualizações periódicas deverão ser interrompidas quando a tela não estiver ativa.

---

## 5.5 Administração local

A interface destina-se à administração do agente instalado no computador atual.

Ela não é um painel central de múltiplos computadores.

---

# 6. Layout geral

## 6.1 Estrutura

A interface deverá possuir:

```text
+------------------------------------------------------------+
| Cabeçalho                                                  |
+----------------------+-------------------------------------+
| Menu lateral         | Área de conteúdo                    |
|                      |                                     |
| Dashboard            |                                     |
| Impressoras          |                                     |
| Trabalhos            |                                     |
| Histórico            |                                     |
| Teste                |                                     |
| Configurações        |                                     |
| Logs                 |                                     |
| Atualizações         |                                     |
| Sobre                |                                     |
+----------------------+-------------------------------------+
| Rodapé                                                     |
+------------------------------------------------------------+
```

---

## 6.2 Cabeçalho

O cabeçalho deverá apresentar:

* nome `Print Agent`;
* versão instalada;
* estado atual;
* ação para atualizar a tela;
* acesso à tela Sobre.

Exemplo:

```text
Print Agent 1.0.0                         ● OPERACIONAL
```

---

## 6.3 Menu

Itens obrigatórios:

```text
Dashboard
Impressoras
Trabalhos
Histórico
Teste de impressão
Configurações
Logs
Atualizações
Sobre
```

O item ativo deverá permanecer destacado.

---

## 6.4 Rodapé

O rodapé poderá apresentar:

```text
Print Agent 1.0.0
Java 17
br.eng.eliseu.printagent
```

---

# 7. Navegação

## 7.1 Comportamento

A navegação deverá ocorrer sem abrir novas janelas.

A interface poderá utilizar:

* hash routing;
* history API;
* troca de seções no mesmo documento;
* páginas HTML separadas.

A escolha deverá preservar compatibilidade e simplicidade.

---

## 7.2 Rotas sugeridas

```text
/
#/dashboard
#/impressoras
#/trabalhos
#/historico
#/teste
#/configuracoes
#/logs
#/atualizacoes
#/sobre
```

O uso de hash routing é recomendado para evitar necessidade de configuração adicional no Spring Boot.

---

## 7.3 Estado da navegação

Ao atualizar o navegador, a tela atual deverá ser mantida quando possível.

---

# 8. Cliente da API

## 8.1 Módulo central

Toda comunicação HTTP deverá ser centralizada em:

```text
api.js
```

A interface não deverá repetir código de:

* `fetch`;
* timeout;
* parsing do envelope;
* tratamento de erro;
* cabeçalhos;
* cancelamento da requisição.

---

## 8.2 Função base

Exemplo conceitual:

```javascript
async function requisitar(
    caminho,
    opcoes = {},
    timeoutMs = 15000
) {
    const controlador = new AbortController();

    const temporizador = setTimeout(
        () => controlador.abort(),
        timeoutMs
    );

    try {
        const resposta = await fetch(caminho, {
            ...opcoes,
            headers: {
                Accept: 'application/json',
                ...(opcoes.body
                    ? { 'Content-Type': 'application/json' }
                    : {}),
                ...opcoes.headers
            },
            signal: controlador.signal,
            cache: 'no-store'
        });

        const corpo = resposta.status === 204
            ? null
            : await resposta.json();

        if (!resposta.ok) {
            throw normalizarErroHttp(resposta, corpo);
        }

        if (corpo && corpo.sucesso === false) {
            throw normalizarErroEnvelope(corpo);
        }

        return corpo;
    } finally {
        clearTimeout(temporizador);
    }
}
```

---

## 8.3 Timeout

Valor padrão:

```text
15 segundos
```

Consultas de capacidades poderão possuir timeout maior.

Valor sugerido:

```text
30 segundos
```

---

## 8.4 Falha de conexão

Quando o agente não responder, apresentar:

```text
Não foi possível conectar ao Print Agent.

Verifique se o agente está instalado e em execução neste computador.
```

---

## 8.5 Erros da API

Quando houver lista de erros, todos deverão ser apresentados.

Exemplo:

```text
Não foi possível concluir a operação.

• O nome da impressora deve ser informado.
• A quantidade de cópias deve estar entre 1 e 999.
```

---

# 9. Componentes reutilizáveis

## 9.1 Cartão de status

Utilizado no Dashboard.

Estrutura:

* título;
* valor;
* descrição;
* estado opcional.

Exemplo:

```text
Impressoras encontradas
5
```

---

## 9.2 Tabela

As tabelas deverão possuir:

* cabeçalho;
* estado de carregamento;
* estado vazio;
* estado de erro;
* ações;
* responsividade.

---

## 9.3 Modal

Utilizado para:

* confirmação;
* detalhes;
* capacidades;
* erros;
* ações destrutivas.

---

## 9.4 Alerta

Tipos:

```text
sucesso
informacao
aviso
erro
```

Alertas não deverão depender apenas de cor.

---

## 9.5 Carregamento

Exemplos:

```text
Carregando...
Atualizando impressoras...
Enviando impressão...
Salvando configurações...
```

---

## 9.6 Estado vazio

Exemplo:

```text
Nenhum trabalho está aguardando impressão.
```

---

# 10. Dashboard

## 10.1 Objetivo

Apresentar uma visão rápida do estado do agente.

---

## 10.2 Endpoint

```text
GET /api/v1/status
```

---

## 10.3 Dados exibidos

* status;
* versão;
* Java;
* sistema operacional;
* porta;
* horário de inicialização;
* trabalhos na fila;
* impressoras encontradas;
* impressora padrão;
* quantidade de erros no histórico;
* atualização disponível.

---

## 10.4 Cartões

Cartões sugeridos:

```text
Estado do agente
Impressoras
Trabalhos na fila
Histórico
Erros
Atualização
```

---

## 10.5 Exemplo

```text
+----------------------+  +----------------------+
| Estado               |  | Impressoras          |
| OPERACIONAL          |  | 5                    |
+----------------------+  +----------------------+

+----------------------+  +----------------------+
| Trabalhos na fila    |  | Erros                |
| 0                    |  | 1                    |
+----------------------+  +----------------------+
```

---

## 10.6 Atualização automática

Intervalo sugerido:

```text
5 segundos
```

A atualização deverá ocorrer somente enquanto o Dashboard estiver visível.

---

## 10.7 Estado degradado

Caso não existam impressoras:

```text
Nenhuma impressora foi localizada.
```

Caso o Provider esteja indisponível:

```text
O sistema de impressão não está disponível.
```

---

# 11. Tela de Impressoras

## 11.1 Objetivo

Listar e diagnosticar impressoras instaladas.

---

## 11.2 Endpoints

```text
GET /api/v1/impressoras
POST /api/v1/impressoras/atualizar
GET /api/v1/impressoras/{nome}
GET /api/v1/impressoras/{nome}/capacidades
```

---

## 11.3 Colunas

| Coluna | Descrição                               |
| ------ | --------------------------------------- |
| Nome   | Nome registrado no sistema operacional. |
| Status | Estado normalizado.                     |
| Padrão | Indica impressora padrão.               |
| Ações  | Capacidades e teste.                    |

---

## 11.4 Status

Valores:

```text
READY
BUSY
OFFLINE
STOPPED
UNKNOWN
```

Textos exibidos:

| Valor   | Texto               |
| ------- | ------------------- |
| READY   | Disponível          |
| BUSY    | Ocupada             |
| OFFLINE | Offline             |
| STOPPED | Parada              |
| UNKNOWN | Estado desconhecido |

---

## 11.5 Ações

```text
Atualizar impressoras
Consultar capacidades
Realizar teste
Copiar nome
```

---

## 11.6 Atualização

Ao clicar em:

```text
Atualizar impressoras
```

a interface deverá:

1. desabilitar o botão;
2. apresentar estado de carregamento;
3. chamar o endpoint;
4. recarregar a lista;
5. apresentar resultado.

---

## 11.7 Capacidades

As capacidades deverão ser exibidas em modal ou painel lateral.

Exemplo:

| Propriedade | Valores                     |
| ----------- | --------------------------- |
| PageSize    | w100h80, w100h100, w100h150 |
| Resolution  | 203dpi                      |
| Orientation | 0, 1, 2, 3                  |
| PrintSpeed  | 2, 3, 4                     |

---

## 11.8 Cópia do nome

A ação deverá copiar exatamente o valor retornado pela API.

Exemplo:

```text
ElginL42
```

A interface deverá confirmar:

```text
Nome da impressora copiado.
```

---

# 12. Tela de Trabalhos

## 12.1 Objetivo

Apresentar os trabalhos atuais e seus estados.

---

## 12.2 Endpoints

```text
GET /api/v1/trabalhos
GET /api/v1/trabalhos/{id}
DELETE /api/v1/trabalhos/{id}
```

---

## 12.3 Colunas

| Coluna     | Descrição                |
| ---------- | ------------------------ |
| ID         | UUID reduzido.           |
| Nome       | Nome do trabalho.        |
| Impressora | Destino.                 |
| Tipo       | ZPL, PDF ou RAW.         |
| Cópias     | Quantidade.              |
| Status     | Estado atual.            |
| Criado em  | Data e hora.             |
| Ações      | Detalhes e cancelamento. |

---

## 12.4 UUID reduzido

A tabela poderá apresentar apenas os primeiros caracteres.

Exemplo:

```text
8fd1b4fb
```

Nos detalhes, o UUID completo deverá ser exibido.

---

## 12.5 Tipo amigável

| Tipo técnico    | Texto |
| --------------- | ----- |
| application/zpl | ZPL   |
| application/pdf | PDF   |
| text/plain      | RAW   |

---

## 12.6 Atualização automática

Intervalo sugerido:

```text
3 segundos
```

A atualização deverá ocorrer somente com a tela visível.

---

## 12.7 Filtros

Filtros locais:

* nome;
* impressora;
* status;
* tipo;
* UUID.

---

## 12.8 Cancelamento

O botão deverá estar habilitado somente nos estados:

```text
RECEBIDO
VALIDADO
NA_FILA
```

Confirmação:

```text
Deseja cancelar este trabalho de impressão?

O documento não será enviado para a impressora.
```

---

## 12.9 Detalhes

O painel de detalhes deverá exibir:

* UUID completo;
* nome;
* impressora;
* tipo;
* cópias;
* status;
* data de criação;
* data de início;
* data de término;
* mensagem de erro;
* configurações de impressão.

Não deverá exibir o conteúdo completo do documento.

---

# 13. Tela de Histórico

## 13.1 Objetivo

Consultar os trabalhos finalizados.

---

## 13.2 Endpoint

```text
GET /api/v1/historico
DELETE /api/v1/historico
```

---

## 13.3 Estados

```text
CONCLUIDO
ERRO
CANCELADO
```

---

## 13.4 Colunas

| Coluna        | Descrição                     |
| ------------- | ----------------------------- |
| ID            | Identificador reduzido.       |
| Nome          | Nome do trabalho.             |
| Impressora    | Destino.                      |
| Tipo          | Tipo do documento.            |
| Cópias        | Quantidade.                   |
| Resultado     | Estado final.                 |
| Duração       | Tempo entre início e término. |
| Finalizado em | Data e hora.                  |
| Ações         | Detalhes.                     |

---

## 13.5 Filtros

* nome;
* impressora;
* tipo;
* estado;
* data;
* mensagem de erro.

---

## 13.6 Limpeza

A ação:

```text
Limpar histórico
```

deverá exigir confirmação.

Mensagem:

```text
Deseja limpar o histórico mantido em memória?

Os arquivos de log não serão removidos.
```

---

## 13.7 Informação sobre persistência

Exibir:

```text
O histórico é mantido apenas em memória e será limpo quando o Print Agent for reiniciado.
```

---

# 14. Tela de Teste de Impressão

## 14.1 Objetivo

Permitir teste manual das impressoras.

---

## 14.2 Endpoint

```text
POST /api/v1/testes/impressao
```

---

## 14.3 Campos

| Campo         | Obrigatório |
| ------------- | ----------: |
| Impressora    |         Sim |
| Tipo de teste |         Sim |
| Configurações |         Não |

---

## 14.4 Tipos

```text
ZPL
RAW
PDF
```

---

## 14.5 Impressora

A lista deverá ser carregada por:

```text
GET /api/v1/impressoras
```

A impressora padrão deverá aparecer previamente selecionada.

---

## 14.6 Configurações

A interface deverá permitir adicionar linhas de chave e valor.

Exemplo:

| Chave         | Valor   | Ação    |
| ------------- | ------- | ------- |
| PageSize      | w100h60 | Remover |
| PrintDarkness | 20      | Remover |

Botão:

```text
Adicionar configuração
```

---

## 14.7 Sugestão por capacidades

Após selecionar a impressora, a interface poderá carregar as capacidades.

Quando disponíveis, as chaves e valores poderão ser selecionados em listas.

Quando indisponíveis, os campos continuarão livres.

---

## 14.8 Envio

Botão:

```text
Enviar teste
```

Durante o envio:

* desabilitar botão;
* evitar duplo clique;
* exibir carregamento.

---

## 14.9 Resultado

Apresentar:

```text
Teste criado com sucesso.

Trabalho: 8fd1b4fb-21db-4e3f-94b2-dfb83b2d3f0d
Status: RECEBIDO
```

Oferecer ação:

```text
Acompanhar trabalho
```

---

## 14.10 Aviso

Exibir:

```text
O estado CONCLUIDO indica que o sistema de impressão aceitou o trabalho. Isso não garante que o papel ou a etiqueta tenha saído fisicamente da impressora.
```

---

# 15. Tela de Configurações

## 15.1 Objetivo

Consultar e alterar configurações operacionais autorizadas.

---

## 15.2 Endpoints

```text
GET /api/v1/configuracoes
PUT /api/v1/configuracoes
```

---

## 15.3 Seções

```text
Histórico
Trabalhos
Provider
Impressão
Atualização
Logs
```

---

## 15.4 Campos

### Histórico

```text
Tamanho máximo
```

### Trabalhos

```text
Timeout síncrono em segundos
```

### Provider

```text
Timeout de comando em segundos
```

### Impressão

```text
Tratamento de configurações desconhecidas
```

Valores:

```text
IGNORAR
REJEITAR
```

`IGNORAR` desconsidera propriedades não suportadas e permite continuar quando as
demais propriedades e os padrões do driver forem suficientes; caso contrário, o
trabalho falha com mensagem clara. `REJEITAR` impede o envio quando existir uma
propriedade não suportada.

### Atualização

```text
Habilitar atualização
Verificar ao iniciar
```

### Logs

```text
Nível
```

Valores:

```text
ERROR
WARN
INFO
DEBUG
```

---

## 15.5 Salvamento

Botão:

```text
Salvar configurações
```

Após sucesso:

```text
Configurações atualizadas com sucesso.
```

Quando exigir reinicialização:

```text
As configurações foram salvas, mas exigem reinicialização do Print Agent.
```

---

## 15.6 Restauração

Botão:

```text
Restaurar valores padrão
```

Confirmação obrigatória.

---

## 15.7 Porta e CORS

A porta e as origens CORS não deverão ser editáveis na versão 1 pela interface.

Essas propriedades deverão ser alteradas diretamente no arquivo externo.

---

# 16. Tela de Logs

## 16.1 Objetivo

Consultar informações técnicas de diagnóstico.

---

## 16.2 Endpoints

```text
GET /api/v1/logs
GET /api/v1/logs/arquivos
GET /api/v1/logs/arquivos/{nome}
```

---

## 16.3 Filtros

* quantidade de linhas;
* nível;
* busca textual;
* arquivo.

---

## 16.4 Quantidade de linhas

Opções sugeridas:

```text
100
200
500
1000
```

Valor padrão:

```text
500
```

---

## 16.5 Exibição

Os logs deverão ser exibidos em área monoespaçada.

Exemplo:

```text
2026-07-15T18:30:10.100-03:00 INFO  [main] Aplicação iniciada.
2026-07-15T18:31:20.100-03:00 ERROR [print-worker] Falha de impressão.
```

---

## 16.6 Atualização automática

Deverá vir desabilitada.

Quando ativada, intervalo sugerido:

```text
5 segundos
```

---

## 16.7 Download

A lista de arquivos deverá exibir:

* nome;
* tamanho;
* data de modificação;
* ação de download.

---

## 16.8 Segurança

O nome do arquivo recebido da API deverá ser utilizado apenas no endpoint documentado.

A interface não deverá construir caminhos locais.

---

# 17. Tela de Atualizações

## 17.1 Objetivo

Informar se existe uma nova versão do agente.

---

## 17.2 Endpoints

```text
GET /api/v1/atualizacoes
POST /api/v1/atualizacoes/verificar
```

---

## 17.3 Dados

* versão atual;
* versão disponível;
* disponibilidade;
* notas;
* data da última verificação.

---

## 17.4 Sem atualização

```text
O Print Agent está atualizado.

Versão instalada: 1.0.0
```

---

## 17.5 Com atualização

```text
Nova versão disponível: 1.1.0

Correções de impressão no Windows.
```

Ações possíveis:

```text
Baixar atualização
Ver notas
Verificar novamente
```

---

## 17.6 Falha de consulta

```text
Não foi possível verificar atualizações.

A impressão continuará funcionando normalmente.
```

---

# 18. Tela Sobre

## 18.1 Objetivo

Exibir informações da instalação.

---

## 18.2 Dados

* produto;
* versão;
* data do build;
* Java;
* Spring Boot;
* sistema operacional;
* namespace;
* endereço;
* porta;
* arquivo de configuração;
* diretório de logs.

---

## 18.3 Exemplo

```text
Print Agent

Versão: 1.0.0
Java: 17
Spring Boot: 3.x
Namespace: br.eng.eliseu.printagent
Sistema operacional: Linux
Endereço: 127.0.0.1
Porta: 18181
```

---

# 19. Mensagens da interface

## 19.1 Sucesso

```text
Operação realizada com sucesso.
Trabalho recebido com sucesso.
Trabalho cancelado com sucesso.
Catálogo de impressoras atualizado.
Configurações salvas.
```

---

## 19.2 Informação

```text
Nenhum trabalho está na fila.
Nenhum registro foi encontrado no histórico.
As capacidades desta impressora não foram disponibilizadas.
```

---

## 19.3 Aviso

```text
A impressora está offline.
O histórico será limpo ao reiniciar o agente.
A configuração exige reinicialização.
```

---

## 19.4 Erro

```text
Não foi possível conectar ao Print Agent.
A impressora informada não foi localizada.
O sistema de impressão não está disponível.
Não foi possível concluir a operação.
```

---

# 20. Confirmações

Confirmações obrigatórias:

```text
Cancelar trabalho
Limpar histórico
Restaurar configurações
Aplicar atualização
```

Exemplo:

```text
Deseja cancelar este trabalho?

Essa ação não poderá ser desfeita.
```

---

# 21. Acessibilidade

## 21.1 Teclado

As principais ações deverão ser acessíveis por teclado.

---

## 21.2 Rótulos

Todo campo deverá possuir `label`.

---

## 21.3 Foco

Ao abrir modal, o foco deverá ir para o primeiro elemento relevante.

Ao fechar, deverá retornar ao elemento que abriu o modal.

---

## 21.4 Contraste

A interface deverá possuir contraste adequado.

---

## 21.5 Cores

Estados não deverão ser comunicados somente por cor.

---

## 21.6 Mensagens

Erros críticos não deverão desaparecer automaticamente.

---

## 21.7 Tabelas

Tabelas deverão possuir cabeçalhos semânticos.

---

# 22. Responsividade

## 22.1 Desktop

A interface deverá funcionar adequadamente a partir de:

```text
1024 x 768
```

---

## 22.2 Telas menores

Em telas menores:

* o menu poderá ser recolhido;
* tabelas poderão permitir rolagem horizontal;
* cartões deverão empilhar verticalmente;
* modais deverão ocupar maior largura.

---

## 22.3 Dispositivos móveis

A interface poderá funcionar em dispositivos móveis, mas o cenário principal continuará sendo desktop.

---

# 23. Aparência

## 23.1 Tema

A versão 1 deverá utilizar tema claro.

Tema escuro não é obrigatório.

---

## 23.2 Tipografia

Deverão ser utilizadas fontes do sistema.

Exemplo:

```css
font-family:
    system-ui,
    -apple-system,
    BlinkMacSystemFont,
    "Segoe UI",
    sans-serif;
```

Não utilizar fontes externas.

---

## 23.3 Cores

A implementação deverá centralizar cores em variáveis CSS.

Exemplo:

```css
:root {
    --cor-fundo: #f5f6f8;
    --cor-superficie: #ffffff;
    --cor-texto: #1f2937;
    --cor-borda: #d1d5db;
    --cor-sucesso: #16794b;
    --cor-aviso: #9a6700;
    --cor-erro: #b42318;
}
```

Os valores finais poderão variar.

---

## 23.4 Ícones

Ícones deverão ser:

* locais;
* simples;
* acompanhados de texto;
* não obrigatórios para compreensão.

---

# 24. Segurança da interface

## 24.1 Conteúdo externo

A interface não deverá carregar conteúdo remoto.

---

## 24.2 HTML dinâmico

Dados recebidos da API deverão ser inseridos como texto.

Não utilizar `innerHTML` com conteúdo não confiável.

---

## 24.3 Logs

Linhas de log deverão ser escapadas antes da exibição.

---

## 24.4 Mensagens

Mensagens da API não deverão ser interpretadas como HTML.

---

## 24.5 Armazenamento

Não armazenar em `localStorage`:

* ZPL;
* PDF;
* RAW;
* logs completos;
* dados de trabalhos.

Preferências visuais simples poderão ser armazenadas.

---

# 25. Desempenho

## 25.1 Recursos

A página inicial deverá ser pequena e carregar rapidamente.

---

## 25.2 Polling

As chamadas periódicas deverão ser canceladas quando:

* a tela deixar de estar ativa;
* a aba ficar oculta, quando possível;
* o usuário sair da seção.

---

## 25.3 Tabelas

O histórico padrão contém no máximo 100 itens e poderá ser filtrado localmente.

---

## 25.4 Logs

A interface não deverá tentar renderizar arquivos inteiros de log.

---

# 26. Tratamento de erros

## 26.1 Falha HTTP

A interface deverá apresentar a mensagem do envelope.

---

## 26.2 JSON inválido

Quando a resposta do servidor não puder ser interpretada:

```text
O Print Agent retornou uma resposta inválida.
```

---

## 26.3 Timeout

```text
A operação excedeu o tempo limite.
```

---

## 26.4 Agente parado

```text
O Print Agent não está respondendo.
```

---

## 26.5 Provider indisponível

```text
O sistema de impressão não está disponível neste momento.
```

---

# 27. Critérios de aceite

A Web UI será considerada pronta quando:

1. abrir em `http://localhost:18181`;
2. funcionar sem Internet;
3. apresentar o Dashboard;
4. listar impressoras;
5. consultar capacidades;
6. atualizar o catálogo;
7. listar trabalhos;
8. consultar detalhes;
9. cancelar trabalho pendente;
10. listar histórico;
11. limpar histórico;
12. executar teste ZPL;
13. executar teste RAW;
14. executar teste PDF quando suportado;
15. consultar configurações;
16. salvar configurações;
17. consultar logs;
18. baixar logs;
19. consultar atualizações;
20. exibir informações da instalação;
21. tratar falha de conexão;
22. impedir duplo envio;
23. não armazenar documentos no navegador;
24. funcionar nos navegadores suportados;
25. respeitar os contratos de `API.md`.

---

# 28. Navegadores suportados

A versão 1 deverá funcionar nas versões atuais de:

* Google Chrome;
* Microsoft Edge;
* Firefox.

Safari não faz parte dos critérios obrigatórios da versão 1.

---

# 29. Estrutura JavaScript sugerida

```text
app.js
  inicialização
  navegação
  eventos globais

api.js
  cliente HTTP
  timeout
  envelope
  erros

ui.js
  alertas
  modais
  carregamento
  formatação

dashboard.js
impressoras.js
trabalhos.js
historico.js
testes.js
configuracoes.js
logs.js
atualizacoes.js
sobre.js
```

---

# 30. Formatação de dados

## 30.1 Datas

Apresentação em português:

```text
15/07/2026 18:40:00
```

O valor original ISO-8601 deverá ser preservado internamente.

---

## 30.2 Bytes

Exemplos:

```text
452 B
12,4 KB
8,2 MB
```

---

## 30.3 Duração

Exemplos:

```text
450 ms
2,3 s
1 min 12 s
```

---

## 30.4 UUID

Tabela:

```text
8fd1b4fb
```

Detalhes:

```text
8fd1b4fb-21db-4e3f-94b2-dfb83b2d3f0d
```

---

# 31. Integração com o sistema Presente

A Web UI administrativa é independente do frontend do sistema Presente.

O frontend do Presente utilizará diretamente a API local:

```text
http://localhost:18181/api/v1
```

A interface administrativa não deverá expor funções específicas do sistema Presente.

Não deverão existir termos como:

* evento;
* participante;
* presente;
* pessoa;
* patrimônio;
* etiqueta de produto.

A interface deverá trabalhar apenas com conceitos genéricos:

* impressora;
* documento;
* trabalho;
* fila;
* configuração;
* log.

---

# 32. Fora do escopo

A versão 1 não deverá implementar:

* editor visual de etiquetas;
* visualização ZPL;
* renderização PDF no navegador;
* autenticação;
* usuários;
* permissões;
* múltiplos agentes;
* painel central;
* descoberta de agentes na rede;
* envio manual de ZPL arbitrário pela interface administrativa;
* upload genérico de arquivos;
* tema escuro obrigatório;
* internacionalização.

A impressão de teste utilizará conteúdos internos controlados.

---

# 33. Referências

Especificação principal:

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

Roadmap:

```text
docs/roadmap/ROADMAP.md
```

---
