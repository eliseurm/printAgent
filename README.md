# Print Agent

O Print Agent permite que um sistema envie documentos para as impressoras instaladas no computador do usuário.

Ele funciona como um pequeno serviço local:

```text
Sistema ou navegador → Print Agent → Impressora instalada
```

O agente aceita impressões em ZPL, texto RAW e PDF no Linux. Também oferece uma página para consultar o estado do serviço, visualizar impressoras, acompanhar trabalhos e fazer testes.

## Endereços principais

Depois de iniciar o Print Agent, utilize:

| Recurso | Endereço |
| --- | --- |
| Painel administrativo | <http://localhost:18181/printAgent> |
| API REST | <http://localhost:18181/api/v1> |
| Estado do agente | <http://localhost:18181/api/v1/status> |
| Impressoras instaladas | <http://localhost:18181/api/v1/impressoras> |

O Print Agent aceita conexões somente do próprio computador. Ele não fica acessível para outros computadores da rede.

## Requisitos

Antes de começar, verifique:

- Java 17 instalado;
- impressora instalada no sistema operacional;
- driver da impressora funcionando;
- porta `18181` disponível.

Para confirmar a versão do Java:

```bash
java -version
```

A resposta deve indicar Java 17.

## Como executar no Linux

### 1. Preparar as impressoras

O Linux deve possuir o CUPS instalado e ativo.

Em distribuições baseadas em Ubuntu ou Linux Mint:

```bash
sudo apt update
sudo apt install cups
sudo systemctl enable --now cups
```

Para listar as impressoras reconhecidas:

```bash
lpstat -p
```

### 2. Iniciar o Print Agent

Entre na pasta em que está o arquivo `print-agent-1.0.2.jar` e execute:

```bash
java -jar print-agent-1.0.2.jar
```

Mantenha o terminal aberto enquanto estiver usando o agente.

### 3. Confirmar o funcionamento

Abra no navegador:

<http://localhost:18181/printAgent>

Ou consulte pelo terminal:

```bash
curl http://localhost:18181/api/v1/status
```

## Como executar no Windows

### 1. Preparar as impressoras

Instale a impressora normalmente pelo Windows e faça uma impressão de teste pelo próprio sistema.

O Print Agent somente encontra impressoras visíveis para o usuário que iniciou o processo. Se o agente for executado como serviço, a conta do serviço também precisa ter acesso à impressora.

### 2. Confirmar o Java

Abra o PowerShell e execute:

```powershell
java -version
```

### 3. Iniciar o Print Agent

No PowerShell, entre na pasta que contém o arquivo e execute:

```powershell
java -jar .\print-agent-1.0.2.jar
```

Mantenha a janela aberta enquanto estiver usando o agente.

### 4. Confirmar o funcionamento

Abra no navegador:

<http://localhost:18181/printAgent>

Ou consulte pelo PowerShell:

```powershell
Invoke-RestMethod -Uri "http://localhost:18181/api/v1/status"
```

## Usando o painel administrativo

O painel está disponível em:

<http://localhost:18181/printAgent>

Ele possui as seguintes áreas:

- **Impressoras:** mostra as impressoras encontradas e seus estados;
- **Trabalhos:** mostra as impressões que ainda estão em processamento;
- **Histórico:** mostra os últimos trabalhos concluídos, cancelados ou com erro;
- **Logs:** apresenta informações de funcionamento e erros;
- **Documentação:** contém um guia resumido da API;
- **Teste:** permite editar um JSON e enviá-lo diretamente para a API.

Na aba **Teste**, escolha um exemplo, ajuste o JSON e clique em **Imprimir**. A resposta completa será exibida abaixo do botão.

## Primeiro uso da API

### 1. Verificar o agente

Requisição:

```http
GET /api/v1/status
```

Linux:

```bash
curl http://localhost:18181/api/v1/status
```

Windows PowerShell:

```powershell
Invoke-RestMethod -Uri "http://localhost:18181/api/v1/status"
```

O campo `status` deve apresentar `OPERACIONAL`.

### 2. Descobrir o nome da impressora

Requisição:

```http
GET /api/v1/impressoras
```

Linux:

```bash
curl http://localhost:18181/api/v1/impressoras
```

Windows PowerShell:

```powershell
Invoke-RestMethod -Uri "http://localhost:18181/api/v1/impressoras"
```

Copie exatamente o valor do campo `nome`. Esse valor será usado ao criar uma impressão.

## Imprimindo uma etiqueta ZPL

Crie um arquivo chamado `impressao-zpl.json`:

```json
{
  "impressora": {
    "nome": "ElginL42"
  },
  "configuracoesImpressao": {},
  "trabalho": {
    "nome": "Etiqueta de teste",
    "copias": 1,
    "sincrono": true
  },
  "documento": {
    "tipoConteudo": "application/zpl",
    "codificacao": "utf-8",
    "conteudo": "^XA^FO20,20^A0N,30,30^FDTESTE PRINT AGENT^FS^XZ"
  }
}
```

Substitua `ElginL42` pelo nome retornado pela consulta de impressoras.

### Configurações de impressão da etiqueta

`configuracoesImpressao` contém opções temporárias do trabalho atual. Não existe
uma lista única de propriedades válida para todas as impressoras: as chaves e os
valores dependem do sistema operacional, da fila e do driver instalado. O Print
Agent preserva os nomes originais do driver, sem traduzi-los.

Para descobrir as opções anunciadas por uma impressora, consulte:

```http
GET /api/v1/impressoras/{nome}/capacidades
```

Exemplo:

```bash
curl http://localhost:18181/api/v1/impressoras/ElginL42/capacidades
```

O campo `dados.configuracoes` da resposta relaciona cada propriedade aos valores
disponibilizados pelo driver. Utilize exatamente a grafia retornada. Exemplos
comuns são:

| Propriedade | Finalidade | Exemplos de valores |
| --- | --- | --- |
| `PageSize` | Tamanho da mídia ou etiqueta | `w100h60`, `A4` |
| `Orientation` | Orientação | `0`, `1`, `2`, `3`, `Portrait` |
| `Resolution` | Resolução | `203dpi`, `300dpi` |
| `PrintSpeed` | Velocidade de impressão | `2`, `3`, `4` |
| `PrintDarkness` | Densidade ou intensidade térmica | `0`, `10`, `20` |
| `MediaMethod` | Método de mídia | valor definido pelo driver |
| `PaperType` | Tipo de papel ou mídia | valor definido pelo driver |
| `MirrorImage` | Impressão espelhada | valor definido pelo driver |
| `NegativeImage` | Impressão negativa | valor definido pelo driver |

Essa lista não é fechada. Opções específicas do fabricante também podem ser
enviadas quando forem suportadas pela fila. Por exemplo:

```json
"configuracoesImpressao": {
  "PageSize": "w100h60",
  "PrintDarkness": "20",
  "PrintSpeed": "4",
  "Orientation": "0"
}
```

Regras importantes:

- todas as chaves e todos os valores devem ser strings JSON; use `"20"`, e não `20`;
- as opções valem somente para o trabalho enviado e não alteram o padrão permanente da impressora;
- o tamanho da etiqueta não é convertido pelo agente: envie o valor exato anunciado pelo driver;
- `trabalho.copias` define o número de cópias e não deve ser repetido em `configuracoesImpressao`;
- ao enviar `{}` ou omitir `configuracoesImpressao`, são usados os padrões atuais da fila;
- uma propriedade aceita no JSON pode não ser suportada pelo driver ou pelo provider em uso.

Na implementação atual, as opções são encaminhadas ao comando `lp` na impressão
de PDF no Linux. Nos trabalhos ZPL e `text/plain`, o envio é RAW pelo Java Print
Service e `configuracoesImpressao` ainda não é convertido em atributos do trabalho;
nesses formatos, tamanho, densidade e velocidade normalmente devem estar no
próprio ZPL ou ser configurados na fila/driver.

Linux:

```bash
curl \
  -X POST \
  -H "Content-Type: application/json" \
  --data-binary @impressao-zpl.json \
  http://localhost:18181/api/v1/trabalhos
```

Windows PowerShell:

```powershell
$json = Get-Content -Raw .\impressao-zpl.json

Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:18181/api/v1/trabalhos" `
  -ContentType "application/json" `
  -Body $json
```

## Imprimindo texto RAW

Utilize:

```json
{
  "impressora": {
    "nome": "MinhaImpressora"
  },
  "configuracoesImpressao": {},
  "trabalho": {
    "nome": "Texto de teste",
    "copias": 1,
    "sincrono": true
  },
  "documento": {
    "tipoConteudo": "text/plain",
    "codificacao": "utf-8",
    "conteudo": "TESTE PRINT AGENT"
  }
}
```

O conteúdo é enviado sem cabeçalhos, rodapés ou formatação adicional.

## Imprimindo PDF no Linux

PDF está disponível no Provider Linux. O arquivo deve ser convertido para Base64 e enviado no campo `conteudo`.

Exemplo para converter um PDF:

```bash
base64 -w 0 documento.pdf > documento.base64
```

Estrutura da requisição:

```json
{
  "impressora": {
    "nome": "MinhaImpressora"
  },
  "configuracoesImpressao": {},
  "trabalho": {
    "nome": "Documento PDF",
    "copias": 1,
    "sincrono": true
  },
  "documento": {
    "tipoConteudo": "application/pdf",
    "codificacao": "base64",
    "conteudo": "COLE_AQUI_O_CONTEUDO_BASE64"
  }
}
```

Na versão 1.0.2, PDF ainda não é suportado no Windows. O suporte está planejado para a versão 1.2.0.

## Modo síncrono e assíncrono

O campo `trabalho.sincrono` controla quanto tempo a chamada aguarda:

- `true`: a API aguarda a conclusão, o erro ou o limite de tempo;
- `false`: a API aceita o trabalho e responde sem esperar a impressão.

Mesmo no modo síncrono, todos os trabalhos passam pela mesma fila.

## Consultando trabalhos

Trabalhos atuais:

```http
GET /api/v1/trabalhos
```

Um trabalho específico:

```http
GET /api/v1/trabalhos/{id}
```

Histórico:

```http
GET /api/v1/historico
```

Exemplo:

```bash
curl http://localhost:18181/api/v1/historico
```

## Entendendo a resposta

As respostas JSON seguem este formato:

```json
{
  "sucesso": true,
  "mensagem": "Operação realizada com sucesso.",
  "dados": {},
  "erros": [],
  "dataHora": "2026-07-15T15:30:00-03:00"
}
```

Quando ocorrer um problema:

- `sucesso` será `false`;
- `mensagem` apresentará um resumo;
- `erros` conterá o código, a descrição e o campo relacionado.

## Estados de um trabalho

| Estado | Significado |
| --- | --- |
| `RECEBIDO` | A API recebeu o trabalho. |
| `VALIDADO` | Os dados foram validados. |
| `NA_FILA` | O trabalho está aguardando. |
| `IMPRIMINDO` | O trabalho está sendo enviado. |
| `CONCLUIDO` | O sistema de impressão aceitou o trabalho. |
| `ERRO` | O envio não pôde ser concluído. |
| `CANCELADO` | O trabalho foi cancelado antes do envio. |

`CONCLUIDO` informa que o sistema operacional aceitou o trabalho. Isso não garante que o papel tenha saído fisicamente da impressora.

## Consultando logs

Pelo navegador:

<http://localhost:18181/printAgent>

Pela API:

```bash
curl "http://localhost:18181/api/v1/logs?linhas=300"
```

Os arquivos de log também podem ser listados:

```http
GET /api/v1/logs/arquivos
```

## Problemas comuns

### A página não abre

Verifique se o agente está em execução:

```text
http://localhost:18181/api/v1/status
```

Se necessário, confira se a porta está sendo usada por outro programa.

Linux:

```bash
ss -ltnp | grep 18181
```

Windows PowerShell:

```powershell
Get-NetTCPConnection -LocalPort 18181
```

### A impressora não aparece

1. Confirme se ela está instalada no sistema operacional.
2. Faça uma impressão de teste pelo Windows ou CUPS.
3. Reinicie o Print Agent.
4. Verifique se o usuário que executa o agente tem acesso à impressora.

### `IMPRESSORA_NAO_ENCONTRADA`

O nome informado no JSON deve ser exatamente igual ao nome retornado por:

```text
GET /api/v1/impressoras
```

### ZPL é impresso como texto

Confirme se a impressora entende ZPL e se o driver ou a fila está configurado para envio RAW.

### O trabalho foi concluído, mas nada foi impresso

Consulte:

- a fila do sistema operacional;
- o estado físico da impressora;
- papel ou etiquetas;
- conexão USB ou de rede;
- aba **Logs** do painel.

### Erro ao imprimir PDF no Windows

Esse formato ainda não é suportado no Windows na versão 1.0.2. Utilize ZPL ou RAW, ou faça a impressão PDF em um computador Linux.

## Encerrando o agente

Se ele estiver aberto em um terminal, pressione:

```text
Ctrl + C
```

Isso encerra o serviço de forma controlada.

## Segurança

- O agente escuta somente em `127.0.0.1`.
- Não abra a porta `18181` no firewall para acesso externo.
- Não configure `server.address` como `0.0.0.0`.
- Não envie documentos confidenciais para serviços externos para montar as requisições.

## Documentação técnica

Para detalhes completos, consulte:

- [`docs/api/API.md`](docs/api/API.md);
- [`docs/api/JSON.md`](docs/api/JSON.md);
- [`docs/web/WEBUI.md`](docs/web/WEBUI.md);
- [`docs/instalacao/INSTALACAO.md`](docs/instalacao/INSTALACAO.md).
